package cz.cvut.kbss.termit.service.repository;

import cz.cvut.kbss.termit.model.UserGroup;
import cz.cvut.kbss.termit.persistence.dao.GenericDao;
import cz.cvut.kbss.termit.persistence.dao.UserGroupDao;
import cz.cvut.kbss.termit.security.SecurityConstants;
import cz.cvut.kbss.termit.service.business.UserGroupService;
import cz.cvut.kbss.termit.util.Utils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
public class UserGroupRepositoryService extends BaseRepositoryService<UserGroup, UserGroup>
        implements UserGroupService {

    private final UserGroupDao dao;

    private final UserRepositoryService userService;

    public UserGroupRepositoryService(Validator validator, UserGroupDao dao, UserRepositoryService userService) {
        super(validator);
        this.dao = dao;
        this.userService = userService;
    }

    @Override
    protected GenericDao<UserGroup> getPrimaryDao() {
        return dao;
    }

    @Override
    protected UserGroup mapToDto(UserGroup entity) {
        return entity;
    }

    @PreAuthorize("hasRole('" + SecurityConstants.ROLE_ADMIN + "')")
    @Transactional
    @Override
    public void addUsers(UserGroup target, URI... toAdd) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(toAdd);
        if (toAdd.length == 0) {
            return;
        }
        Arrays.stream(toAdd).map(u -> userService.findRequired(u).toUser()).forEach(target::addMember);
        dao.update(target);
    }

    @PreAuthorize("hasRole('" + SecurityConstants.ROLE_ADMIN + "')")
    @Transactional
    @Override
    public void removeUsers(UserGroup target, URI... toRemove) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(toRemove);
        if (toRemove.length == 0) {
            return;
        }
        final Set<URI> removeSet = new HashSet<>(Arrays.asList(toRemove));
        Utils.emptyIfNull(target.getMembers()).removeIf(u -> removeSet.contains(u.getUri()));
        dao.update(target);
    }
}
