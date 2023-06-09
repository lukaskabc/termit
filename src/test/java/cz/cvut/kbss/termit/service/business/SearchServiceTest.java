package cz.cvut.kbss.termit.service.business;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.vocabulary.RDF;
import cz.cvut.kbss.jopa.vocabulary.SKOS;
import cz.cvut.kbss.termit.dto.search.FacetedSearchResult;
import cz.cvut.kbss.termit.dto.search.FullTextSearchResult;
import cz.cvut.kbss.termit.dto.search.MatchType;
import cz.cvut.kbss.termit.dto.search.SearchParam;
import cz.cvut.kbss.termit.environment.Environment;
import cz.cvut.kbss.termit.environment.Generator;
import cz.cvut.kbss.termit.exception.ValidationException;
import cz.cvut.kbss.termit.persistence.dao.SearchDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private SearchDao searchDao;

    @InjectMocks
    private SearchService sut;

    @Test
    void fullTextSearchFiltersResultsFromNonMatchingVocabularies() {
        final String searchString = "test";
        final URI vocabulary = Generator.generateUri();
        final FullTextSearchResult ftsr = new FullTextSearchResult(
                Generator.generateUri(),
                "test",
                vocabulary,
                null,
                SKOS.CONCEPT,
                "test",
                "test",
                1.0);
        when(searchDao.fullTextSearchIncludingSnapshots(searchString)).thenReturn(Collections.singletonList(ftsr));
        final List<FullTextSearchResult> result = sut.fullTextSearchOfTerms(searchString, Collections.singleton(
                Generator.generateUri()));
        assertTrue(result.isEmpty());
        verify(searchDao).fullTextSearchIncludingSnapshots(searchString);
    }

    @Test
    void fullTextSearchReturnsResultsFromMatchingVocabularies() {
        final String searchString = "test";
        final URI vocabulary = Generator.generateUri();
        final FullTextSearchResult ftsr = new FullTextSearchResult(
                Generator.generateUri(),
                "test",
                vocabulary,
                true,
                SKOS.CONCEPT,
                "test",
                "test",
                1.0);
        when(searchDao.fullTextSearchIncludingSnapshots(searchString)).thenReturn(Collections.singletonList(ftsr));
        final List<FullTextSearchResult> result = sut.fullTextSearchOfTerms(searchString,
                                                                            Collections.singleton(vocabulary));
        assertEquals(Collections.singletonList(ftsr), result);
        verify(searchDao).fullTextSearchIncludingSnapshots(searchString);
    }

    @Test
    void facetedTermSearchValidatesEachSearchParamBeforeInvokingSearch() {
        final SearchParam spOne = new SearchParam(URI.create(RDF.TYPE), Generator.generateUriString(), MatchType.IRI);
        final SearchParam spTwo = new SearchParam(URI.create(SKOS.NOTATION), "will be removed", MatchType.SUBSTRING);
        spTwo.setValue(null);
        assertThrows(ValidationException.class, () -> sut.facetedTermSearch(List.of(spOne, spTwo)));
        verify(searchDao, never()).facetedTermSearch(anyCollection());
    }

    @Test
    void facetedTermSearchExecutesSearchOnDaoAndReturnsResults() {
        final SearchParam spOne = new SearchParam(URI.create(RDF.TYPE), Generator.generateUriString(), MatchType.IRI);
        final FacetedSearchResult item = new FacetedSearchResult();
        item.setUri(Generator.generateUri());
        item.setLabel(MultilingualString.create("Test term", Environment.LANGUAGE));
        item.setVocabulary(Generator.generateUri());
        item.setTypes(new HashSet<>(spOne.getValue()));
        when(searchDao.facetedTermSearch(anyCollection())).thenReturn(List.of(item));

        final List<FacetedSearchResult> result = sut.facetedTermSearch(Set.of(spOne));
        assertEquals(List.of(item), result);
        verify(searchDao).facetedTermSearch(Set.of(spOne));
    }
}
