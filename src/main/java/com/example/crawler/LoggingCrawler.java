package com.example.crawler;

import com.example.crawler.data.SiteMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.util.function.Supplier;

public class LoggingCrawler implements Crawler {


    private final Crawler crawler;
    private final Supplier<Long> timeSupplier;
    private final Logger logger;

    public LoggingCrawler(Crawler crawler, Supplier<Long> timeSupplier, Logger logger) {
        this.crawler = crawler;
        this.timeSupplier = timeSupplier;
        this.logger = logger;
    }

    @Override
    public SiteMap crawl(String url) throws MalformedURLException {
        logger.info("Crawling from url {}", url);
        long start = timeSupplier.get();

        SiteMap result = null;
        try {
            result = safeCrawl(url, result);
        }finally {
            long duration = timeSupplier.get() - start;
            logger.info("Crawling complete. Time elapsed: {}ms ", duration);
            logger.info("Crawling complete. Pages indexed: {}", result != null ? result.size() : 0);
        }
        return result;
    }

    private SiteMap safeCrawl(String url, SiteMap result) {
        try {
            result = crawler.crawl(url);
        } catch (Exception e) {
            logger.error("Crawling failed", e);
        }
        return result;
    }
}
