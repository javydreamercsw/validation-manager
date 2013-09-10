package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.RequirementStatus;
import com.validation.manager.core.db.RequirementType;
import com.validation.manager.core.db.SpecLevel;
import java.awt.Component;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import net.sourceforge.javydreamercsw.client.ui.components.database.DataBaseTool;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractCreationDialog extends JDialog {

    private static ResourceBundle rb =
            ResourceBundle.getBundle("com.validation.manager.resources.VMMessages");

    public AbstractCreationDialog(Frame owner, boolean modal) {
        super(owner, modal);
    }

    protected String internationalize(String string) {
        return rb.containsKey(string) ? rb.getString(string) : string;
    }

    protected SpecLevel getSelectedSpecLevel(JComboBox level) {
        SpecLevel specLevel = null;
        if (level.getSelectedIndex() > -1) {
            if (DataBaseTool.getEmf() != null) {
                List<Object> projectList = DataBaseManager.createdQuery(
                        "select sl from SpecLevel sl where sl.name='"
                        + level.getSelectedItem() + "'");
                for (Iterator<Object> it2 = projectList.iterator(); it2.hasNext();) {
                    SpecLevel temp = ((SpecLevel) it2.next());
                    if (temp.getName().equals(level.getSelectedItem().toString())) {
                        specLevel = temp;
                        break;
                    }
                }
            }
        }
        return specLevel;
    }

    protected RequirementType getSelectedRequirementType(JComboBox type) {
        RequirementType rt = null;
        if (type.getSelectedIndex() > -1) {
            if (DataBaseTool.getEmf() != null) {
                List<Object> requirementTypeList = DataBaseManager.createdQuery(
                        "select rt from RequirementType rt where rt.name='"
                        + type.getSelectedItem() + "'");
                for (Iterator<Object> it2 = requirementTypeList.iterator(); it2.hasNext();) {
                    RequirementType temp = ((RequirementType) it2.next());
                    if (temp.getName().equals(type.getSelectedItem().toString())) {
                        rt = temp;
                        break;
                    }
                }
            }
        }
        return rt;
    }

    protected RequirementStatus getSelectedRequirementStatus(JComboBox status) {
        RequirementStatus rs = null;
        if (status.getSelectedIndex() > -1) {
            if (DataBaseTool.getEmf() != null) {
                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("id",
                        ((RequirementStatus) status.getSelectedItem()).getId());
                rs = (RequirementStatus) DataBaseManager.namedQuery(
                        "RequirementStatus.findById", parameters).get(0);
            }
        }
        return rs;
    }

    protected Project getSelectedProject(JComboBox list) {
        Project parentProject = null;
        if (list.getSelectedIndex() > 0) {
            if (DataBaseTool.getEmf() != null) {
                List<Object> projectList = DataBaseManager.createdQuery(
                        "select p from Project p where p.name='"
                        + list.getSelectedItem() + "'");
                for (Iterator<Object> it2 = projectList.iterator(); it2.hasNext();) {
                    Project temp = ((Project) it2.next());
                    if (temp.getName().equals(list.getSelectedItem().toString())) {
                        parentProject = temp;
                        break;
                    }
                }
            }
        }
        return parentProject;
    }

    protected void populateProjectList(JComboBox parent, boolean addNull) {
        List<Project> projects = new ArrayList<Project>();
        if (DataBaseTool.getEmf() != null) {
            List<Object> projectList = DataBaseManager.createdQuery("select p from Project p order by p.id");
            for (Iterator<Object> it2 = projectList.iterator(); it2.hasNext();) {
                Project temp = (Project) it2.next();
                projects.add(temp);
            }
        }
        List<String> names = new ArrayList<String>();
        if (addNull) {
            names.add("None");
        }
        for (Iterator<Project> it3 = projects.iterator(); it3.hasNext();) {
            Project proj = it3.next();
            names.add(internationalize(proj.getName()));
        }
        parent.setModel(new DefaultComboBoxModel(names.toArray(new String[projects.size() + (addNull ? 1 : 0)])));
    }

    protected void populateSpecLevelList(JComboBox level) {
        List<SpecLevel> levels = new ArrayList<SpecLevel>();
        if (DataBaseTool.getEmf() != null) {
            List<Object> projectList = DataBaseManager.createdQuery(
                    "select sl from SpecLevel sl order by sl.id");
            for (Iterator<Object> it2 = projectList.iterator(); it2.hasNext();) {
                SpecLevel temp = (SpecLevel) it2.next();
                levels.add(temp);
            }
        }
        List<String> names = new ArrayList<String>();
        for (Iterator<SpecLevel> it3 = levels.iterator(); it3.hasNext();) {
            SpecLevel lvl = it3.next();
            names.add(internationalize(lvl.getName()));
        }
        level.setModel(new DefaultComboBoxModel(names.toArray(new String[levels.size()])));
    }

    protected void populateRequirementTypeList(JComboBox type) {
        List<RequirementType> types = new ArrayList<RequirementType>();
        if (DataBaseTool.getEmf() != null) {
            List<Object> projectList = DataBaseManager.createdQuery(
                    "select rt from RequirementType rt order by rt.id");
            for (Iterator<Object> it2 = projectList.iterator(); it2.hasNext();) {
                RequirementType temp = (RequirementType) it2.next();
                types.add(temp);
            }
        }
        List<String> names = new ArrayList<String>();
        for (Iterator<RequirementType> it3 = types.iterator(); it3.hasNext();) {
            RequirementType lvl = it3.next();
            names.add(internationalize(lvl.getName()));
        }
        type.setModel(new DefaultComboBoxModel(names.toArray(new String[types.size()])));
    }

    protected void populateRequirementStatusList(JComboBox status) {
        status.setRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected, boolean cellHasFocus) {
                RequirementStatus rs = (RequirementStatus) value;
                return new JLabel(rb.containsKey(rs.getStatus())
                        ? rb.getString(rs.getStatus()) : rs.getStatus());
            }
        });
        List<RequirementStatus> statusses = new ArrayList<RequirementStatus>();
        if (DataBaseTool.getEmf() != null) {
            List<Object> projectList = DataBaseManager.createdQuery(
                    "select rs from RequirementStatus rs order by rs.id");
            for (Iterator<Object> it2 = projectList.iterator(); it2.hasNext();) {
                RequirementStatus temp = (RequirementStatus) it2.next();
                statusses.add(temp);
            }
        }
        status.setModel(new DefaultComboBoxModel(statusses.toArray(new RequirementStatus[statusses.size()])));
    }
}
