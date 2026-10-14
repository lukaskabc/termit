package cz.cvut.kbss.termit.model;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.model.annotations.Context;
import cz.cvut.kbss.jopa.model.annotations.OWLAnnotationProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.ParticipationConstraints;
import cz.cvut.kbss.jopa.vocabulary.RDF;
import cz.cvut.kbss.jopa.vocabulary.RDFS;
import cz.cvut.kbss.termit.util.Vocabulary;
import cz.cvut.kbss.termit.validation.PrimaryNotBlank;

import java.net.URI;
import java.util.Set;

@Context(CustomAttribute.CONTEXT)
@OWLClass(iri = Vocabulary.s_c_custom_attribute)
@PrimaryNotBlank({"label"})
public class CustomAttribute extends RdfsResource {

    public static final String CONTEXT = Vocabulary.s_i_termit + "/custom-attributes";

    @ParticipationConstraints(nonEmpty = true)
    @OWLAnnotationProperty(iri = RDFS.DOMAIN)
    private URI domain;

    @ParticipationConstraints(nonEmpty = true)
    @OWLAnnotationProperty(iri = RDFS.RANGE)
    private URI range;

    @OWLAnnotationProperty(iri = Vocabulary.s_p_relationship)
    private Set<URI> annotatedRelationships;

    public CustomAttribute() {}

    public CustomAttribute(URI uri, MultilingualString label, MultilingualString comment) {
        super(uri, label, comment, RDF.PROPERTY);
    }

    public URI getDomain() {
        return domain;
    }

    public void setDomain(URI domain) {
        this.domain = domain;
    }

    public URI getRange() {
        return range;
    }

    public void setRange(URI range) {
        this.range = range;
    }

    public Set<URI> getAnnotatedRelationships() {
        return annotatedRelationships;
    }

    public void setAnnotatedRelationships(Set<URI> annotatedRelationships) {
        this.annotatedRelationships = annotatedRelationships;
    }
}
