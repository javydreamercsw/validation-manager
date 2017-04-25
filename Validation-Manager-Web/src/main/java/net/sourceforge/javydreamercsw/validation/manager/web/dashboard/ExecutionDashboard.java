/*
 * This is the Execution dashboard window
 */
package net.sourceforge.javydreamercsw.validation.manager.web.dashboard;

import com.validation.manager.core.VMUI;
import com.validation.manager.core.db.ExecutionResult;
import com.validation.manager.core.server.core.ExecutionResultServer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.sourceforge.javydreamercsw.validation.manager.web.VMWindow;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI.TCEExtraction;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.openide.util.Lookup;
import org.vaadin.addon.JFreeChartWrapper;

/**
 *
 * @author Javier Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public final class ExecutionDashboard extends VMWindow {

    private final List<TCEExtraction> tce = new ArrayList<>();

    public ExecutionDashboard(List<TCEExtraction> tce) {
        this.tce.clear();
        this.tce.addAll(tce);
        init();
    }

    public ExecutionDashboard(TCEExtraction tce) {
        this.tce.clear();
        this.tce.add(tce);
        init();
    }

    private void init() {
        setCaption("Execution Dashboard");
        center();
        setHeight(100, Unit.PERCENTAGE);
        setWidth(100, Unit.PERCENTAGE);
        //Gather stats
        boolean legend = true;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (TCEExtraction e : tce) {
            ExecutionStats es = new ExecutionStats(e);
            for (Entry<String, Integer> entry : es.stats.entrySet()) {
                legend = e.getTestCase() != null;
                dataset.addValue(new Double(entry.getValue()),
                        e.getTestCase() == null ? "N/A" : e.getTestCase().getName(),
                        Lookup.getDefault().lookup(VMUI.class)
                                .translate(entry.getKey())
                );
            }
        }
        //Build bar graph
        JFreeChart chart = ChartFactory.createBarChart3D(
                "Execution Progress", // chart title
                "Test Case",
                "Amount",
                dataset, // data
                PlotOrientation.VERTICAL,
                legend, // include legend
                true,
                false);
        setContent(new JFreeChartWrapper(chart));
    }

    private class ExecutionStats {

        private Map<String, Integer> stats = new HashMap<>();
        private final TCEExtraction execution;

        public ExecutionStats(TCEExtraction execution) {
            this.execution = execution;
            ExecutionResultServer.getResults().forEach((er) -> {
                stats.put(er.getResultName(), 0);
            });
            execution.getTestCaseExecution().getExecutionStepList().forEach(es -> {
                ExecutionResult result = es.getResultId();
                if (execution.getTestCase() == null
                        || execution.getTestCase().getId()
                                .equals(es.getExecutionStepPK()
                                        .getStepTestCaseId())) {
                    if (result != null) {
                        stats.put(result.getResultName(),
                                stats.get(result.getResultName()) + 1);
                    } else {
                        String pending = ExecutionResultServer
                                .getResult("result.pending")
                                .getResultName();
                        stats.put(pending, stats.get(pending) + 1);
                    }
                }
            });
        }

        /**
         * @return the stats
         */
        public Map<String, Integer> getStats() {
            return stats;
        }

        /**
         * @return the total
         */
        public int getTotal() {
            return execution.getTestCaseExecution().getExecutionStepList().size();
        }
    }
}
