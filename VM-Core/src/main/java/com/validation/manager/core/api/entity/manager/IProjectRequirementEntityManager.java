package com.validation.manager.core.api.entity.manager;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import java.util.Collection;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface IProjectRequirementEntityManager {
    /**
     * Get requirements for specific project.
     * @param p Project to look requirement from
     * @return List of requirements.
     */
    public Collection<Requirement> getEntities(Project p);
}
