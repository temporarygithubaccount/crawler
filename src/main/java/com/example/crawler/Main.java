package com.example.crawler;

import com.example.crawler.data.SiteMap;
import com.example.crawler.pagecrawler.PageCrawlerFactory;
import com.example.crawler.pagecrawler.links.CrawlableLinksExtractorFactory;
import com.example.crawler.pagecrawler.links.filters.LinksFilterFactory;
import com.example.crawler.queue.CrawlerQueueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.util.function.Supplier;

public class Main {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws MalformedURLException {
        Crawler crawler = new LoggingCrawler(
            new CrawlerImpl(
                new CrawlerQueueFactory(),
                getPageCrawlerFactoryInstance()
            ),
            currentTimeMillisSupplier(),
            LogManager.getLogger(CrawlerImpl.class)
        );
        SiteMap sitemap = crawler.crawl("https://monzo.com");
        logger.info(sitemap);
    }

    private static PageCrawlerFactory getPageCrawlerFactoryInstance() {
        return new PageCrawlerFactory(getLinksExtractorFactory());
    }

    private static CrawlableLinksExtractorFactory getLinksExtractorFactory() {
        return new CrawlableLinksExtractorFactory(new LinksFilterFactory());
    }

    private static Supplier<Long> currentTimeMillisSupplier() {
        return () -> System.currentTimeMillis();
    }
}
