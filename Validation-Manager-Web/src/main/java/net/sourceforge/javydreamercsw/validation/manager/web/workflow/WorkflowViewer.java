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

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.pontus.vizcomponent.VizComponent;
import com.vaadin.pontus.vizcomponent.VizComponent.EdgeClickEvent;
import com.vaadin.pontus.vizcomponent.VizComponent.NodeClickEvent;
import com.vaadin.pontus.vizcomponent.model.Graph;
import com.vaadin.pontus.vizcomponent.model.Subgraph;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Workflow;
import com.validation.manager.core.db.WorkflowStep;
import com.validation.manager.core.db.controller.WorkflowJpaController;
import com.validation.manager.core.server.core.WorkflowServer;
import de.steinwedel.messagebox.ButtonOption;
import de.steinwedel.messagebox.MessageBox;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VMWindow;
import org.vaadin.addon.borderlayout.BorderLayout;
import org.vaadin.addon.borderlayout.BorderLayout.Constraint;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class WorkflowViewer extends VMWindow {

    private final VizComponent diagram = new VizComponent();
    private final List<Object> added = new ArrayList<>();
    private final List<Object> deleted = new ArrayList<>();
    private final Map<Integer, Graph.Node> nodes = new HashMap<>();
    //Map has source node and edge. Destination is part of the Edge object
    private final Map<String, AbstractMap.SimpleEntry<Graph.Node, Subgraph.Edge>> edges = new HashMap<>();
    private static final Logger LOG
            = Logger.getLogger(WorkflowViewer.class.getSimpleName());
    private Object selected = null;
    private final BorderLayout bl = new BorderLayout();
    private final ListSelect workflows
            = new ListSelect(TRANSLATOR.translate("general.workflow"));
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
        //Reset nodes to black
        nodes.values().forEach(node -> {
            diagram.addCss(node, "stroke", "black");
            diagram.addTextCss(node, "fill", "black");
        });
        //Reset edges to black
        edges.values().forEach(edge -> {
            diagram.addCss(edge.getValue(), "stroke", "black");
            diagram.addTextCss(edge.getValue(), "fill", "black");
        });
    }

    protected void init() {
        setSizeFull();
        setModal(false);
        diagram.addClickListener((NodeClickEvent e) -> {
            cleanGraph();
            diagram.addCss(e.getNode(), "stroke", "blue");
            diagram.addTextCss(e.getNode(), "fill", "blue");
            selected = e.getNode();
            updateControls();
        });
        diagram.addClickListener((EdgeClickEvent e) -> {
            cleanGraph();
            diagram.addCss(e.getEdge(), "stroke", "blue");
            diagram.addTextCss(e.getEdge(), "fill", "blue");
            selected = e.getEdge();
            updateControls();
        });
        diagram.setSizeFull();
        String width = "200px";
        bl.setMinimumWestWidth(width);
        bl.setMinimumEastWidth(width);
        bl.addComponent(getControls(), Constraint.EAST);
        bl.addComponent(getList(), Constraint.WEST);
        bl.addComponent(diagram, Constraint.CENTER);
        bl.setSizeFull();
        setContent(bl);
    }

    private Component getControls() {
        VerticalLayout controls = new VerticalLayout();
        Button addStep = new Button(TRANSLATOR.translate("general.add.step"));
        VerticalLayout vl = new VerticalLayout();
        TextField name = new TextField(TRANSLATOR.translate("general.name"));
        vl.addComponent(name);
        addStep.addClickListener(listener -> {
            MessageBox prompt = MessageBox.createQuestion()
                    .withCaption(TRANSLATOR.translate("general.add.step"))
                    .withMessage(vl)
                    .withYesButton(() -> {
                        if (name.getValue() != null
                                && !name.getValue().isEmpty()) {
                            Graph.Node node
                                    = new Graph.Node(TRANSLATOR.translate(name.getValue()));
                            nodes.put(--count, node);
                            node.setParam(KEY, "" + count);
                            node.setParam(ITEM_NAME,
                                    TRANSLATOR.translate(name.getValue()));
                            added.add(node);
                            refreshWorkflow();
                        }
                    },
                            ButtonOption.focus(),
                            ButtonOption
                                    .icon(VaadinIcons.CHECK))
                    .withNoButton(ButtonOption.icon(VaadinIcons.CLOSE));
            prompt.getWindow().setIcon(ValidationManagerUI.SMALL_APP_ICON);
            prompt.open();
        });
        addStep.setWidth(100, Unit.PERCENTAGE);
        addStep.setEnabled(workflows.getValue() != null);
        controls.addComponent(addStep);
        Button addTransition = new Button(TRANSLATOR.translate("general.add.transition"));
        VerticalLayout vl2 = new VerticalLayout();
        TextField transitionName = new TextField(TRANSLATOR.translate("general.name"));
        ListSelect nodeList = new ListSelect(TRANSLATOR.translate("general.step"));
        BeanItemContainer<Graph.Node> container
                = new BeanItemContainer<>(Graph.Node.class, nodes.values());
        nodeList.setContainerDataSource(container);
        nodeList.getItemIds().forEach(id -> {
            Graph.Node temp = ((Graph.Node) id);
            nodeList.setItemCaption(id, temp.getId());
        });
        nodeList.setNullSelectionAllowed(false);
        vl2.addComponent(transitionName);
        vl2.addComponent(nodeList);
        addTransition.addClickListener(listener -> {
            MessageBox prompt = MessageBox.createQuestion()
                    .withCaption(TRANSLATOR.translate("general.add.transition"))
                    .withMessage(vl2)
                    .withYesButton(() -> {
                        if (transitionName.getValue() != null
                                && !transitionName.getValue().isEmpty()
                                && selected instanceof Subgraph.Node) {
                            Subgraph.Edge edge
                                    = new Subgraph.Edge();
                            edge.setDest((Subgraph.Node) nodeList.getValue());
                            edges.put(transitionName.getValue(),
                                    new AbstractMap.SimpleEntry<>(
                                            (Subgraph.Node) selected, edge));
                            edge.setParam(KEY, "" + --count);
                            added.add(edge);
                            refreshWorkflow();
                        }
                    },
                            ButtonOption.focus(),
                            ButtonOption
                                    .icon(VaadinIcons.CHECK))
                    .withNoButton(ButtonOption.icon(VaadinIcons.CLOSE));
            prompt.getWindow().setIcon(ValidationManagerUI.SMALL_APP_ICON);
            prompt.open();
        });
        addTransition.setWidth(100, Unit.PERCENTAGE);
        addTransition.setEnabled(selected instanceof Subgraph.Node);
        controls.addComponent(addTransition);
        Button delete = new Button(TRANSLATOR.translate("general.delete"));
        delete.setEnabled(selected != null);
        delete.setWidth(100, Unit.PERCENTAGE);
        delete.addClickListener(listener -> {
            MessageBox prompt = MessageBox.createQuestion()
                    .withCaption(TRANSLATOR.translate("general.delete"))
                    .withMessage(TRANSLATOR.translate("general.delete.confirmation"))
                    .withYesButton(() -> {
                        if (selected instanceof Subgraph.Edge) {
                            Subgraph.Edge edge = (Subgraph.Edge) selected;
                            edges.remove(edge.getParam("label"));
                            addToDelete(edge);
                        } else {
                    Graph.Node node = (Graph.Node) selected;
                    addToDelete(node);
                }
                refreshWorkflow();
                    },
                            ButtonOption.focus(),
                            ButtonOption
                                    .icon(VaadinIcons.CHECK))
                    .withNoButton(ButtonOption.icon(VaadinIcons.CLOSE));
            prompt.getWindow().setIcon(ValidationManagerUI.SMALL_APP_ICON);
            prompt.open();
        });
        controls.addComponent(delete);
        Button rename = new Button(TRANSLATOR.translate("general.rename"));
        rename.setWidth(100, Unit.PERCENTAGE);
        rename.setEnabled(selected != null);
        rename.addClickListener(listener -> {
            Window w = new VMWindow(TRANSLATOR.translate("general.rename"));
            w.setWidth(25, Unit.PERCENTAGE);
            w.setHeight(25, Unit.PERCENTAGE);
            UI.getCurrent().addWindow(w);
        });
        controls.addComponent(rename);
        Button save = new Button(TRANSLATOR.translate("general.save"));
        save.setWidth(100, Unit.PERCENTAGE);
        save.setEnabled(!added.isEmpty() || !deleted.isEmpty());
        save.addClickListener(listener -> {
            List<Graph.Node> nodesToAdd = new ArrayList<>();
            List<Subgraph.Edge> edgesToAdd = new ArrayList<>();
            WorkflowServer ws
                    = new WorkflowServer(((Workflow) workflows.getValue()).getId());
            added.forEach(a -> {
                if (a instanceof Graph.Node) {
                    nodesToAdd.add((Graph.Node) a);
                } else if (a instanceof Subgraph.Edge) {
                    edgesToAdd.add((Subgraph.Edge) a);
                }
            });
            deleted.forEach(a -> {
                LOG.log(Level.INFO, "Deleted: {0}", a);
            });
            nodesToAdd.forEach(node -> {
                try {
                    ws.addStep(node.getParam(ITEM_NAME));
                } catch (VMException ex) {
                    LOG.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
                }
            });
            displayWorkflow(ws.getEntity());
        });
        controls.addComponent(save);
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.setWidth(100, Unit.PERCENTAGE);
        cancel.setEnabled(selected != null);
        cancel.addClickListener(listener -> {
            Workflow w = (Workflow) workflows.getValue();
            if (w != null) {
                displayWorkflow(w);
            }
            deleted.clear();
            added.clear();
        });
        controls.addComponent(cancel);
        return controls;
    }

    /**
     * Create workflow from database
     *
     * @param w workflow to create from
     */
    private void displayWorkflow(Workflow w) {
        Graph graph = new Graph(w.getWorkflowName(), Graph.DIGRAPH);
        nodes.clear();
        //Create the nodes
        w.getWorkflowStepList().forEach(step -> {
            addStep(step, graph);
            //Now add the links
            step.getSourceTransitions().forEach(t -> {
                addStep(t.getWorkflowStepSource(), graph);
                addStep(t.getWorkflowStepTarget(), graph);
                Subgraph.Edge edge
                        = graph.addEdge(nodes.get(t.getWorkflowStepSource()
                                .getWorkflowStepPK().getId()),
                                nodes.get(t.getWorkflowStepTarget()
                                        .getWorkflowStepPK().getId()));
                edge.setParam("label", TRANSLATOR.translate(t.getTransitionName()));
                //Workaround https://github.com/pontusbostrom/VaadinGraphvizComponent/issues/9
                edge.setDest(nodes.get(t.getWorkflowStepTarget()
                        .getWorkflowStepPK().getId()));
                edges.put(TRANSLATOR.translate(t.getTransitionName()),
                        new AbstractMap.SimpleEntry<>(
                                nodes.get(t.getWorkflowStepSource()
                                        .getWorkflowStepPK().getId()), edge));
            });
        });
        diagram.drawGraph(graph);
    }

    /**
     * Recreate graph with the edited values
     */
    private void refreshWorkflow() {
        Graph graph = new Graph(((Workflow) workflows.getValue())
                .getWorkflowName(), Graph.DIGRAPH);
        nodes.values().forEach(node -> {
            graph.addNode(node);
        });
        edges.values().forEach(edge -> {
            graph.addEdge(edge.getKey(), edge.getValue().getDest());
        });
        diagram.drawGraph(graph);
        selected = null;
        updateControls();
    }

    private void addStep(WorkflowStep step, Graph g) {
        if (!nodes.containsKey(step.getWorkflowStepPK().getId())) {
            Graph.Node node
                    = new Graph.Node(TRANSLATOR.translate(step.getStepName()));
            nodes.put(step.getWorkflowStepPK().getId(), node);
            node.setParam(KEY, "" + step.getWorkflowStepPK().getId());
            g.addNode(node);
        }
    }

    private Component getList() {
        BeanItemContainer<Workflow> container
                = new BeanItemContainer<>(Workflow.class,
                        new WorkflowJpaController(DataBaseManager
                                .getEntityManagerFactory())
                                .findWorkflowEntities());
        workflows.setContainerDataSource(container);
        workflows.getItemIds().forEach(id -> {
            Workflow temp = ((Workflow) id);
            workflows.setItemCaption(id,
                    TRANSLATOR.translate(temp.getWorkflowName()));
        });
        workflows.setNullSelectionAllowed(false);
        workflows.addValueChangeListener(listener -> {
            Workflow w = (Workflow) workflows.getValue();
            if (w != null) {
                displayWorkflow(w);
            }
            updateControls();
        });
        workflows.setSizeFull();
        return workflows;
    }

    private void updateControls() {
        bl.removeComponent(Constraint.EAST);
        bl.addComponent(getControls(), Constraint.EAST);
    }

    private void addToDelete(Object obj) {
        LOG.log(Level.INFO, "Adding to delete list: {0}", obj);
        deleted.add(obj);
    }

    private void addToAdd(Object obj) {
        LOG.log(Level.INFO, "Adding to add list: {0}", obj);
        added.add(obj);
    }
}
