package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import com.validation.manager.core.db.Project;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import net.sourceforge.javydreamercsw.client.ui.components.requirement.mapping.importer.RequirementMappingImporterTopComponent;
import org.openide.util.Utilities;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ImportRequirementMapping extends AbstractAction {

    public ImportRequirementMapping() {
        super("Import Requirement Relationship Mapping",
                new ImageIcon("com/validation/manager/resources/icons/Signage/Add Square.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /* Create and display the dialog */
        RequirementMappingImporterTopComponent component
                = new RequirementMappingImporterTopComponent();
        component.setProject(Utilities.actionsGlobalContext().lookup(Project.class));
        component.open();
        component.requestActive();
    }
}
