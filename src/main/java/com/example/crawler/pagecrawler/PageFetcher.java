package com.example.crawler.pagecrawler;

import com.example.crawler.constants.Constants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

public class PageFetcher {

    Document fetch(URL urlToVisit) throws IOException {
        return Jsoup.parse(urlToVisit, Constants.TIMEOUT_MS);
    }
}