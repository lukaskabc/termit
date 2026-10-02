package cz.cvut.kbss.termit.service.repository.migration;

import cz.cvut.kbss.termit.dto.IriMigrationPair;
import cz.cvut.kbss.termit.dto.IriMigrationParams;
import cz.cvut.kbss.termit.exception.InvalidParameterException;
import cz.cvut.kbss.termit.model.Asset;
import cz.cvut.kbss.termit.model.Term;
import cz.cvut.kbss.termit.model.Vocabulary;
import cz.cvut.kbss.termit.model.assignment.TermOccurrence;
import cz.cvut.kbss.termit.persistence.dao.IriMigrationDao;
import cz.cvut.kbss.termit.persistence.dao.changetracking.ChangeTrackingContextResolver;
import cz.cvut.kbss.termit.persistence.namespace.VocabularyNamespaceResolver;
import cz.cvut.kbss.termit.util.Utils;
import jakarta.annotation.Nullable;

import java.net.URI;
import java.util.Objects;

public class IriMigrationProcedure implements Runnable {
    private final IriMigrationDao iriMigrationDao;
    private final ChangeTrackingContextResolver changeTrackingContextResolver;
    private final VocabularyNamespaceResolver vocabularyNamespaceResolver;

    @Nullable
    private final Asset<?> changedAsset;
    private final IriMigrationType migrationType;
    private final IriMigrationPair iris;
    private final IriMigrationParams params;

    IriMigrationProcedure(IriMigrationDao iriMigrationDao,
                          ChangeTrackingContextResolver changeTrackingContextResolver,
                          VocabularyNamespaceResolver vocabularyNamespaceResolver, @Nullable Asset<?> changedAsset,
                          IriMigrationType migrationType, IriMigrationPair iris, IriMigrationParams params) {
        this.iriMigrationDao = Objects.requireNonNull(iriMigrationDao);
        this.changeTrackingContextResolver = Objects.requireNonNull(changeTrackingContextResolver);
        this.vocabularyNamespaceResolver = vocabularyNamespaceResolver;
        this.changedAsset = changedAsset;
        this.migrationType = Objects.requireNonNull(migrationType);
        this.iris = Objects.requireNonNull(iris);
        this.params = Objects.requireNonNull(params);
    }

    /**
     * Perform the migration
     */
    @Override
    public void run() {
        validateMigration();
        iriMigrationDao.migrateIdentifier(iris); // replace every identifier occurrence
        // TODO: well but this is going to change even custom attributes in the change records...
        migrateChangeRecordsGraph();
        migrateOccurrenceGraph();

        // migrate all terms in vocabulary to namespace (ONLY IF CHANGED)
    }

    private void ensureNotExists(URI resource) {
        if (iriMigrationDao.getEntityTypes(resource).findAny().isPresent()) {
            throw new InvalidParameterException("Resource " + Utils.uriToString(resource) + " already exists!");
        }
    }

    private void validateMigration() {
        ensureNotExists(iris.newIri());
        switch (migrationType) {
            case TERM -> validateTermMigration();
            case VOCABULARY -> validateVocabularyMigration();
            case CUSTOM_ATTRIBUTE -> {/* no validation */}
        }
    }

    private void validateVocabularyMigration() {
        if (!(changedAsset instanceof Vocabulary) || !changedAsset.getUri().equals(iris.originalIri())) {
            throw new InvalidParameterException("Changed asset is not expected Vocabulary!");
        }
    }

    private void validateTermMigration() {
        if (!(changedAsset instanceof Term term) || !changedAsset.getUri().equals(iris.originalIri())) {
            throw new InvalidParameterException("Changed asset is not expected Term!");
        }

        // ensure the new term IRI is inside vocabulary namespace
        final String vocabularyNamespace = vocabularyNamespaceResolver.resolveNamespace(term.getVocabulary());
        if (!term.getUri().toString().startsWith(vocabularyNamespace)) {
            throw new InvalidParameterException("New Term IRI " + Utils.uriToString(term.getUri()) +
                    " does not start with Vocabulary namespace <" + vocabularyNamespace + ">");
        }
    }

    private void migrateOccurrenceGraph() {
        URI originalGraph = TermOccurrence.resolveContext(iris.originalIri());
        URI newGraph = TermOccurrence.resolveContext(iris.newIri());
        iriMigrationDao.moveGraph(originalGraph, newGraph);
    }

    private void migrateChangeRecordsGraph() {
        if (migrationType != IriMigrationType.VOCABULARY) {
            return;
        }

        assert changedAsset != null;
        URI originalGraph = changeTrackingContextResolver.resolveChangeTrackingContext(changedAsset);
        changedAsset.setUri(iris.newIri()); // temporarily so that correct tracking context is resolved
        URI newGraph = changeTrackingContextResolver.resolveChangeTrackingContext(changedAsset);
        changedAsset.setUri(iris.originalIri());
        iriMigrationDao.moveGraph(originalGraph, newGraph);
    }
}
