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
package com.validation.manager.core;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.FlywayException;
import com.googlecode.flyway.core.api.MigrationInfo;
import com.googlecode.flyway.core.api.MigrationState;
import com.validation.manager.core.db.controller.VmIdJpaController;
import com.validation.manager.core.server.core.VMIdServer;
import com.validation.manager.core.server.core.VMSettingServer;
import static java.lang.Class.forName;
import static java.lang.Long.valueOf;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import static javax.persistence.Persistence.createEntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TableGenerator;
import javax.sql.DataSource;
import org.eclipse.persistence.jpa.JpaHelper;
import org.h2.jdbcx.JdbcDataSource;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class DataBaseManager {

    private static EntityManagerFactory emf;
    private static Map<String, Object> properties;
    //Default. Can be overwritten using setPersistenceUnitName(String aPU)
    private static String PU = "VMPU";
    private static EntityManager em;
    private static boolean dbError = false;
    private static final Logger LOG
            = getLogger(DataBaseManager.class.getSimpleName());
    private static DBState state = DBState.START_UP;
    private static boolean locked = false;
    private static boolean usingContext;
    protected static boolean demo;
    private static Long demoResetPeriod;
    private static boolean versioning_enabled = true;
    private static DataSource dataSource = null;
    private static Connection connection = null;

    /**
     * @return the versioning enabled
     */
    public static boolean isVersioningEnabled() {
        return versioning_enabled;
    }

    /**
     * @param aVersioning_enabled the versioning_enabled to set
     */
    public static void setVersioningEnabled(boolean aVersioning_enabled) {
        versioning_enabled = aVersioning_enabled;
    }

    /**
     * @return the demoResetPeriod
     */
    public static Long getDemoResetPeriod() {
        return demoResetPeriod;
    }

    public static void clean() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        if (isDemo()) {
            LOG.warning("Resetting database since it is on demo mode.");
            //Clean the database on demo
            flyway.clean();
            Map<String, Object> p = new HashMap<>();
            p.put("eclipselink.ddl-generation", "drop-and-create-tables");
            p.put("eclipselink.ddl-generation.output-mode", "database");
            p.put("eclipselink.deploy-on-startup", "true");
            JpaHelper.getEntityManagerFactory(getEntityManager())
                    .refreshMetadata(p);
            //Update the data
            flyway.init();
            flyway.migrate();
        }
    }

    private DataBaseManager() {
    }

    @SuppressWarnings("unchecked")
    private static void processFields(Field[] fields) {
        try {
            for (Field field : fields) {
                if (field.isAnnotationPresent(TableGenerator.class)) {
                    field.setAccessible(true);
                    TableGenerator annotation = field.getAnnotation(TableGenerator.class);
                    field.setAccessible(false);
                    Map<String, Object> parameters = new HashMap<>();
                    String tableName = annotation.pkColumnValue();
                    parameters.put("tableName", tableName);
                    if (namedQuery("VmId.findByTableName", parameters, false).isEmpty()) {
                        LOG.log(Level.FINE, "Adding: {0}: {1}",
                                new Object[]{tableName, annotation.initialValue() - 1});
                        VMIdServer temp = new VMIdServer(tableName, annotation.initialValue() - 1);
                        temp.write2DB();
                        LOG.log(Level.FINE, "Added: {0}: {1}",
                                new Object[]{tableName, annotation.initialValue() - 1});
                    }
                }
            }
        }
        catch (VMException ex1) {
            LOG.log(Level.SEVERE, null, ex1);
        }
        finally {
            if (LOG.isLoggable(Level.CONFIG)) {
                VmIdJpaController controller = new VmIdJpaController(
                        getEntityManagerFactory());
                controller.findVmIdEntities().stream().forEach((next) -> {
                    LOG.log(Level.CONFIG, "{0}, {1}, {2}", new Object[]{next.getId(),
                        next.getTableName(), next.getLastId()});
                });
            }
        }
    }

    /**
     * @return the properties
     */
    public static Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Get the current persistence unit name
     *
     * @return current persistence unit name
     */
    public static String getPersistenceUnitName() {
        return PU;
    }

    /**
     * @param aPU the PU to set
     */
    public static void setPersistenceUnitName(String aPU) {
        PU = aPU;
        LOG.log(Level.FINE, "Changed persistence unit name to: {0}", PU);
        //Set it to null so it's recreated with new Persistence Unit next time is requested.
        emf = null;
        em = null;
        try {
            reload();
        }
        catch (VMException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public static void setEntityManagerFactory(EntityManagerFactory newEMF) {
        emf = newEMF;
    }

    /**
     * @return the emf
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null && !dbError) {
            try {
                InitialContext ctx = new InitialContext();
                //Use the context defined Database connection
                PU = (String) ctx.lookup("java:comp/env/validation-manager/JNDIDB");
                try {
                    demo = (Boolean) ctx.lookup("java:comp/env/validation-manager/demo");
                }
                catch (NamingException e) {
                    LOG.log(Level.SEVERE, null, e);
                    demo = false;
                }
                if (isDemo()) {
                    try {
                        demoResetPeriod = (Long) ctx
                                .lookup("java:comp/env/validation-manager/demo-period");
                    }
                    catch (NamingException e) {
                        LOG.log(Level.SEVERE, null, e);
                        demoResetPeriod = valueOf(0);
                    }
                    if (getDemoResetPeriod() > 0) {
                        Long millis = getDemoResetPeriod();
                        String format = String.format("%02d min, %02d sec",
                                TimeUnit.MILLISECONDS.toMinutes(millis),
                                TimeUnit.MILLISECONDS.toSeconds(millis)
                                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
                        );
                        LOG.log(Level.WARNING,
                                "Instance configured as demo, database will reset"
                                + " each {0}", format);
                    }
                }
                final String JNDIDB = (String) ctx.lookup("java:comp/env/validation-manager/JNDIDB");
                emf = createEntityManagerFactory(JNDIDB);
                LOG.log(Level.FINE, "Using context defined database connection: {0}", JNDIDB);
                usingContext = true;
            }
            catch (NamingException e) {
                LOG.log(Level.FINE, null, e);
                if (!usingContext) {
                    LOG.log(Level.WARNING,
                            "Manually specified connection parameters. "
                            + "Using pre-defined persistence unit: {0}", PU);
                    emf = createEntityManagerFactory(PU);
                } else {
                    LOG.log(Level.SEVERE,
                            "Context doesn't exist. Check your configuration.", e);
                    dbError = true;
                }
            }
        }
        return emf;
    }

    /**
     * @return the demo
     */
    public static boolean isDemo() {
        return demo;
    }

    public static EntityManager getEntityManager() {
        if (em == null) {
            em = getEntityManagerFactory().createEntityManager();
            LOG.log(Level.FINE,
                    "Creating EntityManager from: {0}", PU);
            properties = em.getProperties();
        }
        return em;
    }

    public static List<Object> createdQuery(String query) {
        return createdQuery(query, null);
    }

    @SuppressWarnings("unchecked")
    public static List<Object> createdQuery(String query, Map<String, Object> parameters) {
        Query q = getEntityManager().createQuery(query);
        if (parameters != null) {
            Iterator<Entry<String, Object>> entries = parameters.entrySet().iterator();
            while (entries.hasNext()) {
                Entry<String, Object> e = entries.next();
                q.setParameter(e.getKey(), e.getValue());
            }
        }
        return q.getResultList();
    }

    /**
     * Named query that will modify the database
     *
     * @param query query to execute
     */
    public static void namedUpdateQuery(String query) {
        namedQuery(query, null, true);
    }

    public static List<Object> nativeQuery(String query) {
        List<Object> resultList
                = getEntityManager().createNativeQuery(query).getResultList();
        return resultList;
    }

    public static <T extends Object> List<T> createdQuery(String query, T result) {
        List<T> resultList
                = getEntityManager().createQuery(query).getResultList();
        return resultList;
    }

    public static void nativeUpdateQuery(String query) {
        getEntityManager().createNativeQuery(query).executeUpdate();
    }

    /**
     * Named query (not for updates)
     *
     * @param query query to execute
     * @return query result
     */
    public static List<Object> namedQuery(String query) {
        return namedQuery(query, null, false);
    }

    /**
     * Named query that will modify the database
     *
     * @param query query to execute
     * @param parameters query parameters
     */
    public static void namedUpdateQuery(String query, Map<String, Object> parameters) {
        namedQuery(query, parameters, true);
    }

    /**
     * Named query (not for updates)
     *
     * @param query query to execute
     * @param parameters query parameters
     * @return query result
     */
    public static List<Object> namedQuery(String query,
            Map<String, Object> parameters) {
        return namedQuery(query, parameters, false);
    }

    @SuppressWarnings("unchecked")
    private static List<Object> namedQuery(String query,
            Map<String, Object> parameters, boolean change) {
        EntityTransaction transaction = getEntityManager().getTransaction();
        if (change) {
            transaction.begin();
        }
        Query q = getEntityManager().createNamedQuery(query);
        if (parameters != null) {
            Iterator<Entry<String, Object>> entries
                    = parameters.entrySet().iterator();
            while (entries.hasNext()) {
                Entry<String, Object> e = entries.next();
                q.setParameter(e.getKey(), e.getValue());
            }
        }
        if (change) {
            transaction.commit();
        }
        return q.getResultList();
    }

    public static void close() {
        getEntityManager().close();
        getEntityManagerFactory().close();
        emf = null;
        em = null;
    }

    public static void reload() throws VMException {
        reload(false);
    }

    public static void reload(boolean close) throws VMException {
        if (close) {
            close();
        }
        updateDBState();
        getEntityManager();
    }

    public static void updateDBState() {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dataSource = (javax.sql.DataSource) new InitialContext()
                    .lookup("java:comp/env/jdbc/VMDB");
            connection = dataSource.getConnection();
        }
        catch (NamingException ne) {
            LOG.log(Level.FINE, null, ne);
            if (emf == null) {
                try {
                    //It might be the tests, use an H2 Database
                    dataSource = new JdbcDataSource();
                    ((JdbcDataSource) dataSource).setPassword("");
                    ((JdbcDataSource) dataSource).setUser("vm_user");
                    ((JdbcDataSource) dataSource).setURL(
                            "jdbc:h2:file:./target/data/test/validation-manager-test;AUTO_SERVER=TRUE");
                    //Load the H2 driver
                    forName("org.h2.Driver");
                    connection = dataSource.getConnection();
                }
                catch (ClassNotFoundException | SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            } else {
                EntityTransaction transaction = getEntityManager().getTransaction();
                transaction.begin();
                connection = getEntityManager().unwrap(java.sql.Connection.class);
                transaction.commit();
            }
        }
        catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        if (getConnection() != null) {
            try {
                stmt = getConnection().prepareStatement("select * from vm_setting");
                rs = stmt.executeQuery();
                if (!rs.next()) {
                    //Tables there but empty? Not safe to proceed
                    setState(DBState.NEED_MANUAL_UPDATE);
                }
            }
            catch (SQLException ex) {
                LOG.log(Level.FINE, null, ex);
                //Need INIT, probably nothing there
                setState(DBState.NEED_INIT);
                //Create the database
                getEntityManager();
            }
            finally {
                try {
                    getConnection().close();
                }
                catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                }
                catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }
        if (dataSource != null) {
            getEntityManagerFactory();
            //Initialize flyway
            initializeFlyway(dataSource);
            updateDatabase(dataSource);
        } else {
            state = DBState.ERROR;
        }

        if (state != DBState.VALID) {
            waitForDB();
        }
    }

    private static void updateDatabase(DataSource dataSource) {
        Flyway flyway = new Flyway();
        try {
            flyway.setDataSource(dataSource);
            flyway.setLocations("db.migration");
            LOG.fine("Starting migration...");
            flyway.migrate();
            LOG.fine("Done!");
        }
        catch (FlywayException fe) {
            LOG.log(Level.SEVERE, "Unable to migrate data", fe);
            setState(DBState.ERROR);
        }
        try {
            LOG.fine("Validating migration...");
            flyway.validate();
            LOG.fine("Done!");
            setState(flyway.info().current().getState()
                    == MigrationState.SUCCESS ? DBState.VALID : DBState.ERROR);
        }
        catch (FlywayException fe) {
            LOG.log(Level.SEVERE, "Unable to validate", fe);
            setState(DBState.ERROR);
        }
    }

    protected static void setState(DBState newState) {
        state = newState;
    }

    public static void waitForDB() {
        while (getState() != DBState.VALID
                && getState() != DBState.UPDATED
                && getState() != DBState.ERROR) {
            LOG.log(Level.FINE,
                    "Waiting for DB initialization. Current state: {0}",
                    (getState() != null ? getState().name() : null));
            try {
                Thread.currentThread().sleep(100);
            }
            catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        LOG.log(Level.FINE, "DB ready, resuming...");
    }

    /**
     * @return the state
     */
    public static DBState getState() {
        return state;
    }

    /**
     * @param lock the lock to set
     */
    public static void setLocked(boolean lock) {
        LOG.log(Level.WARNING, "{0} the database.",
                (lock ? "Locking" : "Unlocking"));
        locked = lock;
    }

    public static String getVersion() {
        return getVersionNumber()
                + ((VMSettingServer.getSetting("version.postfix")
                        .getStringVal().isEmpty()
                        ? "" : " "
                        + VMSettingServer.getSetting("version.postfix")
                                .getStringVal()));
    }

    public static String getVersionNumber() {
        StringBuilder version = new StringBuilder();
        version.append(VMSettingServer.getSetting("version.high").getIntVal());
        version.append(".");
        version.append(VMSettingServer.getSetting("version.mid").getIntVal());
        version.append(".");
        version.append(VMSettingServer.getSetting("version.low").getIntVal());
        return version.toString();
    }

    /**
     * @return the locked
     */
    public static boolean isLocked() {
        return locked;
    }

    private static void initializeFlyway(DataSource dataSource) {
        assert dataSource != null;
        setState(DBState.START_UP);
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        if (isDemo()) {
            //Clean the database on demo
            clean();
            close();
            getEntityManagerFactory();
        }
        MigrationInfo status = flyway.info().current();
        if (status == null) {
            setState(DBState.NEED_INIT);
            LOG.fine("Initialize the metadata...");
            try {
                flyway.init();
                LOG.fine("Done!");
            }
            catch (FlywayException fe) {
                LOG.log(Level.SEVERE, "Unable to initialize database", fe);
                setState(DBState.ERROR);
            }
        } else {
            LOG.fine("Database has Flyway metadata already...");
            displayDBStatus(status);
        }
    }

    private static void displayDBStatus(MigrationInfo status) {
        LOG.log(Level.FINE, "Description: {0}\nState: {1}\nVersion: {2}",
                new Object[]{status.getDescription(), status.getState(), status.getVersion()});
    }

    /**
     * @return the connection
     */
    private static Connection getConnection() {
        return connection;
    }
}
