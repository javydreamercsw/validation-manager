/* 
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.javydreamercsw.validation.manager.web.component;

import com.vaadin.data.Item;
import com.vaadin.sebastian.indeterminatecheckbox.IndeterminateCheckBox;
import com.vaadin.ui.TreeTable;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class TreeTableCheckBox extends IndeterminateCheckBox {

    private final TreeTable tt;
    private final static Logger LOG
            = Logger.getLogger(TreeTableCheckBox.class.getSimpleName());
    private final Object objectId;

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
        if (tt != null
                && value != null
                && !Objects.equals(value, getState().value)) {
            if (tt.hasChildren(getObjectId())
                    && getState().value != null) {
                //Switching from false to true. Select all children
                tt.getChildren(getObjectId()).forEach((o) -> {
                    Item item = tt.getItem(o);
                    Object val = item.getItemProperty("Name").getValue();
                    if (val instanceof TreeTableCheckBox) {
                        TreeTableCheckBox ttcb = (TreeTableCheckBox) val;
                        ttcb.setValue(value);
                    }
                });
            }
            Object parentId = tt.getParent(getObjectId());
            if (!value && parentId != null) {
                //Switching from true to false. Mark parent as undeterminated
                TreeTableCheckBox parent
                        = ((TreeTableCheckBox) tt.getItem(parentId)
                                .getItemProperty("Name").getValue());
                if (parent.getValue() != null && parent.getValue()) {
                    LOG.log(Level.INFO, "Setting {0} to undetermined.",
                            parentId);
                    parent.setValue(null);
                } else {
                    LOG.info("Parent not selected!");
                }
            }
        }
        super.setInternalValue(value);
    }

    /**
     * @return the objectId
     */
    public Object getObjectId() {
        return objectId;
    }
}
