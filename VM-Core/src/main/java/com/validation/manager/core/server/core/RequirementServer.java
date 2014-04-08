package com.validation.manager.core.server.core;

import static com.validation.manager.core.DataBaseManager.createdQuery;
import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.isVersioningEnabled;
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
import static com.validation.manager.core.server.core.ProjectServer.getRequirements;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class RequirementServer extends Requirement
        implements EntityServer<Requirement>, VersionableServer<Requirement> {

    private static final Logger LOG
            = getLogger(RequirementServer.class.getSimpleName());

    public RequirementServer(String id, String desc, RequirementSpecNodePK rsn,
            String notes, int requirementType, int requirementStatus) {
        setNotes(notes);
        setRequirementSpecNode(
                new RequirementSpecNodeJpaController(getEntityManagerFactory()).findRequirementSpecNode(rsn));
        setUniqueId(id);
        setDescription(desc);
        setRequirementStatusId(new RequirementStatusJpaController(
                getEntityManagerFactory()).findRequirementStatus(requirementStatus));
        setRequirementTypeId(new RequirementTypeJpaController(
                getEntityManagerFactory()).findRequirementType(requirementType));
    }

    public static void deleteRequirement(Requirement r)
            throws IllegalOrphanException, NonexistentEntityException {
        RequirementJpaController controller
                = new RequirementJpaController(getEntityManagerFactory());
        if (controller.findRequirement(r.getId()) != null) {
            controller.destroy(r.getId());
        }
    }

    public RequirementServer(Requirement r) {
        RequirementJpaController controller
                = new RequirementJpaController(getEntityManagerFactory());
        Requirement requirement = controller.findRequirement(r.getId());
        if (requirement != null) {
            update((RequirementServer) this, requirement);
        } else {
            throw new RuntimeException("Unable to find requirement with id: "
                    + r.getId());
        }
    }

    private void copyRelationships(Requirement target, Requirement source) {
        if (source.getRequirementHasExceptionList() != null) {
            target.getRequirementHasExceptionList().clear();
            target.getRequirementHasExceptionList().addAll(source.getRequirementHasExceptionList());
        }
        if (source.getRequirementList() != null) {
            target.getRequirementList().clear();
            target.getRequirementList().addAll(source.getRequirementList());
        }
        if (source.getRequirementList1() != null) {
            target.getRequirementList1().clear();
            target.getRequirementList1().addAll(source.getRequirementList1());
        }
        if (source.getRiskControlList() != null) {
            target.getRiskControlList().clear();
            target.getRiskControlList().addAll(source.getRiskControlList());
        }
        if (source.getStepList() != null) {
            target.getStepList().clear();
            target.getStepList().addAll(source.getStepList());
        }
    }

    @Override
    public int write2DB() throws Exception {
        //Make sure unique id is trimmed
        setUniqueId(getUniqueId().trim());
        if (getId() > 0) {
            //Check what has changed, if is only relationshipd, don't version
            //Get the one from DB
            if (isVersioningEnabled() && isChangeVersionable()) {
                //One exists already, need to make a copy of the requirement
                Requirement req = new Requirement(getUniqueId(), getDescription(),
                        getNotes(),
                        getMajorVersion(),
                        getMidVersion(),
                        getMinorVersion() + 1);
                /**
                 * TODO: GUI should allow user to either: 
                 * 1) Blindly copy the test coverage. 
                 * 2) Review the current test cases covering previous version and
                 * deciding if those still cover the requirement changes in a
                 * one by one basis. 
                 * 3) Don't do anything, leaving it uncovered.
                 */
                //Copy the relationships
                copyRelationships(req, this);
                //Store in data base.
                new RequirementJpaController(
                        getEntityManagerFactory()).create(req);
                update(this, req);
            }
            Requirement req = new RequirementJpaController(
                    getEntityManagerFactory())
                    .findRequirement(getId());
            update(req, this);
            new RequirementJpaController(
                    getEntityManagerFactory()).edit(req);
        } else {
            Requirement req = new Requirement(getUniqueId(), getDescription());
            update(req, this);
            new RequirementJpaController(
                    getEntityManagerFactory()).create(req);
            setId(req.getId());
        }
        return getId();
    }

    public Requirement getEntity() {
        return new RequirementJpaController(
                getEntityManagerFactory()).findRequirement(
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
        List<Requirement> requirements = getRequirements(project);
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
        LOG.log(Level.INFO, "Getting test coverage for: {0}...",
                getUniqueId());
        update();
        List<Requirement> children = getEntity().getRequirementList1();
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
            LOG.log(Level.INFO, "Found: {0} related requirements.",
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

    public void update() {
        update(this, getEntity());
    }

    public void addChildRequirement(Requirement child) throws Exception {
        //See: http://stackoverflow.com/questions/19848505/jpa-netbeans-and-many-to-many-relationship-to-self
        RequirementServer childS = new RequirementServer(child);
        childS.getRequirementList().add(getEntity());
        getRequirementList1().add(child);
        childS.write2DB();
        write2DB();
        update();
    }

    @Override
    public List<Requirement> getVersions() {
        List<Requirement> versions = new ArrayList<Requirement>();
        parameters.clear();
        parameters.put("uniqueId", getEntity().getUniqueId());
        for (Object obj : createdQuery(
                "SELECT r FROM Requirement r WHERE r.uniqueId = :uniqueId",
                parameters)) {
            versions.add((Requirement) obj);
        }
        return versions;
    }

    public boolean isChangeVersionable() {
        return !getEntity().getDescription().equals(getDescription())
                || !getEntity().getNotes().equals(getNotes());
    }
}
