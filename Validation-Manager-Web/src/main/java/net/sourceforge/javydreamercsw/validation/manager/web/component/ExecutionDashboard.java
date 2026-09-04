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

import static com.validation.manager.core.ContentProvider.TRANSLATOR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.openide.util.Lookup;
import org.vaadin.addon.JFreeChartWrapper;

import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.ExecutionResult;
import com.validation.manager.core.server.core.ExecutionResultServer;
import com.validation.manager.core.tool.TCEExtraction;

/**
 *
 * @author Javier Ortiz Bultronjavier.ortiz.78@gmail.com
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
        setCaption(TRANSLATOR.translate("execution.dash"));
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
                                Lookup.getDefault().lookup(InternationalizationProvider.class)
                                        .translate(er.getResultName())
                        );
                    }
                });
            });
        });
        //Build bar graph
        JFreeChart chart = ChartFactory.createBarChart(                TRANSLATOR.translate("execution.progress"), // chart title
                TRANSLATOR.translate("test.case"),
                TRANSLATOR.translate("general.amount"),
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
