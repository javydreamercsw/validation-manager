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
import com.validation.manager.core.tool.message.MessageHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import org.openide.util.Exceptions;
import static org.openide.util.Lookup.getDefault;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class RequirementServer extends Requirement
        implements EntityServer<Requirement>, VersionableServer<Requirement> {

    private static final Logger LOG
            = getLogger(RequirementServer.class.getSimpleName());
    /**
     * Having this static map reduces drastically the amount of resources used
     * to constantly calculate test coverage. This is causing huge performance
     * issues on the client. Basically reduces the calculation to once per
     * requirement.
     */
    private static final Map<String, Integer> COVERAGE_MAP
            = new HashMap<String, Integer>();

    public RequirementServer(String id, String desc, RequirementSpecNodePK rsn,
            String notes, int requirementType, int requirementStatus) {
        setNotes(notes);
        if (rsn != null) {
            setRequirementSpecNode(
                    new RequirementSpecNodeJpaController(DataBaseManager
                            .getEntityManagerFactory())
                            .findRequirementSpecNode(rsn));
        }
        setUniqueId(id);
        setDescription(desc);
        setRequirementStatusId(new RequirementStatusJpaController(
                DataBaseManager.getEntityManagerFactory())
                .findRequirementStatus(requirementStatus));
        setRequirementTypeId(new RequirementTypeJpaController(
                DataBaseManager.getEntityManagerFactory())
                .findRequirementType(requirementType));
    }

    public static void deleteRequirement(Requirement r)
            throws IllegalOrphanException, NonexistentEntityException {
        RequirementJpaController controller
                = new RequirementJpaController(DataBaseManager.getEntityManagerFactory());
        if (controller.findRequirement(r.getId()) != null) {
            controller.destroy(r.getId());
        }
    }

    public RequirementServer(int id) {
        super();
        RequirementJpaController controller
                = new RequirementJpaController(DataBaseManager
                        .getEntityManagerFactory());
        Requirement requirement = controller.findRequirement(id);
        if (requirement != null) {
            update((RequirementServer) this, requirement);
        } else {
            throw new RuntimeException("Unable to find "
                    + "requirement with id: "
                    + id);
        }
    }

    public RequirementServer(Requirement r) {
        this(r.getId());
    }

    private void copyRelationships(Requirement target, Requirement source) {
        if (source.getVmExceptionList() != null) {
            target.getVmExceptionList().clear();
            target.getVmExceptionList().addAll(source.getVmExceptionList());
        }
        if (source.getRequirementList() != null) {
            target.getRequirementList().clear();
            target.getRequirementList().addAll(source.getRequirementList());
        }
        if (source.getRequirementList1() != null) {
            target.getRequirementList1().clear();
            target.getRequirementList1().addAll(source.getRequirementList1());
        }
        if (source.getRiskControlHasRequirementList() != null) {
            target.getRiskControlHasRequirementList().clear();
            target.getRiskControlHasRequirementList()
                    .addAll(source.getRiskControlHasRequirementList());
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
        if (source.getRequirementTypeId() != null) {
            target.setRequirementTypeId(source.getRequirementTypeId());
        }
    }

    @Override
    public int write2DB() throws Exception {
        //Make sure unique id is trimmed
        setUniqueId(getUniqueId().trim());
        if (getId() != null && getId() > 0) {
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

    @Override
    public Requirement getEntity() {
        return new RequirementJpaController(
                DataBaseManager.getEntityManagerFactory()).findRequirement(
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
        target.setVmExceptionList(source.getVmExceptionList());
        target.setRiskControlHasRequirementList(source
                .getRiskControlHasRequirementList());
        target.setStepList(source.getStepList());
        target.setUniqueId(source.getUniqueId());
        target.setId(source.getId());
        super.update(target, source);
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

    private String getCoverageMapID(Requirement req) {
        return req.getId() + "-"
                + req.getUniqueId().trim();
    }

    public int getTestCoverage() {
        //Reset to 0 for new calculation.
        int coverage = 0;
        if (!COVERAGE_MAP.containsKey(getCoverageMapID(getEntity()))) {
            LOG.log(Level.FINE, "Getting test coverage for: {0}...",
                    getUniqueId());
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
                LOG.log(Level.FINE, "Found: {0} related requirements.",
                        children.size());
                //Check coverage for children
                for (Requirement r : children) {
                    if (COVERAGE_MAP.containsKey(getCoverageMapID(r))) {
                        coverage += COVERAGE_MAP.get(getCoverageMapID(r));
                    } else {
                        coverage += new RequirementServer(r).getTestCoverage();
                    }
                }
                coverage /= children.size();
            }
            LOG.log(Level.FINE, "{0} Coverage: {1}",
                    new Object[]{getUniqueId(), coverage});
            //Update the map
            COVERAGE_MAP.put(getCoverageMapID(getEntity()), coverage);
        } else {
            coverage = COVERAGE_MAP.get(getCoverageMapID(getEntity()));
        }
        //If still negative this means it is not coveed at all.
        return coverage;
    }

    @Override
    public void update() {
        //Mark it for recalculation
        COVERAGE_MAP.remove(getEntity().getId() + "-"
                + getEntity().getUniqueId().trim());
        update(this, getEntity());
    }

    public void addChildRequirement(Requirement child) throws Exception {
        boolean circular = false;
        //Prevent circular dependencies
        for (Requirement r : getRequirementList()) {
            if (child.getUniqueId().trim().equals(r.getUniqueId().trim())) {
                circular = true;
                break;
            }
        }
        if (!circular) {
            for (Requirement r : getRequirementList1()) {
                if (child.getUniqueId().trim().equals(r.getUniqueId().trim())) {
                    circular = true;
                    break;
                }
            }
        }
        if (!circular) {
            if (!getRequirementList1().contains(child)) {
                getRequirementList1().add(child);
                write2DB();
                update();
            }
        } else {
            MessageHandler handler = getDefault().lookup(MessageHandler.class);
            String message = new StringBuilder().append("Ignored addition of ")
                    .append(child.getUniqueId()).append(" as a children of ")
                    .append(getUniqueId())
                    .append(". It would have caused a circular dependecy.")
                    .toString();
            if (handler != null) {
                handler.warn(message);
            } else {
                LOG.warning(message);
            }
        }
    }

    @Override
    public List<Requirement> getVersions() {
        List<Requirement> versions
                = new ArrayList<>();
        parameters.clear();
        parameters.put("uniqueId", getEntity()
                .getUniqueId().trim());
        DataBaseManager.namedQuery(
                "Requirement.findByUniqueId",
                parameters).forEach((obj) -> {
                    versions.add((Requirement) obj);
                });
        return versions;
    }

    public static void main(String[] args) {
        List<String> params = new ArrayList<>();
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
            int counter = 0, circular = 0;
            List<String> processed = new ArrayList<>();
            LOG.log(Level.INFO, "Analyzing {0}. Please wait...",
                    parameters.get("javax.persistence.jdbc.url"));
            for (final Requirement req : new RequirementJpaController(
                    DataBaseManager.getEntityManagerFactory()).findRequirementEntities()) {
                if (!processed.contains(req.getUniqueId().trim())) {
                    processed.add(req.getUniqueId().trim());
                    try {
                        //Trim the requirement unique id
                        RequirementServer r = new RequirementServer(req);
                        if (!r.getUniqueId().equals(req.getUniqueId().trim())) {
                            r.setUniqueId(req.getUniqueId().trim());
                            r.write2DB();
                            req.setUniqueId(r.getUniqueId());
                            LOG.log(Level.FINE, "Trimmed unique id for: {0}",
                                    r.getUniqueId());
                        }
                        //Make sure to move requirement node relationships as well
                        List<Requirement> versions = r.getVersions();
                        Requirement older = Collections.min(versions, null);
                        Requirement newer = Collections.max(versions, null);
                        Collections.sort(versions);
                        int lastRequirementType = 0;
                        int lastRequirementStatus = 0;
                        RequirementSpecNodePK lastNode = null;
                        for (Requirement t : versions) {
                            RequirementServer temp = new RequirementServer(t);
                            //Detect circular relationships
                            if (temp.getRequirementList().size() > 0
                                    && temp.getRequirementList1().size() > 0) {
                                List<String> toRemove
                                        = new ArrayList<>();
                                //Has both children and parents
                                LOG.log(Level.INFO,
                                        "Inspecting {0} for circular dependencies.",
                                        temp.getUniqueId());
                                for (Requirement parent : temp.getRequirementList1()) {
                                    //Check all parents of this requirement
                                    for (Requirement child : parent.getRequirementList()) {
                                        //Check if the parent has this requirement as a child
                                        if (child.getUniqueId().equals(temp.getUniqueId())) {
                                            if (!toRemove.contains(parent.getUniqueId())) {
                                                LOG.log(Level.INFO,
                                                        "Circular dependency "
                                                        + "detected between {0} and {1}",
                                                        new Object[]{temp.getUniqueId(),
                                                            parent.getUniqueId()});

                                                RequirementServer p = new RequirementServer(parent);
                                                assert temp.getRequirementList().contains(parent);
                                                temp.getRequirementList().remove(parent);
                                                LOG.log(Level.INFO, "Remove link from {0} to {1}",
                                                        new Object[]{temp.getUniqueId(),
                                                            parent.getUniqueId()});
                                                temp.write2DB();
                                                temp.update();
                                                assert !temp.getRequirementList().contains(parent);
                                                p.update();
                                                assert p.getRequirementList1().contains(temp);
                                                toRemove.add(parent.getUniqueId());
                                                circular++;
                                            }
                                        }
                                    }
                                }
                            }
                            //Fix requirement type
                            if (temp.getRequirementTypeId() != null) {
                                if (temp.getRequirementTypeId().getId() > lastRequirementType) {
                                    lastRequirementType = temp.getRequirementTypeId().getId();
                                }
                            } else {
                                if (lastRequirementType > 0) {
                                    LOG.log(Level.FINE,
                                            "Updated Requirement type for: {0}",
                                            temp.toString());
                                    temp.setRequirementTypeId(
                                            new RequirementTypeJpaController(
                                                    DataBaseManager.getEntityManagerFactory())
                                                    .findRequirementType(lastRequirementType));
                                    temp.write2DB();
                                    lastRequirementType = temp.getRequirementTypeId().getId();
                                    counter++;
                                }
                            }
                            //Fix requirement status
                            if (temp.getRequirementStatusId() != null) {
                                if (temp.getRequirementStatusId().getId() > lastRequirementStatus) {
                                    lastRequirementStatus = temp.getRequirementStatusId().getId();
                                }
                            } else {
                                if (lastRequirementStatus > 0) {
                                    LOG.log(Level.FINE,
                                            "Updated Requirement status for: {0}",
                                            temp.toString());
                                    temp.setRequirementStatusId(
                                            new RequirementStatusJpaController(
                                                    DataBaseManager.getEntityManagerFactory())
                                                    .findRequirementStatus(lastRequirementStatus));
                                    temp.write2DB();
                                    lastRequirementStatus = temp.getRequirementStatusId().getId();
                                    counter++;
                                }
                            }
                            //Fix spec node
                            if (temp.getRequirementSpecNode() != null) {
                                if (temp.getRequirementSpecNode().getRequirementSpecNodePK() != null) {
                                    lastNode = temp.getRequirementSpecNode().getRequirementSpecNodePK();
                                }
                            } else {
                                if (lastNode != null) {
                                    LOG.log(Level.FINE,
                                            "Updated Requirement Spec node: {0}",
                                            temp.toString());
                                    temp.setRequirementSpecNode(
                                            new RequirementSpecNodeJpaController(
                                                    DataBaseManager.getEntityManagerFactory())
                                                    .findRequirementSpecNode(lastNode));
                                    temp.write2DB();
                                    lastNode = temp.getRequirementSpecNode().getRequirementSpecNodePK();
                                    counter++;
                                }
                            }
                        }
                        if (!req.getRequirementList1().isEmpty()) {
                            //Remove duplicate children (same id different versions)
                            LOG.log(Level.FINE, "Checking children of: {0}",
                                    req.getUniqueId());
                            List<Requirement> reqs = new ArrayList<>();
                            req.getRequirementList1().forEach((child) -> {
                                boolean found = false;
                                //Compare with the ones in the list
                                ArrayList<Requirement> copiedList
                                        = new ArrayList<>(reqs);
                                for (Requirement inList : copiedList) {
                                    if (inList.getUniqueId().trim().equals(child.getUniqueId().trim())) {
                                        found = true;
                                        //They are the same, keep only the newer one
                                        LOG.log(Level.FINE, "{0} vs. {1}",
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
                            });
                            //Now that we cleaned the list, let's replace it with the correct ones.
                            LOG.log(Level.FINE, "Initial amount of children: {0}",
                                    r.getRequirementList1().size());
                            if (LOG.isLoggable(Level.FINE)) {
                                r.getRequirementList1().forEach((x) -> {
                                    LOG.info(x.toString());
                                });
                            }
                            //Remove the ones currently in the list
                            r.getRequirementList1().clear();
                            //Add the new ones
                            r.getRequirementList1().addAll(reqs);
                            r.write2DB();
                            LOG.log(Level.FINE, "Updated amount of children: {0}",
                                    r.getRequirementList1().size());
                            if (LOG.isLoggable(Level.FINE)) {
                                reqs.forEach((x) -> {
                                    LOG.info(x.toString());
                                });
                            }
                        }
                        List<String> updated = new ArrayList<>();
                        if (!updated.contains(newer.getUniqueId())
                                && newer.compareTo(older) > 0
                                && newer.getRequirementSpecNode() == null
                                && older.getRequirementSpecNode() != null) {
                            r.copyRelationships(newer, older);
                            LOG.log(Level.FINE,
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
            LOG.log(Level.INFO,
                    "Fixed {0} requirement relationships.", counter);
            LOG.log(Level.INFO,
                    "Fixed {0} requirement circular relationships.", circular);
            DataBaseManager.close();
        } else {
            LOG.severe(new StringBuilder().append("Missing parameters to be "
                    + "provided. The following parameters need to be "
                    + "provided:\n").append(params.toString()).toString());
        }
    }

    public static List<Requirement> getLatestChildren(Requirement parent) {
        List<Requirement> finalList = new ArrayList<>();
        List<Requirement> children = new ArrayList<>();
        List<Requirement> toAdd = new ArrayList<>();
        List<Requirement> toRemove = new ArrayList<>();
        parent.getRequirementList1().forEach((req) -> {
            //Make sure to remove duplicates (versions of the same requirement)
            boolean found = false;
            for (Requirement in : toAdd) {
                if (in.getUniqueId().trim().equals(req.getUniqueId().trim())) {
                    //Check if we have a new version
                    //They have the same Unique ID, so they are versions of the same requirement
                    if (in.compareTo(req) < 0) {
                        //The one in is older. Remove it and replace with the new one
                        toRemove.add(in);
                        toAdd.add(req);
                        found = true;
                    } else {
                        //The one in is either the same or greater, just keep it
                    }
                }
            }
            if (!found) {
                finalList.add(req);
            }
        });
        toAdd.forEach((add) -> {
            finalList.add(add);
        });
        toRemove.forEach((remove) -> {
            finalList.remove(remove);
        });
        children.addAll(finalList);
        return children;
    }
}
