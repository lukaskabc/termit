package cz.cvut.kbss.termit.service.security.authorization;

import cz.cvut.kbss.termit.model.Asset;

import java.util.Optional;

/**
 * Authorizes access to assets of the target type.
 * <p>
 * This class provides custom authorization logic that cannot be (at least not easily) done using SpEL. Instead, methods
 * of this class should be invoked by authorization mechanisms such as {@link org.springframework.security.access.prepost.PreAuthorize}.
 *
 * @param <T> Asset type to which access is to be authorized
 */
public interface AssetAuthorizationService<T extends Asset<?>> {

    /**
     * Checks whether the current use can read the optionally provided asset.
     *
     * @param asset Asset wrapped in an optional
     * @return {@code true} if read access is authorized for the current user or the argument is empty, {@code false}
     * otherwise
     * @see #canRead(Asset)
     */
    default boolean canRead(Optional<T> asset) {
        return asset.isEmpty() || canRead(asset.get());
    }

    /**
     * Checks whether the current user can read the specified asset.
     * <p>
     * Read access means that the user may view the specified asset in a collection of assets as well access the details
     * of the specified asset.
     *
     * @param asset Resource access to which is to be authorized
     * @return {@code true} if read access is authorized for the current user, {@code false} otherwise
     */
    boolean canRead(T asset);

    /**
     * Checks whether the current user can modify the specified asset.
     * <p>
     * Note that modification does not include removal, that is handled separately by {@link #canRead(T)}.
     *
     * @param asset Resource access to which is to be authorized
     * @return {@code true} if the current user can modify the specified asset, {@code false} otherwise
     * @see #canRemove(T)
     */
    boolean canModify(T asset);

    /**
     * Checks whether the current user can remove the specified asset.
     * <p>
     * Note that this checks only authorization conditions, there may be other domain conditions for removal.
     *
     * @param asset Resource access to which is to be authorized
     * @return {@code true} if the current user can remove the specified asset, {@code false} otherwise
     */
    boolean canRemove(T asset);
}
