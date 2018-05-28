package com.example.crawler.pagecrawler.links.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import static com.example.crawler.TestConstants.MONZO_URL;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LinksFilterTest {

    private LinksFilter linksFilter;
    private IsExternalURL isExternalURL;
    private IsImageURL isImageURL;

    @BeforeEach
    public void setup() {
        isExternalURL = Mockito.mock(IsExternalURL.class);
        isImageURL = Mockito.mock(IsImageURL.class);
        linksFilter = new LinksFilter(isExternalURL, isImageURL);
    }

    @Test
    public void returnFalseWhenUrlIsNull() {
        assertThat(linksFilter.filterUrlsToCrawl(null)).isFalse();
    }


    @Test
    public void returnTrueWhenAllFiltersReturnFalse() {
        assertThat(linksFilter.filterUrlsToCrawl(MONZO_URL)).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
        "false, true,",
        "true, false",
        "true, true"
    })
    public void returnFalseWhenOneOrMoreCollaboratorReturnsTrue(
        boolean isExternalURLResult,
        boolean isImageUrlResult
    ) {
        when(isExternalURL.verify(any())).thenReturn(isExternalURLResult);
        when(isImageURL.verify(any())).thenReturn(isImageUrlResult);
        assertThat(linksFilter.filterUrlsToCrawl(MONZO_URL)).isFalse();
    }

}