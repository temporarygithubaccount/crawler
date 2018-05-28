package com.example.crawler;

import com.example.crawler.data.SiteMap;

import java.net.MalformedURLException;

/**
 * I could compose more "Crawler" implementation as I did in LoggingCrawler to implement
 * different concerns in different classes (e.g. metrics, etc.)
 */
public interface Crawler {
    SiteMap crawl(String url) throws MalformedURLException;
}
