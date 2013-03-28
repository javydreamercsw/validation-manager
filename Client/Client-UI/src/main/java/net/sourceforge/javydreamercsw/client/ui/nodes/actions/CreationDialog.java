package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.SpecLevel;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import net.sourceforge.javydreamercsw.client.ui.DataBaseTool;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class CreationDialog extends JDialog {

    public CreationDialog(Frame owner, boolean modal) {
        super(owner, modal);
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

    protected void populateProjectList(JComboBox parent) {
        List<Project> projects = new ArrayList<Project>();
        if (DataBaseTool.getEmf() != null) {
            List<Object> projectList = DataBaseManager.createdQuery("select p from Project p order by p.id");
            for (Iterator<Object> it2 = projectList.iterator(); it2.hasNext();) {
                Project temp = (Project) it2.next();
                projects.add(temp);
            }
        }
        List<String> names = new ArrayList<String>();
        names.add("None");
        for (Iterator<Project> it3 = projects.iterator(); it3.hasNext();) {
            Project proj = it3.next();
            names.add(proj.getName());
        }
        parent.setModel(new DefaultComboBoxModel(names.toArray(new String[projects.size() + 1])));
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
            names.add(lvl.getName());
        }
        level.setModel(new DefaultComboBoxModel(names.toArray(new String[levels.size()])));
    }
}
