package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.StepServer;
import com.validation.manager.core.server.core.TestCaseServer;
import java.awt.Component;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
public class EditTestStepDialog extends javax.swing.JDialog {

    private final List<Requirement> linkedRequirements = new ArrayList<Requirement>();
    private final boolean edit;
    private Step step;

    /**
     * Creates new form EditTestStepDialog
     */
    public EditTestStepDialog(java.awt.Frame parent, boolean modal, boolean edit) {
        super(parent, modal);
        this.edit = edit;
        initComponents();
        setIconImage(new ImageIcon("com/validation/manager/resources/icons/VMSmall.png").getImage());
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
                        ((Requirement) ((DefaultListModel) requirements.getModel())
                        .getElementAt(index)).getUniqueId());
            }
        });
        if (edit) {
            //Get the selected Step
            step = Utilities.actionsGlobalContext().lookup(Step.class);
            //Update the linked requirements
            for (Requirement req : step.getRequirementList()) {
                ((DefaultListModel) requirements.getModel()).addElement(req);
            }
            //Update other fields
            if (step.getNotes() != null && !step.getNotes().trim().isEmpty()) {
                notes.setText(step.getNotes());
            }
            if (step.getText() != null && step.getText().length > 0) {
                text.setText(new String(step.getText()));
            }
            if (step.getExpectedResult() != null
                    && step.getExpectedResult().length > 0) {
                result.setText(new String(step.getExpectedResult()));
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
        jScrollPane1 = new javax.swing.JScrollPane();
        text = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        result = new javax.swing.JTextArea();
        save = new javax.swing.JButton();
        cancel = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        requirements = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        notes = new javax.swing.JTextArea();
        chooseRequirements = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(EditTestStepDialog.class, "EditTestStepDialog.jLabel1.text")); // NOI18N

        text.setColumns(20);
        text.setRows(5);
        jScrollPane1.setViewportView(text);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(EditTestStepDialog.class, "EditTestStepDialog.jLabel2.text")); // NOI18N

        result.setColumns(20);
        result.setRows(5);
        jScrollPane2.setViewportView(result);

        org.openide.awt.Mnemonics.setLocalizedText(save, org.openide.util.NbBundle.getMessage(EditTestStepDialog.class, "EditTestStepDialog.save.text")); // NOI18N
        save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cancel, org.openide.util.NbBundle.getMessage(EditTestStepDialog.class, "EditTestStepDialog.cancel.text")); // NOI18N
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        requirements.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(requirements);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(EditTestStepDialog.class, "EditTestStepDialog.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(EditTestStepDialog.class, "EditTestStepDialog.jLabel4.text")); // NOI18N

        notes.setColumns(20);
        notes.setRows(5);
        jScrollPane4.setViewportView(notes);

        org.openide.awt.Mnemonics.setLocalizedText(chooseRequirements, org.openide.util.NbBundle.getMessage(EditTestStepDialog.class, "EditTestStepDialog.chooseRequirements.text")); // NOI18N
        chooseRequirements.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseRequirementsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(save)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(chooseRequirements, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cancel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chooseRequirements)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancel)
                    .addComponent(save))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        dispose();
    }//GEN-LAST:event_cancelActionPerformed

    private void saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionPerformed
        if (result.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid Expected Result",
                    "Invalid Value",
                    JOptionPane.WARNING_MESSAGE);
        } else if (text.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid Instruction",
                    "Invalid Value",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            //Process
            StepServer ss;
            TestCase tc;
            if (edit) {
                ss = new StepServer(Utilities.actionsGlobalContext().lookup(Step.class));
            } else {
                tc = Utilities.actionsGlobalContext().lookup(TestCase.class);
                TestCaseServer tcs = new TestCaseServer(tc.getTestCasePK());
                ss = new StepServer(tc, tcs.getStepList().size() + 1, //Add at the end by default
                        text.getText().trim());
            }
            try {
                if (!result.getText().trim().isEmpty()) {
                    ss.setExpectedResult(result.getText().getBytes("UTF-8"));
                }
            } catch (UnsupportedEncodingException ex) {
                Exceptions.printStackTrace(ex);
            }
            try {
                ss.write2DB();
            } catch (NonexistentEntityException ex) {
                Exceptions.printStackTrace(ex);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            //Add linked requirements
            ss.getRequirementList().clear();
            for (Iterator<Requirement> it = linkedRequirements.iterator(); it.hasNext();) {
                ss.getRequirementList().add(it.next());
            }
            try {
                ss.write2DB();
            } catch (NonexistentEntityException ex) {
                Exceptions.printStackTrace(ex);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            dispose();
        }
    }//GEN-LAST:event_saveActionPerformed

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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancel;
    private javax.swing.JButton chooseRequirements;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea notes;
    private javax.swing.JList requirements;
    private javax.swing.JTextArea result;
    private javax.swing.JButton save;
    private javax.swing.JTextArea text;
    // End of variables declaration//GEN-END:variables
}