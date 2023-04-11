package cz.cvut.kbss.termit.service.business;

import cz.cvut.kbss.termit.dto.acl.AccessControlListDto;
import cz.cvut.kbss.termit.model.acl.AccessControlList;
import cz.cvut.kbss.termit.model.acl.AccessControlRecord;
import cz.cvut.kbss.termit.model.util.HasIdentifier;

import java.net.URI;
import java.util.Optional;

/**
 * Service for managing {@link AccessControlList}s (ACLs).
 * <p>
 * Note that only management of ACLs is supported by this service. Access control itself is handled by TODO.
 */
public interface AccessControlListService {

    /**
     * Finds an {@link AccessControlList} with the specified identifier.
     *
     * @param id ACL identifier
     * @return Matching ACL
     * @throws cz.cvut.kbss.termit.exception.NotFoundException If no matching ACL exists
     */
    AccessControlList findRequired(URI id);

    /**
     * Gets a reference to an {@link AccessControlList} with the specified identifier.
     *
     * @param id ACL identifier
     * @return Reference to a matching ACL
     * @throws cz.cvut.kbss.termit.exception.NotFoundException If no matching ACL exists
     */
    AccessControlList getRequiredReference(URI id);

    /**
     * Finds an {@link AccessControlList} guarding access to the specified subject.
     *
     * @param subject Subject of the ACL
     * @return Matching ACL instance wrapped in an {@link Optional}, empty {@link Optional} if no such ACL exists
     * @see #findForAsDto(HasIdentifier)
     */
    Optional<AccessControlList> findFor(HasIdentifier subject);

    /**
     * Finds an {@link AccessControlList} guarding access to the specified subject and returns it as DTO.
     *
     * @param subject Subject of the ACL
     * @return Matching ACL instance DTO wrapped in an {@link Optional}, empty {@link Optional} if no such ACL exists
     * @see #findFor(HasIdentifier)
     */
    Optional<AccessControlListDto> findForAsDto(HasIdentifier subject);

    /**
     * Creates and persists an {@link AccessControlList} with default access records and returns it.
     * <p>
     * The created ACL has the following records:
     * <ul>
     *     <li>{@link cz.cvut.kbss.termit.model.acl.AccessLevel#SECURITY} for the current user (if available)</li>
     *     <li>{@link cz.cvut.kbss.termit.model.acl.AccessLevel#SECURITY} for all authors of the specified subject (if {@link cz.cvut.kbss.termit.model.changetracking.PersistChangeRecord}s exist for the specified asset)</li>
     *     <li>Access level for {@link cz.cvut.kbss.termit.security.SecurityConstants#ROLE_FULL_USER} and {@link cz.cvut.kbss.termit.security.SecurityConstants#ROLE_RESTRICTED_USER} based on configuration</li>
     * </ul>
     *
     * @return New ACL with default records
     */
    AccessControlList createFor(HasIdentifier subject);

    /**
     * Adds the specified record to the specified target {@link AccessControlList}.
     *
     * @param acl    Target ACL
     * @param record Record to add to the ACL
     */
    void addRecord(AccessControlList acl, AccessControlRecord<?> record);

    /**
     * Removes the specified record from the specified target {@link AccessControlList}.
     *
     * @param acl    Target ACL
     * @param record Record to remove from the ACL
     */
    void removeRecord(AccessControlList acl, AccessControlRecord<?> record);

    /**
     * Updates access level in specified record in the specified target {@link AccessControlList}.
     * <p>
     * Only access level can be changed in an existing {@link AccessControlRecord}. Changing holder requires creating a
     * record.
     *
     * @param acl    Target ACL
     * @param record Updated access control record
     */
    void updateRecordAccessLevel(AccessControlList acl, AccessControlRecord<?> record);
}
