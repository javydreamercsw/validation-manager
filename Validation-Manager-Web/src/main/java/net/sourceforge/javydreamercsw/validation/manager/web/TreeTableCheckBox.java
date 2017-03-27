/*
 * CheckBox for TreeTables. It selects all children when selected.
 */
package net.sourceforge.javydreamercsw.validation.manager.web;

import com.vaadin.data.Item;
import com.vaadin.sebastian.indeterminatecheckbox.IndeterminateCheckBox;
import com.vaadin.ui.TreeTable;
import java.util.Objects;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class TreeTableCheckBox extends IndeterminateCheckBox {

    private final TreeTable tt;
    private final static Logger LOG
            = Logger.getLogger(TreeTableCheckBox.class.getSimpleName());
    private Object objectId;

    public TreeTableCheckBox(TreeTable tt, Object objectId) {
        this.tt = tt;
        this.objectId = objectId;
        initialize();
    }

    public TreeTableCheckBox(TreeTable tt, String caption, Object objectId) {
        super(caption);
        this.tt = tt;
        this.objectId = objectId;
        initialize();
    }

    public TreeTableCheckBox(TreeTable tt, String caption,
            boolean initialState, Object objectId) {
        super(caption, initialState);
        this.tt = tt;
        this.objectId = objectId;
        initialize();
    }

    private void initialize() {
        setUserCanToggleIndeterminate(false);
    }

    @Override
    protected void setInternalValue(Boolean value) {
        if (!Objects.equals(value, getState().value) && value != null
                && (tt != null && tt.hasChildren(objectId))) {
            tt.getChildren(objectId).forEach((o) -> {
                Item item = tt.getItem(o);
                Object val = item.getItemProperty("Name").getValue();
                if (val instanceof TreeTableCheckBox) {
                    TreeTableCheckBox ttcb = (TreeTableCheckBox) val;
                    ttcb.setValue(value);
                }
            });
        }
        //Switching from false to true. Select all children
        super.setInternalValue(value);
    }
}
