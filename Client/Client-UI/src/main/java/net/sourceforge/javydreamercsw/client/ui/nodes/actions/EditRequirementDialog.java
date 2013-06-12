package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.RequirementStatus;
import com.validation.manager.core.db.RequirementType;
import com.validation.manager.core.db.controller.RequirementStatusJpaController;
import com.validation.manager.core.db.controller.RequirementTypeJpaController;
import com.validation.manager.core.server.core.RequirementServer;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class EditRequirementDialog extends javax.swing.JDialog {

    private final List<Requirement> linkedRequirements = new ArrayList<Requirement>();
    private final boolean edit;
    private Requirement requirement;
    private static final ResourceBundle rb =
            ResourceBundle.getBundle("com.validation.manager.resources.VMMessages");

    /**
     * Creates new form EditRequirementDialog
     */
    public EditRequirementDialog(java.awt.Frame parent, boolean modal, boolean edit) {
        super(parent, modal);
        initComponents();
        setIconImage(new ImageIcon("com/validation/manager/resources/icons/VMSmall.png").getImage());
        this.edit = edit;
        //Populate lists
        //Get types
        RequirementTypeJpaController rtc =
                new RequirementTypeJpaController(DataBaseManager.getEntityManagerFactory());
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
        status.setRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                String label;
                if (index >= 0) {
                    label = ((RequirementStatus) value).getStatus();
                } else {
                    label = ((RequirementStatus) list.getSelectedValue()).getStatus();
                }
                if (rb.containsKey(label)) {
                    label = rb.getString(label);
                }
                return new JLabel(label);
            }
        });
        type.setRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                return index >= 0 ? new JLabel(
                        ((RequirementType) value).getName())
                        : new JLabel(((RequirementType) list.getSelectedValue()).getName());
            }
        });
        for (RequirementType rt : rtc.findRequirementTypeEntities()) {
            ((DefaultComboBoxModel) type.getModel()).addElement(rt);
        }
        type.setSelectedIndex(0);
        //Get statuses
        RequirementStatusJpaController rsc =
                new RequirementStatusJpaController(DataBaseManager.getEntityManagerFactory());
        for (RequirementStatus rs : rsc.findRequirementStatusEntities()) {
            ((DefaultComboBoxModel) status.getModel()).addElement(rs);
        }
        status.setSelectedIndex(0);
        if (edit) {
            //Get the selected Step
            requirement = Utilities.actionsGlobalContext().lookup(Requirement.class);
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
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        uniqueID = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        description = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        notes = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        chooseRequirements = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        requirements = new javax.swing.JList();
        save = new javax.swing.JButton();
        cancel = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        status = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        type = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(EditRequirementDialog.class, "EditRequirementDialog.jLabel1.text")); // NOI18N

        uniqueID.setText(org.openide.util.NbBundle.getMessage(EditRequirementDialog.class, "EditRequirementDialog.uniqueID.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(EditRequirementDialog.class, "EditRequirementDialog.jLabel2.text")); // NOI18N

        description.setColumns(20);
        description.setRows(5);
        jScrollPane1.setViewportView(description);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(EditRequirementDialog.class, "EditRequirementDialog.jLabel3.text")); // NOI18N

        notes.setColumns(20);
        notes.setRows(5);
        jScrollPane2.setViewportView(notes);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(EditRequirementDialog.class, "EditRequirementDialog.jLabel4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(chooseRequirements, org.openide.util.NbBundle.getMessage(EditRequirementDialog.class, "EditRequirementDialog.chooseRequirements.text")); // NOI18N
        chooseRequirements.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseRequirementsActionPerformed(evt);
            }
        });

        jScrollPane3.setViewportView(requirements);

        org.openide.awt.Mnemonics.setLocalizedText(save, org.openide.util.NbBundle.getMessage(EditRequirementDialog.class, "EditRequirementDialog.save.text")); // NOI18N
        save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cancel, org.openide.util.NbBundle.getMessage(EditRequirementDialog.class, "EditRequirementDialog.cancel.text")); // NOI18N
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(EditRequirementDialog.class, "EditRequirementDialog.jLabel5.text")); // NOI18N

        status.setAutoscrolls(true);
        status.setDoubleBuffered(true);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(EditRequirementDialog.class, "EditRequirementDialog.jLabel6.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(chooseRequirements))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                            .addComponent(jScrollPane2)
                            .addComponent(uniqueID)
                            .addComponent(status, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(type, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancel)
                    .addComponent(save))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chooseRequirementsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseRequirementsActionPerformed
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                final RequirementSelectionDialog dialog =
                        new RequirementSelectionDialog(new javax.swing.JFrame(),
                        true, linkedRequirements);
                dialog.setLocationRelativeTo(null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        dialog.dispose();
                    }
                });
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
                req = new RequirementServer(Utilities.actionsGlobalContext().lookup(Requirement.class));
                requirement.setUniqueId(uniqueID.getText().trim());
            } else {
                RequirementSpecNode rsn =
                        Utilities.actionsGlobalContext().lookup(RequirementSpecNode.class);
                req = new RequirementServer(uniqueID.getText().trim(),
                        description.getText().trim(),
                        rsn.getRequirementSpecNodePK(), notes.getText().trim(),
                        ((RequirementType) type.getSelectedItem()).getId(),
                        ((RequirementStatus) status.getSelectedItem()).getId());
            }
            try {
                req.write2DB();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            //Add linked requirements
            req.getRequirementList().clear();
            for (Iterator<Requirement> it = linkedRequirements.iterator(); it.hasNext();) {
                req.getRequirementList().add(it.next());
            }
            try {
                req.write2DB();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            dispose();
        }
    }//GEN-LAST:event_saveActionPerformed

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        dispose();
    }//GEN-LAST:event_cancelActionPerformed
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea notes;
    private javax.swing.JList requirements;
    private javax.swing.JButton save;
    private javax.swing.JComboBox status;
    private javax.swing.JComboBox type;
    private javax.swing.JTextField uniqueID;
    // End of variables declaration//GEN-END:variables
}
