package com.validation.manager.test;

import com.validation.manager.core.DBState;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.TestProjectTest;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.VmUserJpaController;
import com.validation.manager.core.server.core.VMUserServer;
import com.validation.manager.core.tool.MD5;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import junit.framework.TestCase;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractVMTestCase extends TestCase {

    protected VmUser designer, tester, leader;
    /**
     * Parameters for queries.
     */
    protected HashMap<String, Object> parameters = new HashMap<String, Object>();
    /**
     * Query results.
     */
    protected List<Object> result;
    public static boolean deleteDatabase = true;

    @Override
    protected void setUp() throws Exception {
        DataBaseManager.setPersistenceUnitName("TestVMPU");
        assertTrue(DataBaseManager.getState().equals(DBState.VALID));
    }

    @Override
    protected void tearDown() throws Exception {
        if (deleteDatabase) {
            Connection conn = null;
            Statement stmt = null;
            try {
                DataSource ds = new JdbcDataSource();
                ((JdbcDataSource) ds).setPassword("");
                ((JdbcDataSource) ds).setUser("vm_user");
                ((JdbcDataSource) ds).setURL(
                        "jdbc:h2:file:data/test/validation-manager-test;AUTO_SERVER=TRUE");
                //Load the H2 driver
                Class.forName("org.h2.Driver");
                conn = ds.getConnection();
                stmt = conn.createStatement();
                stmt.executeUpdate("DROP ALL OBJECTS DELETE FILES");
            } catch (SQLException ex) {
                Logger.getLogger(AbstractVMTestCase.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(AbstractVMTestCase.class.getName()).log(Level.SEVERE, null, ex);
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
        DataBaseManager.close();
        //Restore to default to make it atomic.
        deleteDatabase=true;
    }

    protected void createTestUsers() {
        try {
            VMUserServer temp = new VMUserServer("test1",
                    "password", "test@test.com", "first", "last");
            temp.write2DB();
            designer = new VmUserJpaController(DataBaseManager.getEntityManagerFactory()).findVmUser(temp.getId());
            temp = new VMUserServer("test2",
                    "password", "test@test.com", "first", "last");
            temp.write2DB();
            tester = new VmUserJpaController(DataBaseManager.getEntityManagerFactory()).findVmUser(temp.getId());
            temp = new VMUserServer("test3",
                    MD5.encrypt("password"), "test@test.com", "first", "last");
            temp.write2DB();
            leader = new VmUserJpaController(DataBaseManager.getEntityManagerFactory()).findVmUser(temp.getId());
            Logger.getLogger(TestProjectTest.class.getSimpleName()).log(Level.INFO, "Done!");
        } catch (Exception ex) {
            Logger.getLogger(AbstractVMTestCase.class.getSimpleName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void deleteTestUsers() {
        try {
            VMUserServer.deleteUser(designer);
            VMUserServer.deleteUser(tester);
            VMUserServer.deleteUser(leader);
        } catch (Exception ex) {
            Logger.getLogger(AbstractVMTestCase.class.getSimpleName()).log(Level.SEVERE, null, ex);
        }
    }
}
