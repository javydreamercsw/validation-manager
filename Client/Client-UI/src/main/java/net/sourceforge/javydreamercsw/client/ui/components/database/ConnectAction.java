package net.sourceforge.javydreamercsw.client.ui.components.database;

import com.validation.manager.core.api.entity.manager.VMEntityManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Cancellable;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;

@ActionID(
        category = "Tools",
        id = "net.sourceforge.javydreamercsw.client.ui.ConnectAction")
@ActionRegistration(
        iconBase = "net/sourceforge/javydreamercsw/client/ui/database/database-connect-icon.png",
        displayName = "#CTL_ConnectAction")
@ActionReferences({
    @ActionReference(path = "Toolbars/Actions", position = 300),
    @ActionReference(path = "Shortcuts", name = "D-C")
})
@Messages("CTL_ConnectAction=Connect")
public final class ConnectAction implements ActionListener {

    private static final Logger LOG
            = Logger.getLogger(ConnectAction.class.getSimpleName());
    private final RequestProcessor RP
            = new RequestProcessor("Database Connector", 1, true);
    private RequestProcessor.Task theTask = null;
    private ProgressHandle ph;

    @Override
    public void actionPerformed(ActionEvent e) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ph = ProgressHandleFactory.createHandle("Database Connector",
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
                        DataBaseTool.connect();
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
        //Initialize any EntityManager that needs it
        for (VMEntityManager m : Lookup.getDefault().lookupAll(VMEntityManager.class)) {
            LOG.log(Level.FINE, "{0} registered!",
                    new Object[]{m.getClass().getSimpleName(),
                        m.getEntities().size()});
        }
    }
}
