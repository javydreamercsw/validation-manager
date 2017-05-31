package com.validation.manager.core.server.core;

import com.validation.manager.core.db.History;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 * @param <T> Class
 */
public interface VersionableServer<T> {

    /**
     * Get the versions of this entity.
     *
     * @return History list.
     */
    List<History> getHistoryList();
}
