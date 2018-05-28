package com.example.crawler.pagecrawler;

import com.example.crawler.pagecrawler.links.CrawlableLinksExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


/**
 * Missing features:
 * - retry on timeouts
 * - skip pdfs or other non-web-page urls
 * - does it follow redirects?
 */
public class PageCrawler {

    private static final Logger logger = LogManager.getLogger();

    private final PageFetcher pageFetcher;
    private final CrawlableLinksExtractor linkExtractor;

    public PageCrawler(PageFetcher pageFetcher, CrawlableLinksExtractor linkExtractor) {
        this.pageFetcher = pageFetcher;
        this.linkExtractor = linkExtractor;
    }

    public Supplier<List<URL>> extractCrawlableLinks(URL urlToVisit) {
        return () -> {
            logger.debug("visiting {} on thread {} ", urlToVisit, Thread.currentThread().getId());
            try {
                Document doc = pageFetcher.fetch(urlToVisit);
                return linkExtractor.getLinks(doc);

            } catch (Exception e) {
                logger.warn("Error visiting {}", urlToVisit, e);
                return new ArrayList<>();
            }
        };
    }
}
