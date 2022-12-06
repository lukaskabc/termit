package cz.cvut.kbss.termit.service.export;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.termit.dto.TermInfo;
import cz.cvut.kbss.termit.environment.Environment;
import cz.cvut.kbss.termit.environment.Generator;
import cz.cvut.kbss.termit.model.Term;
import cz.cvut.kbss.termit.util.Vocabulary;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExcelTermExporterTest {

    private final ExcelTermExporter sut = new ExcelTermExporter();

    @Test
    void exportExportsTermToExcelRow() {
        final Term term = Generator.generateTermWithId();
        term.setTypes(Collections.singleton(Vocabulary.s_c_object));
        term.setAltLabels(new HashSet<>(Arrays.asList(MultilingualString.create("Building", Environment.LANGUAGE),
                                                      MultilingualString.create("Construction",
                                                                                Environment.LANGUAGE))));
        term.setHiddenLabels(
                new HashSet<>(Arrays.asList(MultilingualString.create("Building", Environment.LANGUAGE),
                                            MultilingualString.create("Construction", Environment.LANGUAGE))));
        term.setSources(new LinkedHashSet<>(
                Arrays.asList(Generator.generateUri().toString(), "PSP/c-1/p-2/b-c", "PSP/c-1/p-2/b-f")));
        term.setParentTerms(new HashSet<>(Generator.generateTermsWithIds(5)));
        term.setSubTerms(IntStream.range(0, 5).mapToObj(i -> generateTermInfo()).collect(Collectors.toSet()));
        term.setNotations(Collections.singleton("A"));
        term.setExamples(Collections.singleton(MultilingualString.create("hospital", Environment.LANGUAGE)));
        final XSSFRow row = generateExcel();
        sut.export(term, row);
        assertEquals(term.getUri().toString(), row.getCell(0).getStringCellValue());
        term.getLabel().getValue().values()
            .forEach(v -> assertThat(row.getCell(1).getStringCellValue(), containsString(v)));
        assertTrue(row.getCell(2).getStringCellValue().matches(".+;.+"));
        term.getAltLabels().forEach(s -> assertTrue(row.getCell(2).getStringCellValue().contains(s.get())));
        assertTrue(row.getCell(3).getStringCellValue().matches(".+;.+"));
        term.getHiddenLabels().forEach(s -> assertTrue(row.getCell(3).getStringCellValue().contains(s.get())));
        term.getDefinition().getValue().values()
            .forEach(v -> assertThat(row.getCell(4).getStringCellValue(), containsString(v)));
        term.getDescription().getValue().values()
            .forEach(v -> assertThat(row.getCell(5).getStringCellValue(), containsString(v)));
        assertEquals(term.getTypes().iterator().next(), row.getCell(6).getStringCellValue());
        assertTrue(row.getCell(7).getStringCellValue().matches(".+;.+"));
        term.getSources().forEach(s -> assertThat(row.getCell(7).getStringCellValue(), containsString(s)));
        assertTrue(row.getCell(8).getStringCellValue().matches(".+;.+"));
        term.getParentTerms()
            .forEach(st -> assertThat(row.getCell(8).getStringCellValue(), containsString(st.getUri().toString())));
        assertTrue(row.getCell(9).getStringCellValue().matches(".+;.+"));
        term.getSubTerms()
            .forEach(st -> assertThat(row.getCell(9).getStringCellValue(), containsString(st.getUri().toString())));
        term.getNotations().forEach(n -> assertThat(row.getCell(14).getStringCellValue(), containsString(n)));
        term.getExamples().forEach(ms -> assertThat(row.getCell(15).getStringCellValue(), containsString(ms.get())));
    }

    private static TermInfo generateTermInfo() {
        final TermInfo ti = new TermInfo(Generator.generateUri());
        ti.setLabel(MultilingualString.create("Term " + Generator.randomInt(), Environment.LANGUAGE));
        ti.setVocabulary(Generator.generateUri());
        return ti;
    }

    private static XSSFRow generateExcel() {
        final XSSFWorkbook wb = new XSSFWorkbook();
        final XSSFSheet sheet = wb.createSheet("test");
        return sheet.createRow(0);
    }

    @Test
    void exportHandlesEmptyOptionalAttributeValues() {
        final Term term = Generator.generateTermWithId();
        term.setDescription(null);
        term.setDefinition(null);
        final XSSFRow row = generateExcel();
        sut.export(term, row);
        assertEquals(term.getUri().toString(), row.getCell(0).getStringCellValue());
        term.getLabel().getValue().values()
            .forEach(v -> assertThat(row.getCell(1).getStringCellValue(), containsString(v)));
    }

    @Test
    void exportHandlesSkippingEmptyColumns() {
        final Term term = Generator.generateTermWithId();
        term.setDescription(null);
        term.setSources(new LinkedHashSet<>(
                Arrays.asList(Generator.generateUri().toString(), "PSP/c-1/p-2/b-c", "PSP/c-1/p-2/b-f")));
        final XSSFRow row = generateExcel();
        sut.export(term, row);
        assertEquals(term.getUri().toString(), row.getCell(0).getStringCellValue());
        term.getLabel().getValue().values()
            .forEach(v -> assertThat(row.getCell(1).getStringCellValue(), containsString(v)));
        assertTrue(row.getCell(7).getStringCellValue().matches(".+;.+"));
        term.getSources().forEach(s -> assertTrue(row.getCell(7).getStringCellValue().contains(s)));
    }

    @Test
    void exportHandlesNullAltLabelsAttribute() {
        final Term term = Generator.generateTermWithId();
        final MultilingualString hiddenOne = MultilingualString.create("budova", "cs");
        final MultilingualString hiddenTwo = MultilingualString.create("budovy", "cs");
        term.setAltLabels(null);
        term.setHiddenLabels(new HashSet<>(Arrays.asList(hiddenOne, hiddenTwo)));

        final XSSFRow row = generateExcel();
        sut.export(term, row);
        assertEquals(term.getUri().toString(), row.getCell(0).getStringCellValue());
        term.getHiddenLabels().forEach(ms -> ms.getValue().values()
                                               .forEach(v -> assertThat(row.getCell(3).getStringCellValue(),
                                                                        containsString(v))));
    }

    @Test
    void exportIncludesRelatedAndInverseRelatedTerms() {
        final Term term = Generator.generateMultiLingualTerm(Environment.LANGUAGE, "cs");
        term.setVocabulary(Generator.generateUri());
        term.setRelated(IntStream.range(0, 5)
                                 .mapToObj(i -> new TermInfo(Generator.generateTermWithId(term.getVocabulary())))
                                 .collect(Collectors.toSet()));
        term.setInverseRelated(IntStream.range(0, 5)
                                        .mapToObj(i -> new TermInfo(Generator.generateTermWithId(term.getVocabulary())))
                                        .collect(Collectors.toSet()));

        final XSSFRow row = generateExcel();
        sut.export(term, row);
        final String related = row.getCell(10).getStringCellValue();
        assertTrue(related.matches(".+;.+"));
        term.getRelated().forEach(t -> assertTrue(related.contains(t.getUri().toString())));
        term.getInverseRelated().forEach(t -> assertTrue(related.contains(t.getUri().toString())));
    }

    @Test
    void exportIncludesRelatedMatchAndInverseRelatedMatchTerms() {
        final Term term = Generator.generateMultiLingualTerm(Environment.LANGUAGE, "cs");
        term.setVocabulary(Generator.generateUri());
        term.setRelatedMatch(IntStream.range(0, 5).mapToObj(i -> new TermInfo(Generator.generateTermWithId()))
                                      .collect(Collectors.toSet()));
        term.setInverseRelatedMatch(IntStream.range(0, 5).mapToObj(i -> new TermInfo(Generator.generateTermWithId()))
                                             .collect(Collectors.toSet()));

        final XSSFRow row = generateExcel();
        sut.export(term, row);
        final String relatedMatch = row.getCell(11).getStringCellValue();
        assertTrue(relatedMatch.matches(".+;.+"));
        term.getRelatedMatch().forEach(t -> assertTrue(relatedMatch.contains(t.getUri().toString())));
        term.getInverseRelatedMatch().forEach(t -> assertTrue(relatedMatch.contains(t.getUri().toString())));
    }

    @Test
    void exportIncludesExactMatchAndInverseExactMatchTerms() {
        final Term term = Generator.generateMultiLingualTerm(Environment.LANGUAGE, "cs");
        term.setVocabulary(Generator.generateUri());
        term.setExactMatchTerms(IntStream.range(0, 5).mapToObj(i -> new TermInfo(Generator.generateTermWithId()))
                                         .collect(Collectors.toSet()));
        term.setInverseExactMatchTerms(IntStream.range(0, 5).mapToObj(i -> new TermInfo(Generator.generateTermWithId()))
                                                .collect(Collectors.toSet()));

        final XSSFRow row = generateExcel();
        sut.export(term, row);
        final String exactMatch = row.getCell(12).getStringCellValue();
        assertTrue(exactMatch.matches(".+;.+"));
        term.getExactMatchTerms().forEach(t -> assertTrue(exactMatch.contains(t.getUri().toString())));
        term.getInverseExactMatchTerms().forEach(t -> assertTrue(exactMatch.contains(t.getUri().toString())));
    }

    @Test
    void exportEnsuresNoDuplicatesInRelatedRelatedMatchAndExactMatchTerms() {
        final Term term = Generator.generateMultiLingualTerm(Environment.LANGUAGE, "cs");
        term.setVocabulary(Generator.generateUri());
        final Set<TermInfo> asserted = IntStream.range(0, 5).mapToObj(i -> new TermInfo(Generator.generateTermWithId()))
                                                .collect(Collectors.toSet());
        term.setRelated(new HashSet<>(asserted));
        term.setRelatedMatch(new HashSet<>(asserted));
        term.setExactMatchTerms(new HashSet<>(asserted));
        final Set<TermInfo> inverse = IntStream.range(0, 5).mapToObj(i -> new TermInfo(Generator.generateTermWithId()))
                                               .collect(Collectors.toSet());
        term.setInverseRelated(new HashSet<>(inverse));
        term.setInverseRelatedMatch(new HashSet<>(inverse));
        term.setInverseExactMatchTerms(new HashSet<>(inverse));
        term.consolidateInferred();

        final XSSFRow row = generateExcel();
        sut.export(term, row);
        final String resultRelated = row.getCell(10).getStringCellValue();
        final String[] relatedIris = resultRelated.split(";");
        assertEquals(asserted.size() + inverse.size(), relatedIris.length);
        final String resultRelatedMatch = row.getCell(11).getStringCellValue();
        final String[] relatedMatchIris = resultRelatedMatch.split(";");
        assertEquals(asserted.size() + inverse.size(), relatedMatchIris.length);
        final String resultExactMatch = row.getCell(12).getStringCellValue();
        final String[] exactMatchIris = resultExactMatch.split(";");
        assertEquals(asserted.size() + inverse.size(), exactMatchIris.length);
    }

    @Test
    void exportExportsMultilingualAttributesAsValuesWithLanguageInParentheses() {
        final Term term = Generator.generateMultiLingualTerm(Environment.LANGUAGE, "cs");
        final XSSFRow row = generateExcel();
        sut.export(term, row);
        final String label = row.getCell(1).getStringCellValue();
        assertTrue(label.matches(".+;.+"));
        term.getLabel().getValue()
            .forEach((lang, value) -> assertThat(label, containsString(value + "(" + lang + ")")));
        final String definition = row.getCell(4).getStringCellValue();
        assertTrue(definition.matches(".+;.+"));
        term.getDefinition().getValue()
            .forEach((lang, value) -> assertThat(definition, containsString(value + "(" + lang + ")")));
        final String description = row.getCell(5).getStringCellValue();
        assertTrue(description.matches(".+;.+"));
        term.getDescription().getValue()
            .forEach((lang, value) -> assertThat(description, containsString(value + "(" + lang + ")")));
    }

    @Test
    void exportRemovesMarkdownMarkupFromDefinitionAndScopeNote() {
        final Term term = Generator.generateTermWithId();
        final String markdown = "# This is a headline\n" +
                "**This is bold text** and _this is italics_";
        final String text = "This is a headline\n\nThis is bold text and this is italics";
        term.getDefinition().set(Environment.LANGUAGE, markdown);
        term.getDescription().set(Environment.LANGUAGE, markdown);

        final XSSFRow row = generateExcel();
        sut.export(term, row);
        assertThat(row.getCell(4).getStringCellValue(), containsString(text));
        assertThat(row.getCell(5).getStringCellValue(), containsString(text));
    }
}
