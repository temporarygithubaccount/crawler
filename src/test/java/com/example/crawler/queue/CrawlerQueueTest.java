package com.example.crawler.queue;

import com.example.crawler.data.UrlAndParent;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.crawler.TestConstants.MONZO_URL;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CrawlerQueueTest {

    private static final int POLL_TIMEOUT = 1000;
    private static final int CAPACITY = 10000;

    private CrawlerQueue crawlerQueue;

    @BeforeEach
    public void setup() {
        crawlerQueue = new CrawlerQueue(POLL_TIMEOUT, CAPACITY);
    }

    @Test
    public void whenQueueIsEmpty_hasNextReturnsFalse() {
        assertThat(crawlerQueue.hasNext()).isFalse();
    }

    @Test
    public void whenQueueIsNotEmpty_hasNextReturnsTrue() {
        crawlerQueue.enqueue(MONZO_URL, null);

        assertThat(crawlerQueue.hasNext()).isTrue();
    }

    @Test
    public void whenQueueIsEmpty_WaitingPollTimeoutSecondsForNewElements() {
        StopWatch stopWatch = new StopWatch();

        crawlerQueue.hasNext();

        assertThat(stopWatch.timeElapsedFromCreation()).isCloseTo(POLL_TIMEOUT, Percentage.withPercentage(10));
    }

    @Test
    public void whenQueueIsNotEmpty_DontWait() {
        crawlerQueue.enqueue(MONZO_URL, null);
        StopWatch stopWatch = new StopWatch();

        crawlerQueue.hasNext();

        assertThat(stopWatch.timeElapsedFromCreation()).isLessThan(100);
    }

    @Test
    public void whenInnerNonEmptyQueueHasThreadFailure_hasNextReturnsFalse() {
        crawlerQueue.enqueue(MONZO_URL, null);
        Thread.currentThread().interrupt();

        assertThat(crawlerQueue.hasNext()).isFalse();
    }

    @Test
    public void whenNextCalledOnEmptyQueue_thenThrow() {
        assertThrows(NoSuchElementException.class, () -> crawlerQueue.next());
    }

    @Test
    public void whenNextCalledOnEmptyQueueAfterHasNext_thenThrow() {
        crawlerQueue.hasNext();
        assertThrows(NoSuchElementException.class, () -> crawlerQueue.next());
    }

    @Test
    public void whenNElementsEnqueued_thenCallingNextNTimesReturnAllOfThem() {
        List<UrlAndParent> elements = generateTestUrlAndParentsObjects(CAPACITY);

        elements.stream()
            .forEach(element -> crawlerQueue.enqueue(element.url, element.parent));

        List<UrlAndParent> results = new ArrayList<>();
        while (crawlerQueue.hasNext()) {
            results.add(crawlerQueue.next());
        }

        assertThat(results).isEqualTo(elements);
    }

    /**
     * Running on 2 different threads at the same time the producer and the consumer
     * and verify at the end the dequeued elements matches the enqueued ones [order does not matter]
     */
    @Test
    public void whenElementsEnqueuedAndRemovedAtTheSameTime_thenDontLoseAnyOfThem() throws ExecutionException, InterruptedException {
        List<UrlAndParent> elements = generateTestUrlAndParentsObjects(CAPACITY);

        CompletableFuture<Void> enqueueing = CompletableFuture.runAsync(
            () -> elements.stream()
                .forEach(element -> crawlerQueue.enqueue(element.url, element.parent))
            , Executors.newSingleThreadExecutor());

        List<UrlAndParent> results = new ArrayList<>();
        CompletableFuture<Void> dequeueing = CompletableFuture.runAsync(
            () -> {
                while (crawlerQueue.hasNext()) {
                    results.add(crawlerQueue.next());
                }
            }, Executors.newSingleThreadExecutor());

        CompletableFuture.allOf(enqueueing, dequeueing).get();

        //order might vary, but the elements must be the same
        assertThat(results).hasSameSizeAs(elements);
        assertThat(results).containsExactlyInAnyOrder(elements.toArray(new UrlAndParent[CAPACITY]));
    }

    @Test
    public void whenEnqueueOperationFails_DontSurfaceException() {
        Thread.currentThread().interrupt();
        crawlerQueue.enqueue(MONZO_URL, null);
    }

    @Test
    public void whenEnqueueOperationFails_elementsIsNotAddedToQueue() {
        Thread.currentThread().interrupt();

        crawlerQueue.enqueue(MONZO_URL, null);

        assertThat(crawlerQueue.hasNext()).isFalse();
    }

    private List<UrlAndParent> generateTestUrlAndParentsObjects(int elements) {
        return IntStream.range(0, elements)
            .mapToObj(i -> {
                try {
                    return new UrlAndParent(
                        new URL("http://www.site.com/" + i),
                        new URL("http://www.parentsite.com/" + i)
                    );
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());
    }

    private static class StopWatch {
        final long creationMillis;

        StopWatch() {
            creationMillis = System.currentTimeMillis();
        }

        long timeElapsedFromCreation() {
            return System.currentTimeMillis() - creationMillis;
        }
    }
}