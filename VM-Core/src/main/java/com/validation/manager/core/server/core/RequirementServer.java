/* 
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.VMException;
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
import com.validation.manager.core.tool.message.MessageHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class RequirementServer extends Requirement
        implements EntityServer<Requirement>, VersionableServer<Requirement> {

    private static final Logger LOG
            = Logger.getLogger(RequirementServer.class.getSimpleName());
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

    public RequirementServer(int id) throws VMException {
        super();
        RequirementJpaController controller
                = new RequirementJpaController(DataBaseManager
                        .getEntityManagerFactory());
        Requirement requirement = controller.findRequirement(id);
        if (requirement != null) {
            update((RequirementServer) this, requirement);
        } else {
            throw new VMException("Unable to find "
                    + "requirement with id: "
                    + id);
        }
    }

    public RequirementServer(Requirement r) throws VMException {
        this(r.getId());
        update();
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
        update();
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
        target.setDescription(source.getDescription());
        target.setId(source.getId());
        target.setNotes(source.getNotes());
        target.setParentRequirementId(source.getParentRequirementId());
        target.setRequirementList(source.getRequirementList());
        target.setRequirementSpecNode(source.getRequirementSpecNode());
        target.setRequirementStatusId(source.getRequirementStatusId());
        target.setRequirementTypeId(source.getRequirementTypeId());
        target.setRiskControlHasRequirementList(source.getRiskControlHasRequirementList());
        target.setStepList(source.getStepList());
        target.setUniqueId(source.getUniqueId());
        super.update(target, source);
    }

    public static boolean isDuplicate(Requirement req) {
        //Must be unique within a project.
        Project project
                = req.getRequirementSpecNode().getRequirementSpec().getProject();
        List<Requirement> requirements = Tool.extractRequirements(project);
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
            List<Requirement> children = getEntity().getRequirementList();
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
                        try {
                            coverage += new RequirementServer(r).getTestCoverage();
                        }
                        catch (VMException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
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
            if (Objects.equals(child.getId(), r.getId())) {
                circular = true;
                break;
            }
        }
        if (circular) {
            MessageHandler handler = Lookup.getDefault()
                    .lookup(MessageHandler.class);
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
        } else {
            RequirementServer cs = new RequirementServer(child);
            cs.setParentRequirementId(getEntity());
            cs.write2DB();
            update();
            assert Objects.equals(new RequirementServer(child)
                    .getParentRequirementId().getId(), getId());
            assert getEntity().getRequirementList().size() > 0;
        }
    }
}
