package com.validation.manager.core;

import java.util.HashMap;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 * @param <T> Entity
 */
public interface EntityServer<T> {
    static HashMap parameters = new HashMap();
    /**
     * Write Entity to database
     *
     * @return Id for entity (new if just created)
     * @throws Exception
     */
    public int write2DB() throws Exception;

    /**
     * Gets the entity represented by this EntityServer. Easy method to access
     * underlying entity for use with controllers and such.
     *
     * @return
     */
    public T getEntity();

    /**
     * Update the target with the source object.
     *
     * @param target object to update.
     * @param source object to update from.
     */
    public void update(T target, T source);
    
    /**
     * Update the enclosed entity.
     *
     */
    public void update();
}
