package com.example.crawler.pagecrawler.links.filters;

import java.net.URL;

public class LinksFilter {

    private final IsExternalURL isExternalURL;
    private final IsImageURL isImageURL;

    public LinksFilter(IsExternalURL isExternalURL, IsImageURL isImageURL) {
        this.isExternalURL = isExternalURL;
        this.isImageURL = isImageURL;
    }

    public boolean filterUrlsToCrawl(URL absoluteUrl) {
        return absoluteUrl != null && !isExternalURL.verify(absoluteUrl) && !isImageURL.verify(absoluteUrl);
    }

}
