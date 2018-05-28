package com.example.crawler.pagecrawler.links;

import com.example.crawler.pagecrawler.links.filters.LinksFilterFactory;
import com.example.crawler.pagecrawler.links.transformer.URLConverter;

public class CrawlableLinksExtractorFactory {

    private final LinksFilterFactory linksFilterFactory;

    public CrawlableLinksExtractorFactory(LinksFilterFactory linksFilterFactory) {
        this.linksFilterFactory = linksFilterFactory;
    }

    public CrawlableLinksExtractor get(String host, String protocol) {
        return new CrawlableLinksExtractor(
            new URLConverter(String.format("%s://%s", protocol, host)),
            linksFilterFactory.get(host)
        );
    }
}