package com.example.crawler.pagecrawler.links.filters;

public class LinksFilterFactory {

    //harcoded for the moment, can get less ugly with a DI framework
    public LinksFilter get(String host) {
        return new LinksFilter(
            new IsExternalURL(host),
            new IsImageURL()
        );
    }
}