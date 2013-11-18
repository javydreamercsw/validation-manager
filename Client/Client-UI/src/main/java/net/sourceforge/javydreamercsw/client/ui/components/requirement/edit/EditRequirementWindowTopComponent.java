/*
 * Component to edit requirements.
 */
package net.sourceforge.javydreamercsw.client.ui.components.requirement.edit;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.RequirementStatus;
import com.validation.manager.core.db.RequirementType;
import com.validation.manager.core.db.controller.RequirementStatusJpaController;
import com.validation.manager.core.db.controller.RequirementTypeJpaController;
import com.validation.manager.core.server.core.RequirementServer;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.RequirementSelectionDialog;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.OutlineView;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//net.sourceforge.javydreamercsw.client.ui.nodes.actions//EditRequirementWindow//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "EditRequirementWindowTopComponent",
        iconBase = "com/validation/manager/resources/icons/Papermart/Document.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_EditRequirementWindowAction",
        preferredID = "EditRequirementWindowTopComponent"
)
@Messages({
    "CTL_EditRequirementWindowAction=EditRequirementWindow",
    "CTL_EditRequirementWindowTopComponent=EditRequirementWindow Window",
    "HINT_EditRequirementWindowTopComponent=This is a EditRequirementWindow window"
})
public final class EditRequirementWindowTopComponent extends TopComponent
        implements LookupListener {

    private final Lookup.Result<Requirement> result
            = Utilities.actionsGlobalContext().lookupResult(Requirement.class);
    private final List<Requirement> linkedRequirements = new ArrayList<>();
    private final List<RequirementStatus> statuses = new ArrayList<>();
    private final List<RequirementType> types = new ArrayList<>();
    private boolean edit = false;
    private Requirement requirement;
    private static final Logger LOG
            = Logger.getLogger(EditRequirementWindowTopComponent.class.getSimpleName());
    private static final ResourceBundle rb
            = ResourceBundle.getBundle("com.validation.manager.resources.VMMessages");
    private final CoveringStepFactory testCaseFactory;
    private final ExplorerManager em = new ExplorerManager();

    public EditRequirementWindowTopComponent() {
        initComponents();
        result.addLookupListener((EditRequirementWindowTopComponent) this);
        setName(Bundle.CTL_EditRequirementWindowTopComponent());
        setToolTipText(Bundle.HINT_EditRequirementWindowTopComponent());
        testCaseFactory = new CoveringStepFactory();
        //TODO: Get working.
//        root = new AbstractNode(Children.create(testCaseFactory, true));
//        String[] properties = new String[]{"Test Case", "testCase",
//            "Step", "step"};
//        ((OutlineView) stepsPane).setPropertyColumns(properties);
//        ArrayList<String> columns = new ArrayList<String>();
//        columns.add("testCase");
//        columns.add("step");
//        ((OutlineView) stepsPane).getOutline().setDefaultRenderer(String.class,
//                new TableCellRenderer() {
//
//                    @Override
//                    public Component getTableCellRendererComponent(JTable table,
//                    Object value, boolean isSelected,
//                    boolean hasFocus, int row, int column) {
//                        Component component = new JLabel();
//                        if (value instanceof Boolean) {
//                            component = new JCheckBox();
//                        }
//                        return component;
//                    }
//                });
//        ((OutlineView) stepsPane).getOutline().setModel(
//                DefaultOutlineModel.createOutlineModel(
//                new TestCaseTreeModel(),
//                new TestCaseRowModel(columns),
//                true, "Test Case"));
//        getExplorerManager().setRootContext(root);
//        associateLookup(ExplorerUtils.createLookup(getExplorerManager(),
//                getActionMap()));
    }

    /**
     * @param requirement the requirement to set
     */
    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chooseRequirements = new javax.swing.JButton();
        status = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        description = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        cancel = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        uniqueID = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        requirements = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        notes = new javax.swing.JTextArea();
        type = new javax.swing.JComboBox();
        save = new javax.swing.JButton();
        stepsPane = new OutlineView();
        jLabel7 = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(chooseRequirements, org.openide.util.NbBundle.getMessage(EditRequirementWindowTopComponent.class, "EditRequirementWindowTopComponent.chooseRequirements.text")); // NOI18N
        chooseRequirements.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseRequirementsActionPerformed(evt);
            }
        });

        status.setAutoscrolls(true);
        status.setDoubleBuffered(true);

        description.setColumns(20);
        description.setRows(5);
        jScrollPane1.setViewportView(description);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(EditRequirementWindowTopComponent.class, "EditRequirementWindowTopComponent.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cancel, org.openide.util.NbBundle.getMessage(EditRequirementWindowTopComponent.class, "EditRequirementWindowTopComponent.cancel.text")); // NOI18N
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(EditRequirementWindowTopComponent.class, "EditRequirementWindowTopComponent.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(EditRequirementWindowTopComponent.class, "EditRequirementWindowTopComponent.jLabel6.text")); // NOI18N

        uniqueID.setText(org.openide.util.NbBundle.getMessage(EditRequirementWindowTopComponent.class, "EditRequirementWindowTopComponent.uniqueID.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(EditRequirementWindowTopComponent.class, "EditRequirementWindowTopComponent.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(EditRequirementWindowTopComponent.class, "EditRequirementWindowTopComponent.jLabel5.text")); // NOI18N

        jScrollPane3.setViewportView(requirements);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(EditRequirementWindowTopComponent.class, "EditRequirementWindowTopComponent.jLabel4.text")); // NOI18N

        notes.setColumns(20);
        notes.setRows(5);
        jScrollPane2.setViewportView(notes);

        org.openide.awt.Mnemonics.setLocalizedText(save, org.openide.util.NbBundle.getMessage(EditRequirementWindowTopComponent.class, "EditRequirementWindowTopComponent.save.text")); // NOI18N
        save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(EditRequirementWindowTopComponent.class, "EditRequirementWindowTopComponent.jLabel7.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(save)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cancel))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(chooseRequirements)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
                            .addComponent(jScrollPane2)
                            .addComponent(uniqueID)
                            .addComponent(status, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(type, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(stepsPane))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(37, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(uniqueID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel3))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(stepsPane, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4))
                    .addComponent(type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chooseRequirements))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancel)
                    .addComponent(save))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chooseRequirementsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseRequirementsActionPerformed
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                final RequirementSelectionDialog dialog
                        = new RequirementSelectionDialog(new javax.swing.JFrame(),
                                true, linkedRequirements);
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
                //Wait for the dialog to be finished
                while (dialog.isVisible()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                linkedRequirements.clear();
                //Clear the model to catch any removals
                ((DefaultListModel) requirements.getModel()).removeAllElements();
                //Add the ones selected on the selection dialog.
                for (Requirement req : dialog.getRequirements()) {
                    ((DefaultListModel) requirements.getModel()).addElement(req);
                }
            }
        });
    }//GEN-LAST:event_chooseRequirementsActionPerformed

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        close();
    }//GEN-LAST:event_cancelActionPerformed

    private void saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionPerformed
        if (description.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid description",
                    "Invalid Value",
                    JOptionPane.WARNING_MESSAGE);
        } else if (uniqueID.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid description",
                    "Invalid Value",
                    JOptionPane.WARNING_MESSAGE);
        } else if (type.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a valid Requirement Type",
                    "Invalid Value",
                    JOptionPane.WARNING_MESSAGE);
        } else if (status.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a valid Requirement Status",
                    "Invalid Value",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            //Process
            RequirementServer req;
            if (edit) {
                if (requirement == null) {
                    req = new RequirementServer(Utilities.actionsGlobalContext().lookup(Requirement.class));
                } else {
                    req = new RequirementServer(requirement);
                }
                requirement.setUniqueId(uniqueID.getText().trim());
            } else {
                RequirementSpecNode rsn
                        = Utilities.actionsGlobalContext().lookup(RequirementSpecNode.class);
                req = new RequirementServer(uniqueID.getText().trim(),
                        description.getText().trim(),
                        rsn.getRequirementSpecNodePK(), notes.getText().trim(),
                        ((RequirementType) type.getSelectedItem()).getId(),
                        ((RequirementStatus) status.getSelectedItem()).getId());
            }
            req.setRequirementStatusId((RequirementStatus) status.getSelectedItem());
            req.setRequirementTypeId(((RequirementType) type.getSelectedItem()));
            req.setDescription(description.getText().trim());
            req.setNotes(notes.getText().trim());
            req.setRequirementList(linkedRequirements);
            try {
                req.write2DB();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            try {
                req.write2DB();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        close();
    }//GEN-LAST:event_saveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancel;
    private javax.swing.JButton chooseRequirements;
    private javax.swing.JTextArea description;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea notes;
    private javax.swing.JList requirements;
    private javax.swing.JButton save;
    private javax.swing.JComboBox status;
    private javax.swing.JScrollPane stepsPane;
    private javax.swing.JComboBox type;
    private javax.swing.JTextField uniqueID;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        displayRequirement();
    }

    @Override
    public void componentClosed() {
        result.removeLookupListener((EditRequirementWindowTopComponent) this);
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

    private void clear() {
        linkedRequirements.clear();
        statuses.clear();
        types.clear();
    }

    private void displayRequirement() {
        clear();
        //Populate lists
        //Get types
        RequirementTypeJpaController rtc
                = new RequirementTypeJpaController(DataBaseManager.getEntityManagerFactory());
        requirements.setModel(new DefaultListModel() {
            @Override
            public void addElement(Object obj) {
                linkedRequirements.add((Requirement) obj);
                super.addElement(obj);
            }

            @Override
            public int getSize() {
                return linkedRequirements.size();
            }

            @Override
            public Object getElementAt(int i) {
                return linkedRequirements.get(i);
            }
        });
        requirements.setCellRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {

                return new JLabel(
                        ((Requirement) value).getUniqueId());
            }
        });
        requirements.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                JList l = (JList) e.getSource();
                ListModel m = l.getModel();
                int index = l.locationToIndex(e.getPoint());
                if (index > -1) {
                    l.setToolTipText(((Requirement) m.getElementAt(index)).getDescription());
                }
            }
        });
        status.setModel(new DefaultComboBoxModel<RequirementStatus>() {
            @Override
            public void addElement(RequirementStatus obj) {
                if (!statuses.contains(obj)) {
                    statuses.add(obj);
                    super.addElement(obj);
                }
            }

            @Override
            public void removeAllElements() {
                super.removeAllElements();
                statuses.clear();
            }

            @Override
            public void removeElement(Object anObject) {
                super.removeElement(anObject);
                statuses.remove((RequirementStatus) anObject);
            }

            @Override
            public void removeElementAt(int index) {
                super.removeElementAt(index);
                statuses.remove(index);
            }

            @Override
            public void insertElementAt(RequirementStatus anObject, int index) {
                super.insertElementAt(anObject, index);
                statuses.add(index, anObject);
            }

            @Override
            public RequirementStatus getElementAt(int index) {
                return statuses.get(index);
            }

            @Override
            public int getSize() {
                return statuses.size();
            }
        });
        status.setRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                String label;
                label = ((RequirementStatus) value).getStatus();
                if (rb.containsKey(label)) {
                    label = rb.getString(label);
                }
                return new JLabel(label);
            }
        });
        type.setModel(new DefaultComboBoxModel<RequirementType>() {
            @Override
            public void addElement(RequirementType obj) {
                if (!types.contains(obj)) {
                    types.add(obj);
                    super.addElement(obj);
                }
            }

            @Override
            public void removeAllElements() {
                super.removeAllElements();
                types.clear();
            }

            @Override
            public void removeElement(Object anObject) {
                super.removeElement(anObject);
                types.remove((RequirementType) anObject);
            }

            @Override
            public void removeElementAt(int index) {
                super.removeElementAt(index);
                types.remove(index);
            }

            @Override
            public void insertElementAt(RequirementType anObject, int index) {
                super.insertElementAt(anObject, index);
                types.add(index, anObject);
            }

            @Override
            public RequirementType getElementAt(int index) {
                return types.get(index);
            }

            @Override
            public int getSize() {
                return types.size();
            }
        });
        type.setRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                return new JLabel(((RequirementType) value).getName());
            }
        });
        for (RequirementType rt : rtc.findRequirementTypeEntities()) {
            ((DefaultComboBoxModel) type.getModel()).addElement(rt);
        }
        type.setSelectedIndex(0);
        //Get statuses
        RequirementStatusJpaController rsc
                = new RequirementStatusJpaController(DataBaseManager.getEntityManagerFactory());
        for (RequirementStatus rs : rsc.findRequirementStatusEntities()) {
            ((DefaultComboBoxModel) status.getModel()).addElement(rs);
        }
        status.setSelectedIndex(0);
        if (isEdit()) {
            //Get the selected Step
            setRequirement(Utilities.actionsGlobalContext().lookup(Requirement.class));
            uniqueID.setText(requirement.getUniqueId());
            //Update the linked requirements
            for (Requirement req : requirement.getRequirementList()) {
                ((DefaultListModel) requirements.getModel()).addElement(req);
            }
            //Update other fields
            if (requirement.getNotes() != null
                    && !requirement.getNotes().trim().isEmpty()) {
                notes.setText(requirement.getNotes());
            }
            if (requirement.getDescription() != null
                    && requirement.getDescription().length() > 0) {
                description.setText(requirement.getDescription());
            }
            if (requirement.getRequirementStatusId() != null) {
                status.setSelectedItem(requirement.getRequirementStatusId());
            }
            if (requirement.getRequirementTypeId() != null) {
                type.setSelectedItem(requirement.getRequirementTypeId());
            }
        }
        if (testCaseFactory != null) {
            testCaseFactory.refresh();
        }
    }

    @Override
    public void resultChanged(LookupEvent le) {
        Collection<? extends Requirement> results = result.allInstances();
        if (!results.isEmpty()) {
            displayRequirement();
        }
    }

    /**
     * @return the edit
     */
    public boolean isEdit() {
        return edit;
    }

    /**
     * @param edit the edit to set
     */
    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    private ExplorerManager getExplorerManager() {
        return em;
    }
}
