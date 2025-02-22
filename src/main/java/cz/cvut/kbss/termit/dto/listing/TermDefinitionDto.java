package cz.cvut.kbss.termit.dto.listing;

import cz.cvut.kbss.jopa.model.annotations.ConstructorResult;
import cz.cvut.kbss.jopa.model.annotations.Inferred;
import cz.cvut.kbss.jopa.model.annotations.OWLAnnotationProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.cvut.kbss.jopa.model.annotations.ParticipationConstraints;
import cz.cvut.kbss.jopa.model.annotations.SparqlResultSetMapping;
import cz.cvut.kbss.jopa.model.annotations.VariableResult;
import cz.cvut.kbss.jopa.model.annotations.util.NonEntity;
import cz.cvut.kbss.jopa.vocabulary.SKOS;
import cz.cvut.kbss.termit.model.Asset;

import java.net.URI;

/**
 * DTO for listing of terms with definitions.
 */
@SparqlResultSetMapping(name = "TermDefinitionDto",
                        classes = @ConstructorResult(targetClass = TermDefinitionDto.class,
                                                     variables = {
                                                             @VariableResult(name = "term", type = URI.class),
                                                             @VariableResult(name = "label", type = String.class),
                                                             @VariableResult(name = "definition", type = String.class),
                                                             @VariableResult(name = "vocabulary", type = URI.class)
                                                     }))
@NonEntity
@OWLClass(iri = SKOS.CONCEPT)
public class TermDefinitionDto extends Asset<String> {
    @ParticipationConstraints(nonEmpty = true)
    @OWLAnnotationProperty(iri = SKOS.PREF_LABEL)
    private String label;

    @OWLAnnotationProperty(iri = SKOS.DEFINITION)
    private String definition;

    @Inferred
    @OWLObjectProperty(iri = cz.cvut.kbss.termit.util.Vocabulary.s_p_je_pojmem_ze_slovniku)
    private URI vocabulary;

    public TermDefinitionDto(URI uri, String label, String definition, URI vocabulary) {
        setUri(uri);
        this.label = label;
        this.definition = definition;
        this.vocabulary = vocabulary;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    public URI getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(URI vocabulary) {
        this.vocabulary = vocabulary;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
