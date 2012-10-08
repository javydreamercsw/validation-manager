package com.validation.manager.core;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface EntityServer {

    /**
     * Write Entity to database
     *
     * @return Id for entity (new if just created)
     * @throws Exception
     */
    public int write2DB() throws Exception;
}
