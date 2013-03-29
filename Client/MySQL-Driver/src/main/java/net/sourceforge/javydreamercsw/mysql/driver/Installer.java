package net.sourceforge.javydreamercsw.mysql.driver;

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

    public static final String mysqlDriverClass = "com.mysql.jdbc.Driver";
    private static final String defaultConnection = "MySQL Template";
    private static final Logger LOG =
            Logger.getLogger(Installer.class.getCanonicalName());

    @Override
    public void restored() {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                //Check drivers
                if (JDBCDriverManager.getDefault().getDrivers(mysqlDriverClass).length == 0) {
                    try {
                        JDBCDriverManager.getDefault().addDriver(
                                JDBCDriver.create("mysql", "MySQL",
                                mysqlDriverClass,
                                new URL[]{new URL(
                            "nbinst:/modules/ext/mysql/mysql-connector-java-5.1.23.jar")}));
                    } catch (DatabaseException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (MalformedURLException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                //Check defined database location
                if (ConnectionManager.getDefault().getConnection(defaultConnection) == null) {
                    //None there, create the default
                    if (JDBCDriverManager.getDefault().getDrivers(mysqlDriverClass).length > 0) {
                        try {
                            ConnectionManager.getDefault().addConnection(DatabaseConnection.create(
                                    JDBCDriverManager.getDefault().getDrivers(mysqlDriverClass)[0],
                                    "jdbc:mysql://localhost:3306/validation_manager",
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
