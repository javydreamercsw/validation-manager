package net.sourceforge.javydreamercsw.mysql.driver;

import java.net.MalformedURLException;
import java.net.URL;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.db.explorer.DatabaseException;
import org.netbeans.api.db.explorer.JDBCDriver;
import org.netbeans.api.db.explorer.JDBCDriverManager;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;

public class Installer extends ModuleInstall {

    public static final String mysqlDriverClass = "com.mysql.jdbc.Driver";
    private static final String defaultConnection = "MySQL Template";

    @Override
    public void restored() {
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
                } catch (DatabaseException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }
}
