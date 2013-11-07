package net.sourceforge.javydreamercsw.h2.driver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.db.explorer.DatabaseException;
import org.netbeans.api.db.explorer.JDBCDriver;
import org.netbeans.api.db.explorer.JDBCDriverManager;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall {

    public static final String h2DriverClass = "org.h2.Driver";
    private static final String defaultConnection = "Default Validation Manager";
    private static final Logger LOG =
            Logger.getLogger(Installer.class.getCanonicalName());

    @Override
    public void restored() {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                //Check drivers
                if (JDBCDriverManager.getDefault().getDrivers(h2DriverClass).length == 0) {
                    try {
                        JDBCDriverManager.getDefault().addDriver(
                                JDBCDriver.create("h2", "H2",
                                h2DriverClass,
                                new URL[]{new URL(
                            "nbinst:/modules/ext/com.h2database/h2-1.3.171.jar")}));
                    } catch (DatabaseException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (MalformedURLException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                //Check defined database location
                if (ConnectionManager.getDefault().getConnection(defaultConnection) == null) {
                    //None there, create the default
                    if (JDBCDriverManager.getDefault().getDrivers(h2DriverClass).length > 0) {
                        try {
                            ConnectionManager.getDefault().addConnection(DatabaseConnection.create(
                                    JDBCDriverManager.getDefault().getDrivers(h2DriverClass)[0],
                                    "jdbc:h2:file:~/VM/data/validation-manager",
                                    "user", "", "password", false,
                                    defaultConnection));
                        } catch (Exception ex) {
                            LOG.log(Level.WARNING, "Error adding connection: "
                                    + defaultConnection, ex);
                        }
                    }
                }
            }
        });
    }
}
