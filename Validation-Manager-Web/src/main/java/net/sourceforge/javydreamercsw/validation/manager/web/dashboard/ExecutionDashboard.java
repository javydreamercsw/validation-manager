/*
 * This is the Execution dashboard window
 */
package net.sourceforge.javydreamercsw.validation.manager.web.dashboard;

import com.validation.manager.core.VMUI;
import com.validation.manager.core.db.ExecutionResult;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.server.core.ExecutionResultServer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.javydreamercsw.validation.manager.web.VMWindow;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.Rotation;
import org.openide.util.Lookup;
import org.vaadin.addon.JFreeChartWrapper;

/**
 *
 * @author Javier Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public final class ExecutionDashboard extends VMWindow {

    private final List<TestCaseExecution> tce = new ArrayList<>();
    private final Map<String, Integer> stats = new HashMap<>();

    public ExecutionDashboard(List<TestCaseExecution> tce) {
        this.tce.clear();
        this.tce.addAll(tce);
        init();
    }

    public ExecutionDashboard(TestCaseExecution tce) {
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
        int totalTestCases = 0;
        totalTestCases = tce.stream().map((e) -> {
            ExecutionStats es = new ExecutionStats(e);
            es.stats.entrySet().forEach(entry -> {
                stats.put(entry.getKey(), entry.getValue());
            });
            return e;
        }).map((e) -> e.getExecutionStepList().size())
                .reduce(totalTestCases, Integer::sum);
        //Build percentage pie graph
        DefaultPieDataset dataset = new DefaultPieDataset();
        stats.entrySet().forEach((entry) -> {
            dataset.setValue(Lookup.getDefault().lookup(VMUI.class)
                    .translate(entry.getKey()),
                    new Double(entry.getValue()));
        });
        JFreeChart chart = ChartFactory.createPieChart3D(
                "Execution Progress", // chart title
                dataset, // data
                true, // include legend
                true,
                false);

        final PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(270);
        plot.setForegroundAlpha(0.60f);
        plot.setInteriorGap(0.02);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setNoDataMessage("No data to display");
        setContent(new JFreeChartWrapper(chart));
    }

    private class ExecutionStats {

        private Map<String, Integer> stats = new HashMap<>();
        private final TestCaseExecution tce;

        public ExecutionStats(TestCaseExecution tce) {
            this.tce = tce;
            ExecutionResultServer.getResults().forEach((er) -> {
                stats.put(er.getResultName(), 0);
            });
            tce.getExecutionStepList().forEach(es -> {
                ExecutionResult result = es.getResultId();
                if (result != null) {
                    stats.put(result.getResultName(),
                            stats.get(result.getResultName()) + 1);
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
            return tce.getExecutionStepList().size();
        }
    }
}
