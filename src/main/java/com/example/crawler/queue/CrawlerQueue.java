package com.example.crawler.queue;

import com.example.crawler.data.UrlAndParent;
import com.example.crawler.constants.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class CrawlerQueue implements Iterator<UrlAndParent> {

    private static final Logger logger = LogManager.getLogger();

    //coupled hardcoded implementation, but I don't plan on changing it for the moment
    private ArrayBlockingQueue<UrlAndParent> urlsToVisit;

    private final int pollTimeout;
    private UrlAndParent next;
    private boolean nextMethodCanBeCalled = false;

    public CrawlerQueue(int pollTimeout, int capacity) {
        this.pollTimeout = pollTimeout;
        urlsToVisit = new ArrayBlockingQueue<>(capacity);
    }

    @Override
    public boolean hasNext() {
        try {
            next = urlsToVisit.poll(pollTimeout, TimeUnit.MILLISECONDS);
            if (next != null) {
                nextMethodCanBeCalled = true;
                return true;
            }
        } catch (InterruptedException e) { }
        return false;
    }

    @Override
    public UrlAndParent next() {
        if (!nextMethodCanBeCalled) {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
        }
        nextMethodCanBeCalled = false; //safety mechanism avoiding next() being called twice in a row.
        return next;
    }

    //this could return a boolean, but at the moment I don't have retry mechanisms so I would not be using it
    public void enqueue(URL url, URL parent) {
        try {
            urlsToVisit.put(new UrlAndParent(url, parent));
        } catch (InterruptedException e) {
            logger.error("Error enqueuing url {}", url, e);
        }
    }
}
