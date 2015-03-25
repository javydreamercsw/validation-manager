package com.validation.manager.test;

import com.validation.manager.core.DBState;
import static com.validation.manager.core.DataBaseManager.close;
import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.getState;
import static com.validation.manager.core.DataBaseManager.setPersistenceUnitName;
import com.validation.manager.core.db.TestProjectTest;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.VmUserJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.VMUserServer;
import static com.validation.manager.core.tool.MD5.encrypt;
import static com.validation.manager.test.TestHelper.deleteUser;
import static java.lang.Class.forName;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import static java.util.logging.Logger.getLogger;
import javax.sql.DataSource;
import junit.framework.TestCase;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.AfterClass;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
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

    @Override
    protected void setUp() throws Exception {
        setPersistenceUnitName("TestVMPU");
        assertEquals(DBState.VALID, getState());
    }

    @Override
    @AfterClass
    protected void tearDown() throws Exception {
        if (deleteDatabase) {
            deleteDatabase = false;
            Connection conn = null;
            Statement stmt = null;
            try {
                Map<String, Object> properties = getEntityManagerFactory().getProperties();
                DataSource ds = new JdbcDataSource();
                ((JdbcDataSource) ds).setPassword((String) properties.get("javax.persistence.jdbc.password"));
                ((JdbcDataSource) ds).setUser((String) properties.get("javax.persistence.jdbc.user"));
                ((JdbcDataSource) ds).setURL((String) properties.get("javax.persistence.jdbc.url"));
                //Load the H2 driver
                forName("org.h2.Driver");
                conn = ds.getConnection();
                stmt = conn.createStatement();
                stmt.executeUpdate("DROP ALL OBJECTS DELETE FILES");
            } catch (SQLException | ClassNotFoundException ex) {
                getLogger(AbstractVMTestCase.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                } catch (SQLException ex) {
                    fail();
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    fail();
                }
            }
        }
        close();
        //Restore to default to make it atomic.
        deleteDatabase = true;
    }

    protected void createTestUsers() {
        try {
            VMUserServer temp = new VMUserServer("test1",
                    "password", "test@test.com", "first", "last");
            temp.write2DB();
            designer = new VmUserJpaController(getEntityManagerFactory()).findVmUser(temp.getId());
            temp = new VMUserServer("test2",
                    "password", "test@test.com", "first", "last");
            temp.write2DB();
            tester = new VmUserJpaController(getEntityManagerFactory()).findVmUser(temp.getId());
            temp = new VMUserServer("test3",
                    encrypt("password"), "test@test.com", "first", "last");
            temp.write2DB();
            leader = new VmUserJpaController(getEntityManagerFactory()).findVmUser(temp.getId());
            getLogger(TestProjectTest.class.getSimpleName()).log(Level.INFO, "Done!");
        } catch (Exception ex) {
            getLogger(AbstractVMTestCase.class.getSimpleName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void deleteTestUsers() {
        try {
            deleteUser(designer);
            deleteUser(tester);
            deleteUser(leader);
        } catch (IllegalOrphanException | NonexistentEntityException ex) {
            getLogger(AbstractVMTestCase.class.getSimpleName()).log(Level.SEVERE, null, ex);
        }
    }
}
