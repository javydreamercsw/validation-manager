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
}
