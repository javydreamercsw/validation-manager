package com.validation.manager.core.api.entity.manager;

import java.util.Collection;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface VMEntityManager<T> {

    /**
     * Check if this manager supports the provided entity.
     *
     * @param entity Entity to support.
     * @return true if supported, false otherwise.
     */
    boolean supportEntity(Class entity);
    
    /**
     * Update entity within the manager.
     * @param entity Entity to update.
     */
    void updateEntity(T entity);
    
    /**
     * Remove entity from manager.
     * @param entity 
     */
    void removeEntity(T entity);
    
    /**
     * Remove entity from manager.
     * @param entity 
     */
    void addEntity(T entity);
    
    /**
     * Get entity from manager.
     * @param entity 
     * @return Entity from manager.
     */
    T getEntity(Object entity);
    
    /**
     * Get Entities within the manager.
     * @return List of entities. If versionable entity, it returns the latest 
     * version of each entity.
     */
    Collection<T> getEntities();
}
