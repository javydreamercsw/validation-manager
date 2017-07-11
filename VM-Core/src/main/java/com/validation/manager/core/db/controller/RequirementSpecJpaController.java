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
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.SpecLevel;
import com.validation.manager.core.db.RequirementSpecNode;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.Baseline;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecPK;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class RequirementSpecJpaController implements Serializable {

    public RequirementSpecJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RequirementSpec requirementSpec) throws PreexistingEntityException, Exception {
        if (requirementSpec.getRequirementSpecPK() == null) {
            requirementSpec.setRequirementSpecPK(new RequirementSpecPK());
        }
        if (requirementSpec.getRequirementSpecNodeList() == null) {
            requirementSpec.setRequirementSpecNodeList(new ArrayList<>());
        }
        if (requirementSpec.getBaselineList() == null) {
            requirementSpec.setBaselineList(new ArrayList<>());
        }
        requirementSpec.getRequirementSpecPK().setProjectId(requirementSpec.getProject().getId());
        requirementSpec.getRequirementSpecPK().setSpecLevelId(requirementSpec.getSpecLevel().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Project project = requirementSpec.getProject();
            if (project != null) {
                project = em.getReference(project.getClass(), project.getId());
                requirementSpec.setProject(project);
            }
            SpecLevel specLevel = requirementSpec.getSpecLevel();
            if (specLevel != null) {
                specLevel = em.getReference(specLevel.getClass(), specLevel.getId());
                requirementSpec.setSpecLevel(specLevel);
            }
            List<RequirementSpecNode> attachedRequirementSpecNodeList = new ArrayList<>();
            for (RequirementSpecNode requirementSpecNodeListRequirementSpecNodeToAttach : requirementSpec.getRequirementSpecNodeList()) {
                requirementSpecNodeListRequirementSpecNodeToAttach = em.getReference(requirementSpecNodeListRequirementSpecNodeToAttach.getClass(), requirementSpecNodeListRequirementSpecNodeToAttach.getRequirementSpecNodePK());
                attachedRequirementSpecNodeList.add(requirementSpecNodeListRequirementSpecNodeToAttach);
            }
            requirementSpec.setRequirementSpecNodeList(attachedRequirementSpecNodeList);
            List<Baseline> attachedBaselineList = new ArrayList<>();
            for (Baseline baselineListBaselineToAttach : requirementSpec.getBaselineList()) {
                baselineListBaselineToAttach = em.getReference(baselineListBaselineToAttach.getClass(), baselineListBaselineToAttach.getId());
                attachedBaselineList.add(baselineListBaselineToAttach);
            }
            requirementSpec.setBaselineList(attachedBaselineList);
            em.persist(requirementSpec);
            if (project != null) {
                project.getRequirementSpecList().add(requirementSpec);
                project = em.merge(project);
            }
            if (specLevel != null) {
                specLevel.getRequirementSpecList().add(requirementSpec);
                specLevel = em.merge(specLevel);
            }
            for (RequirementSpecNode requirementSpecNodeListRequirementSpecNode : requirementSpec.getRequirementSpecNodeList()) {
                RequirementSpec oldRequirementSpecOfRequirementSpecNodeListRequirementSpecNode = requirementSpecNodeListRequirementSpecNode.getRequirementSpec();
                requirementSpecNodeListRequirementSpecNode.setRequirementSpec(requirementSpec);
                requirementSpecNodeListRequirementSpecNode = em.merge(requirementSpecNodeListRequirementSpecNode);
                if (oldRequirementSpecOfRequirementSpecNodeListRequirementSpecNode != null) {
                    oldRequirementSpecOfRequirementSpecNodeListRequirementSpecNode.getRequirementSpecNodeList().remove(requirementSpecNodeListRequirementSpecNode);
                    oldRequirementSpecOfRequirementSpecNodeListRequirementSpecNode = em.merge(oldRequirementSpecOfRequirementSpecNodeListRequirementSpecNode);
                }
            }
            for (Baseline baselineListBaseline : requirementSpec.getBaselineList()) {
                RequirementSpec oldRequirementSpecOfBaselineListBaseline = baselineListBaseline.getRequirementSpec();
                baselineListBaseline.setRequirementSpec(requirementSpec);
                baselineListBaseline = em.merge(baselineListBaseline);
                if (oldRequirementSpecOfBaselineListBaseline != null) {
                    oldRequirementSpecOfBaselineListBaseline.getBaselineList().remove(baselineListBaseline);
                    oldRequirementSpecOfBaselineListBaseline = em.merge(oldRequirementSpecOfBaselineListBaseline);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findRequirementSpec(requirementSpec.getRequirementSpecPK()) != null) {
                throw new PreexistingEntityException("RequirementSpec " + requirementSpec + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RequirementSpec requirementSpec) throws IllegalOrphanException, NonexistentEntityException, Exception {
        requirementSpec.getRequirementSpecPK().setProjectId(requirementSpec.getProject().getId());
        requirementSpec.getRequirementSpecPK().setSpecLevelId(requirementSpec.getSpecLevel().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RequirementSpec persistentRequirementSpec = em.find(RequirementSpec.class, requirementSpec.getRequirementSpecPK());
            Project projectOld = persistentRequirementSpec.getProject();
            Project projectNew = requirementSpec.getProject();
            SpecLevel specLevelOld = persistentRequirementSpec.getSpecLevel();
            SpecLevel specLevelNew = requirementSpec.getSpecLevel();
            List<RequirementSpecNode> requirementSpecNodeListOld = persistentRequirementSpec.getRequirementSpecNodeList();
            List<RequirementSpecNode> requirementSpecNodeListNew = requirementSpec.getRequirementSpecNodeList();
            List<Baseline> baselineListOld = persistentRequirementSpec.getBaselineList();
            List<Baseline> baselineListNew = requirementSpec.getBaselineList();
            List<String> illegalOrphanMessages = null;
            for (RequirementSpecNode requirementSpecNodeListOldRequirementSpecNode : requirementSpecNodeListOld) {
                if (!requirementSpecNodeListNew.contains(requirementSpecNodeListOldRequirementSpecNode)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain RequirementSpecNode " + requirementSpecNodeListOldRequirementSpecNode + " since its requirementSpec field is not nullable.");
                }
            }
            for (Baseline baselineListOldBaseline : baselineListOld) {
                if (!baselineListNew.contains(baselineListOldBaseline)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Baseline " + baselineListOldBaseline + " since its requirementSpec field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (projectNew != null) {
                projectNew = em.getReference(projectNew.getClass(), projectNew.getId());
                requirementSpec.setProject(projectNew);
            }
            if (specLevelNew != null) {
                specLevelNew = em.getReference(specLevelNew.getClass(), specLevelNew.getId());
                requirementSpec.setSpecLevel(specLevelNew);
            }
            List<RequirementSpecNode> attachedRequirementSpecNodeListNew = new ArrayList<>();
            for (RequirementSpecNode requirementSpecNodeListNewRequirementSpecNodeToAttach : requirementSpecNodeListNew) {
                requirementSpecNodeListNewRequirementSpecNodeToAttach = em.getReference(requirementSpecNodeListNewRequirementSpecNodeToAttach.getClass(), requirementSpecNodeListNewRequirementSpecNodeToAttach.getRequirementSpecNodePK());
                attachedRequirementSpecNodeListNew.add(requirementSpecNodeListNewRequirementSpecNodeToAttach);
            }
            requirementSpecNodeListNew = attachedRequirementSpecNodeListNew;
            requirementSpec.setRequirementSpecNodeList(requirementSpecNodeListNew);
            List<Baseline> attachedBaselineListNew = new ArrayList<>();
            for (Baseline baselineListNewBaselineToAttach : baselineListNew) {
                baselineListNewBaselineToAttach = em.getReference(baselineListNewBaselineToAttach.getClass(), baselineListNewBaselineToAttach.getId());
                attachedBaselineListNew.add(baselineListNewBaselineToAttach);
            }
            baselineListNew = attachedBaselineListNew;
            requirementSpec.setBaselineList(baselineListNew);
            requirementSpec = em.merge(requirementSpec);
            if (projectOld != null && !projectOld.equals(projectNew)) {
                projectOld.getRequirementSpecList().remove(requirementSpec);
                projectOld = em.merge(projectOld);
            }
            if (projectNew != null && !projectNew.equals(projectOld)) {
                projectNew.getRequirementSpecList().add(requirementSpec);
                projectNew = em.merge(projectNew);
            }
            if (specLevelOld != null && !specLevelOld.equals(specLevelNew)) {
                specLevelOld.getRequirementSpecList().remove(requirementSpec);
                specLevelOld = em.merge(specLevelOld);
            }
            if (specLevelNew != null && !specLevelNew.equals(specLevelOld)) {
                specLevelNew.getRequirementSpecList().add(requirementSpec);
                specLevelNew = em.merge(specLevelNew);
            }
            for (RequirementSpecNode requirementSpecNodeListNewRequirementSpecNode : requirementSpecNodeListNew) {
                if (!requirementSpecNodeListOld.contains(requirementSpecNodeListNewRequirementSpecNode)) {
                    RequirementSpec oldRequirementSpecOfRequirementSpecNodeListNewRequirementSpecNode = requirementSpecNodeListNewRequirementSpecNode.getRequirementSpec();
                    requirementSpecNodeListNewRequirementSpecNode.setRequirementSpec(requirementSpec);
                    requirementSpecNodeListNewRequirementSpecNode = em.merge(requirementSpecNodeListNewRequirementSpecNode);
                    if (oldRequirementSpecOfRequirementSpecNodeListNewRequirementSpecNode != null && !oldRequirementSpecOfRequirementSpecNodeListNewRequirementSpecNode.equals(requirementSpec)) {
                        oldRequirementSpecOfRequirementSpecNodeListNewRequirementSpecNode.getRequirementSpecNodeList().remove(requirementSpecNodeListNewRequirementSpecNode);
                        oldRequirementSpecOfRequirementSpecNodeListNewRequirementSpecNode = em.merge(oldRequirementSpecOfRequirementSpecNodeListNewRequirementSpecNode);
                    }
                }
            }
            for (Baseline baselineListNewBaseline : baselineListNew) {
                if (!baselineListOld.contains(baselineListNewBaseline)) {
                    RequirementSpec oldRequirementSpecOfBaselineListNewBaseline = baselineListNewBaseline.getRequirementSpec();
                    baselineListNewBaseline.setRequirementSpec(requirementSpec);
                    baselineListNewBaseline = em.merge(baselineListNewBaseline);
                    if (oldRequirementSpecOfBaselineListNewBaseline != null && !oldRequirementSpecOfBaselineListNewBaseline.equals(requirementSpec)) {
                        oldRequirementSpecOfBaselineListNewBaseline.getBaselineList().remove(baselineListNewBaseline);
                        oldRequirementSpecOfBaselineListNewBaseline = em.merge(oldRequirementSpecOfBaselineListNewBaseline);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RequirementSpecPK id = requirementSpec.getRequirementSpecPK();
                if (findRequirementSpec(id) == null) {
                    throw new NonexistentEntityException("The requirementSpec with id " + id + " no longer exists.");
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

    public void destroy(RequirementSpecPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RequirementSpec requirementSpec;
            try {
                requirementSpec = em.getReference(RequirementSpec.class, id);
                requirementSpec.getRequirementSpecPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The requirementSpec with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RequirementSpecNode> requirementSpecNodeListOrphanCheck = requirementSpec.getRequirementSpecNodeList();
            for (RequirementSpecNode requirementSpecNodeListOrphanCheckRequirementSpecNode : requirementSpecNodeListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This RequirementSpec (" + requirementSpec + ") cannot be destroyed since the RequirementSpecNode " + requirementSpecNodeListOrphanCheckRequirementSpecNode + " in its requirementSpecNodeList field has a non-nullable requirementSpec field.");
            }
            List<Baseline> baselineListOrphanCheck = requirementSpec.getBaselineList();
            for (Baseline baselineListOrphanCheckBaseline : baselineListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This RequirementSpec (" + requirementSpec + ") cannot be destroyed since the Baseline " + baselineListOrphanCheckBaseline + " in its baselineList field has a non-nullable requirementSpec field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Project project = requirementSpec.getProject();
            if (project != null) {
                project.getRequirementSpecList().remove(requirementSpec);
                project = em.merge(project);
            }
            SpecLevel specLevel = requirementSpec.getSpecLevel();
            if (specLevel != null) {
                specLevel.getRequirementSpecList().remove(requirementSpec);
                specLevel = em.merge(specLevel);
            }
            em.remove(requirementSpec);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RequirementSpec> findRequirementSpecEntities() {
        return findRequirementSpecEntities(true, -1, -1);
    }

    public List<RequirementSpec> findRequirementSpecEntities(int maxResults, int firstResult) {
        return findRequirementSpecEntities(false, maxResults, firstResult);
    }

    private List<RequirementSpec> findRequirementSpecEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RequirementSpec.class));
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

    public RequirementSpec findRequirementSpec(RequirementSpecPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RequirementSpec.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getRequirementSpecCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RequirementSpec> rt = cq.from(RequirementSpec.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
