package cz.cvut.kbss.termit.model;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.model.annotations.Context;
import cz.cvut.kbss.jopa.model.annotations.EnumType;
import cz.cvut.kbss.jopa.model.annotations.Enumerated;
import cz.cvut.kbss.jopa.model.annotations.Individual;
import cz.cvut.kbss.jopa.model.annotations.OWLAnnotationProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.ParticipationConstraints;
import cz.cvut.kbss.jopa.vocabulary.RDF;
import cz.cvut.kbss.jopa.vocabulary.RDFS;
import cz.cvut.kbss.jopa.vocabulary.SKOS;
import cz.cvut.kbss.jopa.vocabulary.XSD;
import cz.cvut.kbss.termit.util.Vocabulary;

import java.net.URI;
import java.util.Set;

@Context(CustomAttribute.CONTEXT)
@OWLClass(iri = Vocabulary.s_c_custom_attribute)
public class CustomAttribute extends RdfsResource {

    public static final String CONTEXT = Vocabulary.s_i_termit + "/custom-attributes";

    @ParticipationConstraints(nonEmpty = true)
    @Enumerated(EnumType.OBJECT_ONE_OF)
    @OWLAnnotationProperty(iri = RDFS.DOMAIN)
    private Domain domain;

    @ParticipationConstraints(nonEmpty = true)
    @Enumerated(EnumType.OBJECT_ONE_OF)
    @OWLAnnotationProperty(iri = RDFS.RANGE)
    private Range range;

    @Enumerated(EnumType.OBJECT_ONE_OF)
    @OWLAnnotationProperty(iri = Vocabulary.s_p_relationship)
    private Set<Relationship> annotatedRelationships;

    public CustomAttribute() {}

    public CustomAttribute(URI uri, MultilingualString label, MultilingualString comment) {
        super(uri, label, comment, RDF.PROPERTY);
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public Set<Relationship> getAnnotatedRelationships() {
        return annotatedRelationships;
    }

    public void setAnnotatedRelationships(Set<Relationship> annotatedRelationships) {
        this.annotatedRelationships = annotatedRelationships;
    }

    public enum Domain {
        @Individual(iri = SKOS.CONCEPT)
        TERM,
        @Individual(iri = SKOS.CONCEPT_SCHEME)
        VOCABULARY,
        @Individual(iri = RDF.STATEMENT)
        RELATION
    }

    public enum Range {
        @Individual(iri = XSD.BOOLEAN)
        BOOLEAN,
        @Individual(iri = XSD.INT)
        INT,
        @Individual(iri = SKOS.CONCEPT)
        TERM,
        @Individual(iri = XSD.STRING)
        STRING,
        @Individual(iri = RDF.LANG_STRING)
        LANG_STRING,
        @Individual(iri = RDFS.RESOURCE)
        RESOURCE
    }

    public enum Relationship {
        @Individual(iri = SKOS.RELATED)
        RELATED,
        @Individual(iri = SKOS.RELATED_MATCH)
        RELATED_MATCH,

        @Individual(iri = SKOS.BROADER)
        BROADER,
        @Individual(iri = SKOS.BROAD_MATCH)
        BROAD_MATCH,

        @Individual(iri = SKOS.NARROWER)
        NARROWER,
        @Individual(iri = SKOS.NARROW_MATCH)
        NARROW_MATCH,

        @Individual(iri = SKOS.EXACT_MATCH)
        EXACT_MATCH
    }
}
