package com.example.crawler.pagecrawler.links.filters;

import java.net.URL;

public class IsExternalURL {
    private final String host;

    public IsExternalURL(String host) {
        this.host = host;
    }

    public boolean verify(URL url) {
        return !url.getHost().equals(host);
    }
}