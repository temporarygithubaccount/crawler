package com.example.crawler.pagecrawler.links.filters;

import java.net.URL;
import java.util.regex.Pattern;

public class IsImageURL {

    private static final Pattern imageUrl = Pattern.compile(".*\\.(jpg|jpeg|png|gif|JPG|JPEG|PNG|GIF)");

    public boolean verify(URL url) {
        return imageUrl.matcher(url.toString()).matches();
    }
}
