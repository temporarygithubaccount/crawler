package com.example.crawler;

import com.example.crawler.data.SiteMap;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.util.function.Supplier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class LoggingCrawlerTest {

    private Crawler decoratedCrawler;
    private Supplier<Long> timeMillisSupplier;
    private Logger logger;
    private LoggingCrawler loggingCrawler;
    private SiteMap siteMap;

    @BeforeEach
    public void setup() throws MalformedURLException {
        decoratedCrawler = Mockito.mock(Crawler.class);
        timeMillisSupplier = Mockito.mock(Supplier.class);
        when(timeMillisSupplier.get()).thenReturn(5L);
        logger = Mockito.mock(Logger.class);
        loggingCrawler = new LoggingCrawler(decoratedCrawler, timeMillisSupplier, logger);
        siteMap = Mockito.mock(SiteMap.class);
    }

    @Test
    public void testReturnDecoratedObjectResult() throws MalformedURLException {
        when(decoratedCrawler.crawl("aSpecificUrl")).thenReturn(siteMap);

        assertThat(loggingCrawler.crawl("aSpecificUrl")).isSameAs(siteMap);
    }

    @Test
    public void testLogNumberOfResults() throws MalformedURLException {
        when(decoratedCrawler.crawl("aSpecificUrl")).thenReturn(siteMap);

        assertThat(loggingCrawler.crawl("aSpecificUrl")).isSameAs(siteMap);
    }

    @Test
    public void testSwallowInnerCrawlerExceptions() throws MalformedURLException {
        when(decoratedCrawler.crawl(anyString())).thenThrow(new RuntimeException());

        loggingCrawler.crawl("shouldNotThrow");
    }

    @Test
    public void testReturnNullOnInnerCrawlerException() throws MalformedURLException {
        when(decoratedCrawler.crawl(anyString())).thenThrow(new RuntimeException());

        assertThat(loggingCrawler.crawl("shouldNotThrow")).isNull();
    }

    @Test
    public void testLogExceptionOnInnerCrawlerException() throws MalformedURLException {
        when(decoratedCrawler.crawl(anyString())).thenThrow(new RuntimeException());

        loggingCrawler.crawl("shouldNotThrow");

        verify(logger).error(anyString(), any(RuntimeException.class));
    }

    @ParameterizedTest(name = "endTimeMillis=[{arguments}]")
    @ValueSource(longs = { 100L, 1000L, 3125L })
    public void testLogsUrlAndRecordsTimeCorrectly(long endTimeMillis) throws MalformedURLException {
        when(timeMillisSupplier.get()).thenReturn(30L).thenReturn(endTimeMillis);

        loggingCrawler.crawl("aSite");

        verify(logger).info(anyString(), Mockito.eq(endTimeMillis-30L));
    }

    @ParameterizedTest(name = "sitemap.size=[{arguments}]")
    @ValueSource(ints = { 0, 10, 30 })
    public void testLogsNumberOfResults(int numberOfCrawledPages) throws MalformedURLException {
        when(decoratedCrawler.crawl(anyString())).thenReturn(siteMap);
        when(siteMap.size()).thenReturn(numberOfCrawledPages);

        loggingCrawler.crawl("aSite");

        verify(logger).info(anyString(), Mockito.eq(numberOfCrawledPages));
    }

    @Test
    public void testLogZeroResultsWhenSiteMapIsNull() throws MalformedURLException {
        when(decoratedCrawler.crawl(anyString())).thenReturn(null);

        loggingCrawler.crawl("aSite");

        verify(logger).info(anyString(), Mockito.eq(0));
    }
}