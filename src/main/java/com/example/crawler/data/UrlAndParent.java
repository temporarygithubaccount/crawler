package com.example.crawler.data;

import java.net.URL;
import java.util.Objects;

public class UrlAndParent {
    public final URL url;
    public final URL parent;

    public UrlAndParent(URL url, URL parent) {
        this.url = url;
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UrlAndParent) {
            UrlAndParent other =  (UrlAndParent) obj;
            return Objects.equals(other.parent, parent) && Objects.equals(other.url, url);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, url);
    }
}
