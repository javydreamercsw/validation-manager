package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.TestProjectServer;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import net.sourceforge.javydreamercsw.client.ui.ProjectExplorerComponent;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class CreateTestProjectDialog extends AbstractCreationDialog {

    /**
     * Creates new form CreateTestProjectDialog
     */
    public CreateTestProjectDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        populateProjectList(project, false);
        setIconImage(new ImageIcon("com/validation/manager/resources/icons/VMSmall.png").getImage());
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
        name = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        notes = new javax.swing.JTextArea();
        save = new javax.swing.JButton();
        cancel = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        project = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CreateTestProjectDialog.class, "CreateTestProjectDialog.jLabel1.text")); // NOI18N

        name.setText(org.openide.util.NbBundle.getMessage(CreateTestProjectDialog.class, "CreateTestProjectDialog.name.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CreateTestProjectDialog.class, "CreateTestProjectDialog.jLabel2.text")); // NOI18N

        notes.setColumns(20);
        notes.setRows(5);
        jScrollPane1.setViewportView(notes);

        org.openide.awt.Mnemonics.setLocalizedText(save, org.openide.util.NbBundle.getMessage(CreateTestProjectDialog.class, "CreateTestProjectDialog.save.text")); // NOI18N
        save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cancel, org.openide.util.NbBundle.getMessage(CreateTestProjectDialog.class, "CreateTestProjectDialog.cancel.text")); // NOI18N
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(CreateTestProjectDialog.class, "CreateTestProjectDialog.jLabel3.text")); // NOI18N

        project.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(name))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(save)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cancel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(project, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(project, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(save)
                    .addComponent(cancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionPerformed
        if (name.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid project name",
                    "Invalid Value",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            TestProjectServer tps = new TestProjectServer(name.getText().trim(), true);
            Project selectedProject = getSelectedProject(project);
            if (!notes.getText().isEmpty()) {
                tps.setNotes(notes.getText());
            }
            try {
                tps.write2DB();
                if (selectedProject != null) {
                    ProjectServer ps = new ProjectServer(selectedProject);
                    ps.getTestProjectList().add(tps.getEntity());
                    ps.write2DB();
                }
            } catch (IllegalOrphanException ex) {
                Exceptions.printStackTrace(ex);
            } catch (NonexistentEntityException ex) {
                Exceptions.printStackTrace(ex);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            dispose();
            ProjectExplorerComponent.refresh();
        }
    }//GEN-LAST:event_saveActionPerformed

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        dispose();
    }//GEN-LAST:event_cancelActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField name;
    private javax.swing.JTextArea notes;
    private javax.swing.JComboBox project;
    private javax.swing.JButton save;
    // End of variables declaration//GEN-END:variables
}
