/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.client.ui.components.testcase.importer;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import net.sourceforge.javydreamercsw.client.ui.components.test.importer.ImporterInterface;
import org.openide.windows.TopComponent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractImportTopComponent extends TopComponent implements ImporterInterface {

    protected final DefaultComboBoxModel model;
    protected final List<DefaultTableModel> tables = new ArrayList<DefaultTableModel>();
    // Variables declaration - do not modify
    protected JButton addDelimiterButton;
    protected JComboBox delimiter;
    protected JTextField delimiterField;
    protected JCheckBox header;
    protected JButton importButton;
    protected JTable importedTable;
    protected JLabel jLabel1;
    protected JLabel jLabel2;
    protected JScrollPane jScrollPane1;
    protected JButton saveButton;
    protected JSpinner spinner;
    private static final Logger LOG
            = Logger.getLogger(AbstractImportTopComponent.class.getSimpleName());

    public AbstractImportTopComponent() {
        Vector comboBoxItems = new Vector();
        comboBoxItems.add(",");
        comboBoxItems.add(";");
        comboBoxItems.add(".");
        delimiter.setSelectedIndex(0);
        model = new DefaultComboBoxModel(comboBoxItems);
        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                LOG.log(Level.INFO, "Value changed to: {0}", spinner.getValue());
                displayTable((int) Math.round(Double.valueOf(
                        spinner.getValue().toString())));
            }
        });
    }

    protected void handleHeaderActionPerformed() {
        displayTable((int) Math.round(Double.valueOf(spinner.getValue().toString())));
    }

    protected void handleAddDelimiterButtonActionPerformed() {
        model.addElement(delimiterField.getText().trim());
    }
}
