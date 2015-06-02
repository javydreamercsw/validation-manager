package net.sourceforge.javydreamercsw.graph;

import com.validation.manager.core.api.entity.manager.VMEntityManager;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.Step;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;
import net.sourceforge.javydreamercsw.graph.dialog.GraphOptionDialog;
import net.sourceforge.javydreamercsw.javafx.lib.ChartProvider;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

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
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "net.sourceforge.javydreamercsw.graph.ProjectDetailsTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_ProjectDetailsAction",
        preferredID = "ProjectDetailsTopComponent"
)
@Messages({
    "CTL_ProjectDetailsAction=Project Details",
    "CTL_ProjectDetailsTopComponent=Project Details Window",
    "HINT_ProjectDetailsTopComponent=This is a Project Details window"
})
public final class ProjectDetailsTopComponent extends TopComponent
        implements LookupListener {

    private static final long serialVersionUID = -1769266654873679426L;
    private static JFXPanel chartFxPanel;
    private Lookup.Result<Project> result = null;
    private Project currentProject;
    private VMEntityManager rem = null;
    private TilePane pane = new TilePane();
    private List<Integer> projects = new ArrayList<>();

    public ProjectDetailsTopComponent() {
        initComponents();
        setName(Bundle.CTL_ProjectDetailsTopComponent());
        setToolTipText(Bundle.HINT_ProjectDetailsTopComponent());
        WindowManager.getDefault().invokeWhenUIReady(() -> {
            // create javafx panel for charts
            chartFxPanel = new JFXPanel();
            chartFxPanel.setPreferredSize(getSize());
            //JTable
            DecimalFormatRenderer renderer = new DecimalFormatRenderer();
            renderer.setHorizontalAlignment(JLabel.RIGHT);
            chartTablePanel.setLayout(new BorderLayout());
            chartTablePanel.add(chartFxPanel, BorderLayout.CENTER);
        });
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

    class GraphRetriever extends Thread {

        private final ChartProvider cp;

        public GraphRetriever(ChartProvider cp) {
            super("Chart Retriever for: " + cp.getName());
            this.cp = cp;
        }

        @Override
        public void run() {
            Chart chart = cp.getChart(currentProject);
            chart.setOnMousePressed((MouseEvent event) -> {
                if (event.isSecondaryButtonDown()) {
                    new GraphOptionDialog(cp, null, true).setVisible(true);
                }
            });
            pane.getChildren().add(chart);
        }
    }

    private void updateProject(Project newProject) {
        if (newProject != null) {
            if (currentProject == null
                    || (currentProject != null
                    && !Objects.equals(currentProject.getId(), newProject.getId()))) {
                currentProject = newProject;
                if (!projects.contains(currentProject.getId())) {
                    createProjectDetails(currentProject);
                    projects.add(currentProject.getId());
                }
                VBox box = new VBox();
                ScrollPane sp = new ScrollPane();
                sp.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
                sp.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
                sp.setContent(pane);
                sp.setPrefHeight(getSize().height);
                sp.setPrefWidth(getSize().width);
                sp.setFitToHeight(true);
                sp.setFitToWidth(true);
                box.getChildren().add(sp);
                Scene scene = new Scene(box, getSize().width, getSize().height);
                chartFxPanel.setScene(scene);
                //Clear grpahs for the new ones
                pane.getChildren().clear();
                //Look for providers
                Lookup.getDefault().lookupAll(ChartProvider.class)
                        .stream().filter((cp) -> (cp.supports(Project.class)))
                        .forEach((cp) -> {
                            Platform.runLater(new GraphRetriever(cp));
                        });
            }
        }
    }

    private static class DecimalFormatRenderer
            extends DefaultTableCellRenderer {

        private static final DecimalFormat formatter
                = new DecimalFormat("#.0");
        private static final long serialVersionUID = -7787226130899799277L;

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            value = formatter.format((Number) value);
            return super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);
        }
    }

    private void createProjectDetails(Project p) {
        JScrollPane detailsPane = new javax.swing.JScrollPane();
        JTable detailTable = new javax.swing.JTable();
        //Wait for RequirementEntityManager
        for (VMEntityManager m : Lookup.getDefault()
                .lookupAll(VMEntityManager.class)) {
            if (m.supportEntity(Requirement.class)) {
                rem = m;
                break;
            }
        }
        if (rem != null) {
            while (!rem.isInitialized()) {
                try {
                    //Wait
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        List<Requirement> requirements = new ArrayList<>();
        if (rem == null) {
            throw new RuntimeException("Unable to find a VMEntityManager "
                    + "for Requirements!");
        }
        //Need to sort requirements by Requirement Spec level
        Map<Integer, List<Requirement>> specs = new TreeMap<>();
        List<String> processed = new ArrayList<>();
        for (RequirementSpec rs : p.getRequirementSpecList()) {
            int id = rs.getRequirementSpecPK().getId();
            if (!specs.containsKey(id)) {
                specs.put(id, new ArrayList<>());
            }
            for (RequirementSpecNode rsn
                    : rs.getRequirementSpecNodeList()) {
                for (Requirement r : rsn.getRequirementList()) {
                    //Unprocessed and approved requirements only.
                    if (!processed.contains(r.getUniqueId())
                            && r.getRequirementStatusId().getId() == 2) {
                        specs.get(id).add((Requirement) rem.getEntity(r.getUniqueId()));
                        processed.add(r.getUniqueId());
                    }
                }
            }
        }
        specs.entrySet().stream().forEach((entry) -> {
            requirements.addAll(entry.getValue());
        });
        Collections.sort(requirements, (Requirement o1, Requirement o2)
                -> o1.getUniqueId().compareTo(o2.getUniqueId()));
        Object[][] values = new Object[requirements.size()][3];
        int i = 0;
        for (Requirement r : requirements) {
            values[i][0] = r.getUniqueId();
            values[i][1] = r.getDescription();
            //See for displaying a table within a table: 
            //http://blog.marcnuri.com/blog/default/2007/04/04/Displaying-a-jTable-inside-another-jTable-JTable-cellRenderer
            List<String> list = new ArrayList<>();
            for (Step step : r.getStepList()) {
                list.add("Test: "
                        + step.getTestCase().getTest().getName()
                        + ", Test Case: "
                        + step.getTestCase().getName()
                        + ", step "
                        + step.getStepSequence());
            }
            String[] arr = list.toArray(new String[list.size()]);
            values[i][2] = arr;
            i++;
        }
        detailTable.setModel(new javax.swing.table.DefaultTableModel(
                values,
                new String[]{
                    "Requirement", "Description", "Coverage"
                }
        ) {
            private static final long serialVersionUID = -6158042587124966771L;
            Class[] types = new Class[]{
                java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean[]{
                false, false, false
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        //Set renderer for the coverage
        TableColumnModel tcm = detailTable.getColumnModel();
        TableColumn tc = tcm.getColumn(2);
        tc.setCellRenderer(new TableCellRenderer() {
            /* These are necessary variables to store the row's height */
            private int minHeight = -1;
            private int currHeight = -1;
            /* Magic Happens */

            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                /* If what we're displaying isn't an array of values we
                 return the normal renderer*/
                if (value.getClass().isArray()) {
                    final Object[] passed = (Object[]) value;
                    /* We calculate the row's height to display data
                     * THis is not complete and has some bugs that
                     * will be analyzed in further articles */
                    if (minHeight == -1) {
                        minHeight = table.getRowHeight();
                    }
                    if (currHeight != passed.length * minHeight) {
                        currHeight = passed.length * minHeight;
                        table.setRowHeight(row, currHeight < 1 ? 1 : currHeight);
                    }
                    /* We create the table that will hold the multivalue
                     *fields and that will be embedded in the main table */
                    return new JTable(
                            new AbstractTableModel() {
                                private static final long serialVersionUID = 7434377947963338162L;

                                @Override
                                public int getColumnCount() {
                                    return 1;
                                }

                                @Override
                                public int getRowCount() {
                                    return passed.length;
                                }

                                @Override
                                public Object getValueAt(int rowIndex,
                                        int columnIndex) {
                                    return passed[rowIndex];
                                }

                                @Override
                                public boolean isCellEditable(int row,
                                        int col) {
                                    return true;
                                }
                            });
                } else {
                    return table.getDefaultRenderer(
                            value.getClass()).getTableCellRendererComponent(
                                    table, value, isSelected, hasFocus,
                                    row, column);
                }
            }
        });
        detailsPane.setViewportView(detailTable);

        jTabbedPane1.addTab("Details (" + p.getName() + ")", detailsPane); // NOI18N
    }

    public class LineWrapCellRenderer extends JScrollPane implements TableCellRenderer {

        private static final long serialVersionUID = 2766827135113284343L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            try {
                JTextArea area = createTextAreaFitToText((String) value,
                        this.getPreferredSize());
                add(area);
            } catch (BadLocationException ex) {
                add(new JLabel((String) value));
            }
            return this;
        }

        public JTextArea createTextAreaFitToText(String message,
                Dimension minimalSize) throws BadLocationException {

            JTextArea aMessagePanel = new JTextArea();
            aMessagePanel.setText(message);

            /*for modelToView to work, the text area has to be sized. It doesn't matter if it's visible or not.*/
            aMessagePanel.setPreferredSize(minimalSize);
            aMessagePanel.setSize(minimalSize);

            Rectangle r = aMessagePanel.modelToView(aMessagePanel.getDocument().getLength());

            Dimension d = new Dimension(minimalSize.width, r.y + r.height);
            aMessagePanel.setPreferredSize(d);
            return aMessagePanel;

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        chartTablePanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        javax.swing.GroupLayout chartTablePanelLayout = new javax.swing.GroupLayout(chartTablePanel);
        chartTablePanel.setLayout(chartTablePanelLayout);
        chartTablePanelLayout.setHorizontalGroup(
            chartTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 395, Short.MAX_VALUE)
        );
        chartTablePanelLayout.setVerticalGroup(
            chartTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 273, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(ProjectDetailsTopComponent.class, "ProjectDetailsTopComponent.chartTablePanel.TabConstraints.tabTitle"), chartTablePanel); // NOI18N

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chartTablePanel;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        result = Utilities.actionsGlobalContext().lookupResult(Project.class);
        result.allItems();
        result.addLookupListener(this);
        currentProject = Utilities.actionsGlobalContext().lookup(Project.class);
        if (currentProject != null) {
            //There's a project already selected. Display it's graphs.
            //This is usefull if the window is opened after project is already selected.
            // create JavaFX scene
            Platform.runLater(() -> {
                updateProject(currentProject);
            });
        }
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
