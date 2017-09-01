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
package com.validation.manager.core.db.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpecNodePK;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class RequirementSpecNodeJpaController implements Serializable {

    public RequirementSpecNodeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RequirementSpecNode requirementSpecNode) throws PreexistingEntityException, Exception {
        if (requirementSpecNode.getRequirementSpecNodePK() == null) {
            requirementSpecNode.setRequirementSpecNodePK(new RequirementSpecNodePK());
        }
        if (requirementSpecNode.getRequirementSpecNodeList() == null) {
            requirementSpecNode.setRequirementSpecNodeList(new ArrayList<>());
        }
        if (requirementSpecNode.getRequirementList() == null) {
            requirementSpecNode.setRequirementList(new ArrayList<>());
        }
        requirementSpecNode.getRequirementSpecNodePK().setRequirementSpecId(requirementSpecNode.getRequirementSpec().getRequirementSpecPK().getId());
        requirementSpecNode.getRequirementSpecNodePK().setRequirementSpecSpecLevelId(requirementSpecNode.getRequirementSpec().getRequirementSpecPK().getSpecLevelId());
        requirementSpecNode.getRequirementSpecNodePK().setRequirementSpecProjectId(requirementSpecNode.getRequirementSpec().getRequirementSpecPK().getProjectId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RequirementSpec requirementSpec = requirementSpecNode.getRequirementSpec();
            if (requirementSpec != null) {
                requirementSpec = em.getReference(requirementSpec.getClass(), requirementSpec.getRequirementSpecPK());
                requirementSpecNode.setRequirementSpec(requirementSpec);
            }
            RequirementSpecNode requirementSpecNodeRel = requirementSpecNode.getRequirementSpecNode();
            if (requirementSpecNodeRel != null) {
                requirementSpecNodeRel = em.getReference(requirementSpecNodeRel.getClass(), requirementSpecNodeRel.getRequirementSpecNodePK());
                requirementSpecNode.setRequirementSpecNode(requirementSpecNodeRel);
            }
            List<RequirementSpecNode> attachedRequirementSpecNodeList = new ArrayList<>();
            for (RequirementSpecNode requirementSpecNodeListRequirementSpecNodeToAttach : requirementSpecNode.getRequirementSpecNodeList()) {
                requirementSpecNodeListRequirementSpecNodeToAttach = em.getReference(requirementSpecNodeListRequirementSpecNodeToAttach.getClass(), requirementSpecNodeListRequirementSpecNodeToAttach.getRequirementSpecNodePK());
                attachedRequirementSpecNodeList.add(requirementSpecNodeListRequirementSpecNodeToAttach);
            }
            requirementSpecNode.setRequirementSpecNodeList(attachedRequirementSpecNodeList);
            List<Requirement> attachedRequirementList = new ArrayList<>();
            for (Requirement requirementListRequirementToAttach : requirementSpecNode.getRequirementList()) {
                requirementListRequirementToAttach = em.getReference(requirementListRequirementToAttach.getClass(), requirementListRequirementToAttach.getId());
                attachedRequirementList.add(requirementListRequirementToAttach);
            }
            requirementSpecNode.setRequirementList(attachedRequirementList);
            em.persist(requirementSpecNode);
            if (requirementSpec != null) {
                requirementSpec.getRequirementSpecNodeList().add(requirementSpecNode);
                requirementSpec = em.merge(requirementSpec);
            }
            if (requirementSpecNodeRel != null) {
                requirementSpecNodeRel.getRequirementSpecNodeList().add(requirementSpecNode);
                requirementSpecNodeRel = em.merge(requirementSpecNodeRel);
            }
            for (RequirementSpecNode requirementSpecNodeListRequirementSpecNode : requirementSpecNode.getRequirementSpecNodeList()) {
                RequirementSpecNode oldRequirementSpecNodeOfRequirementSpecNodeListRequirementSpecNode = requirementSpecNodeListRequirementSpecNode.getRequirementSpecNode();
                requirementSpecNodeListRequirementSpecNode.setRequirementSpecNode(requirementSpecNode);
                requirementSpecNodeListRequirementSpecNode = em.merge(requirementSpecNodeListRequirementSpecNode);
                if (oldRequirementSpecNodeOfRequirementSpecNodeListRequirementSpecNode != null) {
                    oldRequirementSpecNodeOfRequirementSpecNodeListRequirementSpecNode.getRequirementSpecNodeList().remove(requirementSpecNodeListRequirementSpecNode);
                    oldRequirementSpecNodeOfRequirementSpecNodeListRequirementSpecNode = em.merge(oldRequirementSpecNodeOfRequirementSpecNodeListRequirementSpecNode);
                }
            }
            for (Requirement requirementListRequirement : requirementSpecNode.getRequirementList()) {
                RequirementSpecNode oldRequirementSpecNodeOfRequirementListRequirement = requirementListRequirement.getRequirementSpecNode();
                requirementListRequirement.setRequirementSpecNode(requirementSpecNode);
                requirementListRequirement = em.merge(requirementListRequirement);
                if (oldRequirementSpecNodeOfRequirementListRequirement != null) {
                    oldRequirementSpecNodeOfRequirementListRequirement.getRequirementList().remove(requirementListRequirement);
                    oldRequirementSpecNodeOfRequirementListRequirement = em.merge(oldRequirementSpecNodeOfRequirementListRequirement);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findRequirementSpecNode(requirementSpecNode.getRequirementSpecNodePK()) != null) {
                throw new PreexistingEntityException("RequirementSpecNode " + requirementSpecNode + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RequirementSpecNode requirementSpecNode) throws IllegalOrphanException, NonexistentEntityException, Exception {
        requirementSpecNode.getRequirementSpecNodePK().setRequirementSpecId(requirementSpecNode.getRequirementSpec().getRequirementSpecPK().getId());
        requirementSpecNode.getRequirementSpecNodePK().setRequirementSpecSpecLevelId(requirementSpecNode.getRequirementSpec().getRequirementSpecPK().getSpecLevelId());
        requirementSpecNode.getRequirementSpecNodePK().setRequirementSpecProjectId(requirementSpecNode.getRequirementSpec().getRequirementSpecPK().getProjectId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RequirementSpecNode persistentRequirementSpecNode = em.find(RequirementSpecNode.class, requirementSpecNode.getRequirementSpecNodePK());
            RequirementSpec requirementSpecOld = persistentRequirementSpecNode.getRequirementSpec();
            RequirementSpec requirementSpecNew = requirementSpecNode.getRequirementSpec();
            RequirementSpecNode requirementSpecNodeRelOld = persistentRequirementSpecNode.getRequirementSpecNode();
            RequirementSpecNode requirementSpecNodeRelNew = requirementSpecNode.getRequirementSpecNode();
            List<RequirementSpecNode> requirementSpecNodeListOld = persistentRequirementSpecNode.getRequirementSpecNodeList();
            List<RequirementSpecNode> requirementSpecNodeListNew = requirementSpecNode.getRequirementSpecNodeList();
            List<Requirement> requirementListOld = persistentRequirementSpecNode.getRequirementList();
            List<Requirement> requirementListNew = requirementSpecNode.getRequirementList();
            List<String> illegalOrphanMessages = null;
            for (Requirement requirementListOldRequirement : requirementListOld) {
                if (!requirementListNew.contains(requirementListOldRequirement)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Requirement " + requirementListOldRequirement + " since its requirementSpecNode field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (requirementSpecNew != null) {
                requirementSpecNew = em.getReference(requirementSpecNew.getClass(), requirementSpecNew.getRequirementSpecPK());
                requirementSpecNode.setRequirementSpec(requirementSpecNew);
            }
            if (requirementSpecNodeRelNew != null) {
                requirementSpecNodeRelNew = em.getReference(requirementSpecNodeRelNew.getClass(), requirementSpecNodeRelNew.getRequirementSpecNodePK());
                requirementSpecNode.setRequirementSpecNode(requirementSpecNodeRelNew);
            }
            List<RequirementSpecNode> attachedRequirementSpecNodeListNew = new ArrayList<>();
            for (RequirementSpecNode requirementSpecNodeListNewRequirementSpecNodeToAttach : requirementSpecNodeListNew) {
                requirementSpecNodeListNewRequirementSpecNodeToAttach = em.getReference(requirementSpecNodeListNewRequirementSpecNodeToAttach.getClass(), requirementSpecNodeListNewRequirementSpecNodeToAttach.getRequirementSpecNodePK());
                attachedRequirementSpecNodeListNew.add(requirementSpecNodeListNewRequirementSpecNodeToAttach);
            }
            requirementSpecNodeListNew = attachedRequirementSpecNodeListNew;
            requirementSpecNode.setRequirementSpecNodeList(requirementSpecNodeListNew);
            List<Requirement> attachedRequirementListNew = new ArrayList<>();
            for (Requirement requirementListNewRequirementToAttach : requirementListNew) {
                requirementListNewRequirementToAttach = em.getReference(requirementListNewRequirementToAttach.getClass(), requirementListNewRequirementToAttach.getId());
                attachedRequirementListNew.add(requirementListNewRequirementToAttach);
            }
            requirementListNew = attachedRequirementListNew;
            requirementSpecNode.setRequirementList(requirementListNew);
            requirementSpecNode = em.merge(requirementSpecNode);
            if (requirementSpecOld != null && !requirementSpecOld.equals(requirementSpecNew)) {
                requirementSpecOld.getRequirementSpecNodeList().remove(requirementSpecNode);
                requirementSpecOld = em.merge(requirementSpecOld);
            }
            if (requirementSpecNew != null && !requirementSpecNew.equals(requirementSpecOld)) {
                requirementSpecNew.getRequirementSpecNodeList().add(requirementSpecNode);
                requirementSpecNew = em.merge(requirementSpecNew);
            }
            if (requirementSpecNodeRelOld != null && !requirementSpecNodeRelOld.equals(requirementSpecNodeRelNew)) {
                requirementSpecNodeRelOld.getRequirementSpecNodeList().remove(requirementSpecNode);
                requirementSpecNodeRelOld = em.merge(requirementSpecNodeRelOld);
            }
            if (requirementSpecNodeRelNew != null && !requirementSpecNodeRelNew.equals(requirementSpecNodeRelOld)) {
                requirementSpecNodeRelNew.getRequirementSpecNodeList().add(requirementSpecNode);
                requirementSpecNodeRelNew = em.merge(requirementSpecNodeRelNew);
            }
            for (RequirementSpecNode requirementSpecNodeListOldRequirementSpecNode : requirementSpecNodeListOld) {
                if (!requirementSpecNodeListNew.contains(requirementSpecNodeListOldRequirementSpecNode)) {
                    requirementSpecNodeListOldRequirementSpecNode.setRequirementSpecNode(null);
                    requirementSpecNodeListOldRequirementSpecNode = em.merge(requirementSpecNodeListOldRequirementSpecNode);
                }
            }
            for (RequirementSpecNode requirementSpecNodeListNewRequirementSpecNode : requirementSpecNodeListNew) {
                if (!requirementSpecNodeListOld.contains(requirementSpecNodeListNewRequirementSpecNode)) {
                    RequirementSpecNode oldRequirementSpecNodeOfRequirementSpecNodeListNewRequirementSpecNode = requirementSpecNodeListNewRequirementSpecNode.getRequirementSpecNode();
                    requirementSpecNodeListNewRequirementSpecNode.setRequirementSpecNode(requirementSpecNode);
                    requirementSpecNodeListNewRequirementSpecNode = em.merge(requirementSpecNodeListNewRequirementSpecNode);
                    if (oldRequirementSpecNodeOfRequirementSpecNodeListNewRequirementSpecNode != null && !oldRequirementSpecNodeOfRequirementSpecNodeListNewRequirementSpecNode.equals(requirementSpecNode)) {
                        oldRequirementSpecNodeOfRequirementSpecNodeListNewRequirementSpecNode.getRequirementSpecNodeList().remove(requirementSpecNodeListNewRequirementSpecNode);
                        oldRequirementSpecNodeOfRequirementSpecNodeListNewRequirementSpecNode = em.merge(oldRequirementSpecNodeOfRequirementSpecNodeListNewRequirementSpecNode);
                    }
                }
            }
            for (Requirement requirementListNewRequirement : requirementListNew) {
                if (!requirementListOld.contains(requirementListNewRequirement)) {
                    RequirementSpecNode oldRequirementSpecNodeOfRequirementListNewRequirement = requirementListNewRequirement.getRequirementSpecNode();
                    requirementListNewRequirement.setRequirementSpecNode(requirementSpecNode);
                    requirementListNewRequirement = em.merge(requirementListNewRequirement);
                    if (oldRequirementSpecNodeOfRequirementListNewRequirement != null && !oldRequirementSpecNodeOfRequirementListNewRequirement.equals(requirementSpecNode)) {
                        oldRequirementSpecNodeOfRequirementListNewRequirement.getRequirementList().remove(requirementListNewRequirement);
                        oldRequirementSpecNodeOfRequirementListNewRequirement = em.merge(oldRequirementSpecNodeOfRequirementListNewRequirement);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RequirementSpecNodePK id = requirementSpecNode.getRequirementSpecNodePK();
                if (findRequirementSpecNode(id) == null) {
                    throw new NonexistentEntityException("The requirementSpecNode with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(RequirementSpecNodePK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RequirementSpecNode requirementSpecNode;
            try {
                requirementSpecNode = em.getReference(RequirementSpecNode.class, id);
                requirementSpecNode.getRequirementSpecNodePK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The requirementSpecNode with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Requirement> requirementListOrphanCheck = requirementSpecNode.getRequirementList();
            for (Requirement requirementListOrphanCheckRequirement : requirementListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This RequirementSpecNode (" + requirementSpecNode + ") cannot be destroyed since the Requirement " + requirementListOrphanCheckRequirement + " in its requirementList field has a non-nullable requirementSpecNode field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            RequirementSpec requirementSpec = requirementSpecNode.getRequirementSpec();
            if (requirementSpec != null) {
                requirementSpec.getRequirementSpecNodeList().remove(requirementSpecNode);
                requirementSpec = em.merge(requirementSpec);
            }
            RequirementSpecNode requirementSpecNodeRel = requirementSpecNode.getRequirementSpecNode();
            if (requirementSpecNodeRel != null) {
                requirementSpecNodeRel.getRequirementSpecNodeList().remove(requirementSpecNode);
                requirementSpecNodeRel = em.merge(requirementSpecNodeRel);
            }
            List<RequirementSpecNode> requirementSpecNodeList = requirementSpecNode.getRequirementSpecNodeList();
            for (RequirementSpecNode requirementSpecNodeListRequirementSpecNode : requirementSpecNodeList) {
                requirementSpecNodeListRequirementSpecNode.setRequirementSpecNode(null);
                requirementSpecNodeListRequirementSpecNode = em.merge(requirementSpecNodeListRequirementSpecNode);
            }
            em.remove(requirementSpecNode);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RequirementSpecNode> findRequirementSpecNodeEntities() {
        return findRequirementSpecNodeEntities(true, -1, -1);
    }

    public List<RequirementSpecNode> findRequirementSpecNodeEntities(int maxResults, int firstResult) {
        return findRequirementSpecNodeEntities(false, maxResults, firstResult);
    }

    private List<RequirementSpecNode> findRequirementSpecNodeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RequirementSpecNode.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        }
        finally {
            em.close();
        }
    }

    public RequirementSpecNode findRequirementSpecNode(RequirementSpecNodePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RequirementSpecNode.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getRequirementSpecNodeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RequirementSpecNode> rt = cq.from(RequirementSpecNode.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
