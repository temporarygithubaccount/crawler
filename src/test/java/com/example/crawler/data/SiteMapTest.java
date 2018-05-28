package com.example.crawler.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.MalformedURLException;
import java.net.URL;

import static com.example.crawler.TestConstants.MONZO_URL;
import static com.example.crawler.TestConstants.MONZO_URL_STRING;
import static com.example.crawler.TestConstants.REDDIT_URL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SiteMapTest {

    private SiteMap sitemap;

    @ParameterizedTest(name = "pages=[{arguments}]")
    @ValueSource(ints = { 0, 10, 30 })
    public void testSizeMethodReturningNumberOfElementsAdded(int pages) throws MalformedURLException {
        sitemap = new SiteMap(MONZO_URL);

        for (int i = 0; i < pages; i++) {
            sitemap.addPage(new UrlAndParent(new URL(MONZO_URL_STRING + i), null));
        }

        assertThat(sitemap.size()).isEqualTo(pages);
    }

    @Test
    public void testContainsMethodReturningFalseForSiteNotAdded() throws MalformedURLException {
        sitemap = new SiteMap(MONZO_URL);

        assertThat(sitemap.contains(REDDIT_URL)).isFalse();
    }

    @Test
    public void testContainsMethodReturningTrueForSiteAdded() throws MalformedURLException {
        sitemap = new SiteMap(MONZO_URL);

        sitemap.addPage(new UrlAndParent(REDDIT_URL, null));

        assertThat(sitemap.contains(REDDIT_URL)).isTrue();
    }

    @Test
    public void testContainsMethodReturningTrueForSiteAddedWithNotNullParent() throws MalformedURLException {
        sitemap = new SiteMap(MONZO_URL);

        sitemap.addPage(new UrlAndParent(MONZO_URL, null));
        sitemap.addPage(new UrlAndParent(REDDIT_URL, MONZO_URL));

        assertThat(sitemap.contains(MONZO_URL)).isTrue();
        assertThat(sitemap.contains(REDDIT_URL)).isTrue();
    }

    @Test
    public void testWhenAddingSiteWithParentNotInSiteMapThenThrow() throws MalformedURLException {
        sitemap = new SiteMap(MONZO_URL);

        assertThrows(RuntimeException.class,
            () -> sitemap.addPage(new UrlAndParent(REDDIT_URL, MONZO_URL)));
    }

    @Test
    public void testTheConstructorURLIsNotIndexed() throws MalformedURLException {
        sitemap = new SiteMap(MONZO_URL);

        assertThat(sitemap.contains(MONZO_URL)).isFalse();
    }

    @Test
    public void testToStringForEmptySiteMap() {
        sitemap = new SiteMap(MONZO_URL);

        assertThat(sitemap.toString()).isEqualTo("");
    }

    @Test
    public void testToStringForSinglePageSiteMap() {
        sitemap = new SiteMap(MONZO_URL);
        sitemap.addPage(new UrlAndParent(MONZO_URL, null));

        assertThat(sitemap.toString()).isEqualTo("http://monzo.com/");
    }

    @Test
    public void testToStringForSiteMapWithThreeNested() throws MalformedURLException {
        sitemap = new SiteMap(MONZO_URL);
        sitemap.addPage(new UrlAndParent(MONZO_URL, null));
        sitemap.addPage(new UrlAndParent(REDDIT_URL, MONZO_URL));
        sitemap.addPage(new UrlAndParent(new URL("http://www.amazon.com"), REDDIT_URL));

        assertThat(sitemap.toString()).isEqualTo(
            "http://monzo.com/\n" +
            "├── http://www.reddit.com\n" +
            "    ├── http://www.amazon.com");
    }

    @Test
    public void testToStringForSiteMapWithThreeNonNested() throws MalformedURLException {
        sitemap = new SiteMap(MONZO_URL);
        sitemap.addPage(new UrlAndParent(MONZO_URL, null));
        sitemap.addPage(new UrlAndParent(REDDIT_URL, MONZO_URL));
        sitemap.addPage(new UrlAndParent(new URL("http://www.amazon.com"), MONZO_URL));

        assertThat(sitemap.toString()).isEqualTo(
            "http://monzo.com/\n" +
            "├── http://www.reddit.com\n" +
            "├── http://www.amazon.com");
    }
}