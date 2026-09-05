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

import static net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider.TRANSLATOR;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.openide.util.Lookup;
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
        setHeaderTitle(TRANSLATOR.translate("execution.dash"));
        setWidth("100%");
        setHeight("100%");
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
        //Render the chart to PNG bytes (the JFreeChartWrapper add-on has no
        //Flow port).
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ChartUtils.writeChartAsPNG(bos, chart, 800, 600);
            byte[] png = bos.toByteArray();
            StreamResource resource = new StreamResource("execution-dash.png",
                    () -> new ByteArrayInputStream(png));
            Image image = new Image(resource,
                    TRANSLATOR.translate("execution.progress"));
            image.setWidth("100%");
            image.setHeight("100%");
            add(image);
        } catch (java.io.IOException ex) {
            //Fell back to the raw chart title on rendering failure
            com.vaadin.flow.component.html.Span error
                    = new com.vaadin.flow.component.html.Span(
                            TRANSLATOR.translate("execution.progress"));
            add(error);
        }
        open();
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
