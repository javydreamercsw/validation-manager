package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
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
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import org.openide.util.Exceptions;

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
            target.getRequirementHasExceptionList()
                    .addAll(source.getRequirementHasExceptionList());
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
        if (source.getRequirementSpecNode() != null) {
            target.setRequirementSpecNode(source.getRequirementSpecNode());
        }
        if (source.getRequirementStatusId() != null) {
            target.setRequirementStatusId(source.getRequirementStatusId());
        }
        if(source.getRequirementTypeId()!=null){
            target.setRequirementTypeId(source.getRequirementTypeId());
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
                 * TODO: GUI should allow user to either: 1) Blindly copy the
                 * test coverage. 2) Review the current test cases covering
                 * previous version and deciding if those still cover the
                 * requirement changes in a one by one basis. 3) Don't do
                 * anything, leaving it uncovered.
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

    @Override
    public Requirement getEntity() {
        return new RequirementJpaController(
                getEntityManagerFactory()).findRequirement(
                        getId());
    }

    @Override
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

    @Override
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
        parameters.put("uniqueId", getEntity().getUniqueId().trim() + "%");
        for (Object obj : DataBaseManager.createdQuery(
                "SELECT r FROM Requirement r WHERE r.uniqueId like :uniqueId",
                parameters)) {
            versions.add((Requirement) obj);
        }
        return versions;
    }

    @Override
    public boolean isChangeVersionable() {
        String description = getEntity().getDescription();
        if (description == null) {
            description = "";
        }
        String notes = getEntity().getNotes();
        if (notes == null) {
            notes = "";
        }
        return !description.equals(getDescription())
                || !notes.equals(getNotes())
                || !getEntity().getUniqueId().trim()
                .equals(getUniqueId().trim());
    }

    public static void main(String[] args) {
        List<String> params = new ArrayList<String>();
        params.add("javax.persistence.jdbc.url");
        params.add("javax.persistence.jdbc.password");
        params.add("javax.persistence.jdbc.driver");
        params.add("javax.persistence.jdbc.user");
        /**
         * Check requirements to make sure they only have latest version of a
         * linked requirement as children.
         */
        if (args.length == params.size()) {
            parameters.clear();
            int count = 0;
            for (String key : params) {
                parameters.put(key, args[count]);
                count++;
            }
            EntityManagerFactory emf
                    = Persistence.createEntityManagerFactory("VMPU", parameters);
            DataBaseManager.setEntityManagerFactory(emf);
            int counter = 0;
            List<String> processed = new ArrayList<String>();
            for (final Requirement req : new RequirementJpaController(
                    DataBaseManager.getEntityManagerFactory()).findRequirementEntities()) {
                if (req.getUniqueId().trim().equals("IMSR0001")
                        && !processed.contains(req.getUniqueId().trim())) {
                    processed.add(req.getUniqueId().trim());
                    try {
                        //Trim the requirement unique id
                        RequirementServer r = new RequirementServer(req);
                        if (!r.getUniqueId().equals(req.getUniqueId().trim())) {
                            r.setUniqueId(req.getUniqueId().trim());
                            r.write2DB();
                            req.setUniqueId(r.getUniqueId());
                            LOG.log(Level.INFO, "Trimmed unique id for: {0}",
                                    r.getUniqueId());
                        }
                        //Make sure to move requirement node relationships as well
                        List<Requirement> versions = r.getVersions();
                        Requirement older = Collections.min(versions, null);
                        Requirement newer = Collections.max(versions, null);
                        //Copy requirement type
                        Collections.sort(versions);
                        int last = 0;
                        for (Requirement t : versions) {
                            if (t.getRequirementTypeId() != null
                                    && t.getRequirementTypeId().getId() > last) {
                                last = t.getRequirementTypeId().getId();
                            } else {
                                if (last > 0) {
                                    LOG.log(Level.INFO,
                                            "Updated Requirement type for: {0}",
                                            t.toString());
                                    RequirementServer temp = new RequirementServer(t);
                                    temp.setRequirementTypeId(
                                            new RequirementTypeJpaController(
                                                    DataBaseManager.getEntityManagerFactory())
                                            .findRequirementType(last));
                                    temp.write2DB();
                                }
                            }
                        }
                        if (!req.getRequirementList1().isEmpty()) {
                            //Remove duplicate children (same id different versions)
                            LOG.log(Level.INFO, "Checking children of: {0}",
                                    req.getUniqueId());
                            List<Requirement> reqs = new ArrayList<Requirement>();
                            for (Requirement child : req.getRequirementList1()) {
                                boolean found = false;
                                //Compare with the ones in the list
                                ArrayList<Requirement> copiedList
                                        = new ArrayList<Requirement>(reqs);
                                for (Requirement inList : copiedList) {
                                    if (inList.getUniqueId().trim().equals(child.getUniqueId().trim())) {
                                        found = true;
                                        //They are the same, keep only the newer one
                                        LOG.log(Level.INFO, "{0} vs. {1}",
                                                new Object[]{inList.toString(),
                                                    child.toString()});
                                        if (inList.compareTo(child) < 0) {
                                            //Child is newer, replace the one in the list
                                            LOG.fine("Replacing copy on the list with newer version.");
                                            reqs.remove(inList);
                                            reqs.add(child);
                                        } else if (inList.compareTo(child) == 0) {
                                            LOG.fine("Same version, ignore.");
                                        } else {
                                            LOG.fine("Newer version already in list, ignoring.");
                                        }
                                    }
                                }
                                if (!found) {
                                    //Not there, add it
                                    reqs.add(child);
                                }
                            }
                            //Now that we cleaned the list, let's replace it with the correct ones.
                            LOG.log(Level.INFO, "Initial amount of children: {0}",
                                    r.getRequirementList1().size());
                            if (LOG.isLoggable(Level.INFO)) {
                                for (Requirement x : r.getRequirementList1()) {
                                    LOG.fine(x.toString());
                                }
                            }
                            //Remove the ones currently in the list
                            r.getRequirementList1().clear();
                            //Add the new ones
                            r.getRequirementList1().addAll(reqs);
                            r.write2DB();
                            LOG.log(Level.INFO, "Updated amount of children: {0}",
                                    r.getRequirementList1().size());
                            if (LOG.isLoggable(Level.INFO)) {
                                for (Requirement x : reqs) {
                                    LOG.fine(x.toString());
                                }
                            }
                        }
                        counter = 0;
                        List<String> updated = new ArrayList<String>();
                        if (!updated.contains(newer.getUniqueId())
                                && newer.compareTo(older) > 0
                                && newer.getRequirementSpecNode() == null
                                && older.getRequirementSpecNode() != null) {
                            r.copyRelationships(newer, older);
                            LOG.log(Level.INFO,
                                    "Updated relationships for: {0}", newer);
                            r.write2DB();
                            updated.add(r.getUniqueId().trim());
                            counter++;
                        }
                        new RequirementServer(newer).write2DB();
                    } catch (RollbackException ex) {
                        //Relationship already exists.
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
            LOG.log(Level.INFO, "Fixed {0} requirement relationships.", counter);
            DataBaseManager.close();
        } else {
            LOG.severe(new StringBuilder().append("Missing parameters to be "
                    + "provided. The following parameters need to be "
                    + "provided:\n").append(params.toString()).toString());
        }
    }

    public static List<Requirement> getLatestChildren(Requirement parent) {
        List<Requirement> children = new ArrayList<Requirement>();
        List<Requirement> toAdd = new ArrayList<Requirement>();
        for (Requirement req : parent.getRequirementList1()) {
            //Make sure to remove duplicates (versions of the same requirement)
            boolean found = false;
            for (Requirement in : toAdd) {
                if (in.getUniqueId().trim().equals(req.getUniqueId().trim())) {
                    //Check if we have a new version
                    //They have the same Unique ID, so they are versions of the same requirement
                    if (in.compareTo(req) < 0) {
                        //The one in is older. Remove it and replace with the new one
                        toAdd.remove(in);
                        toAdd.add(req);
                        found = true;
                    } else {
                        //The one in is either the same or greater, just keep it
                    }
                }
            }
            if (!found) {
                toAdd.add(req);
            }
        }
        children.addAll(toAdd);
        return children;
    }
}
