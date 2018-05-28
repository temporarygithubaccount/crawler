package com.example.crawler.pagecrawler.links;

import com.example.crawler.pagecrawler.links.filters.LinksFilter;
import com.example.crawler.pagecrawler.links.transformer.URLConverter;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class CrawlableLinksExtractor {

    private final URLConverter URLConverter;
    private final LinksFilter linkFilter;

    public CrawlableLinksExtractor(URLConverter urlConverter,
                                   LinksFilter linkFilter) {
        this.URLConverter = urlConverter;
        this.linkFilter = linkFilter;
    }

    public List<URL> getLinks(Document doc) {
        return getAllLinks(doc)
            .stream()
            .map(URLConverter::getAbsoluteURL)
            .filter(linkFilter::filterUrlsToCrawl)
            .collect(Collectors.toList());
    }

    private List<String> getAllLinks(Document doc) {
        return doc
            .select("a[href]")
            .eachAttr("href");
    }
}