package com.validation.manager.core.server.core;

import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 * @param <T>
 */
public interface VersionableServer<T> {

    /**
     * Get the versions of this entity.
     *
     * @return
     */
    List<T> getVersions();
    
    /**
     * Check if the changes in the entity should be versionable or not.
     * Useful to avoid versioning changes in relationships, etc 
     * that don't modify the entity.
     * @return 
     */
    boolean isChangeVersionable();
}
