package com.example.crawler;

import com.example.crawler.constants.Constants;
import com.example.crawler.data.SiteMap;
import com.example.crawler.data.UrlAndParent;
import com.example.crawler.pagecrawler.PageCrawler;
import com.example.crawler.pagecrawler.PageCrawlerFactory;
import com.example.crawler.queue.CrawlerQueue;
import com.example.crawler.queue.CrawlerQueueFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Fetching pages, parsing the content and filtering out uninteresting links is done
 * in the workers thread, while enqueuing results in the workers queue is done in a separate thread
 * (to avoid too many locks in the queue - not sure if this was necessary or was just overengineering, should be tested.
 */
public class CrawlerImpl implements Crawler {

    private final CrawlerQueueFactory queueFactory;
    private final PageCrawlerFactory pageCrawlerFactory;
    private final ExecutorService resultCollectingThread = Executors.newSingleThreadExecutor();
    private final ExecutorService workers = Executors.newWorkStealingPool(Constants.THREADS);

    public CrawlerImpl(CrawlerQueueFactory queueFactory, PageCrawlerFactory pageCrawlerFactory) {
        this.queueFactory = queueFactory;
        this.pageCrawlerFactory = pageCrawlerFactory;
    }

    @Override
    public SiteMap crawl(String url) throws MalformedURLException {
        URL rootUrl = new URL(url); //at this point I don't need a more specific validation and extracting a custom validator to pass my unit tests

        PageCrawler pageCrawler = pageCrawlerFactory.get(rootUrl.getHost(), rootUrl.getProtocol());
        SiteMap siteMap = new SiteMap(rootUrl);

        CrawlerQueue queue = queueFactory.get();
        queue.enqueue(rootUrl, null);

        while (queue.hasNext()) {
            UrlAndParent nextUrl = queue.next();
            URL urlToVisit = nextUrl.url;
            if (!siteMap.contains(urlToVisit)) {
                siteMap.addPage(nextUrl);
                CompletableFuture
                    .supplyAsync(pageCrawler.extractCrawlableLinks(urlToVisit), workers)
                    .thenAcceptAsync(links -> links.forEach(link -> queue.enqueue(link, urlToVisit)), resultCollectingThread);
            }
        }

        resultCollectingThread.shutdown();
        workers.shutdown();
        return siteMap;
    }
}
