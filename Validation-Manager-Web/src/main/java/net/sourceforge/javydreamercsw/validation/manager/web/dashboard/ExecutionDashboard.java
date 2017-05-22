/*
 * This is the Execution dashboard window
 */
package net.sourceforge.javydreamercsw.validation.manager.web.dashboard;

import com.validation.manager.core.db.ExecutionResult;
import com.validation.manager.core.server.core.ExecutionResultServer;
import com.validation.manager.core.tool.TCEExtraction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.sourceforge.javydreamercsw.validation.manager.web.VMWindow;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.vaadin.addon.JFreeChartWrapper;

/**
 *
 * @author Javier Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public final class ExecutionDashboard extends VMWindow {

    private final List<TCEExtraction> extractions = new ArrayList<>();

    public ExecutionDashboard(List<TCEExtraction> tce) {
        this.extractions.clear();
        this.extractions.addAll(tce);
        init();
    }

    public ExecutionDashboard(TCEExtraction tce) {
        this.extractions.clear();
        this.extractions.add(tce);
        init();
    }

    private void init() {
        setCaption("execution.dash");
        center();
        setHeight(100, Unit.PERCENTAGE);
        setWidth(100, Unit.PERCENTAGE);
        //Gather stats
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        extractions.forEach((e) -> {
            ExecutionStats es = new ExecutionStats(e);
            es.stats.entrySet().forEach((entry) -> {
                //This is for the whole execution
                ExecutionResultServer.getResults().forEach((er) -> {
                    if (e.getTestCase() == null
                            || e.getTestCase().getName().equals(entry.getKey())) {
                        dataset.addValue(new Double(entry.getValue()
                                .get(er.getResultName())),
                                entry.getKey(),
                                ValidationManagerUI.getInstance()
                                        .translate(er.getResultName())
                        );
                    }
                });
            });
        });
        //Build bar graph
        JFreeChart chart = ChartFactory.createBarChart3D(
                "execution.progress", // chart title
                "test.case",
                "general.amount",
                dataset, // data
                PlotOrientation.VERTICAL,
                true, // include legend
                true,
                false);
        setContent(new JFreeChartWrapper(chart));
    }

    private class ExecutionStats {

        //----------Test Case, Result, Amount
        private Map<String, Map<String, Integer>> stats = new TreeMap<>();
        private final TCEExtraction execution;

        public ExecutionStats(TCEExtraction execution) {
            this.execution = execution;
            execution.getTestCaseExecution().getExecutionStepList().forEach(es -> {
                ExecutionResult result = es.getResultId();
                String tcName = es.getStep().getTestCase().getName();
                if (!stats.containsKey(tcName)) {
                    stats.put(tcName, getResultMap());
                }
                if (result != null) {
                    stats.get(tcName).put(result.getResultName(),
                            stats.get(tcName).get(result.getResultName()) + 1);
                } else {
                    String pending = ExecutionResultServer
                            .getResult("result.pending")
                            .getResultName();
                    stats.get(tcName).put(pending,
                            stats.get(tcName).get(pending) + 1);
                }
            });
        }

        //Creates a clean map to store results.
        private Map<String, Integer> getResultMap() {
            Map<String, Integer> results = new HashMap<>();
            ExecutionResultServer.getResults().forEach((er) -> {
                results.put(er.getResultName(), 0);
            });
            return results;
        }

        /**
         * @return the stats
         */
        public Map<String, Map<String, Integer>> getStats() {
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
