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

import com.vaadin.sebastian.indeterminatecheckbox.IndeterminateCheckBox;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class TreeTableCheckBox extends IndeterminateCheckBox {

    /**
     * Minimal tree access the checkbox needs to keep parent and children in
     * sync. Implemented by the caller on top of its Vaadin 8 tree
     * ({@link com.vaadin.data.TreeData} backed) structure, which also resolves
     * the checkbox shown for each item.
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
        setUserCanToggleIndeterminate(false);
    }

    @Override
    public Boolean getValue() {
        return getState().value;
    }

    @Override
    protected void doSetValue(Boolean value) {
        if (navigator != null
                && value != null
                && !Objects.equals(value, getState().value)) {
            if (navigator.hasChildren(getObjectId())
                    && getState().value != null) {
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
                if (parent != null && parent.getValue() != null
                        && parent.getValue()) {
                    LOG.log(Level.INFO, "Setting {0} to undetermined.",
                            parentId);
                    parent.setValue(null);
                } else {
                    LOG.info("Parent not selected!");
                }
            }
        }
        getState().value = value;
    }

    /**
     * @return the objectId
     */
    public Object getObjectId() {
        return objectId;
    }
}
