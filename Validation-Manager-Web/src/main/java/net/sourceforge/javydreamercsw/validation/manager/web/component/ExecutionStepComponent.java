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

import com.vaadin.data.Binder;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateTimeField;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
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
        Binder<ExecutionStep> binder = new Binder<>(ExecutionStep.class);
        binder.setBean(es);
        layout.addComponent(((VMUI) UI.getCurrent())
                .createStepHistoryTable(TRANSLATOR.translate("step.detail"),
                        Arrays.asList(es.getStepHistory()), false));
        if (es.getResultId() != null) {
            TextField result = new TextField(TRANSLATOR.translate("general.result"));
            binder.bind(result, "resultId.resultName");
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
            DateTimeField start = new DateTimeField(TRANSLATOR.translate("execution.start"));
            binder.forField(start)
                    .withConverter(this::toDate, this::toDateTime)
                    .bind("executionStart");
            layout.addComponent(start);
        }
        if (es.getExecutionEnd() != null) {
            DateTimeField end = new DateTimeField(TRANSLATOR.translate("execution.end"));
            binder.forField(end)
                    .withConverter(this::toDate, this::toDateTime)
                    .bind("executionEnd");
            layout.addComponent(end);
        }
        if (es.getExecutionTime() != null && es.getExecutionTime() > 0) {
            TextField time = new TextField(TRANSLATOR.translate("execution.time"));
            binder.forField(time)
                    .withConverter(Double::parseDouble, String::valueOf)
                    .bind("executionTime");
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
            if (es.getExecutionStepPK() == null) {
                ((VMUI) UI.getCurrent()).displayObject(((VMUI) UI.getCurrent())
                        .getSelectdValue());
            } else {
                ((VMUI) UI.getCurrent()).displayObject(es, false);
            }
        });
        binder.setReadOnly(true);
        layout.setSizeFull();
        setSizeFull();
    }

    private Date toDate(LocalDateTime dateTime) {
        return dateTime == null ? null
                : Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private LocalDateTime toDateTime(Date date) {
        return date == null ? null
                : LocalDateTime.ofInstant(date.toInstant(),
                        ZoneId.systemDefault());
    }
}
