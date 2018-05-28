package com.example.crawler.pagecrawler.links.transformer;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class URLConverterTest {

    private static final String PROTOCOL_AND_HOST = "http://monzo.com";

    private URLConverter urlConverter;

    @BeforeEach
    public void setup() {
        urlConverter = new URLConverter(PROTOCOL_AND_HOST);
    }

    @Test
    public void returnNullForInvalidUrl() {
        assertThat(urlConverter.getAbsoluteURL("111invalid")).isNull();
    }

    @Test
    public void returnAbsoluteURL() throws MalformedURLException {
        URL result = urlConverter.getAbsoluteURL("http://www.somesite.com");

        assertThat(result).isEqualTo(new URL("http://somesite.com"));
    }

    @Test
    public void transformRelativeURLtoAbsolute() throws MalformedURLException {
        URL result = urlConverter.getAbsoluteURL("/somepage");

        assertThat(result).isEqualTo(new URL(PROTOCOL_AND_HOST+"/somepage"));
    }

    @Test
    public void deduplicateUrlsWithSamePaths() {
        assertThat(
            urlConverter.getAbsoluteURL("path/somepage#yes")
        ).isEqualTo(
            urlConverter.getAbsoluteURL("path/somepage#no")
        );
    }
}