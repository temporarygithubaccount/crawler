package com.example.crawler;

import java.net.MalformedURLException;
import java.net.URL;

public class TestConstants {

    public static final String MONZO_URL_STRING = "http://monzo.com/";
    public static URL MONZO_URL = makeURL(MONZO_URL_STRING);
    public static URL REDDIT_URL = makeURL("http://www.reddit.com");

    public static URL makeURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            //simply will not happen
            throw new RuntimeException(url + " is well formed, this won't happen", e);
        }
    }
}
