/*
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.validation.manager.test;

import com.validation.manager.core.DBState;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.HistoryField;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.VmUserJpaController;
import com.validation.manager.core.history.Auditable;
import com.validation.manager.core.history.Versionable;
import com.validation.manager.core.server.core.VMUserServer;
import static java.lang.Class.forName;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import junit.framework.TestCase;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public abstract class AbstractVMTestCase extends TestCase {

    protected VmUser designer, tester, leader;
    /**
     * Parameters for queries.
     */
    protected HashMap<String, Object> parameters = new HashMap<>();
    /**
     * Query results.
     */
    protected List<Object> result;
    public static boolean deleteDatabase = true;
    private static final Logger LOG
            = Logger.getLogger(AbstractVMTestCase.class.getSimpleName());

    @Before
    @Override
    protected void setUp() throws Exception {
        LOG.info("Setting up database!");
        DataBaseManager.setPersistenceUnitName("TestVMPU");
        assertEquals(DBState.VALID, DataBaseManager.getState());
        postSetUp();
        LOG.info("Done!");
    }

    @After
    @Override
    protected void tearDown() throws Exception {
        LOG.info("Deleting database!");
        deleteTestUsers();
        Connection conn = null;
        Statement stmt = null;
        try {
            Map<String, Object> properties
                    = DataBaseManager.getEntityManagerFactory()
                            .getProperties();
            DataSource ds = new JdbcDataSource();
            ((JdbcDataSource) ds).setPassword((String) properties
                    .get("javax.persistence.jdbc.password"));
            ((JdbcDataSource) ds).setUser((String) properties
                    .get("javax.persistence.jdbc.user"));
            ((JdbcDataSource) ds).setURL((String) properties
                    .get("javax.persistence.jdbc.url"));
            //Load the H2 driver
            forName("org.h2.Driver");
            conn = ds.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("DROP ALL OBJECTS DELETE FILES");
        }
        catch (SQLException | ClassNotFoundException ex) {
            LOG.log(Level.SEVERE,
                    null, ex);
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (SQLException ex) {
                fail();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException ex) {
                fail();
            }
        }
        DataBaseManager.close();
        postTearDown();
        LOG.info("Done!");
    }

    protected void createTestUsers() {
        try {
            VMUserServer temp = new VMUserServer("test1",
                    "password", "Test", "Designer", "test@test.com");
            temp.write2DB();
            designer = new VmUserJpaController(DataBaseManager
                    .getEntityManagerFactory())
                    .findVmUser(temp.getId());
            temp = new VMUserServer("test2",
                    "password", "Mr.", "Tester", "test@test.com");
            temp.write2DB();
            tester = new VmUserJpaController(DataBaseManager
                    .getEntityManagerFactory())
                    .findVmUser(temp.getId());
            temp = new VMUserServer("test3",
                    "password", "Test", "Lead", "test@test.com");
            temp.write2DB();
            leader = new VmUserJpaController(DataBaseManager
                    .getEntityManagerFactory())
                    .findVmUser(temp.getId());
            LOG.log(Level.INFO, "Done!");
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Code to be performed after set up.
     */
    protected void postSetUp() {

    }

    /**
     * Code to be performed after set teardown.
     */
    protected void postTearDown() {

    }

    protected void deleteTestUsers() {
        designer = null;
        tester = null;
        leader = null;
    }

    public List<Field> getAuditableFields(Versionable v) {
        List<Field> r = new ArrayList<>();
        FieldUtils.getFieldsListWithAnnotation(v.getClass(), Auditable.class)
                .stream().filter((field)
                        -> (field.isAnnotationPresent(Auditable.class)))
                .forEachOrdered((field) -> {
                    r.add(field);
                });
        return r;
    }

    public boolean checkHistory(Versionable v) {
        History current = v.getHistoryList().get(v.getHistoryList().size() - 1);
        List<Field> af = getAuditableFields(v);
        assertEquals(af.size(), current.getHistoryFieldList().size());
        assertTrue(af.size() > 0);
        for (HistoryField hf : current.getHistoryFieldList()) {
            try {
                //Compare audit field vs. the record in history.
                Object o = FieldUtils.readField(FieldUtils.getField(v.getClass(),
                        hf.getFieldName(), true), v);
                if (!Versionable.fieldMatchHistory(hf, o)) {
                    return false;
                }
            }
            catch (SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        v.getHistoryList().forEach(h -> {
            LOG.info(h.toString());
        });
        return true;
    }
}
