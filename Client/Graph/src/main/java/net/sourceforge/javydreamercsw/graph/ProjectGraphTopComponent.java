package net.sourceforge.javydreamercsw.graph;

import com.validation.manager.core.db.Project;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import net.sourceforge.javydreamercsw.javafx.lib.ChartProvider;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//net.sourceforge.javydreamercsw.graph//ProjectGraph//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "ProjectGraphTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "net.sourceforge.javydreamercsw.graph.ProjectGraphTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_ProjectGraphAction",
        preferredID = "ProjectGraphTopComponent"
)
@Messages({
    "CTL_ProjectGraphAction=Project Graph",
    "CTL_ProjectGraphTopComponent=Project Graph Window",
    "HINT_ProjectGraphTopComponent=This is a Project Graph window"
})
public final class ProjectGraphTopComponent extends TopComponent
        implements LookupListener {

    private static final int PANEL_WIDTH_INT = 675;
    private static final int PANEL_HEIGHT_INT = 400;
    private static JFXPanel chartFxPanel;
    private Chart chart;
    private Lookup.Result<Project> result = null;
    private Project currentProject;

    public ProjectGraphTopComponent() {
        initComponents();
        setName(Bundle.CTL_ProjectGraphTopComponent());
        setToolTipText(Bundle.HINT_ProjectGraphTopComponent());
        // create javafx panel for charts
        chartFxPanel = new JFXPanel();
        chartFxPanel.setPreferredSize(new Dimension(PANEL_WIDTH_INT, PANEL_HEIGHT_INT));

        //JTable
        DecimalFormatRenderer renderer = new DecimalFormatRenderer();
        renderer.setHorizontalAlignment(JLabel.RIGHT);
        chartTablePanel.setLayout(new BorderLayout());
        chartTablePanel.add(chartFxPanel, BorderLayout.CENTER);
    }

    public Scene getScene() {
        return chartFxPanel.getScene();
    }

    @Override
    public void resultChanged(LookupEvent le) {
        Lookup.Result res = (Lookup.Result) le.getSource();
        Collection instances = res.allInstances();

        if (!instances.isEmpty()) {
            Iterator it = instances.iterator();
            while (it.hasNext()) {
                Object item = it.next();
                if (item instanceof Project) {
                    Project p = (Project) item;
                    // create JavaFX scene
                    Platform.runLater(() -> {
                        updateProject(p);
                    });
                }
            }
        }
    }

    private void updateProject(Project newProject) {
        if (newProject != null) {
            if (currentProject == null
                    || (currentProject != null
                    && !Objects.equals(currentProject.getId(), newProject.getId()))) {
                currentProject = newProject;
                //Look for providers
                for (ChartProvider cp : Lookup.getDefault().lookupAll(ChartProvider.class)) {
                    if (cp.supports(Project.class)) {
                        chart = cp.getChart(newProject);
                        chartFxPanel.setScene(new Scene(chart));
                    }
                }
            }
        }
    }

    private static class DecimalFormatRenderer
            extends DefaultTableCellRenderer {

        private static final DecimalFormat formatter
                = new DecimalFormat("#.0");

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            value = formatter.format((Number) value);
            return super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chartTablePanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridLayout());

        javax.swing.GroupLayout chartTablePanelLayout = new javax.swing.GroupLayout(chartTablePanel);
        chartTablePanel.setLayout(chartTablePanelLayout);
        chartTablePanelLayout.setHorizontalGroup(
            chartTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 468, Short.MAX_VALUE)
        );
        chartTablePanelLayout.setVerticalGroup(
            chartTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 423, Short.MAX_VALUE)
        );

        add(chartTablePanel);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chartTablePanel;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        result = Utilities.actionsGlobalContext().lookupResult(Project.class);
        result.allItems();
        result.addLookupListener(this);
    }

    @Override
    public void componentClosed() {
        result.removeLookupListener(this);
        result = null;
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
