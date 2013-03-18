package net.sourceforge.javydreamercsw.client.ui;

import java.net.MalformedURLException;
import java.net.URL;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseException;
import org.netbeans.api.db.explorer.JDBCDriver;
import org.netbeans.api.db.explorer.JDBCDriverManager;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;

public class Installer extends ModuleInstall {
    
    @Override
    public void restored() {
        //Check drivers
        if(JDBCDriverManager.getDefault().getDrivers("com.mysql.jdbc.Driver").length==0){
            try {
                JDBCDriverManager.getDefault().addDriver(
                        JDBCDriver.create("mysql","MySQL",
                        "com.mysql.jdbc.Driver",
                        new URL[]{new URL(
                        "jdbc:mysql://localhost:3306/dbname")}));
            } catch (DatabaseException ex) {
                Exceptions.printStackTrace(ex);
            } catch (MalformedURLException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        //Check defined database location
        if(ConnectionManager.getDefault().getConnections().length==0){
            //None there, create the default
//            DatabaseConnection.
        }
    }
}
