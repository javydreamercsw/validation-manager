package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpecNodePK;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.db.controller.RequirementSpecNodeJpaController;
import com.validation.manager.core.db.controller.RequirementStatusJpaController;
import com.validation.manager.core.db.controller.RequirementTypeJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.tool.Tool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class RequirementServer extends Requirement
        implements EntityServer<Requirement>, VersionableServer<Requirement> {

    private static final Logger LOG
            = Logger.getLogger(RequirementServer.class.getSimpleName());

    public RequirementServer(String id, String desc, RequirementSpecNodePK rsn,
            String notes, int requirementType, int requirementStatus) {
        setNotes(notes);
        setRequirementSpecNode(
                new RequirementSpecNodeJpaController(DataBaseManager
                        .getEntityManagerFactory()).findRequirementSpecNode(rsn));
        setUniqueId(id);
        setDescription(desc);
        setRequirementStatusId(new RequirementStatusJpaController(
                DataBaseManager.getEntityManagerFactory()).findRequirementStatus(requirementStatus));
        setRequirementTypeId(new RequirementTypeJpaController(
                DataBaseManager.getEntityManagerFactory()).findRequirementType(requirementType));
    }

    public static void deleteRequirement(Requirement r)
            throws IllegalOrphanException, NonexistentEntityException {
        RequirementJpaController controller
                = new RequirementJpaController(DataBaseManager.getEntityManagerFactory());
        if (controller.findRequirement(r.getId()) != null) {
            controller.destroy(r.getId());
        }
    }

    public RequirementServer(Requirement r) {
        RequirementJpaController controller
                = new RequirementJpaController(DataBaseManager.getEntityManagerFactory());
        Requirement requirement = controller.findRequirement(r.getId());
        if (requirement != null) {
            update((RequirementServer) this, requirement);
        } else {
            throw new RuntimeException("Unable to find requirement with id: "
                    + r.getId());
        }
    }

    @Override
    public int write2DB() throws Exception {
        if (getId() != null && getId() > 0) {
            if (DataBaseManager.isVersioningEnabled()) {
                //One exists already, need to make a copy of the requirement
                Requirement req = new Requirement(getUniqueId(), getDescription(),
                        getNotes(),
                        getMajorVersion(),
                        getMidVersion(),
                        getMinorVersion() + 1);
                //Store in db
                new RequirementJpaController(
                        DataBaseManager.getEntityManagerFactory()).create(req);
                //Update this to the new version
                update(this, req);
                /**
                 * TODO: This will have the requirement as uncovered. GUI should
                 * allow user to either: 1) Blindly copy the test coverage. 2)
                 * Review the current test cases covering previous version and
                 * deciding if those still cover the requirement changes in a
                 * one by one basis. 3) Don't do anything, leaving it uncovered.
                 */
            }
            Requirement req = new RequirementJpaController(
                    DataBaseManager.getEntityManagerFactory())
                    .findRequirement(getId());
            update(req, this);
            new RequirementJpaController(
                    DataBaseManager.getEntityManagerFactory()).edit(req);
        } else {
            Requirement req = new Requirement(getUniqueId(), getDescription());
            update(req, this);
            new RequirementJpaController(
                    DataBaseManager.getEntityManagerFactory()).create(req);
            setId(req.getId());
        }
        return getId();
    }

    public Requirement getEntity() {
        return new RequirementJpaController(
                DataBaseManager.getEntityManagerFactory()).findRequirement(
                        getId());
    }

    public void update(Requirement target, Requirement source) {
        if (source.getNotes() != null) {
            target.setNotes(source.getNotes());
        }
        if (source.getDescription() != null) {
            target.setDescription(source.getDescription());
        }
        target.setRequirementSpecNode(source.getRequirementSpecNode());
        target.setRequirementList(source.getRequirementList());
        target.setRequirementList1(source.getRequirementList1());
        target.setRequirementStatusId(source.getRequirementStatusId());
        target.setRequirementTypeId(source.getRequirementTypeId());
        target.setRiskControlList(source.getRiskControlList());
        target.setStepList(source.getStepList());
        target.setUniqueId(source.getUniqueId());
        target.setId(source.getId());
        target.setMajorVersion(source.getMajorVersion());
        target.setMidVersion(source.getMidVersion());
        target.setMinorVersion(source.getMinorVersion());
    }

    public static boolean isDuplicate(Requirement req) {
        //Must be unique within a project.
        Project project
                = req.getRequirementSpecNode().getRequirementSpec().getProject();
        List<Requirement> requirements = ProjectServer.getRequirements(project);
        int count = 0;
        for (Requirement r : requirements) {
            if (r.getUniqueId().equals(req.getUniqueId())) {
                count++;
                if (count > 1) {
                    break;
                }
            }
        }
        return count > 1;
    }

    public int getTestCoverage() {
        int coverage = 0;
        LOG.log(Level.FINE, "Getting test coverage for: {0}...",
                getUniqueId());
        update();
        List<Requirement> children = getChildrenRequirement(getEntity());
        if (children.isEmpty()) {
            LOG.log(Level.FINE, "No child requirements");
            //Has test cases and no related requirements
            if (getStepList().size() > 0) {
                LOG.log(Level.FINE, "Found: {0} related steps.",
                        getStepList().size());
                coverage = 100;
            }
            //Has nothing, leave at 0.
        } else {
            //Get total of instances
            LOG.log(Level.FINE, "Found: {0} related requirements.",
                    children.size());
            //Check coverage for children
            for (Requirement r : children) {
                coverage += r.getStepList().isEmpty() ? 0 : 100;
            }
            coverage /= children.size();
        }
        LOG.log(Level.FINE, "{0} Coverage: {1}",
                new Object[]{getUniqueId(), coverage});
        return coverage;
    }

    /**
     * This returns the requirement children to this requirement.
     *
     * @param r requirement to get children from
     * @return list containing the child requirements
     */
    public static List<Requirement> getChildrenRequirement(Requirement r) {
        List<Requirement> children = new ArrayList<Requirement>();
        for (Requirement obj : new RequirementServer(r).getRequirementList()) {
            LOG.log(Level.FINE, "Adding child: {0}", obj.getUniqueId());
            children.add(obj);
        }
        Tool.removeDuplicates(children);
        return children;
    }

    /**
     * This returns the requirement parent to this requirement.
     *
     * @param r requirement to get parents from
     * @return list containing the parent requirements
     */
    public static List<Requirement> getParentRequirement(Requirement r) {
        List<Requirement> parents = new ArrayList<Requirement>();
        for (Requirement obj : new RequirementServer(r).getRequirementList1()) {
            LOG.log(Level.FINE, "Adding parent: {0}", obj.getUniqueId());
            parents.add(obj);
        }
        Tool.removeDuplicates(parents);
        return parents;
    }

    public void update() {
        update(this, getEntity());
    }

    public void addChildRequirement(Requirement child) throws Exception {
        //See: http://stackoverflow.com/questions/19848505/jpa-netbeans-and-many-to-many-relationship-to-self
        getRequirementList().add(child);
        RequirementServer childS = new RequirementServer(child);
        childS.getRequirementList1().add(getEntity());
        write2DB();
        childS.write2DB();
        update();
    }

    @Override
    public List<Requirement> getVersions() {
        List<Requirement> versions = new ArrayList<Requirement>();
        parameters.clear();
        parameters.put("uniqueId", getEntity().getUniqueId());
        for (Object obj : DataBaseManager.createdQuery(
                "SELECT r FROM Requirement r WHERE r.uniqueId = :uniqueId", 
                parameters)) {
            versions.add((Requirement) obj);
        }
        return versions;
    }
}
