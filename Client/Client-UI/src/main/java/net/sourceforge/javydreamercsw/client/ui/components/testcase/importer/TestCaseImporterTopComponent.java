package net.sourceforge.javydreamercsw.client.ui.components.testcase.importer;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.TestCaseServer;
import com.validation.manager.core.tool.message.MessageHandler;
import com.validation.manager.core.tool.table.extractor.TableExtractor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileFilter;
import net.sourceforge.javydreamercsw.client.ui.components.AbstractImportTopComponent;
import net.sourceforge.javydreamercsw.client.ui.components.ImportMappingInterface;
import static net.sourceforge.javydreamercsw.client.ui.components.testcase.importer.Bundle.CTL_TestCaseImporterTopComponent;
import static net.sourceforge.javydreamercsw.client.ui.components.testcase.importer.Bundle.HINT_TestCaseImporterTopComponent;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.EditTestCaseDialog;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.TaskListener;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//net.sourceforge.javydreamercsw.client.ui.components.testcase.importer//TestCaseImporter//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "TestCaseImporterTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_TestCaseImporterAction",
        preferredID = "TestCaseImporterTopComponent"
)
@Messages({
    "CTL_TestCaseImporterAction=Test Case Importer",
    "CTL_TestCaseImporterTopComponent=Test Case Importer Window",
    "HINT_TestCaseImporterTopComponent=This is a Test Case Importer window",
    "TestCaseImporterTopComponent.jLabel1.text=Table:",
    "TestCaseImporterTopComponent.addDelimiterButton.text=Add Delimiter",
    "TestCaseImporterTopComponent.delimiterField.text=",
    "TestCaseImporterTopComponent.jLabel2.text=Requirement Delimiter",
    "TestCaseImporterTopComponent.saveButton.text=Save",
    "TestCaseImporterTopComponent.header.text=Data has Header?",
    "TestCaseImporterTopComponent.importButton.text=Import"
})
public class TestCaseImporterTopComponent extends AbstractImportTopComponent {

    private static final Logger LOG
            = Logger.getLogger(TestCaseImporterTopComponent.class.getSimpleName());
    private static final long serialVersionUID = -7506655107681422195L;
    protected TestCase tc;
    protected TestPlan tp;

    public TestCaseImporterTopComponent() {
        super();
        setName(CTL_TestCaseImporterTopComponent());
        setToolTipText(HINT_TestCaseImporterTopComponent());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        spinner = new javax.swing.JSpinner();
        jScrollPane1 = new javax.swing.JScrollPane();
        importedTable = new javax.swing.JTable();
        importButton = new javax.swing.JButton();
        header = new javax.swing.JCheckBox();
        saveButton = new javax.swing.JButton();
        delimiter = new JComboBox(getModel());
        jLabel2 = new javax.swing.JLabel();
        delimiterField = new javax.swing.JTextField();
        addDelimiterButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(TestCaseImporterTopComponent.class, "TestCaseImporterTopComponent.jLabel1.text")); // NOI18N

        spinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 10, 1));
        spinner.setEnabled(false);

        importedTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(importedTable);

        org.openide.awt.Mnemonics.setLocalizedText(importButton, org.openide.util.NbBundle.getMessage(TestCaseImporterTopComponent.class, "TestCaseImporterTopComponent.importButton.text")); // NOI18N
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(header, org.openide.util.NbBundle.getMessage(TestCaseImporterTopComponent.class, "TestCaseImporterTopComponent.header.text")); // NOI18N
        header.setEnabled(false);
        header.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                headerActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(saveButton, org.openide.util.NbBundle.getMessage(TestCaseImporterTopComponent.class, "TestCaseImporterTopComponent.saveButton.text")); // NOI18N
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        delimiter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { ",", ";", " " }));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(TestCaseImporterTopComponent.class, "TestCaseImporterTopComponent.jLabel2.text")); // NOI18N

        delimiterField.setText(org.openide.util.NbBundle.getMessage(TestCaseImporterTopComponent.class, "TestCaseImporterTopComponent.delimiterField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addDelimiterButton, org.openide.util.NbBundle.getMessage(TestCaseImporterTopComponent.class, "TestCaseImporterTopComponent.addDelimiterButton.text")); // NOI18N
        addDelimiterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDelimiterButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(2, 2, 2)
                        .addComponent(spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(header))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(importButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(addDelimiterButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(delimiterField, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(saveButton, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addGap(3, 3, 3)
                                    .addComponent(delimiter, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(delimiterField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addDelimiterButton))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(delimiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(importButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(header))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveButton))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed
        ImportAction importAction = new ImportAction();
        importAction.actionPerformed(evt);
    }//GEN-LAST:event_importButtonActionPerformed

    private void headerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_headerActionPerformed
        displayTable((int) Math.round(Double.valueOf(spinner.getValue().toString())));
    }//GEN-LAST:event_headerActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        save();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void addDelimiterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDelimiterButtonActionPerformed
        getModel().addElement(delimiterField.getText().trim());
    }//GEN-LAST:event_addDelimiterButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addDelimiterButton;
    private javax.swing.JComboBox delimiter;
    private javax.swing.JTextField delimiterField;
    private javax.swing.JCheckBox header;
    private javax.swing.JButton importButton;
    private javax.swing.JTable importedTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton saveButton;
    private javax.swing.JSpinner spinner;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    /**
     * @param tp the TestPlan to set
     */
    public void setTestPlan(TestPlan tp) {
        this.tp = tp;
    }

    @Override
    public void componentClosed() {
        enableUI(false);
        tables.clear();
        tc = null;
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public void init() {
        initComponents();
    }

    @Override
    public DefaultCellEditor getEditor() {
        return new TestCaseImportEditor();
    }

    @Override
    public JTextField getDelimetterField() {
        return delimiterField;
    }

    @Override
    public JCheckBox getHeaderCheckbox() {
        return header;
    }

    @Override
    public JScrollPane getScrollPane() {
        return jScrollPane1;
    }

    @Override
    public void setImportTable(JTable table) {
        importedTable = table;
    }

    @Override
    public JTable getImportTable() {
        return importedTable;
    }

    @Override
    public JSpinner getSpinner() {
        return spinner;
    }

    @Override
    public JComboBox getDelimiter() {
        return delimiter;
    }

    @Override
    public void setModel(DefaultComboBoxModel model) {
        this.model = model;
    }

    @Override
    public DefaultComboBoxModel getModel() {
        return model;
    }

    @Override
    public JButton getSaveButton() {
        return saveButton;
    }

    /**
     * Recursive method to find root project from the current project.
     *
     * @param p Project to start searching from.
     * @return Root project
     */
    private Project getRootProject(Project p) {
        if (p.getParentProjectId() == null) {
            //No parents, we found it!
            return p;
        } else {
            //Still has parents, keep searching up in the tree
            return getRootProject(p.getParentProjectId());
        }
    }

    private void getSubProjects(Project root, List<Project> toAdd) {
        if (!toAdd.contains(root)) {
            //Add root project as well, if not already in the list.
            toAdd.add(root);
        }
        //Add child projects
        root.getProjectList().stream().filter((sub)
                -> (!toAdd.contains(sub))).map((sub) -> {
            toAdd.add(sub);
            return sub;
        }).forEachOrdered((sub) -> {
            getSubProjects(sub, toAdd);
        });
    }

    private final class ImportAction implements ActionListener {

        private final RequestProcessor RP
                = new RequestProcessor("Document Importer", 1, true);
        private RequestProcessor.Task theTask = null;
        private ProgressHandle ph;
        private boolean valid = false;

        @Override
        public void actionPerformed(ActionEvent e) {
            ph = ProgressHandleFactory.createHandle("Test Case Document Importer",
                    new Cancellable() {

                @Override
                public boolean cancel() {
                    return handleCancel();
                }
            });
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    JFileChooser fc = new JFileChooser();
                    fc.setFileFilter(new FileFilter() {

                        @Override
                        public boolean accept(File f) {
                            return f.isDirectory()
                                    || (f.isFile()
                                    && (f.getName().endsWith(".xls")
                                    || f.getName().endsWith(".xlsx")
                                    || f.getName().endsWith(".xlsm")
                                    || f.getName().endsWith(".doc")
                                    || f.getName().endsWith(".docx")));
                        }

                        @Override
                        public String getDescription() {
                            return "Validation Manager Test Import Files";
                        }
                    });
                    int returnVal = fc.showOpenDialog(new JFrame());
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        TableExtractor te = new TableExtractor(file);
                        try {
                            tables.clear();
                            tables.addAll(te.extractTables());
                            if (tables.size() > 0) {
                                double max = Double.valueOf("" + tables.size());
                                spinner.setModel(new SpinnerNumberModel(1.0, 1.0,
                                        max, 1.0));
                                spinner.setValue(1.0);
                                LOG.log(Level.FINE, "Loaded {0} tables!",
                                        tables.size());
                                setValid(true);
                                displayTable(1);
                            } else {
                                LOG.log(Level.FINE, "Found no tables!");
                            }
                            tables.forEach((dtm) -> {
                                int columns = dtm.getColumnCount();
                                Object[] mappingRow = new Object[columns];
                                for (int i = 0; i < columns; i++) {
                                    //Mapping row
                                    mappingRow[i] = "Select Mapping";
                                }
                                //Insert mapping row
                                dtm.insertRow(0, mappingRow);
                            });
                        } catch (FileNotFoundException ex) {
                            Exceptions.printStackTrace(ex);
                        } catch (ClassNotFoundException | IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                    enableUI(isValid());
                }
            };
            theTask = RP.create(runnable); //the task is not started yet

            theTask.addTaskListener(new TaskListener() {
                public void taskFinished(Task task) {
                    ph.finish();
                }

                @Override
                public void taskFinished(org.openide.util.Task task) {
                    ph.finish();
                    if (isValid()) {
                        //TODO: internationalize
                        Lookup.getDefault().lookup(MessageHandler.class)
                                .plain("Document import completed succesfully!");
                    } else {
                        Lookup.getDefault().lookup(MessageHandler.class)
                                .error("Errors during document import!");
                    }
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

        /**
         * @return the valid
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * @param valid the valid to set
         */
        public void setValid(boolean valid) {
            this.valid = valid;
        }
    }

    private final class SaveAction implements ActionListener {

        private final RequestProcessor RP
                = new RequestProcessor("Test Case Importer", 1, true);
        private RequestProcessor.Task theTask = null;
        private ProgressHandle ph;

        @Override
        public void actionPerformed(ActionEvent e) {
            enableUI(false);
            ph = ProgressHandleFactory.createHandle("Test Case Importer",
                    new Cancellable() {

                @Override
                public boolean cancel() {
                    return handleCancel();
                }
            });
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    LOG.info("Saving imported table...");
                    setImportSuccess(true);
                    List<String> mapping
                            = TestCaseImporterTopComponent.this.checkMappings();
                    //Create the test case to import into
                    /* Create and display the dialog */
                    if (isImportSuccess()) {
                        setDialog(new EditTestCaseDialog(new javax.swing.JFrame(),
                                true, false));
                        getDialog().setLocationRelativeTo(null);
                        ((EditTestCaseDialog) getDialog()).setTestCase(tc);
                        getDialog().addWindowListener(new java.awt.event.WindowAdapter() {
                            @Override
                            public void windowClosing(java.awt.event.WindowEvent e) {
                                getDialog().dispose();
                            }
                        });
                        getDialog().setVisible(true);
                        while (getDialog().isVisible()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                        tc = ((EditTestCaseDialog) getDialog()).getTestCase();
                        if (tc == null) {
                            showImportError("Test Case Creation unsuccessful!");
                            setImportSuccess(false);
                        }
                    }
                    //start the progresshandle the progress UI will show 500s after
                    ph.start(); //we must start the PH before we swith to determinate
                    int items = importedTable.getModel().getRowCount()
                            - (header.isSelected() ? 1 : 0) - 1;
                    LOG.log(Level.FINE, "Items to import: {0}", items);
                    ph.switchToDeterminate(items);
                    if (isImportSuccess()) {
                        setImportSuccess(false);
                        process(mapping);
                    }
                }
            };
            theTask = RP.create(runnable); //the task is not started yet

            theTask.addTaskListener(new TaskListener() {
                public void taskFinished(Task task) {
                    ph.finish();
                }

                @Override
                public void taskFinished(org.openide.util.Task task) {
                    if (isImportSuccess()) {
                        //TODO: internationalize
                        Lookup.getDefault().lookup(MessageHandler.class)
                                .plain("Import completed succesfully!");
                    } else {
                        Lookup.getDefault().lookup(MessageHandler.class)
                                .error("Errors during import!");
                    }
                    ph.finish();
                    enableUI(true);
                }
            });
            //this actually start the task
            theTask.schedule(0);
        }

        private boolean handleCancel() {
            LOG.info("handleCancel");
            if (null == theTask) {
                return false;
            }
            ph.finish();
            return theTask.cancel();
        }

        private void process(List<String> mapping) {
            List<Project> projects = new ArrayList<>();
            //Add projects that has this test on their plans
            ProjectServer.getProjects().forEach((p) -> {
                p.getTestProjectList().stream().filter((temp)
                        -> (Objects.equals(temp.getId(),
                                tp.getTestProject().getId()))).map((_item) -> {
                    LOG.log(Level.FINE, "Project ID: {0}", p.getId());
                    return _item;
                }).filter((_item) -> (!projects.contains(p))).map((_item) -> {
                    projects.add(p);
                    return _item;
                }).forEachOrdered((_item) -> {
                    getSubProjects(getRootProject(p), projects);
                });
            });
            TestCaseServer tcs = new TestCaseServer(tc);
            //We got the created test, now let's import the rest.
            //Start on second row as first one is the mapping row.
            //Start on third row if there are headers in the data
            int step_counter = 0;
            int start = 1 + (header.isSelected() ? 1 : 0);
            for (int row = start; row < importedTable.getModel().getRowCount(); row++) {
                int progress = row - start + 1;
                LOG.log(Level.FINE, "Processing row: {0}", progress);
                List<Requirement> requirements = new ArrayList<>();
                String description = "", criteria = "", notes = "";
                for (int col = 0; col < importedTable.getModel().getColumnCount(); col++) {
                    if (!mapping.get(col).equals(
                            TestCaseImportMapping.IGNORE.getValue())) {
                        //Column is to be imported
                        if (mapping.get(col).equals(
                                TestCaseImportMapping.DESCRIPTION.getValue())) {
                            description = (String) importedTable.getModel().getValueAt(row, col);
                            LOG.log(Level.FINE, "Description: {0}", description);
                        } else if (mapping.get(col).equals(
                                TestCaseImportMapping.NOTES.getValue())) {
                            notes = (String) importedTable.getModel().getValueAt(row, col);
                            LOG.log(Level.FINE, "Notes: {0}", notes);
                        } else if (mapping.get(col).equals(
                                TestCaseImportMapping.ACCEPTANCE_CRITERIA.getValue())) {
                            criteria = (String) importedTable.getModel().getValueAt(row, col);
                            LOG.log(Level.FINE, "Criteria: {0}", criteria);
                        } else if (mapping.get(col).equals(
                                TestCaseImportMapping.REQUIREMENT.getValue())) {
                            //Process requirements
                            String reqs = (String) importedTable.getModel().getValueAt(row, col);
                            if (reqs != null) {
                                StringTokenizer st = new StringTokenizer(reqs,
                                        delimiter.getSelectedItem().toString());
                                while (st.hasMoreTokens()) {
                                    String token = st.nextToken().trim();
                                    LOG.log(Level.FINE, "Requirement: {0}", token);
                                    boolean found = false;
                                    for (Project p : projects) {
                                        LOG.log(Level.FINE,
                                                "Looking on project: {0}", p.getName());
                                        for (Requirement r : ProjectServer.getRequirements(p)) {
                                            if (r.getUniqueId().trim().equals(token.trim())) {
                                                requirements.add(r);
                                                found = true;
                                                LOG.log(Level.FINE, "Found it!");
                                                break;
                                            }
                                        }
                                    }
                                    if (!found) {
                                        if (!token.trim().toLowerCase().equals("n/a")) {
                                            LOG.log(Level.WARNING,
                                                    "Unable to find requirement: "
                                                    + "{0} for step {1}",
                                                    new Object[]{token.trim(),
                                                        row + 1});
                                        }
                                    }
                                }
                            }
                        } else {
                            throw new RuntimeException(MessageFormat.format(
                                    "Unhandled mapping: {0}", mapping.get(col)));
                        }
                    }
                }
                try {
                    step_counter++;
                    tcs.addStep(step_counter, description, notes, criteria,
                            requirements);
                } catch (NonexistentEntityException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
                ph.progress(progress);
            }
            setImportSuccess(true);
        }
    }

    protected void save() {
        SaveAction saveAction = new SaveAction();
        saveAction.actionPerformed(null);
    }

    @Override
    public ImportMappingInterface getMapping() {
        return TestCaseImportMapping.IGNORE;
    }
}
