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

import com.vaadin.flow.component.checkbox.Checkbox;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class TreeTableCheckBox extends Checkbox {

    /**
     * Minimal tree access the checkbox needs to keep parent and children in
     * sync. Implemented by the caller on top of its tree ({@link com.vaadin.flow.data.provider.hierarchy.TreeData}
     * backed) structure, which also resolves the checkbox shown for each item.
     */
    public interface TreeNavigator extends Serializable {

        boolean hasChildren(Object objectId);

        Collection<Object> getChildren(Object objectId);

        Object getParent(Object objectId);

        /**
         * @return the checkbox displayed for the given item, or null if the
         * item has none.
         */
        TreeTableCheckBox getCheckBox(Object objectId);
    }

    private final static Logger LOG
            = Logger.getLogger(TreeTableCheckBox.class.getSimpleName());
    private final TreeNavigator navigator;
    private final Object objectId;
    // Tri-state mirror of the v8 value: null models the indeterminate state
    // (the Flow Checkbox's model value can't hold null).
    private Boolean value;

    public TreeTableCheckBox(TreeNavigator navigator, Object objectId) {
        this.navigator = navigator;
        this.objectId = objectId;
        initialize();
    }

    public TreeTableCheckBox(TreeNavigator navigator, String caption,
            Object objectId) {
        super(caption);
        this.navigator = navigator;
        this.objectId = objectId;
        initialize();
    }

    public TreeTableCheckBox(TreeNavigator navigator, String caption,
            boolean initialState, Object objectId) {
        super(caption, initialState);
        this.navigator = navigator;
        this.objectId = objectId;
        initialize();
    }

    private void initialize() {
        //The parent/child cascade runs for both user toggles and programmatic
        //value changes, like the v8 doSetValue() did.
        // TODO: (phase-4b-2) native Checkbox is bi-state on click; the
        // tri-state (indeterminate) mirror here is programmatic only.
        addValueChangeListener(e -> applyValue(e.getValue()));
        if (this.value == null) {
            setIndeterminate(true);
        }
    }

    /**
     * @return the tri-state value; null means indeterminate (v8 semantics).
     */
    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean value) {
        if (value == null) {
            //Tri-state: mark indeterminate without touching "checked".
            this.value = null;
            setIndeterminate(true);
        } else {
            //Triggers setPresentationValue() and the value change listeners.
            super.setValue(value);
        }
    }

    @Override
    protected void setPresentationValue(Boolean value) {
        applyValue(value);
        setIndeterminate(false);
        super.setPresentationValue(value);
    }

    private void applyValue(Boolean value) {
        if (navigator != null
                && value != null
                && !Objects.equals(value, this.value)) {
            if (navigator.hasChildren(getObjectId())
                    && this.value != null) {
                //Switching from false to true. Select all children
                navigator.getChildren(getObjectId()).forEach((o) -> {
                    TreeTableCheckBox ttcb = navigator.getCheckBox(o);
                    if (ttcb != null) {
                        ttcb.setValue(value);
                    }
                });
            }
            Object parentId = navigator.getParent(getObjectId());
            if (!value && parentId != null) {
                //Switching from true to false. Mark parent as undeterminated
                TreeTableCheckBox parent = navigator.getCheckBox(parentId);
                if (parent != null && Boolean.TRUE.equals(parent.getValue())) {
                    LOG.log(Level.INFO, "Setting {0} to undetermined.",
                            parentId);
                    parent.setValue(null);
                } else {
                    LOG.info("Parent not selected!");
                }
            }
        }
        this.value = value;
    }

    /**
     * @return the objectId
     */
    public Object getObjectId() {
        return objectId;
    }
}
