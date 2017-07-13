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

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroupFieldFactory;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.VmUser;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class ExecutionStepComponent extends Panel {

    private final ExecutionStep es;
    private static final Logger LOG
            = Logger.getLogger(ExecutionStepComponent.class.getSimpleName());

    public ExecutionStepComponent(ExecutionStep es) {
        this.es = es;
        setCaption(TRANSLATOR.translate("execution.step.detail"));
        init();
    }

    public ExecutionStepComponent(ExecutionStep es, String caption) {
        super(caption);
        this.es = es;
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        setContent(layout);
        addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(es.getClass());
        binder.setItemDataSource(es);
        FieldGroupFieldFactory defaultFactory = binder.getFieldFactory();
        binder.setFieldFactory(new FieldGroupFieldFactory() {

            @Override
            public <T extends Field> T createField(Class<?> dataType, Class<T> fieldType) {
                if (dataType.isAssignableFrom(VmUser.class)) {
                    BeanItemContainer<VmUser> userEntityContainer
                            = new BeanItemContainer<>(VmUser.class);
                    userEntityContainer.addBean(es.getAssignee());
                    Field field = new TextField(es.getAssignee() == null
                            ? TRANSLATOR.translate("general.not.applicable")
                            : es.getAssignee().getFirstName() + " "
                            + es.getAssignee().getLastName());
                    return fieldType.cast(field);
                }

                return defaultFactory.createField(dataType, fieldType);
            }
        });
        layout.addComponent(((VMUI) UI.getCurrent())
                .createStepHistoryTable(TRANSLATOR.translate("step.detail"),
                        Arrays.asList(es.getStepHistory()), false));
        if (es.getResultId() != null) {
            Field<?> result = binder.buildAndBind(TRANSLATOR.translate("general.result"),
                    "resultId.resultName");
            layout.addComponent(result);
        }
        if (es.getComment() != null) {
            TextArea comment = new TextArea(TRANSLATOR.translate("general.comment"));
            binder.bind(comment, "comment");
            layout.addComponent(comment);
        }
        if (es.getAssignee() != null) {
            TextField assignee = new TextField(TRANSLATOR.translate("general.assignee"));
            VmUser u = es.getAssignee();
            assignee.setValue(u.toString());
            assignee.setReadOnly(true);
            layout.addComponent(assignee);
        }
        if (es.getExecutionStart() != null) {
            Field<?> start = binder.buildAndBind(TRANSLATOR.translate("execution.start"),
                    "executionStart");
            layout.addComponent(start);
        }
        if (es.getExecutionEnd() != null) {
            Field<?> end = binder.buildAndBind(TRANSLATOR.translate("execution.end"),
                    "executionEnd");
            layout.addComponent(end);
        }
        if (es.getExecutionTime() != null && es.getExecutionTime() > 0) {
            Field<?> time = binder.buildAndBind(TRANSLATOR.translate("execution.time"),
                    "executionTime");
            layout.addComponent(time);
        }
        if (!es.getHistoryList().isEmpty()) {
            layout.addComponent(((VMUI) UI.getCurrent())
                    .createRequirementHistoryTable(
                            TRANSLATOR.translate("related.requirements"),
                            es.getHistoryList(), true));
        }
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
            if (es.getExecutionStepPK() == null) {
                ((VMUI) UI.getCurrent()).displayObject(((VMUI) UI.getCurrent())
                        .getSelectdValue());
            } else {
                ((VMUI) UI.getCurrent()).displayObject(es, false);
            }
        });
        binder.setReadOnly(true);
        binder.bindMemberFields(this);
        layout.setSizeFull();
        setSizeFull();
    }
}
