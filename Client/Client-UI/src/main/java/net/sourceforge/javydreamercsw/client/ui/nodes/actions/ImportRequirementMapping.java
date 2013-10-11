/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import net.sourceforge.javydreamercsw.client.ui.components.reuirement.mapping.importer.RequirementMappingImporterTopComponent;

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
        component.open();
        component.requestActive();
    }
}
