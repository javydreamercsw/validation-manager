package net.sourceforge.javydreamercsw.client.ui.components.database;

import net.sourceforge.javydreamercsw.client.ui.components.project.explorer.ProjectExplorerComponent;
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
        final DatabaseSelection dialog
                = new DatabaseSelection(new javax.swing.JFrame(), true);
        dialog.setVisible(true);
        while (dialog.isVisible()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        DatabaseConnection conn = ProjectExplorerComponent.getConnection();
        if (conn != null) {
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
            DataBaseManager.setEntityManagerFactory(getEmf());
            ProjectExplorerComponent.refresh();
        }
    }

    /**
     * @return the emf
     */
    public static EntityManagerFactory getEmf() {
        return emf;
    }
}
