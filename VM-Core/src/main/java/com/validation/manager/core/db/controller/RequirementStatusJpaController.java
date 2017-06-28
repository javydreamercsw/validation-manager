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
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementStatus;
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
public class RequirementStatusJpaController implements Serializable {

    public RequirementStatusJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RequirementStatus requirementStatus) {
        if (requirementStatus.getRequirementList() == null) {
            requirementStatus.setRequirementList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Requirement> attachedRequirementList = new ArrayList<>();
            for (Requirement requirementListRequirementToAttach : requirementStatus.getRequirementList()) {
                requirementListRequirementToAttach = em.getReference(requirementListRequirementToAttach.getClass(), requirementListRequirementToAttach.getId());
                attachedRequirementList.add(requirementListRequirementToAttach);
            }
            requirementStatus.setRequirementList(attachedRequirementList);
            em.persist(requirementStatus);
            for (Requirement requirementListRequirement : requirementStatus.getRequirementList()) {
                RequirementStatus oldRequirementStatusIdOfRequirementListRequirement = requirementListRequirement.getRequirementStatusId();
                requirementListRequirement.setRequirementStatusId(requirementStatus);
                requirementListRequirement = em.merge(requirementListRequirement);
                if (oldRequirementStatusIdOfRequirementListRequirement != null) {
                    oldRequirementStatusIdOfRequirementListRequirement.getRequirementList().remove(requirementListRequirement);
                    oldRequirementStatusIdOfRequirementListRequirement = em.merge(oldRequirementStatusIdOfRequirementListRequirement);
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

    public void edit(RequirementStatus requirementStatus) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RequirementStatus persistentRequirementStatus = em.find(RequirementStatus.class, requirementStatus.getId());
            List<Requirement> requirementListOld = persistentRequirementStatus.getRequirementList();
            List<Requirement> requirementListNew = requirementStatus.getRequirementList();
            List<String> illegalOrphanMessages = null;
            for (Requirement requirementListOldRequirement : requirementListOld) {
                if (!requirementListNew.contains(requirementListOldRequirement)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Requirement " + requirementListOldRequirement + " since its requirementStatusId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Requirement> attachedRequirementListNew = new ArrayList<>();
            for (Requirement requirementListNewRequirementToAttach : requirementListNew) {
                requirementListNewRequirementToAttach = em.getReference(requirementListNewRequirementToAttach.getClass(), requirementListNewRequirementToAttach.getId());
                attachedRequirementListNew.add(requirementListNewRequirementToAttach);
            }
            requirementListNew = attachedRequirementListNew;
            requirementStatus.setRequirementList(requirementListNew);
            requirementStatus = em.merge(requirementStatus);
            for (Requirement requirementListNewRequirement : requirementListNew) {
                if (!requirementListOld.contains(requirementListNewRequirement)) {
                    RequirementStatus oldRequirementStatusIdOfRequirementListNewRequirement = requirementListNewRequirement.getRequirementStatusId();
                    requirementListNewRequirement.setRequirementStatusId(requirementStatus);
                    requirementListNewRequirement = em.merge(requirementListNewRequirement);
                    if (oldRequirementStatusIdOfRequirementListNewRequirement != null && !oldRequirementStatusIdOfRequirementListNewRequirement.equals(requirementStatus)) {
                        oldRequirementStatusIdOfRequirementListNewRequirement.getRequirementList().remove(requirementListNewRequirement);
                        oldRequirementStatusIdOfRequirementListNewRequirement = em.merge(oldRequirementStatusIdOfRequirementListNewRequirement);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = requirementStatus.getId();
                if (findRequirementStatus(id) == null) {
                    throw new NonexistentEntityException("The requirementStatus with id " + id + " no longer exists.");
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
            RequirementStatus requirementStatus;
            try {
                requirementStatus = em.getReference(RequirementStatus.class, id);
                requirementStatus.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The requirementStatus with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Requirement> requirementListOrphanCheck = requirementStatus.getRequirementList();
            for (Requirement requirementListOrphanCheckRequirement : requirementListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This RequirementStatus (" + requirementStatus + ") cannot be destroyed since the Requirement " + requirementListOrphanCheckRequirement + " in its requirementList field has a non-nullable requirementStatusId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(requirementStatus);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RequirementStatus> findRequirementStatusEntities() {
        return findRequirementStatusEntities(true, -1, -1);
    }

    public List<RequirementStatus> findRequirementStatusEntities(int maxResults, int firstResult) {
        return findRequirementStatusEntities(false, maxResults, firstResult);
    }

    private List<RequirementStatus> findRequirementStatusEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RequirementStatus.class));
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

    public RequirementStatus findRequirementStatus(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RequirementStatus.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getRequirementStatusCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RequirementStatus> rt = cq.from(RequirementStatus.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
