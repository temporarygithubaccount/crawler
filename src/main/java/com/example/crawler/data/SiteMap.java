package com.example.crawler.data;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SiteMap {

    final Map<URL, SiteMapNode> pages = new HashMap<>();
    private final URL rootUrl;

    /**
     * rootUrl is not automatically indexed, you need to addPage for it aswell.
     */
    public SiteMap(URL rootUrl) {
        this.rootUrl = rootUrl;
    }

    /**
     * TODO more strict validation that the rootUrl is the first page added
     */
    public void addPage(UrlAndParent urlAndParent) {
        URL url = urlAndParent.url;
        SiteMapNode node = new SiteMapNode(url);
        pages.put(url, node);
        addLinkToTheNodeInTheParent(node, urlAndParent.parent);
    }

    private void addLinkToTheNodeInTheParent(SiteMapNode node, URL parentUrl) {
        if (parentUrl != null) {
            SiteMapNode parent = pages.get(parentUrl);
            validateInsertionOrder(parentUrl, parent);
            parent.addLink(node);
        }
    }

    //Design decision related to the current way the crawler works.
    // This needs to change if the order of sitemap insertion parent > children changes.
    private void validateInsertionOrder(URL parentUrl, SiteMapNode parent) {
        if (parent == null) {
            throw new RuntimeException(String.format("Parent url %s is not present in the SiteMap", parentUrl));
        }
    }

    public boolean contains(URL urlToVisit) {
        return pages.containsKey(urlToVisit);
    }

    public int size() {
        return pages.size();
    }

    public String toString() {
        SiteMapNode root = pages.get(rootUrl);
        if (root == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        root.recursivePrint(0, stringBuilder);
        stringBuilder.setLength(stringBuilder.length()-1);
        return stringBuilder.toString();
    }

    private static class SiteMapNode {
        private final URL url;
        private final List<SiteMapNode> links = new ArrayList<>();

        public SiteMapNode(URL url) {
            this.url = url;
        }

        private void addLink(SiteMapNode node) {
            links.add(node);
        }

        void recursivePrint(int level, StringBuilder stringBuilder) {
            String prefix = "";
            if (level > 0) {
                prefix = getSpaces(2 * (level-1)) + "├── ";
            }
            stringBuilder.append(prefix + url + "\n");
            for (SiteMapNode child : links) {
                child.recursivePrint(level+1, stringBuilder);
            }
        }

        private String getSpaces(int spaces) {
            return IntStream.range(0, spaces)
                .mapToObj(__ -> "  ")
                .collect(Collectors.joining(""));
        }
    }
}
