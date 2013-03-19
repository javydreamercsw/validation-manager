package net.sourceforge.javydreamercsw.client.ui;

import com.validation.manager.core.DataBaseManager;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class DataBaseTool {

    private static EntityManagerFactory emf;

    public static void connect() {
        if (emf == null) {
            final DatabaseSelection dialog = new DatabaseSelection(new javax.swing.JFrame(), true);
            if (MainTopComponent.getConnection() == null) {
                dialog.setVisible(true);
            }
            while (dialog.isVisible()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            DatabaseConnection conn = MainTopComponent.getConnection();
            Map addedOrOverridenProperties = new HashMap();
            addedOrOverridenProperties.put("javax.persistence.jdbc.url",
                    conn.getDatabaseURL());
            addedOrOverridenProperties.put("javax.persistence.jdbc.password",
                    conn.getPassword());
            addedOrOverridenProperties.put("javax.persistence.jdbc.driver",
                    conn.getDriverClass());
            addedOrOverridenProperties.put("javax.persistence.jdbc.user",
                    conn.getUser());
            emf = Persistence.createEntityManagerFactory(
                    DataBaseManager.getPersistenceUnitName(),
                    addedOrOverridenProperties);
            DataBaseManager.setEntityManagerFactory(emf);
        }
    }
}
