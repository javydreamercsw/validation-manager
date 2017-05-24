package com.validation.manager.core.active.directory;

import com.validation.manager.core.server.core.VMSettingServer;
import com.validation.manager.test.AbstractVMTestCase;
import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.entry.ServerEntry;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmIndex;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.ldap.LdapService;
import org.apache.directory.server.protocol.shared.SocketAcceptor;
import org.apache.directory.server.protocol.shared.store.LdifFileLoader;
import org.apache.directory.server.xdbm.Index;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ADAuthenticatorTest extends AbstractVMTestCase {

    private static final int PORT = new Random().nextInt(10000) + 10000;
    private static final String DOMAIN = "", CONTROLLER = "localhost:" + PORT,
            ROOT = "o=sevenSeas", FILTER = "(&(objectClass=user)(sAMAccountName=%u)";
    private DirectoryService directoryService;
    private LdapService ldapService;
    private Partition partition;

    @Before
    @Override
    public void setUp() {
        try {
            super.setUp();
            //Configure to connect to Apache AD
            try {
                //Enable use of active directory
                VMSettingServer enable = new VMSettingServer("ad.enabled");
                enable.setBoolVal(true);
                enable.write2DB();
                //Set domain
                VMSettingServer domain = new VMSettingServer("ad.domain");
                domain.setStringVal(DOMAIN);
                domain.write2DB();
                //Set controller
                VMSettingServer controller = new VMSettingServer("ad.controller");
                controller.setStringVal(CONTROLLER);
                controller.write2DB();
                //Set root
                VMSettingServer root = new VMSettingServer("ad.root");
                root.setStringVal(ROOT);
                root.write2DB();
                //Set filter
                VMSettingServer filter = new VMSettingServer("ad.filter");
                filter.setStringVal(FILTER);
                filter.write2DB();
            }
            catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            try {
                //Start Apache DS
                System.out.println("Starting Apache DS on port: " + PORT);
                String buildDirectory = System.getProperty("buildDirectory");
                File workingDirectory = new File(buildDirectory, "apacheds-work");
                workingDirectory.mkdir();

                directoryService = new DefaultDirectoryService();
                directoryService.setAllowAnonymousAccess(true);
                directoryService.setAccessControlEnabled(false);
                directoryService.setShutdownHookEnabled(false);
                directoryService.getChangeLog().setEnabled(false);
                directoryService.setWorkingDirectory(workingDirectory);

                File workdir = new File(System.getProperty("java.io.tmpdir"),
                        "apache-ds");
                FileUtils.deleteDirectory(workdir);
                System.out.println("The work directory had been purged");
                workdir.mkdirs();
                directoryService.setWorkingDirectory(workdir);

                partition = addPartition("SevenSeas", "o=sevenSeas");

                SocketAcceptor socketAcceptor = new SocketAcceptor(null);

                ldapService = new LdapService();
                ldapService.setIpPort(PORT);
                ldapService.setSocketAcceptor(socketAcceptor);
                ldapService.setDirectoryService(directoryService);

                directoryService.startup();
                ldapService.start();
                System.out.println("Done!");
                System.out.println("Importing the LDIF...");
                File data = new File("data.ldif");
                assertNotNull(data);
                assertEquals(true, data.exists());
                applyLdif(data);
                System.out.println("Done!");
            }
            catch (Exception ex) {
                Exceptions.printStackTrace(ex);
                fail();
            }
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @After
    @Override
    public void tearDown() throws Exception {
        System.out.println("Stopping Apache DS...");
        ldapService.stop();
        directoryService.shutdown();
        directoryService.getWorkingDirectory().delete();
        System.out.println("Done!");
    }

    /**
     * Test of authenticate method, of class ADAuthenticator.
     */
    @Test
    public void testAuthenticate() {
        System.out.println("authenticate");
        String user = "hhornblo";
        String pass = "secret";
        ADAuthenticator instance = new ADAuthenticator();
        Map<String, Object> r = instance.authenticate(user, pass);
        assertNotNull(r);
        for (Entry<String, Object> e : r.entrySet()) {
            System.out.println(e.getKey() + ": " + e.getValue());
        }
    }

    /**
     * Add a new partition to the server
     *
     * @param partitionId The partition Id
     * @param partitionDn The partition DN
     * @return The newly added partition
     * @throws Exception If the partition can't be added
     */
    private Partition addPartition(String partitionId, String partitionDn)
            throws Exception {
        Partition p = new JdbmPartition();
        p.setId(partitionId);
        p.setSuffix(partitionDn);
        directoryService.addPartition(p);
        p.init(directoryService);
        return p;
    }

    /**
     * Add a new set of index on the given attributes
     *
     * @param attrs The list of attributes to index
     */
    private void addIndex(String... attrs) {
        HashSet<Index<? extends Object, ServerEntry>> indexedAttributes = new HashSet<>();

        for (String attribute : attrs) {
            indexedAttributes.add(new JdbmIndex<>(attribute));
        }
        ((JdbmPartition) partition).setIndexedAttributes(indexedAttributes);
    }

    private void applyLdif(File ldifFile) throws Exception {
        new LdifFileLoader(directoryService.getAdminSession(), ldifFile, null)
                .execute();
    }
}
