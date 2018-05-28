package com.example.crawler;

import com.example.crawler.data.SiteMap;
import com.example.crawler.data.UrlAndParent;
import com.example.crawler.pagecrawler.PageCrawler;
import com.example.crawler.pagecrawler.PageCrawlerFactory;
import com.example.crawler.queue.CrawlerQueue;
import com.example.crawler.queue.CrawlerQueueFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static com.example.crawler.TestConstants.MONZO_URL;
import static com.example.crawler.TestConstants.MONZO_URL_STRING;
import static com.example.crawler.TestConstants.makeURL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//probably too many mocking in here, I could have replaced at least the queue with the real implementation
public class CrawlerImplTest {

    private static final UrlAndParent TEST_QUEUE_ELEMENT = new UrlAndParent(MONZO_URL, null);

    private CrawlerImpl crawler;
    private PageCrawlerFactory pageCrawlerFactory;
    private PageCrawler pageCrawler;
    private CrawlerQueueFactory queueFactory;
    private CrawlerQueue crawlerQueue;

    @BeforeEach
    public void setup() {
        //some mocks behaviour are required to avoid NPE
        pageCrawler = Mockito.mock(PageCrawler.class);
        when(pageCrawler.extractCrawlableLinks(any())).thenReturn(() -> new ArrayList<>());

        pageCrawlerFactory = Mockito.mock(PageCrawlerFactory.class);
        when(pageCrawlerFactory.get(anyString(), anyString())).thenReturn(pageCrawler);

        crawlerQueue = Mockito.mock(CrawlerQueue.class);
        when(crawlerQueue.next()).thenReturn(TEST_QUEUE_ELEMENT);

        queueFactory = Mockito.mock(CrawlerQueueFactory.class);
        when(queueFactory.get()).thenReturn(crawlerQueue);

        crawler = new CrawlerImpl(queueFactory, pageCrawlerFactory);
    }

    @Test
    public void testNotValidUrlReturningException() throws Exception {
        assertThrows(MalformedURLException.class, () -> crawler.crawl("notAValidUrl"));
    }

    @Test
    public void testNullUrlReturningException() throws Exception {
        assertThrows(MalformedURLException.class, () -> crawler.crawl(null));
    }

    @Test
    public void testValidUrlNotReturningException() throws Exception {
    }

    @Test
    public void testInitialUrlEnqueued() throws Exception {
        crawler.crawl(MONZO_URL_STRING);

        verify(crawlerQueue).enqueue(new URL(MONZO_URL_STRING), null);
    }

    @Test
    public void testInitialUrlDequeued() throws Exception {
        when(crawlerQueue.hasNext()).thenReturn(true).thenReturn(false);

        crawler.crawl(MONZO_URL_STRING);

        verify(crawlerQueue).next();
        verify(pageCrawler).extractCrawlableLinks(MONZO_URL);
    }

    @Test
    public void verifyAllElementsInQueueAreDequeued() throws Exception {
        when(crawlerQueue.hasNext())
            .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        when(crawlerQueue.next())
            .thenReturn(new UrlAndParent(makeURL("http://mysite.com/1"), null))
            .thenReturn(new UrlAndParent(makeURL("http://mysite.com/2"), null))
            .thenReturn(new UrlAndParent(makeURL("http://mysite.com/3"), null));

        crawler.crawl(MONZO_URL_STRING);

        verify(pageCrawler, times(3)).extractCrawlableLinks(any());
    }

    @Test
    public void testDuplicatePagesAreNotCrawledTwice() throws Exception {
        when(crawlerQueue.hasNext())
            .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        when(crawlerQueue.next())
            .thenReturn(new UrlAndParent(MONZO_URL, null))
            .thenReturn(new UrlAndParent(MONZO_URL, null))
            .thenReturn(new UrlAndParent(MONZO_URL, null));

        crawler.crawl(MONZO_URL_STRING);

        verify(pageCrawler, times(1)).extractCrawlableLinks(MONZO_URL);
    }

    @Test
    public void testUrlsRetrievedByPageCrawlerAreReEnqueued() throws Exception {
        crawler = new CrawlerImpl(new CrawlerQueueFactory(), pageCrawlerFactory);

        List<URL> linksExtractedFromRoot = Arrays.asList(makeURL("http://site1.com"), makeURL("http://site2.com"));
        when(pageCrawler.extractCrawlableLinks(any())).thenReturn(() -> linksExtractedFromRoot);

        crawler.crawl(MONZO_URL_STRING);

        verify(pageCrawler).extractCrawlableLinks(linksExtractedFromRoot.get(0));
        verify(pageCrawler).extractCrawlableLinks(linksExtractedFromRoot.get(1));
    }

    @Test
    public void testVisitedSitesAreReturnedInSitemapOutput() throws Exception {
        crawler = new CrawlerImpl(new CrawlerQueueFactory(), pageCrawlerFactory);

        List<URL> linksExtractedFromRoot = Arrays.asList(makeURL("http://site1.com"), makeURL("http://site2.com"));
        when(pageCrawler.extractCrawlableLinks(any())).thenReturn(() -> linksExtractedFromRoot);

        SiteMap result = crawler.crawl(MONZO_URL_STRING);

        for (URL url : linksExtractedFromRoot) {
            assertThat(result.contains(url)).isTrue();
        }
        assertThat(result.contains(MONZO_URL));
    }

}
