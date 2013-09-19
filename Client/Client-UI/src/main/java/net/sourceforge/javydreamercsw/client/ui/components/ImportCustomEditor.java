package net.sourceforge.javydreamercsw.client.ui.components;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class ImportCustomEditor extends DefaultCellEditor {

    protected final JComboBox cb;

    public ImportCustomEditor() {
        super(new JComboBox());
        cb = (JComboBox) super.getComponent();
        this.init();
    }

    /**
     * Populated the combo box.
     */
    public abstract void init();

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        return row == 0 ? cb
                : super.getTableCellEditorComponent(table,
                        value, isSelected, row, column);
    }
}
