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

    public static final String mysqlDriverClass = "com.mysql.jdbc.Driver";
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
                        LOG.fine("Registering MySQL driver!");
                        JDBCDriverManager.getDefault().addDriver(
                                JDBCDriver.create("mysql", "MySQL",
                                mysqlDriverClass,
                                new URL[]{new URL(
                            "nbinst:/modules/ext/com.validation.manager.mysql/1/mysql/mysql-connector-java.jar")}));
                    } catch (DatabaseException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (MalformedURLException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        });
    }
}
