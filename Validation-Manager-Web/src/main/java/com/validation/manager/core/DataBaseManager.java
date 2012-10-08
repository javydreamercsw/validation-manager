package com.validation.manager.core;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.exception.FlywayException;
import com.googlecode.flyway.core.metadatatable.MetaDataTableRow;
import com.googlecode.flyway.core.migration.MigrationException;
import com.googlecode.flyway.core.migration.MigrationState;
import com.validation.manager.core.db.VmId;
import com.validation.manager.core.db.controller.VmIdJpaController;
import com.validation.manager.core.server.core.VMIdServer;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TableGenerator;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class DataBaseManager {

    private static EntityManagerFactory emf;
    private static Map<String, Object> properties;
    //Default. Can be overwritten using setPersistenceUnitName(String aPU)
    private static String PU = "VMPU";
    private static EntityManager em;
    private static boolean dbError = false;
    private static final Logger LOG =
            Logger.getLogger(DataBaseManager.class.getSimpleName());
    private static DBState state;
    private static ResourceBundle settings =
            ResourceBundle.getBundle("com.validation.manager.resources.settings");
    private static boolean locked = false;
    private static boolean usingContext;
    private static boolean demo;
    private static Long demoResetPeriod;
    private static DataBaseManager instance;

    private DataBaseManager() {
        try {
            state = DBState.START_UP;
            reload();
        } catch (VMException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public static DataBaseManager get() throws Exception {
        if (instance == null) {
            instance = new DataBaseManager();
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    private static void processFields(Field[] fields) {
        try {
            for (Field field : fields) {
                if (field.isAnnotationPresent(TableGenerator.class)) {
                    field.setAccessible(true);
                    TableGenerator annotation = field.getAnnotation(TableGenerator.class);
                    field.setAccessible(false);
                    HashMap<String, Object> parameters = new HashMap<String, Object>();
                    String tableName = annotation.pkColumnValue();
                    parameters.put("tableName", tableName);
                    if (DataBaseManager.namedQuery("VmId.findByTableName", parameters, false).isEmpty()) {
                        LOG.log(Level.FINE, "Adding: {0}: {1}",
                                new Object[]{tableName, annotation.initialValue() - 1});
                        VMIdServer temp = new VMIdServer(tableName, annotation.initialValue() - 1);
                        temp.write2DB();
                        LOG.log(Level.FINE, "Added: {0}: {1}",
                                new Object[]{tableName, annotation.initialValue() - 1});
                    }
                }
            }
        } catch (VMException ex1) {
            LOG.log(Level.SEVERE, null, ex1);
        } finally {
            if (LOG.isLoggable(Level.CONFIG)) {
                VmIdJpaController controller = new VmIdJpaController(
                        DataBaseManager.getEntityManagerFactory());
                for (Iterator<VmId> it = controller.findVmIdEntities()
                        .iterator(); it.hasNext();) {
                    VmId next = it.next();
                    LOG.log(Level.CONFIG, "{0}, {1}, {2}", new Object[]{next.getId(),
                                next.getTableName(), next.getLastId()});
                }
            }
        }
    }

    private static void generateIDs() {
        if (!dbError) {
            LOG.log(Level.FINE,
                    "Creating ids to work around eclipse issue "
                    + "(https://bugs.eclipse.org/bugs/show_bug.cgi?id=366852)...");
            for (Iterator<EmbeddableType<?>> it =
                    getEntityManager().getMetamodel().getEmbeddables()
                    .iterator(); it.hasNext();) {
                EmbeddableType et = it.next();
                processFields(et.getJavaType().getDeclaredFields());
            }
            for (Iterator<EntityType<?>> it = getEntityManager().getMetamodel().getEntities().iterator(); it.hasNext();) {
                EntityType et = it.next();
                processFields(et.getBindableJavaType().getDeclaredFields());
            }
            LOG.log(Level.FINE, "Done!");
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
        } catch (VMException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the emf
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null && !dbError) {
            try {
                InitialContext ctx = new InitialContext();
                ctx.lookup("java:/comp/env/jdbc/VMDB");
                //Use the context defined Database connection
                PU = (String) ctx.lookup("java:comp/env/validation_manager/JNDIDB");
                try {
                    demo = (Boolean) ctx.lookup("java:comp/env/validation_manager/demo");
                } catch (NamingException e) {
                    LOG.log(Level.SEVERE, null, e);
                    demo = false;
                }
                if (isDemo()) {
                    try {
                        demoResetPeriod = (Long) ctx.lookup("java:comp/env/validation_manager/demo-period");
                    } catch (NamingException e) {
                        LOG.log(Level.SEVERE, null, e);
                        demoResetPeriod = Long.valueOf(0);
                    }
                    if (demoResetPeriod > 0) {
                        LOG.log(Level.WARNING,
                                "Instance configured as demo, database will reset"
                                + " each {0} milliseconds", demoResetPeriod);
                    }
                }
                final String JNDIDB = (String) ctx.lookup("java:comp/env/validation_manager/JNDIDB");
                emf = Persistence.createEntityManagerFactory(JNDIDB);
                LOG.log(Level.INFO, "Using context defined database connection: {0}", JNDIDB);
                usingContext = true;
            } catch (MigrationException e) {
                LOG.log(Level.SEVERE,
                        "Unable to migrate database!", e);
                dbError = true;
            } catch (NamingException e) {
                LOG.log(Level.FINE, null, e);
                demo = false;
                if (!usingContext) {
                    LOG.log(Level.WARNING,
                            "Manually specified connection parameters. "
                            + "Using pre-defined persistence unit: {0}", PU);
                    emf = Persistence.createEntityManagerFactory(PU);
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
    public static List<Object> createdQuery(String query, HashMap<String, Object> parameters) {
        getEntityManager().getTransaction().begin();
        Query q = getEntityManager().createQuery(query);
        if (parameters != null) {
            Iterator<Entry<String, Object>> entries = parameters.entrySet().iterator();
            while (entries.hasNext()) {
                Entry<String, Object> e = entries.next();
                q.setParameter(e.getKey(), e.getValue());
            }
        }
        getEntityManager().getTransaction().commit();
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
        getEntityManager().getTransaction().begin();
        List<Object> resultList = getEntityManager().createNativeQuery(query).getResultList();
        getEntityManager().getTransaction().commit();
        return resultList;
    }

    public static void nativeUpdateQuery(String query) {
        boolean atomic = false;
        if (!getEntityManager().getTransaction().isActive()) {
            getEntityManager().getTransaction().begin();
            atomic = true;
        }
        getEntityManager().createNativeQuery(query).executeUpdate();
        if (atomic) {
            getEntityManager().getTransaction().commit();
        }
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
    public static void namedUpdateQuery(String query, HashMap<String, Object> parameters) {
        namedQuery(query, parameters, true);
    }

    /**
     * Named query (not for updates)
     *
     * @param query query to execute
     * @param parameters query parameters
     * @return query result
     */
    public static List<Object> namedQuery(String query, HashMap<String, Object> parameters) {
        return namedQuery(query, parameters, false);
    }

    @SuppressWarnings("unchecked")
    private static List<Object> namedQuery(String query, HashMap<String, Object> parameters, boolean change) {
        if (change) {
            getEntityManager().getTransaction().begin();
        }
        Query q = getEntityManager().createNamedQuery(query);
        if (parameters != null) {
            Iterator<Entry<String, Object>> entries = parameters.entrySet().iterator();
            while (entries.hasNext()) {
                Entry<String, Object> e = entries.next();
                q.setParameter(e.getKey(), e.getValue());
            }
        }
        if (change) {
            getEntityManager().getTransaction().commit();
        }
        return q.getResultList();
    }

    public static void close() {
        getEntityManager().close();
        getEntityManagerFactory().close();
    }

    public static EntityTransaction getTransaction() {
        return getEntityManager().getTransaction();
    }

    public static void main(String[] args) {
        //Used to update the init script
        //Get the MySQL script file
        File script = new File(new File(System.getProperty("user.dir")).getParent()
                + System.getProperty("file.separator")
                + "DB" + System.getProperty("file.separator")
                + "VM.sql");
        if (script.exists()) {
            try {
                ArrayList<String> contents = readFileAsString(script.getAbsolutePath(), null);
                if (!contents.isEmpty()) {
                    //Create the init.sql file src\java\com\bluecubs\xinco\core\server\db\script
                    File initFile = new File(System.getProperty("user.dir")
                            + System.getProperty("file.separator") + "src"
                            + System.getProperty("file.separator") + "main"
                            + System.getProperty("file.separator") + "resources"
                            + System.getProperty("file.separator") + "com"
                            + System.getProperty("file.separator") + "validation"
                            + System.getProperty("file.separator") + "manager"
                            + System.getProperty("file.separator") + "core"
                            + System.getProperty("file.separator") + "db"
                            + System.getProperty("file.separator") + "script"
                            + System.getProperty("file.separator") + "init.sql");
                    if (initFile.exists()) {
                        initFile.delete();
                    }
                    initFile.createNewFile();
                    setContents(initFile, contents);
                } else {
                    LOG.severe("Unable to convert script!");
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            } catch (VMException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }

    protected static ArrayList<String> readFileAsString(String filePath, Class relativeTo) throws java.io.IOException, VMException {
        InputStream in = null;
        InputStreamReader is = null;
        BufferedReader br = null;
        ArrayList<String> statements = new ArrayList<String>();
        try {
            in = relativeTo == null ? new FileInputStream(new File(filePath))
                    : relativeTo.getResourceAsStream(filePath);
            is = new InputStreamReader(in, "utf8");
            br = new BufferedReader(is);
            String line;
            StringBuilder sql = new StringBuilder();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                sql.append(line).append("\n");
            }
            //The list of statement types to ignore
            ArrayList<String> ignore = new ArrayList<String>();
            ignore.add(ESqlStatementType.sstmysqlset.toString());
            ignore.add(ESqlStatementType.sstinvalid.toString());
            ignore.add(ESqlStatementType.sstmysqluse.toString());
            ignore.add(ESqlStatementType.sstmysqldroptable.toString());
            ignore.add(ESqlStatementType.sstcreatetable.toString());
            ignore.add(ESqlStatementType.sstmysqlsetautocommit.toString());
            ignore.add(ESqlStatementType.sstmysqlcommit.toString());
            ignore.add(ESqlStatementType.sstmysqlstarttransaction.toString());
            //-------------------------------------
            if (!sql.toString().isEmpty()) {
                TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
                sqlparser.sqltext = sql.toString();
                //Check statements for correctness first
                sqlparser.parse();
                //Everything fine, keep going
                for (int i = 0; i < sqlparser.sqlstatements.size(); i++) {
                    if (!ignore.contains(sqlparser.sqlstatements.get(i).sqlstatementtype.toString())) {
                        statements.add(sqlparser.sqlstatements.get(i).toString().replaceAll("`validation_manager`.", ""));
                    }
                }
            }
        } finally {
            if (br != null) {
                br.close();
            }
            if (is != null) {
                is.close();
            }
            if (in != null) {
                in.close();
            }
        }
        return statements;
    }

    /**
     * Change the contents of text file in its entirety, overwriting any
     * existing text.
     *
     * This style of implementation throws all exceptions to the caller.
     *
     * @param aFile is an existing file which can be written to.
     * @param aContents contents to set
     * @throws IllegalArgumentException if param does not comply.
     * @throws FileNotFoundException if the file does not exist.
     * @throws IOException if problem encountered during write.
     */
    static public void setContents(File aFile, ArrayList<String> aContents)
            throws FileNotFoundException, IOException {
        if (aFile == null) {
            throw new IllegalArgumentException("File should not be null.");
        }
        if (!aFile.exists()) {
            throw new FileNotFoundException("File does not exist: " + aFile);
        }
        if (!aFile.isFile()) {
            throw new IllegalArgumentException("Should not be a directory: " + aFile);
        }
        if (!aFile.canWrite()) {
            throw new IllegalArgumentException("File cannot be written: " + aFile);
        }
        FileWriter fw = new FileWriter(aFile);
        //use buffering
        Writer output = new BufferedWriter(fw);
        try {
            //FileWriter always assumes default encoding is OK!
            for (String line : aContents) {
                output.write(line);
                output.write("\n");
            }
        } finally {
            output.close();
            fw.close();
        }
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
        generateIDs();
    }

    public static void updateDBState() {
        DataSource ds = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            ds = (javax.sql.DataSource) new InitialContext().lookup("java:comp/env/jdbc/VMDB");
        } catch (NamingException ne) {
            LOG.log(Level.FINE, null, ne);
            try {
                //It might be the tests, use an H2 Database
                ds = new JdbcDataSource();
                ((JdbcDataSource) ds).setPassword("");
                ((JdbcDataSource) ds).setUser("vm_user");
                ((JdbcDataSource) ds).setURL(
                        "jdbc:h2:file:data/test/validation-manager-test;AUTO_SERVER=TRUE");
                //Load the H2 driver
                Class.forName("org.h2.Driver");
            } catch (ClassNotFoundException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        try {
            conn = ds.getConnection();
            stmt = conn.prepareStatement("select * from vm_setting");
            rs = stmt.executeQuery();
            if (!rs.next()) {
                //Tables there but empty? Not safe to proceed
                setState(DBState.NEED_MANUAL_UPDATE);
            }
        } catch (SQLException ex) {
            LOG.log(Level.FINE, null, ex);
            //Need INIT, probably nothing there
            setState(DBState.NEED_INIT);
            //Create the database
            getEntityManager();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        if (ds != null) {
            //Initialize flyway
            initializeFlyway(ds);
            updateDatabase(ds);
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
            flyway.setLocations("com.validation.manager.core.db.script");
            LOG.info("Starting migration...");
            flyway.migrate();
            LOG.info("Done!");
        } catch (FlywayException fe) {
            LOG.log(Level.SEVERE, "Unable to migrate data", fe);

            setState(DBState.ERROR);
        }
        try {
            LOG.info("Validating migration...");
            flyway.validate();
            LOG.info("Done!");
            setState(flyway.status().getState() == MigrationState.SUCCESS ? DBState.VALID : DBState.ERROR);
        } catch (FlywayException fe) {
            LOG.log(Level.SEVERE, "Unable to validate", fe);
            setState(DBState.ERROR);
        }
    }

    protected static void setState(DBState newState) {
        state = newState;
    }

    public static void waitForDB() {
        while (DataBaseManager.getState() != DBState.VALID
                && DataBaseManager.getState() != DBState.UPDATED
                && DataBaseManager.getState() != DBState.ERROR) {
            LOG.log(Level.INFO,
                    "Waiting for DB initialization. Current state: {0}",
                    (DataBaseManager.getState() != null ? DataBaseManager.getState().name() : null));
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        LOG.log(Level.INFO, "DB ready, resuming...");
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
        return getVersionNumber() + ((settings.getString("version.postfix").isEmpty()
                ? "" : " " + settings.getString("version.postfix")));
    }

    public static String getVersionNumber() {
        StringBuilder version = new StringBuilder();
        version.append(settings.getString("version.high"));
        version.append(".");
        version.append(settings.getString("version.mid"));
        version.append(".");
        version.append(settings.getString("version.low"));
        return version.toString();
    }

    /**
     * Compare two number strings. For example: 2.1.0 == 2.01.00
     *
     * @param first first string to compare
     * @param second second string to compare
     * @return
     */
    public static boolean compareNumberStrings(String first, String second) {
        return compareNumberStrings(first, second, ".");
    }

    /**
     * Compare two number strings. For example: 2.1.0 == 2.01.00
     *
     * @param first first string to compare
     * @param second second string to compare
     * @param separator separator of fields (i.e. for 2.1.0 is '.')
     * @return
     */
    public static boolean compareNumberStrings(String first, String second,
            String separator) {
        StringTokenizer firstST = new StringTokenizer(first, separator);
        StringTokenizer secondST = new StringTokenizer(second, separator);
        if (firstST.countTokens() != secondST.countTokens()) {
            //Different amount of fields, not equal. (i.e. 2.1 and 2.1.1
            return false;
        } else {
            try {
                while (firstST.hasMoreTokens()) {
                    int firstInt = Integer.parseInt(firstST.nextToken());
                    int secondInt = Integer.parseInt(secondST.nextToken());
                    //Both numbers let's continue
                    if (firstInt != secondInt) {
                        return false;
                    }
                }
                //Everything the same
            } catch (java.lang.NumberFormatException e) {
                LOG.log(Level.WARNING, null, e);
                //Is not a number
                return false;
            }
        }
        return true;
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
        MetaDataTableRow status = flyway.status();
        if (status == null) {
            setState(DBState.NEED_INIT);
            LOG.info("Initialize the metadata...");
            try {
                flyway.init();
                LOG.info("Done!");
            } catch (FlywayException fe) {
                LOG.log(Level.SEVERE, "Unable to initialize database", fe);
                setState(DBState.ERROR);
            }
        } else {
            LOG.info("Database has Flyway metadata already...");
            displayDBStatus(status);
        }
    }

    private static void displayDBStatus(MetaDataTableRow status) {
        LOG.log(Level.INFO, "Description: {0}\nState: {1}\nVersion: {2}",
                new Object[]{status.getDescription(), status.getState(), status.getVersion()});
    }
}
