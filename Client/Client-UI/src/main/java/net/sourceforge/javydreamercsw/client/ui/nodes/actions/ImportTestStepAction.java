package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import com.dreamer.outputhandler.OutputHandler;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.controller.TestCaseJpaController;
import com.validation.manager.core.tool.requirement.importer.RequirementImportException;
import com.validation.manager.core.tool.step.StepImporter;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ImportTestStepAction extends AbstractAction {

    public ImportTestStepAction() {
        super("Import Steps",
                new ImageIcon("com/validation/manager/resources/icons/Signage/Add Square.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
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
                        TestCase tc = 
                                Utilities.actionsGlobalContext().lookup(TestCase.class);
                        StepImporter instance = new StepImporter(file,
                                new TestCaseJpaController(
                                DataBaseManager.getEntityManagerFactory())
                                .findTestCase(tc.getTestCasePK()));
                        instance.importFile(true);
                        OutputHandler.setStatus("Importing, please wait!");
                        instance.processImport();
                        OutputHandler.setStatus("Importing done! Please refresh node to see results.");
                    } catch (UnsupportedOperationException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (RequirementImportException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        });
    }
}
