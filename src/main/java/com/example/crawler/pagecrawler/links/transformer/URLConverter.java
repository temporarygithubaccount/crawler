package com.example.crawler.pagecrawler.links.transformer;

import java.net.MalformedURLException;
import java.net.URL;

public class URLConverter {
    private final String protocolAndHost;

    public URLConverter(String protocolAndHost) {
        this.protocolAndHost = protocolAndHost;
    }

    public URL getAbsoluteURL(String link) {
        try {
            if (isRelativeURL(link)) {
                URL url = new URL(protocolAndHost + link);
                return stripQueryStringAndNamedAnchors(url);
            } else {
                return stripQueryStringAndNamedAnchors(new URL(link));
            }
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private URL stripQueryStringAndNamedAnchors(URL url) throws MalformedURLException {
        return new URL(url.getProtocol() + "://" + url.getHost() + url.getPath());
    }

    private boolean isRelativeURL(String link) {
        return link.substring(0,1).equals("/");
    }
}