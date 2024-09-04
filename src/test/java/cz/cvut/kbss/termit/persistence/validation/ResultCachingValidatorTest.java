/*
 * TermIt
 * Copyright (C) 2023 Czech Technical University in Prague
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package cz.cvut.kbss.termit.persistence.validation;

import cz.cvut.kbss.termit.environment.Generator;
import cz.cvut.kbss.termit.event.VocabularyContentModified;
import cz.cvut.kbss.termit.model.Term;
import cz.cvut.kbss.termit.model.validation.ValidationResult;
import cz.cvut.kbss.termit.persistence.dao.VocabularyDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cz.cvut.kbss.termit.util.throttle.TestFutureRunner.runFuture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.anyCollection;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultCachingValidatorTest {

    @Mock
    private Validator validator;

    private ResultCachingValidator sut;

    private URI vocabulary;

    private ValidationResult validationResult;

    @BeforeEach
    void setUp() {
        this.sut = spy(new ResultCachingValidator());
        when(sut.getValidator()).thenReturn(validator);

        vocabulary = Generator.generateUri();
        Term term = Generator.generateTermWithId(vocabulary);
        validationResult = new ValidationResult()
                .setTermUri(term.getUri());
    }

    @Test
    void invokesInternalValidatorWhenNoResultsAreCached() throws Exception {
        final List<ValidationResult> results = Collections.singletonList(validationResult);
        when(validator.runValidation(anyCollection())).thenReturn(results);
        final Set<URI> vocabularies = Collections.singleton(vocabulary);
        final Collection<ValidationResult> result = runFuture(sut.validate(vocabulary, vocabularies));
        assertEquals(results, result);
        verify(validator).runValidation(vocabularies);
    }

    @Test
    void returnsCachedResultsWhenArgumentsMatch() throws Exception {
        final List<ValidationResult> results = Collections.singletonList(validationResult);
        when(validator.runValidation(anyCollection())).thenReturn(results);
        final Set<URI> vocabularies = Collections.singleton(vocabulary);
        final Collection<ValidationResult> resultOne = runFuture(sut.validate(vocabulary, vocabularies));
        verify(validator).runValidation(vocabularies);
        final Collection<ValidationResult> resultTwo = runFuture(sut.validate(vocabulary, vocabularies));
        assertEquals(resultOne, resultTwo);
        assertSame(results, resultOne);
        verifyNoMoreInteractions(validator);
    }

    @Test
    void evictCacheClearsCachedValidationResults() throws Exception {
        final List<ValidationResult> results = Collections.singletonList(validationResult);
        when(validator.runValidation(anyCollection())).thenReturn(results);
        final Set<URI> vocabularies = Collections.singleton(vocabulary);
        final Collection<ValidationResult> resultOne = runFuture(sut.validate(vocabulary, vocabularies));
        verify(validator).runValidation(vocabularies);
        sut.evictVocabularyCache(new VocabularyContentModified(this, vocabulary));
        final Collection<ValidationResult> resultTwo = runFuture(sut.validate(vocabulary, vocabularies));
        verify(validator, times(2)).runValidation(vocabularies);
        assertEquals(resultOne, resultTwo);
        assertSame(results, resultOne);
    }
}
