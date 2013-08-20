package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import net.sourceforge.javydreamercsw.client.ui.components.requirement.edit.EditRequirementWindowTopComponent;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class EditRequirementAction extends AbstractAction {

    private final EditRequirementWindowTopComponent component = 
            new EditRequirementWindowTopComponent();

    public EditRequirementAction() {
        super("Edit Requirement",
                new ImageIcon("com/validation/manager/resources/icons/Papermart/Text-Edit.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /* Create and display the dialog */
        component.setEdit(true);
        component.open();
        component.requestActive();
    }
}
