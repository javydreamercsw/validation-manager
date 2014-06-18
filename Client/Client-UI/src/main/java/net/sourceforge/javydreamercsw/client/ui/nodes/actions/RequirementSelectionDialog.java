package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.controller.RequirementSpecJpaController;
import com.validation.manager.core.server.core.RequirementServer;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.netbeans.api.annotations.common.SuppressWarnings;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementSelectionDialog extends javax.swing.JDialog {

    private List<Requirement> requirements = new ArrayList<>();
    private final DefaultMutableTreeNode top
            = new DefaultMutableTreeNode("Available Requirements");

    /**
     * Creates new form RequirementSelectionDialog
     */
    public RequirementSelectionDialog(java.awt.Frame parent, boolean modal,
            List<Requirement> initial) {
        super(parent, modal);
        //Make sure to remove multiple versions of requirements.
        List<String> processed = new ArrayList<>();
        List<Requirement> finalList = new ArrayList<>();
        for (Requirement r : initial) {
            if (!processed.contains(r.getUniqueId().trim())) {
                RequirementServer rs = new RequirementServer(r);
                finalList.add(Collections.max(rs.getVersions(), null));
                processed.add(rs.getUniqueId().trim());
            }
        }
        initComponents();
        setIconImage(new ImageIcon("com/validation/manager/resources/icons/VMSmall.png").getImage());
        source.setCellRenderer(new InternalRenderer());
        ToolTipManager.sharedInstance().registerComponent(source);
        source.getSelectionModel().setSelectionMode(
                TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        populateNodes();
        selection.setModel(new DefaultListModel() {
            @Override
            public void addElement(Object obj) {
                requirements.add((Requirement) obj);
                super.addElement(obj);
            }

            @Override
            public int getSize() {
                return requirements.size();
            }

            @Override
            public Object getElementAt(int i) {
                return requirements.get(i);
            }

            @Override
            public void removeElementAt(int index) {
                requirements.remove(index);
                super.removeElementAt(index);
            }

            @Override
            public boolean removeElement(Object obj) {
                requirements.remove((Requirement) obj);
                return super.removeElement(obj);
            }

            @Override
            public void removeRange(int fromIndex, int toIndex) {
                for (int i = fromIndex; i <= toIndex; i++) {
                    requirements.remove(i);
                }
                super.removeRange(fromIndex, toIndex);
            }
        });
        selection.setCellRenderer(new SelectedListCellRenderer());
        selection.addMouseMotionListener(new MouseMotionAdapter() {
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
        for (Requirement requirement : finalList) {
            ((DefaultListModel) selection.getModel()).addElement(requirement);
        }
    }

    public class SelectedListCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list,
                Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list,
                    ((Requirement) ((DefaultListModel) selection.getModel()).getElementAt(index)).getUniqueId(),
                    index, isSelected, cellHasFocus);
            return c;
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

        ok = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        source = new JTree(top);
        jScrollPane4 = new javax.swing.JScrollPane();
        selection = new javax.swing.JList();
        add = new javax.swing.JButton();
        remove = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.openide.awt.Mnemonics.setLocalizedText(ok, org.openide.util.NbBundle.getMessage(RequirementSelectionDialog.class, "RequirementSelectionDialog.ok.text")); // NOI18N
        ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okActionPerformed(evt);
            }
        });

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        source.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane3.setViewportView(source);

        selection.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                selectionValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(selection);

        org.openide.awt.Mnemonics.setLocalizedText(add, org.openide.util.NbBundle.getMessage(RequirementSelectionDialog.class, "RequirementSelectionDialog.add.text")); // NOI18N
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(remove, org.openide.util.NbBundle.getMessage(RequirementSelectionDialog.class, "RequirementSelectionDialog.remove.text")); // NOI18N
        remove.setEnabled(false);
        remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(ok))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(remove)
                            .addComponent(add, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(132, 132, 132)
                        .addComponent(add)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(remove)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 175, Short.MAX_VALUE)))
                .addComponent(ok))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okActionPerformed
        setVisible(false);
    }//GEN-LAST:event_okActionPerformed

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
        TreePath[] paths = source.getSelectionPaths();
        for (TreePath tp : paths) {
            Object value = tp.getLastPathComponent();
            if (((DefaultMutableTreeNode) value).getUserObject() instanceof Requirement) {
                Requirement requirement = (Requirement) ((DefaultMutableTreeNode) value).getUserObject();
                if (!requirements.contains(requirement)) {
                    ((DefaultListModel) selection.getModel()).addElement(requirement);
                }
            }
        }
    }//GEN-LAST:event_addActionPerformed

    private void selectionValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_selectionValueChanged
        // Enable remove button when item is selected
        remove.setEnabled(!selection.getSelectedValuesList().isEmpty());
    }//GEN-LAST:event_selectionValueChanged

    private void removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeActionPerformed
        for (Object value : selection.getSelectedValuesList()) {
            Requirement req = (Requirement) value;
            ((DefaultListModel) selection.getModel()).removeElement(req);
        }
    }//GEN-LAST:event_removeActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JButton ok;
    private javax.swing.JButton remove;
    private javax.swing.JList selection;
    private javax.swing.JTree source;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the requirements
     */
    public List<Requirement> getRequirements() {
        return requirements;
    }

    private void populateNodes() {
        List<RequirementSpec> specs = new RequirementSpecJpaController(
                DataBaseManager.getEntityManagerFactory())
                .findRequirementSpecEntities();
        for (RequirementSpec spec : specs) {
            DefaultMutableTreeNode node
                    = new DefaultMutableTreeNode(spec);
            for (RequirementSpecNode rsn : spec.getRequirementSpecNodeList()) {
                List<Requirement> reqs = rsn.getRequirementList();
                //Make sure to remove multiple versions of requirements.
                List<String> processed = new ArrayList<>();
                for (Requirement r : reqs) {
                    if (!processed.contains(r.getUniqueId().trim())) {
                        Requirement max = Collections.max(new RequirementServer(r)
                                .getVersions(), null);
                        node.add(new DefaultMutableTreeNode(max));
                        processed.add(max.getUniqueId().trim());
                    }
                }
            }
            if (node.getChildCount() > 0) {
                top.add(node);
            }
        }
        source.setModel(new javax.swing.tree.DefaultTreeModel(top));
    }

    private class InternalRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
            String label = null;
            if (((DefaultMutableTreeNode) value).getUserObject() instanceof RequirementSpec) {
                RequirementSpec spec = (RequirementSpec) ((DefaultMutableTreeNode) value).getUserObject();
                //setIcon(tutorialIcon);
                setToolTipText("Requirement Specification");
                label = spec.getProject().getName() + ": " + spec.getName();
            } else if (((DefaultMutableTreeNode) value).getUserObject() instanceof Requirement) {
                Requirement req = (Requirement) ((DefaultMutableTreeNode) value).getUserObject();
                //setIcon(tutorialIcon);
                setToolTipText(req.getDescription());
                label = req.getUniqueId();
            } else {
                setToolTipText(null); //no tool tip
            }
            return super.getTreeCellRendererComponent(
                    tree, label != null ? label : value, sel,
                    expanded, leaf, row,
                    hasFocus);
        }
    }
}
