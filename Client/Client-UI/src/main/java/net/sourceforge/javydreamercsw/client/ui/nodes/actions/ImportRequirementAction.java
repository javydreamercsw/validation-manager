package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.controller.RequirementSpecNodeJpaController;
import com.validation.manager.core.tool.requirement.importer.RequirementImportException;
import com.validation.manager.core.tool.requirement.importer.RequirementImporter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ImportRequirementAction extends AbstractAction {

    private static final long serialVersionUID = 5175273885154629542L;
    private static final RequestProcessor RP
            = new RequestProcessor("Requirement Importer", 1, true);
    private static RequestProcessor.Task theTask = null;
    private static ProgressHandle ph;
    private static final Logger LOG
            = Logger.getLogger(ImportRequirementAction.class.getSimpleName());

    public ImportRequirementAction() {
        super("Import Requirements",
                new ImageIcon("com/validation/manager/resources/icons/Signage/Add Square.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            final JFileChooser fc = new JFileChooser();
            fc.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory() || file.getName().endsWith(".xls")
                            || file.getName().endsWith(".xlsx");
                }
                
                @Override
                public String getDescription() {
                    return "Excel Files";
                }
            });
            fc.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.getName().endsWith(".csv");
                }
                
                @Override
                public String getDescription() {
                    return "Comma delimited Files";
                }
            });
            int returnVal = fc.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fc.getSelectedFile();
                    RequirementSpecNode rsns
                            = Utilities.actionsGlobalContext().lookup(RequirementSpecNode.class);
                    RequirementImporter instance = new RequirementImporter(file,
                            new RequirementSpecNodeJpaController(
                                    DataBaseManager.getEntityManagerFactory())
                                    .findRequirementSpecNode(rsns.getRequirementSpecNodePK()));
                    instance.importFile(true);
                    instance.processImport();
                } catch (UnsupportedOperationException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (RequirementImportException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (VMException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
    }
}
