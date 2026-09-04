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

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import static net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider.TRANSLATOR;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
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
public final class ExecutionStepComponent extends VerticalLayout {

    private final ExecutionStep es;
    private static final Logger LOG
            = Logger.getLogger(ExecutionStepComponent.class.getSimpleName());

    public ExecutionStepComponent(ExecutionStep es) {
        this.es = es;
        add(new com.vaadin.flow.component.html.Span(
                TRANSLATOR.translate("execution.step.detail")));
        init();
    }

    public ExecutionStepComponent(ExecutionStep es, String caption) {
        this.es = es;
        add(new com.vaadin.flow.component.html.Span(caption));
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        add(layout);
        Binder<ExecutionStep> binder = new Binder<>(ExecutionStep.class);
        binder.setBean(es);
        layout.add(((ValidationManagerUI) UI.getCurrent())
                .createStepHistoryTable(TRANSLATOR.translate("step.detail"),
                        Arrays.asList(es.getStepHistory()), false));
        if (es.getResultId() != null) {
            TextField result = new TextField(TRANSLATOR.translate("general.result"));
            binder.bind(result, "resultId.resultName");
            layout.add(result);
        }
        if (es.getComment() != null) {
            TextArea comment = new TextArea(TRANSLATOR.translate("general.comment"));
            binder.bind(comment, "comment");
            layout.add(comment);
        }
        if (es.getAssignee() != null) {
            TextField assignee = new TextField(TRANSLATOR.translate("general.assignee"));
            VmUser u = es.getAssignee();
            assignee.setValue(u.toString());
            assignee.setReadOnly(true);
            layout.add(assignee);
        }
        if (es.getExecutionStart() != null) {
            DateTimePicker start = new DateTimePicker(TRANSLATOR.translate("execution.start"));
            binder.forField(start)
                    .withConverter(this::toDate, this::toDateTime)
                    .bind("executionStart");
            layout.add(start);
        }
        if (es.getExecutionEnd() != null) {
            DateTimePicker end = new DateTimePicker(TRANSLATOR.translate("execution.end"));
            binder.forField(end)
                    .withConverter(this::toDate, this::toDateTime)
                    .bind("executionEnd");
            layout.add(end);
        }
        if (es.getExecutionTime() != null && es.getExecutionTime() > 0) {
            TextField time = new TextField(TRANSLATOR.translate("execution.time"));
            binder.forField(time)
                    .withConverter(Double::parseDouble, String::valueOf)
                    .bind("executionTime");
            layout.add(time);
        }
        if (!es.getHistoryList().isEmpty()) {
            layout.add(((ValidationManagerUI) UI.getCurrent())
                    .createRequirementHistoryTable(
                            TRANSLATOR.translate("related.requirements"),
                            es.getHistoryList(), true));
        }
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.addClickListener((event) -> {
            if (es.getExecutionStepPK() == null) {
                ((ValidationManagerUI) UI.getCurrent()).displayObject(((ValidationManagerUI) UI.getCurrent())
                        .getSelectdValue());
            } else {
                ((ValidationManagerUI) UI.getCurrent()).displayObject(es, false);
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
