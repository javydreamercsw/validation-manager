package net.sourceforge.javydreamercsw.client.ui.components.database;

import net.sourceforge.javydreamercsw.client.ui.components.project.explorer.ProjectExplorerComponent;
import com.validation.manager.core.DataBaseManager;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class DataBaseTool {

    private static EntityManagerFactory emf;
    private static final RequestProcessor RP
            = new RequestProcessor("Database Connector", 1, true);
    private static RequestProcessor.Task theTask = null;
    private static ProgressHandle ph;
    private static final Logger LOG
            = Logger.getLogger(DataBaseTool.class.getSimpleName());

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
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ph = ProgressHandleFactory.createHandle(
                        "Connecting to Database, please wait...",
                        new Cancellable() {

                            @Override
                            public boolean cancel() {
                                return handleCancel();
                            }
                        });
                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {
                        //Connect to database
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
                };
                theTask = RP.create(runnable); //the task is not started yet

                theTask.addTaskListener(new TaskListener() {
                    public void taskFinished(RequestProcessor.Task task) {
                        ph.finish();
                        LOG.log(Level.FINE,
                                "Connecting to Database done!");
                    }

                    @Override
                    public void taskFinished(org.openide.util.Task task) {
                        ph.finish();
                        LOG.log(Level.FINE,
                                "Connecting to Database done!");
                    }
                });
                //start the progresshandle the progress UI will show 500s after
                ph.start();

                //this actually start the task
                theTask.schedule(0);
            }

            private boolean handleCancel() {
                LOG.info("handleCancel");
                if (null == theTask) {
                    return false;
                }
                return theTask.cancel();
            }
        });
    }

    /**
     * @return the emf
     */
    public static EntityManagerFactory getEmf() {
        return emf;
    }
}
