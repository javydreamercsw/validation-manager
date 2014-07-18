package net.sourceforge.javydreamercsw.graph;

import com.validation.manager.core.api.entity.manager.IProjectRequirementEntityManager;
import com.validation.manager.core.api.entity.manager.VMEntityManager;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.RequirementServer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.Chart;
import javafx.scene.chart.PieChart;
import net.sourceforge.javydreamercsw.javafx.lib.ChartProvider;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = ChartProvider.class)
public class ProjectRequirementCoverageChartProvider implements ChartProvider<Project> {

    private Properties p = new Properties();
    private final Map<Properties, Class> props = new HashMap<>();
    private Map<String, Integer> stats = new HashMap<>();
    public static final String COVERED = "Covered", PARTIAL = "Partial",
            UNCOVERED = "Uncovered";
    public static final String REQUIREMENT_STATUSES
            = "included-requirement-status";
    public static final String INCLUDE_SUBPROJECTS
            = "included-subprojects";
    private List<Integer> enabledStatus = new ArrayList<>();
    private static final Logger LOG
            = Logger.getLogger(ProjectRequirementCoverageChartProvider.class.getSimpleName());
    private VMEntityManager rem = null;

    public ProjectRequirementCoverageChartProvider() {
        Properties temp = new Properties();
        temp.put(REQUIREMENT_STATUSES,
                "Included Requirement Statuses (ids separated by commas).");
        props.put(temp, String.class);
        temp.clear();
        temp.put(INCLUDE_SUBPROJECTS,
                "Included Subprojects.");
        props.put(temp, Boolean.class);
        //By default enable only Approved
        enabledStatus.add(2);
    }

    @Override
    public boolean supports(Class c) {
        return c.isInstance(Project.class) || c.equals(Project.class);
    }

    @Override
    public Chart getChart(Project entity) {
        //Reset Stats
        stats.clear();
        //Wait for RequirementEntityManager
        for (VMEntityManager m : Lookup.getDefault().lookupAll(VMEntityManager.class)) {
            if (m.supportEntity(Requirement.class)) {
                rem = m;
                break;
            }
        }
        if (rem != null) {
            while (!rem.isInitialized()) {
                try {
                    //Wait
                    LOG.fine("Waiting for Requirement Entity Manager.");
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            LOG.fine("Done waiting for Requirement Entity Manager!");
        }
        //Reset stats.
        stats.put(COVERED, 0);
        stats.put(PARTIAL, 0);
        stats.put(UNCOVERED, 0);
        addProjectRequirements(entity);
        ObservableList<PieChart.Data> pieChartData
                = FXCollections.observableArrayList();
        for (Entry<String, Integer> entry : stats.entrySet()) {
            LOG.log(Level.INFO, "{0}: {1}",
                    new Object[]{entry.getKey(), entry.getValue()});
            PieChart.Data data
                    = new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")",
                            entry.getValue());
                    pieChartData.add(data);
        }
        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Project Requirement Coverage");
        return chart;
    }

    private void addProjectRequirements(Project p) {
        LOG.log(Level.FINE, "Analyzing Project: {0}", p.getName());
        ProjectServer ps = new ProjectServer(p);
        LOG.log(Level.FINE, "Specs: {0}", ps.getRequirementSpecList().size());
        List<String> processed = new ArrayList<>();
        List<Requirement> requirements = new ArrayList<>();
        if (rem != null) {
            IProjectRequirementEntityManager irem
                    = (IProjectRequirementEntityManager) rem;
            requirements.addAll(irem.getEntities(p));
        } else {
            throw new RuntimeException("Unable to find a IProjectRequirementEntityManager!");
        }
        for (Requirement r : requirements) {
            //Only for the ids enabled
            if (enabledStatus.contains(r.getRequirementStatusId().getId())) {
                if (!processed.contains(r.getUniqueId())) {
                    LOG.log(Level.FINE, "Processing: {0} ({1})",
                            new Object[]{r.getUniqueId(),
                                r.getRequirementStatusId().getStatus()});
                    processed.add(r.getUniqueId());
                    int testCoverage = new RequirementServer(r).getTestCoverage();
                    LOG.log(Level.FINE, "Test Coverage: {0}", testCoverage);
                    if (testCoverage == 100) {
                        stats.put(COVERED, stats.get(COVERED) + 1);
                    } else if (testCoverage > 0) {
                        stats.put(PARTIAL, stats.get(PARTIAL) + 1);
                    } else {
                        stats.put(UNCOVERED, stats.get(UNCOVERED) + 1);
                    }
                }
            }
        }
        if (this.p.getOrDefault(INCLUDE_SUBPROJECTS, Boolean.TRUE) == Boolean.TRUE) {
            ps.getChildren().stream().forEach((child) -> {
                addProjectRequirements(child);
            });
        }
        LOG.log(Level.FINE, "Covered: {0}", stats.get(COVERED));
        LOG.log(Level.FINE, "Partial: {0}", stats.get(PARTIAL));
        LOG.log(Level.FINE, "Uncovered: {0}", stats.get(UNCOVERED));
    }

    @Override
    public String getName() {
        return "Project Requirement Coverage";
    }

    @Override
    public String getDescription() {
        return "Shows coverage of requirements for this project.";
    }

    @Override
    public Properties getProperties() {
        return p;
    }

    @Override
    public void setProperties(Properties props) {
        this.p = props;
        enabledStatus.clear();
        if (p.contains(REQUIREMENT_STATUSES)) {
            String statuses = (String) p.get(REQUIREMENT_STATUSES);
            StringTokenizer st = new StringTokenizer(statuses, ",");
            while (st.hasMoreTokens()) {
                enabledStatus.add(Integer.valueOf(st.nextToken().trim()));
            }
        }
    }

    @Override
    public Map<Properties, Class> getPropertyMap() {
        return props;
    }
}
