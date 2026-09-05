/*
 * Copyright 2017 ortizj.
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
package net.sourceforge.javydreamercsw.validation.manager.web.workflow;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import static net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Workflow;
import com.validation.manager.core.db.WorkflowStep;
import com.validation.manager.core.db.controller.WorkflowJpaController;
import com.validation.manager.core.server.core.WorkflowServer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VMWindow;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VizGraph;

/**
 * Workflow manager screen. The graph is rendered with graphviz (DOT rendered
 * to inline SVG via {@link VizGraph}, a wrapper around the WebAssembly build
 * of Graphviz) replacing the v7-era vizcomponent add-on.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class WorkflowViewer extends VMWindow {

    private final VizGraph diagram = new VizGraph();
    private final List<Object> added = new ArrayList<>();
    private final List<Object> deleted = new ArrayList<>();
    private final Map<Integer, String> nodes = new HashMap<>();
    //Map has transition name -> (source step id, destination step id).
    private final Map<String, Map.Entry<Integer, Integer>> edges = new HashMap<>();
    private static final Logger LOG
            = Logger.getLogger(WorkflowViewer.class.getSimpleName());
    private Object selected = null;
    private final VerticalLayout bl = new VerticalLayout();
    private VerticalLayout controls = null;
    private final Select<Workflow> workflows = new Select<>();

    {
        workflows.setLabel(TRANSLATOR.translate("general.workflow"));
    }
    private int count = 0;
    private final String KEY = "key", ITEM_NAME = "itemName";

    public WorkflowViewer() {
        super(TRANSLATOR.translate("workflow.manager"));
        init();
    }

    public WorkflowViewer(String caption) {
        super(caption);
        init();
    }

    private void cleanGraph() {
        diagram.clear();
    }

    protected void init() {
        setSizeFull();
        setModal(false);
        String width = "200px";
        VerticalLayout west = getList();
        west.setWidth(width);
        controls = (VerticalLayout) getControls();
        controls.setWidth(width);
        west.setSizeFull();
        diagram.setSizeFull();
        bl.add(west, diagram, controls);
        bl.setSizeFull();
        bl.expand(diagram);
        add(bl);
    }

    private Component getControls() {
        VerticalLayout controls = new VerticalLayout();
        Button addStep = new Button(TRANSLATOR.translate("general.add.step"));
        VerticalLayout vl = new VerticalLayout();
        TextField name = new TextField(TRANSLATOR.translate("general.name"));
        vl.add(name);
        addStep.addClickListener(listener -> {
            ConfirmDialog prompt = new ConfirmDialog();
            prompt.setHeader(TRANSLATOR.translate("general.add.step"));
            prompt.setText(vl);
            prompt.setConfirmButton(TRANSLATOR.translate("general.yes"),
                    e -> {
                        if (name.getValue() != null
                                && !name.getValue().isEmpty()) {
                            nodes.put(--count,
                                    TRANSLATOR.translate(name.getValue()));
                            added.add(name.getValue());
                            refreshWorkflow();
                        }
                        prompt.close();
                    });
            prompt.setCancelButton(TRANSLATOR.translate("general.cancel"),
                    e -> prompt.close());
            prompt.open();
        });
        addStep.setWidth("100%");
        addStep.setEnabled(workflows.getValue() != null);
        controls.add(addStep);
        Button addTransition = new Button(TRANSLATOR.translate("general.add.transition"));
        VerticalLayout vl2 = new VerticalLayout();
        TextField transitionName = new TextField(TRANSLATOR.translate("general.name"));
        Select<Integer> nodeList = new Select<>();
        nodeList.setLabel(TRANSLATOR.translate("general.step"));
        nodeList.setItems(nodes.keySet());
        nodeList.setItemLabelGenerator(node -> String.valueOf(nodes.get(node)));
        vl2.add(transitionName);
        vl2.add(nodeList);
        addTransition.addClickListener(listener -> {
            ConfirmDialog prompt = new ConfirmDialog();
            prompt.setHeader(TRANSLATOR.translate("general.add.transition"));
            prompt.setText(vl2);
            prompt.setConfirmButton(TRANSLATOR.translate("general.yes"),
                    e -> {
                        if (transitionName.getValue() != null
                                && !transitionName.getValue().isEmpty()
                                && selected instanceof Integer) {
                            edges.put(transitionName.getValue(),
                                    new HashMap.SimpleEntry<>(
                                            (Integer) selected,
                                            nodeList.getValue() == null
                                            ? null : (Integer) nodeList
                                                    .getValue()));
                            added.add(transitionName.getValue());
                            refreshWorkflow();
                        }
                        prompt.close();
                    });
            prompt.setCancelButton(TRANSLATOR.translate("general.cancel"),
                    e -> prompt.close());
            prompt.open();
        });
        addTransition.setWidth("100%");
        addTransition.setEnabled(selected instanceof Integer);
        controls.add(addTransition);
        Button delete = new Button(TRANSLATOR.translate("general.delete"));
        delete.setEnabled(selected != null);
        delete.addClickListener(listener -> {
            ConfirmDialog prompt = new ConfirmDialog();
            prompt.setHeader(TRANSLATOR.translate("general.delete"));
            prompt.setText(TRANSLATOR.translate("general.delete.confirmation"));
            prompt.setConfirmButton(TRANSLATOR.translate("general.yes"),
                    e -> {
                        if (selected instanceof String) {
                            String edgeName = (String) selected;
                            Map.Entry<Integer, Integer> edge
                                    = edges.remove(edgeName);
                            if (edge != null) {
                                addToDelete(edge);
                            }
                        } else if (selected instanceof Integer) {
                            GraphNode node = new GraphNode((Integer) selected);
                            addToDelete(node);
                        }
                        refreshWorkflow();
                        prompt.close();
                    });
            prompt.setCancelButton(TRANSLATOR.translate("general.cancel"),
                    e -> prompt.close());
            prompt.open();
        });
        controls.add(delete);
        Button rename = new Button(TRANSLATOR.translate("general.rename"));
        rename.setWidth("100%");
        rename.setEnabled(selected != null);
        rename.addClickListener(listener -> {
            VMWindow w = new VMWindow(TRANSLATOR.translate("general.rename"));
            w.setWidth("25%");
            w.setHeight("25%");
            ValidationManagerUI.getInstance().openDialog(w);
        });
        controls.add(rename);
        Button save = new Button(TRANSLATOR.translate("general.save"));
        save.setWidth("100%");
        save.setEnabled(!added.isEmpty() || !deleted.isEmpty());
        save.addClickListener(listener -> {
            List<String> nodesToAdd = new ArrayList<>();
            WorkflowServer ws
                    = new WorkflowServer(workflows.getValue().getId());
            added.forEach(a -> {
                if (a instanceof String) {
                    nodesToAdd.add((String) a);
                }
            });
            deleted.forEach(a -> {
                LOG.log(Level.INFO, "Deleted: {0}", a);
            });
            nodesToAdd.forEach(node -> {
                try {
                    ws.addStep(node);
                } catch (VMException ex) {
                    LOG.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
                }
            });
            displayWorkflow(ws.getEntity());
        });
        controls.add(save);
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.setWidth("100%");
        cancel.setEnabled(selected != null);
        cancel.addClickListener(listener -> {
            Workflow w = workflows.getValue();
            if (w != null) {
                displayWorkflow(w);
            }
            deleted.clear();
            added.clear();
        });
        controls.add(cancel);
        return controls;
    }

    /**
     * Create workflow from database
     *
     * @param w workflow to create from
     */
    private void displayWorkflow(Workflow w) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph \"").append(escapeDot(w.getWorkflowName()))
                .append("\" {");
        sb.append("  rankdir=TB;");
        nodes.clear();
        //Create the nodes
        w.getWorkflowStepList().forEach(step -> {
            addStep(step, sb);
            //Now add the links
            step.getSourceTransitions().forEach(t -> {
                addStep(t.getWorkflowStepSource(), sb);
                addStep(t.getWorkflowStepTarget(), sb);
                String source = dotId(t.getWorkflowStepSource()
                        .getWorkflowStepPK().getId());
                String target = dotId(t.getWorkflowStepTarget()
                        .getWorkflowStepPK().getId());
                sb.append("  ").append(source).append(" -> ")
                        .append(target).append(" [label=\"")
                        .append(escapeDot(TRANSLATOR.translate(
                                t.getTransitionName())))
                        .append("\"];");
                edges.put(TRANSLATOR.translate(t.getTransitionName()),
                        new HashMap.SimpleEntry<>(
                                t.getWorkflowStepSource()
                                        .getWorkflowStepPK().getId(),
                                t.getWorkflowStepTarget()
                                        .getWorkflowStepPK().getId()));
            });
        });
        sb.append("}");
        diagram.setGraph(sb.toString());
    }

    /**
     * Recreate graph with the edited values
     */
    private void refreshWorkflow() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph workflow {");
        sb.append("  rankdir=TB;");
        nodes.values().forEach(node -> {
            sb.append("  ").append(dotId(nodeId(node))).append(" [label=\"")
                    .append(escapeDot(node)).append("\"];");
        });
        edges.values().forEach(edge -> {
            if (edge.getValue() != null) {
                sb.append("  ").append(dotId(edge.getKey())).append(" -> ")
                        .append(dotId(edge.getValue())).append(";");
            }
        });
        sb.append("}");
        diagram.setGraph(sb.toString());
        selected = null;
        updateControls();
    }

    private void addStep(WorkflowStep step, StringBuilder sb) {
        if (!nodes.containsKey(step.getWorkflowStepPK().getId())) {
            String node = TRANSLATOR.translate(step.getStepName());
            nodes.put(step.getWorkflowStepPK().getId(), node);
            sb.append("  ").append(dotId(step.getWorkflowStepPK().getId()))
                    .append(" [label=\"").append(escapeDot(node))
                    .append("\"];");
        }
    }

    /**
     * @return a DOT-safe node identifier for a workflow step id (quoted, as
     * ids can be negative for steps added in the editor).
     */
    private String dotId(int id) {
        return "\"n" + id + "\"";
    }

    /**
     * Reverse lookup of a node label's step id (edited-only nodes use
     * negative ids).
     */
    private int nodeId(String label) {
        for (Map.Entry<Integer, String> e : nodes.entrySet()) {
            if (e.getValue().equals(label)) {
                return e.getKey();
            }
        }
        return -1;
    }

    private static String escapeDot(String text) {
        return text == null ? "" : text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }

    private VerticalLayout getList() {
        VerticalLayout holder = new VerticalLayout();
        workflows.setItems(new WorkflowJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findWorkflowEntities());
        workflows.setItemLabelGenerator(temp
                -> TRANSLATOR.translate(temp.getWorkflowName()));
        workflows.setEmptySelectionAllowed(false);
        workflows.addValueChangeListener(listener -> {
            Workflow w = workflows.getValue();
            if (w != null) {
                displayWorkflow(w);
            }
            updateControls();
        });
        workflows.setSizeFull();
        holder.add(workflows);
        return holder;
    }

    private void updateControls() {
        //Rebuild the control column in place (v8 BorderLayout EAST slot)
        if (controls != null) {
            bl.remove(controls);
        }
        controls = (VerticalLayout) getControls();
        bl.addComponentAtIndex(2, controls);
    }

    private void addToDelete(Object obj) {
        LOG.log(Level.INFO, "Adding to delete list: {0}", obj);
        deleted.add(obj);
    }

    private void addToAdd(Object obj) {
        LOG.log(Level.INFO, "Adding to add list: {0}", obj);
        added.add(obj);
    }

    /**
     * Marker for a node pending deletion.
     */
    private static final class GraphNode {

        private final Integer id;

        GraphNode(Integer id) {
            this.id = id;
        }

        Integer getId() {
            return id;
        }

        @Override
        public String toString() {
            return "Node " + id;
        }
    }
}
