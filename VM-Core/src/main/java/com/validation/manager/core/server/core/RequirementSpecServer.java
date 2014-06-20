package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.RequirementSpecJpaController;
import com.validation.manager.core.db.controller.RequirementSpecNodeJpaController;
import com.validation.manager.core.db.controller.SpecLevelJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class RequirementSpecServer extends RequirementSpec
        implements EntityServer<RequirementSpec> {

    public RequirementSpecServer(String name, String description, 
            int projectId, int specLevelId) {
        super(projectId, specLevelId);
        setProject(new ProjectJpaController(getEntityManagerFactory()).findProject(projectId));
        setSpecLevel(new SpecLevelJpaController(getEntityManagerFactory()).findSpecLevel(specLevelId));
        setDescription(description);
        setName(name);
        setVersion(1);
        setModificationDate(new Date());
        setRequirementSpecNodeList(new ArrayList<RequirementSpecNode>());
    }

    public RequirementSpecServer(RequirementSpec rs) {
        super(rs.getRequirementSpecPK());
        update((RequirementSpecServer) this, rs);
    }

    /**
     * Add a node to this spec.
     *
     * This is equivalent to a section in a document.
     *
     * Although database might allow a requirement being on different nodes,
     * this constraint is imposed via software.
     *
     * @param name name of new node
     * @param description description of node
     * @param scope scope of node
     * @param requirements List of requirements for this node
     * @throws Exception if errors creating the node occur
     */
    public void addSpecNode(String name, String description, String scope,
            List<Requirement> requirements) throws Exception {
        RequirementSpecNodeServer sns = new RequirementSpecNodeServer(
                new RequirementSpecJpaController(
                getEntityManagerFactory())
                .findRequirementSpec(getRequirementSpecPK()),
                name, description, scope);
        sns.write2DB();
        for (Requirement req : requirements) {
            req.setRequirementSpecNode(sns);
        }
        getRequirementSpecNodeList().add(
                new RequirementSpecNodeJpaController(
                getEntityManagerFactory())
                .findRequirementSpecNode(sns.getRequirementSpecNodePK()));
        write2DB();
    }

    @Override
    public int write2DB() throws Exception {
        if (getRequirementSpecNodeList() == null) {
            setRequirementSpecNodeList(new ArrayList<RequirementSpecNode>());
        }
        if (getRequirementSpecPK() != null
                && getRequirementSpecPK().getId() > 0) {
            RequirementSpec rs = new RequirementSpecJpaController(
                    getEntityManagerFactory())
                    .findRequirementSpec(getRequirementSpecPK());
            update(rs, this);
            new RequirementSpecJpaController(
                    getEntityManagerFactory()).edit(rs);
        } else {
            RequirementSpec rs = new RequirementSpec();
            update(rs, this);
            new RequirementSpecJpaController(
                    getEntityManagerFactory()).create(rs);
            setRequirementSpecPK(rs.getRequirementSpecPK());
        }
        return getRequirementSpecPK().getId();
    }

    public static void deleteRequirementSpec(RequirementSpec rs)
            throws IllegalOrphanException, NonexistentEntityException {
        new RequirementSpecJpaController(
                getEntityManagerFactory())
                .destroy(rs.getRequirementSpecPK());
    }

    @Override
    public RequirementSpec getEntity() {
        return new RequirementSpecJpaController(
                getEntityManagerFactory())
                .findRequirementSpec(getRequirementSpecPK());
    }

    @Override
    public void update(RequirementSpec target, RequirementSpec source) {
        target.setProject(source.getProject());
        target.setSpecLevel(source.getSpecLevel());
        target.setDescription(source.getDescription());
        target.setName(source.getName());
        target.setVersion(source.getVersion());
        target.setModificationDate(source.getModificationDate());
        target.setRequirementSpecNodeList(source.getRequirementSpecNodeList());
        target.setRequirementSpecPK(source.getRequirementSpecPK());
    }

    public static List<Requirement> getRequirements(RequirementSpec spec) {
        List<Requirement> requirements = new ArrayList<Requirement>();
        RequirementSpecServer rs = new RequirementSpecServer(spec);
        for (RequirementSpecNode rsn : rs.getRequirementSpecNodeList()) {
            requirements.addAll(RequirementSpecNodeServer.getRequirements(rsn));
        }
        return requirements;
    }
    
    @Override
    public void update() {
        update(this, getEntity());
    }
}
