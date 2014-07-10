package net.sourceforge.javydreamercsw.client.ui.components.database;

import com.validation.manager.core.api.entity.manager.VMEntityManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

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

    @Override
    public void actionPerformed(ActionEvent e) {
        //Connect to database
        DataBaseTool.connect();
        //Initialize any EntityManager that needs it
        for (VMEntityManager m : Lookup.getDefault().lookupAll(VMEntityManager.class)) {
            LOG.log(Level.FINE, "{0} registered!",
                    new Object[]{m.getClass().getSimpleName(),
                        m.getEntities().size()});
        }
    }
}
