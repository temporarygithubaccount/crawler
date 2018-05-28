package com.example.crawler.pagecrawler;

import com.example.crawler.TestConstants;
import com.example.crawler.pagecrawler.links.CrawlableLinksExtractor;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.crawler.TestConstants.MONZO_URL;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PageCrawlerTest {

    private static final String PROTOCOL = "aProtocol";
    private static final String HOST = "anHost";

    private PageFetcher pageFetcher;
    private CrawlableLinksExtractor crawlableLinksExtractor;
    private PageCrawler pageCrawler;

    private Document doc;

    @BeforeEach
    public void setup() {
        pageFetcher = Mockito.mock(PageFetcher.class);
        crawlableLinksExtractor = Mockito.mock(CrawlableLinksExtractor.class);
        pageCrawler = new PageCrawler(pageFetcher, crawlableLinksExtractor);
    }

    @Test
    public void returnEmptyListWhenPageFetcherFails() throws IOException {
        when(pageFetcher.fetch(any())).thenThrow(new IOException());

        List<URL> result = pageCrawler.extractCrawlableLinks(MONZO_URL).get();

        assertThat(result).isEmpty();
    }

    @ParameterizedTest(name = "numberOfLinks extracted=[{arguments}]")
    @ValueSource(ints = { 0, 10, 30 })
    public void returnListReturnedFromLinksExtractor(int numberOfLinks) throws Exception {
        List<URL> links = IntStream
            .range(0, numberOfLinks)
            .mapToObj(i -> TestConstants.makeURL("http://awebsite.com/" + i))
            .collect(Collectors.toList()); //TODO add guava and make list immutable
        when(crawlableLinksExtractor.getLinks(any())).thenReturn(links);

        List<URL> result = pageCrawler.extractCrawlableLinks(MONZO_URL).get();

        assertThat(result).hasSize(numberOfLinks);
    }

}