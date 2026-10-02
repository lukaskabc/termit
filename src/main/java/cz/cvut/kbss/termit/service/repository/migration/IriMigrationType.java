package cz.cvut.kbss.termit.service.repository.migration;

import cz.cvut.kbss.jopa.model.IRI;
import cz.cvut.kbss.termit.model.CustomAttribute_;
import cz.cvut.kbss.termit.model.Term_;
import cz.cvut.kbss.termit.model.Vocabulary_;

import java.net.URI;

public enum IriMigrationType {
    VOCABULARY(Vocabulary_.entityClassIRI),
    TERM(Term_.entityClassIRI),
    CUSTOM_ATTRIBUTE(CustomAttribute_.entityClassIRI);

    private final URI entityType;

    IriMigrationType(IRI entityType) {
        this.entityType = entityType.toURI();
    }

    public URI getEntityType() {
        return entityType;
    }

    public static IriMigrationType fromEntityType(URI entityType) {
        for (IriMigrationType type : IriMigrationType.values()) {
            if (type.entityType.equals(entityType)) return type;
        }
        return null;
    }
}
