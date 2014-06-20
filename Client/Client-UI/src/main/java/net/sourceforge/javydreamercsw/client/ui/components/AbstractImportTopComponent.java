package net.sourceforge.javydreamercsw.client.ui.components;

import com.validation.manager.core.tool.message.MessageHandler;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
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
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractImportTopComponent extends TopComponent
        implements ImporterInterface {

    protected final List<DefaultTableModel> tables
            = new ArrayList<>();
    protected DefaultComboBoxModel model;
    private static final Logger LOG
            = Logger.getLogger(AbstractImportTopComponent.class.getSimpleName());
    private boolean importSuccess;
    private JDialog dialog;

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
                LOG.log(Level.FINE, "Value changed to: {0}", getSpinner().getValue());
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
        LOG.log(Level.FINE, "Existing tables: {0}", tables.size());
        //Rebuild the table
        DefaultTableModel tableModel = tables.get(index - 1);
        int columns = tableModel.getColumnCount();
        String[] title = new String[columns];
        final List<TableCellEditor> editors
                = new ArrayList<>();
        for (int i = 0; i < columns; i++) {
            //Default title
            title[i] = MessageFormat.format("Column {0}", i + 1);
            //Fill maping field
            DefaultCellEditor editor = getEditor();
            if (editor != null) {
                editors.add(editor);
            }
        }
        tableModel.setColumnIdentifiers(title);
        setImportTable(new JTable(tableModel) {
            //  Determine editor to be used by row
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (!editors.isEmpty() && row == 0) {
                    return (TableCellEditor) editors.get(column);
                } else {
                    return super.getCellEditor(row, column);
                }
            }
        });
        if (getHeaderCheckbox().isSelected()) {
            TableRowSorter sorter
                    = new TableRowSorter<>(tableModel);
            getImportTable().setRowSorter(sorter);
            RowFilter<DefaultTableModel, Object> rf;
            //If current expression doesn't parse, don't update.
            try {
                //Filter the row with the title
                RowFilter<DefaultTableModel, Object> regexFilter
                        = RowFilter.regexFilter(MessageFormat.format("^{0}",
                                        tableModel.getValueAt(1, 0)));
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

    @Override
    public void enableUI(boolean valid) {
        getSpinner().setEnabled(valid);
        getHeaderCheckbox().setEnabled(valid);
        getImportTable().setEnabled(valid);
        getSaveButton().setEnabled(valid);
    }

    protected void showImportError(String message) {
        Lookup.getDefault().lookup(MessageHandler.class).error(message);
    }

    /**
     * @param importSuccess the importSuccess to set
     */
    protected void setImportSuccess(boolean importSuccess) {
        this.importSuccess = importSuccess;
    }

    /**
     * @return the importSuccess
     */
    protected boolean isImportSuccess() {
        return importSuccess;
    }

    /**
     * @return the dialog
     */
    protected JDialog getDialog() {
        return dialog;
    }

    /**
     * @param dialog the dialog to set
     */
    protected void setDialog(JDialog dialog) {
        this.dialog = dialog;
    }

    public abstract ImportMappingInterface getMapping();

    public List<String> checkMappings() {
        int rows = getImportTable().getModel().getRowCount();
        List<String> mapping = new ArrayList<>(rows);
        for (int i = 0; i < getImportTable().getModel().getColumnCount(); i++) {
            DefaultCellEditor editor
                    = (DefaultCellEditor) getImportTable().getCellEditor(0, i);
            JComboBox combo = (JComboBox) editor.getComponent();
            LOG.log(Level.FINE, "Column {0} is mapped as: {1}",
                    new Object[]{i, combo.getSelectedItem()});
            String value = (String) combo.getSelectedItem();
            //Make sure there's no duplicate mapping
            if (!mapping.isEmpty()
                    && (getMapping().getMappingValue(value) != null
                    && !getMapping().getMappingValue(value).isIgnored()
                    && mapping.contains(value))) {
                showImportError(MessageFormat.format(
                        "Duplicated mapping: {0}", value));
                setImportSuccess(false);
            }
            mapping.add(i, value);
        }
        //Make sure the basics are mapped
        for (ImportMappingInterface tim : getMapping().getValues()) {
            if (tim.isRequired() && !mapping.contains(tim.getValue())) {
                showImportError(MessageFormat.format(
                        "Missing required mapping: {0}", tim.getValue()));
                setImportSuccess(false);
                break;
            }
        }
        return mapping;
    }
}
