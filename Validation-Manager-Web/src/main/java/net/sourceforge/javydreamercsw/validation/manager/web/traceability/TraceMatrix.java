package net.sourceforge.javydreamercsw.validation.manager.web.traceability;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.validation.manager.core.db.Baseline;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.tool.Tool;
import de.datenhahn.vaadin.componentrenderer.ComponentRenderer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Trace Matrix component. Traces relationship from requirements to test case
 * steps including results and issues.
 *
 * @author Javier Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public class TraceMatrix extends Grid {

    private final Project p;

    public TraceMatrix(Project p) {
        this.p = p;
        init();
    }

    public TraceMatrix(Project p, String caption) {
        super(caption);
        this.p = p;
        init();
    }

    private void init() {
        //Let's build the trace matrix. First we link higher level requiremest
        //all the way to lower level.
        Map<Integer, List<Requirement>> map = new TreeMap<>();
        //TODO: Incorporate requirement level. For now assume all are same level.
        Tool.extractRequirements(p).forEach((r) -> {
            if (!map.containsKey(1)) {
                map.put(1, new ArrayList<>());
            }
            map.get(1).add(r);
        });
        BeanItemContainer<Requirement> reqs
                = new BeanItemContainer<>(Requirement.class);
        map.entrySet().forEach((entry) -> {
            reqs.addAll(entry.getValue());
        });
        GeneratedPropertyContainer wrapperCont
                = new GeneratedPropertyContainer(reqs);
        setContainerDataSource(wrapperCont);
        wrapperCont.addGeneratedProperty("executions",
                new PropertyValueGenerator<Label>() {
            @Override
            public Label getValue(Item item, Object itemId, Object propertyId) {
                Requirement v = (Requirement) itemId;
                StringBuilder sb = new StringBuilder();
                for (History h : v.getHistoryList()) {
                    if (!h.getBaselineList().isEmpty()) {
                        for (Baseline b : h.getBaselineList()) {
                            sb.append(b.getBaselineName());
                        }
                    }
                }
                return new Label(sb.toString());
            }

            @Override
            public Class<Label> getType() {
                return Label.class;
            }
        });
        setHeightMode(HeightMode.ROW);
        setHeightByRows(wrapperCont.size());
        setColumns("uniqueId", "executions");
        Grid.Column uniqueId = getColumn("uniqueId");
        uniqueId.setHeaderCaption("ID");
        Grid.Column executions = getColumn("executions");
        executions.setHeaderCaption("Execution(s)");
        executions.setRenderer(new ComponentRenderer());
        wrapperCont.sort(new Object[]{"uniqueId"}, new boolean[]{true});
    }
}