package com.validation.manager.core.tool;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.server.core.ProjectServer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.ImageIcon;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class Tool {

    public static ImageIcon createImageIcon(String path, String description) {
        return createImageIcon(path, description, null);
    }

    public static ImageIcon createImageIcon(String path, String description, Class relativeTo) {
        URL imgURL = relativeTo == null ? Tool.class.getResource(path)
                : relativeTo.getResource(path);
        return imgURL == null ? null : new ImageIcon(imgURL, description);
    }

    public static void removeDuplicates(List list) {
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (set.add(element)) {
                newList.add(element);
            }
        }
        list.clear();
        list.addAll(newList);
    }

    public static List<Requirement> extractRequirements(Project p) {
        ProjectServer ps;
        if (p instanceof ProjectServer) {
            ps = (ProjectServer) p;
        } else {
            ps = new ProjectServer(p);
        }
        List<Requirement> result = new ArrayList<>();
        ps.getRequirementSpecList().forEach(rs -> {
            result.addAll(extractRequirements(rs));
        });
        ps.getProjectList().forEach(sub -> {
            result.addAll(extractRequirements(sub));
        });
        return result;
    }

    public static List<Requirement> extractRequirements(RequirementSpecNode rsn) {
        ArrayList<Requirement> result = new ArrayList<>();
        rsn.getRequirementSpecNodeList().forEach(rsn2 -> {
            result.addAll(extractRequirements(rsn2));
        });
        result.addAll(rsn.getRequirementList());
        return result;
    }

    public static List<Requirement> extractRequirements(RequirementSpec rs) {
        ArrayList<Requirement> result = new ArrayList<>();
        rs.getRequirementSpecNodeList().forEach(rsn -> {
            result.addAll(extractRequirements(rsn));
        });
        return result;
    }
}
