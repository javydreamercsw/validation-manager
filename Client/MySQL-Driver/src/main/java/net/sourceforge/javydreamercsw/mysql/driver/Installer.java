package net.sourceforge.javydreamercsw.mysql.driver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import org.netbeans.api.db.explorer.DatabaseException;
import org.netbeans.api.db.explorer.JDBCDriver;
import org.netbeans.api.db.explorer.JDBCDriverManager;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall {

    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final Logger LOG
            = Logger.getLogger(Installer.class.getCanonicalName());

    @Override
    public void restored() {
        WindowManager.getDefault().invokeWhenUIReady(() -> {
            //Check drivers
            if (JDBCDriverManager.getDefault().getDrivers(DRIVER).length == 0) {
                try {
                    LOG.fine("Registering MySQL driver!");
                    JDBCDriverManager.getDefault().addDriver(
                            JDBCDriver.create("mysql", "MySQL",
                                    DRIVER,
                                    new URL[]{new URL(
                                                "nbinst:/modules/ext/com.validation.manager.mysql/1/mysql/mysql-connector-java.jar")}));
                } catch (DatabaseException | MalformedURLException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
    }
}
