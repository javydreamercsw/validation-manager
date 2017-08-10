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
import com.vaadin.pontus.vizcomponent.VizComponent;
import com.vaadin.pontus.vizcomponent.model.Graph;
import com.vaadin.pontus.vizcomponent.model.Subgraph;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Panel;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Workflow;
import com.validation.manager.core.db.WorkflowStep;
import com.validation.manager.core.db.controller.WorkflowJpaController;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VMWindow;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class WorkflowViewer extends VMWindow {

    private final VizComponent diagram = new VizComponent();
    private final Map<Integer, Graph.Node> nodes = new HashMap<>();

    public WorkflowViewer() {
        super(TRANSLATOR.translate("workflow.manager"));
        init();
    }

    public WorkflowViewer(String caption) {
        super(caption);
        init();
    }

    protected void init() {
        setSizeFull();
        Panel p = new Panel();
        HorizontalSplitPanel hs = new HorizontalSplitPanel();
        hs.setSplitPosition(25, Unit.PERCENTAGE);
        ListSelect workflows
                = new ListSelect(TRANSLATOR.translate("general.workflow"));
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
        });
        hs.setFirstComponent(workflows);
        diagram.setSizeFull();
        hs.setSecondComponent(diagram);
        p.setSizeFull();
        p.setContent(hs);
        setContent(p);
    }

    private void displayWorkflow(Workflow w) {
        Graph graph = new Graph(w.getWorkflowName(), Graph.DIGRAPH);
        nodes.clear();
        //Create the nodes
        w.getWorkflowStepList().forEach(step -> {
            addStep(step);
            //Now add the links
            step.getSourceTransitions().forEach(t -> {
                addStep(t.getWorkflowStepTarget());
                Subgraph.Edge edge
                        = graph.addEdge(nodes.get(t.getWorkflowStepSource()
                                .getWorkflowStepPK().getId()),
                        nodes.get(t.getWorkflowStepTarget()
                                        .getWorkflowStepPK().getId()));
                edge.setParam("label", TRANSLATOR.translate(t.getTransitionName()));
            });
        });
        diagram.drawGraph(graph);
    }

    private void addStep(WorkflowStep step) {
        if (!nodes.containsKey(step.getWorkflowStepPK().getId())) {
            Graph.Node node
                    = new Graph.Node(TRANSLATOR.translate(step.getStepName()));
            nodes.put(step.getWorkflowStepPK().getId(), node);
        }
    }
}
