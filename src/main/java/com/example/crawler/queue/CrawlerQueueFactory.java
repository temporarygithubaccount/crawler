package com.example.crawler.queue;

import com.example.crawler.constants.Constants;

import java.util.function.Supplier;

public class CrawlerQueueFactory implements Supplier<CrawlerQueue> {

    @Override
    public CrawlerQueue get() {
        //TODO capacity might help some tuning
        return new CrawlerQueue(Constants.TIMEOUT_MS, 2 * Constants.THREADS);
    }
}
