/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.client.ui.components.requirement.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableRowSorter;
import net.sourceforge.javydreamercsw.client.ui.components.ImporterInterface;
import org.openide.windows.TopComponent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractImportTopComponent extends TopComponent
        implements ImporterInterface {

    protected final List<DefaultTableModel> tables
            = new ArrayList<DefaultTableModel>();
    protected DefaultComboBoxModel model;
    private static final Logger LOG
            = Logger.getLogger(AbstractImportTopComponent.class.getSimpleName());

    public AbstractImportTopComponent() {
        Vector comboBoxItems = new Vector();
        comboBoxItems.add(",");
        comboBoxItems.add(";");
        comboBoxItems.add(".");
        setModel(new DefaultComboBoxModel(comboBoxItems));
        init();
        getDelimiter().setSelectedIndex(0);
        getSpinner().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                LOG.log(Level.INFO, "Value changed to: {0}", getSpinner().getValue());
                displayTable((int) Math.round(Double.valueOf(
                        getSpinner().getValue().toString())));
            }
        });
    }

    public abstract JTextField getDelimetterField();

    /**
     *
     * @return
     */
    public abstract JCheckBox getHeaderCheckbox();

    /**
     *
     * @return
     */
    public abstract JScrollPane getScrollPane();

    /**
     *
     * @param table
     */
    public abstract void setImportTable(JTable table);

    /**
     *
     * @return
     */
    public abstract JTable getImportTable();

    /**
     *
     * @return
     */
    public abstract JSpinner getSpinner();

    /**
     *
     * @return
     */
    public abstract JComboBox getDelimiter();

    /**
     *
     * @param model
     */
    public abstract void setModel(DefaultComboBoxModel model);

    /**
     *
     * @return
     */
    public abstract DefaultComboBoxModel getModel();

    protected void handleHeaderActionPerformed() {
        displayTable((int) Math.round(Double.valueOf(getSpinner().getValue().toString())));
    }

    protected void handleAddDelimiterButtonActionPerformed() {
        getModel().addElement(getDelimetterField().getText().trim());
    }

    @Override
    public void displayTable(Integer index) {
        LOG.log(Level.FINE, "Changed value to: {0}", index);
        //Rebuild the table
        DefaultTableModel tableModel = tables.get(index - 1);
        int columns = tableModel.getColumnCount();
        String[] title = new String[columns];
        final List<TableCellEditor> editors
                = new ArrayList<TableCellEditor>();
        for (int i = 0; i < columns; i++) {
            //Default title
            title[i] = "Column " + (i + 1);
            //Fill maping field
            editors.add(getEditor());
        }
        tableModel.setColumnIdentifiers(title);
        setImportTable(new JTable(tableModel) {
            //  Determine editor to be used by row
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (row == 0) {
                    return (TableCellEditor) editors.get(column);
                } else {
                    return super.getCellEditor(row, column);
                }
            }
        });
        if (getHeaderCheckbox().isSelected()) {
            TableRowSorter sorter
                    = new TableRowSorter<DefaultTableModel>(tableModel);
            getImportTable().setRowSorter(sorter);
            RowFilter<DefaultTableModel, Object> rf;
            //If current expression doesn't parse, don't update.
            try {
                //Filter the row with the title
                RowFilter<DefaultTableModel, Object> regexFilter
                        = RowFilter.regexFilter("^" + tableModel.getValueAt(1, 0));
                rf = RowFilter.notFilter(regexFilter);
                //Also change the table header
                for (int i = 0; i < columns; i++) {
                    title[i] = tableModel.getValueAt(1, i).toString();
                }
                tableModel.setColumnIdentifiers(title);
            } catch (java.util.regex.PatternSyntaxException e) {
                return;
            }
            if (rf != null) {
                sorter.setRowFilter(rf);
            }
        }
        getScrollPane().setViewportView(getImportTable());
    }
}
