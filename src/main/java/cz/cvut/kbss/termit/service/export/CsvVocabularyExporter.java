/**
 * TermIt
 * Copyright (C) 2019 Czech Technical University in Prague
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
package cz.cvut.kbss.termit.service.export;

import cz.cvut.kbss.termit.dto.export.TabularTermExportUtils;
import cz.cvut.kbss.termit.service.mapper.TermDtoMapper;
import cz.cvut.kbss.termit.exception.UnsupportedOperationException;
import cz.cvut.kbss.termit.model.Term;
import cz.cvut.kbss.termit.model.Vocabulary;
import cz.cvut.kbss.termit.service.export.util.TypeAwareByteArrayResource;
import cz.cvut.kbss.termit.service.repository.TermRepositoryService;
import cz.cvut.kbss.termit.util.TypeAwareResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service("csv")
public class CsvVocabularyExporter implements VocabularyExporter {

    private final TermRepositoryService termService;

    private final TermDtoMapper dtoMapper;

    @Autowired
    public CsvVocabularyExporter(TermRepositoryService termService, TermDtoMapper dtoMapper) {
        this.termService = termService;
        this.dtoMapper = dtoMapper;
    }

    @Override
    public TypeAwareResource exportGlossary(Vocabulary vocabulary, ExportConfig config) {
        Objects.requireNonNull(vocabulary);
        Objects.requireNonNull(config);
        if (ExportType.SKOS == config.getType()) {
            return exportGlossary(vocabulary);
        }
        throw new UnsupportedOperationException("Unsupported export type " + config.getType());
    }

    private TypeAwareByteArrayResource exportGlossary(Vocabulary vocabulary) {
        Objects.requireNonNull(vocabulary);
        final StringBuilder export = new StringBuilder(String.join(",", TabularTermExportUtils.EXPORT_COLUMNS));
        final List<Term> terms = termService.findAllFull(vocabulary);
        terms.stream().map(dtoMapper::termToCsvExportDto).forEach(t -> export.append('\n').append(t.export()));
        return new TypeAwareByteArrayResource(export.toString().getBytes(), ExportFormat.CSV.getMediaType(),
                                              ExportFormat.CSV.getFileExtension());
    }

    @Override
    public boolean supports(String mediaType) {
        return Objects.equals(ExportFormat.CSV.getMediaType(), mediaType);
    }
}
