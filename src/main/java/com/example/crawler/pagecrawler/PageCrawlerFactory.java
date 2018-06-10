package com.example.crawler.pagecrawler;

import com.example.crawler.pagecrawler.links.CrawlableLinksExtractorFactory;

public class PageCrawlerFactory {

    private final CrawlableLinksExtractorFactory linksExtractorFactory;

    public PageCrawlerFactory(CrawlableLinksExtractorFactory linksExtractorFactory) {
        this.linksExtractorFactory = linksExtractorFactory;
    }

    //still some hardcoded dependencies here, it would have been less ugly
    //using a dependency injection framework (e.g. Spring) or adding a PageFetcherFactory
    public PageCrawler get(String host, String protocol) {
        return new PageCrawler(
            new PageFetcher(),
            linksExtractorFactory.get(host, protocol)
        );
    }

}
