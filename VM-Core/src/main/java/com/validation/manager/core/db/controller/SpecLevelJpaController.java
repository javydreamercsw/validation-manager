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
import com.validation.manager.core.db.SpecLevel;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class SpecLevelJpaController implements Serializable {

    public SpecLevelJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SpecLevel specLevel) {
        if (specLevel.getRequirementSpecList() == null) {
            specLevel.setRequirementSpecList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<RequirementSpec> attachedRequirementSpecList = new ArrayList<>();
            for (RequirementSpec requirementSpecListRequirementSpecToAttach : specLevel.getRequirementSpecList()) {
                requirementSpecListRequirementSpecToAttach = em.getReference(requirementSpecListRequirementSpecToAttach.getClass(), requirementSpecListRequirementSpecToAttach.getRequirementSpecPK());
                attachedRequirementSpecList.add(requirementSpecListRequirementSpecToAttach);
            }
            specLevel.setRequirementSpecList(attachedRequirementSpecList);
            em.persist(specLevel);
            for (RequirementSpec requirementSpecListRequirementSpec : specLevel.getRequirementSpecList()) {
                SpecLevel oldSpecLevelOfRequirementSpecListRequirementSpec = requirementSpecListRequirementSpec.getSpecLevel();
                requirementSpecListRequirementSpec.setSpecLevel(specLevel);
                requirementSpecListRequirementSpec = em.merge(requirementSpecListRequirementSpec);
                if (oldSpecLevelOfRequirementSpecListRequirementSpec != null) {
                    oldSpecLevelOfRequirementSpecListRequirementSpec.getRequirementSpecList().remove(requirementSpecListRequirementSpec);
                    oldSpecLevelOfRequirementSpecListRequirementSpec = em.merge(oldSpecLevelOfRequirementSpecListRequirementSpec);
                }
            }
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SpecLevel specLevel) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SpecLevel persistentSpecLevel = em.find(SpecLevel.class, specLevel.getId());
            List<RequirementSpec> requirementSpecListOld = persistentSpecLevel.getRequirementSpecList();
            List<RequirementSpec> requirementSpecListNew = specLevel.getRequirementSpecList();
            List<String> illegalOrphanMessages = null;
            for (RequirementSpec requirementSpecListOldRequirementSpec : requirementSpecListOld) {
                if (!requirementSpecListNew.contains(requirementSpecListOldRequirementSpec)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain RequirementSpec " + requirementSpecListOldRequirementSpec + " since its specLevel field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<RequirementSpec> attachedRequirementSpecListNew = new ArrayList<>();
            for (RequirementSpec requirementSpecListNewRequirementSpecToAttach : requirementSpecListNew) {
                requirementSpecListNewRequirementSpecToAttach = em.getReference(requirementSpecListNewRequirementSpecToAttach.getClass(), requirementSpecListNewRequirementSpecToAttach.getRequirementSpecPK());
                attachedRequirementSpecListNew.add(requirementSpecListNewRequirementSpecToAttach);
            }
            requirementSpecListNew = attachedRequirementSpecListNew;
            specLevel.setRequirementSpecList(requirementSpecListNew);
            specLevel = em.merge(specLevel);
            for (RequirementSpec requirementSpecListNewRequirementSpec : requirementSpecListNew) {
                if (!requirementSpecListOld.contains(requirementSpecListNewRequirementSpec)) {
                    SpecLevel oldSpecLevelOfRequirementSpecListNewRequirementSpec = requirementSpecListNewRequirementSpec.getSpecLevel();
                    requirementSpecListNewRequirementSpec.setSpecLevel(specLevel);
                    requirementSpecListNewRequirementSpec = em.merge(requirementSpecListNewRequirementSpec);
                    if (oldSpecLevelOfRequirementSpecListNewRequirementSpec != null && !oldSpecLevelOfRequirementSpecListNewRequirementSpec.equals(specLevel)) {
                        oldSpecLevelOfRequirementSpecListNewRequirementSpec.getRequirementSpecList().remove(requirementSpecListNewRequirementSpec);
                        oldSpecLevelOfRequirementSpecListNewRequirementSpec = em.merge(oldSpecLevelOfRequirementSpecListNewRequirementSpec);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = specLevel.getId();
                if (findSpecLevel(id) == null) {
                    throw new NonexistentEntityException("The specLevel with id " + id + " no longer exists.");
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

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SpecLevel specLevel;
            try {
                specLevel = em.getReference(SpecLevel.class, id);
                specLevel.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The specLevel with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RequirementSpec> requirementSpecListOrphanCheck = specLevel.getRequirementSpecList();
            for (RequirementSpec requirementSpecListOrphanCheckRequirementSpec : requirementSpecListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This SpecLevel (" + specLevel + ") cannot be destroyed since the RequirementSpec " + requirementSpecListOrphanCheckRequirementSpec + " in its requirementSpecList field has a non-nullable specLevel field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(specLevel);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SpecLevel> findSpecLevelEntities() {
        return findSpecLevelEntities(true, -1, -1);
    }

    public List<SpecLevel> findSpecLevelEntities(int maxResults, int firstResult) {
        return findSpecLevelEntities(false, maxResults, firstResult);
    }

    private List<SpecLevel> findSpecLevelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SpecLevel.class));
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

    public SpecLevel findSpecLevel(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SpecLevel.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getSpecLevelCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SpecLevel> rt = cq.from(SpecLevel.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
