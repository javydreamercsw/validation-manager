package net.sourceforge.javydreamercsw.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import net.sourceforge.javydreamercsw.client.ui.nodes.RootNode;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Tools",
        id = "net.sourceforge.javydreamercsw.client.ui.ConnectAction")
@ActionRegistration(
        iconBase = "net/sourceforge/javydreamercsw/client/ui/database-connect-icon.png",
        displayName = "#CTL_ConnectAction")
@ActionReferences({
    @ActionReference(path = "Toolbars/Actions", position = 300),
    @ActionReference(path = "Shortcuts", name = "D-C")
})
@Messages("CTL_ConnectAction=Connect")
public final class ConnectAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        //Connect to database
        DataBaseTool.connect();
        RootNode.refresh();
    }
}
