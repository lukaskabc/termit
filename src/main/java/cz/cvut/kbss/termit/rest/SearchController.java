/**
 * TermIt Copyright (C) 2019 Czech Technical University in Prague
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package cz.cvut.kbss.termit.rest;

import cz.cvut.kbss.jsonld.JsonLd;
import cz.cvut.kbss.termit.dto.search.FacetedSearchResult;
import cz.cvut.kbss.termit.dto.search.FullTextSearchResult;
import cz.cvut.kbss.termit.dto.search.SearchParam;
import cz.cvut.kbss.termit.service.IdentifierResolver;
import cz.cvut.kbss.termit.service.business.SearchService;
import cz.cvut.kbss.termit.util.Configuration;
import cz.cvut.kbss.termit.util.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Tag(name = "Search", description = "Search API")
@RestController
@RequestMapping("/search")
public class SearchController extends BaseController {

    private final SearchService searchService;

    @Autowired
    public SearchController(IdentifierResolver idResolver, Configuration config, SearchService searchService) {
        super(idResolver, config);
        this.searchService = searchService;
    }

    @Operation(description = "Runs full-text search over asset labels, definitions and descriptions.")
    @ApiResponse(responseCode = "200", description = "Search results.")
    @PreAuthorize("permitAll()")
    @GetMapping(value = "/fts", produces = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public List<FullTextSearchResult> fullTextSearch(@Parameter(description = "Search string.")
                                                     @RequestParam(name = "searchString") String searchString) {
        return searchService.fullTextSearch(searchString);
    }

    @Operation(description = "Runs full-text search over terms, matching their labels, definitions and scope notes.")
    @ApiResponse(responseCode = "200", description = "Search results.")
    @PreAuthorize("permitAll()")
    @GetMapping(value = "/fts/terms", produces = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public List<FullTextSearchResult> fullTextSearchTerms(
            @Parameter(description = "Search string.")
            @RequestParam(name = "searchString") String searchString,
            @Parameter(description = "Identifiers of vocabularies in which to search.")
            @RequestParam(name = "vocabulary", required = false) Set<URI> vocabularies) {
        return searchService.fullTextSearchOfTerms(searchString, Utils.emptyIfNull(vocabularies));
    }

    @Operation(description = "Runs a faceted search using the specified search parameters over all terms.")
    @ApiResponse(responseCode = "200", description = "Search results.")
    @PreAuthorize("permitAll()")
    @PostMapping(value = "/faceted/terms", produces = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE},
                 consumes = {MediaType.APPLICATION_JSON_VALUE})
    public List<FacetedSearchResult> facetedTermSearch(@Parameter(description = "Search parameters.")
                                                       @RequestBody Collection<SearchParam> searchParams) {
        return searchService.facetedTermSearch(searchParams);
    }
}
