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
import com.validation.manager.core.db.RequirementType;
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
public class RequirementTypeJpaController implements Serializable {

    public RequirementTypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RequirementType requirementType) {
        if (requirementType.getRequirementList() == null) {
            requirementType.setRequirementList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Requirement> attachedRequirementList = new ArrayList<>();
            for (Requirement requirementListRequirementToAttach : requirementType.getRequirementList()) {
                requirementListRequirementToAttach = em.getReference(requirementListRequirementToAttach.getClass(), requirementListRequirementToAttach.getId());
                attachedRequirementList.add(requirementListRequirementToAttach);
            }
            requirementType.setRequirementList(attachedRequirementList);
            em.persist(requirementType);
            for (Requirement requirementListRequirement : requirementType.getRequirementList()) {
                RequirementType oldRequirementTypeIdOfRequirementListRequirement = requirementListRequirement.getRequirementTypeId();
                requirementListRequirement.setRequirementTypeId(requirementType);
                requirementListRequirement = em.merge(requirementListRequirement);
                if (oldRequirementTypeIdOfRequirementListRequirement != null) {
                    oldRequirementTypeIdOfRequirementListRequirement.getRequirementList().remove(requirementListRequirement);
                    oldRequirementTypeIdOfRequirementListRequirement = em.merge(oldRequirementTypeIdOfRequirementListRequirement);
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

    public void edit(RequirementType requirementType) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RequirementType persistentRequirementType = em.find(RequirementType.class, requirementType.getId());
            List<Requirement> requirementListOld = persistentRequirementType.getRequirementList();
            List<Requirement> requirementListNew = requirementType.getRequirementList();
            List<String> illegalOrphanMessages = null;
            for (Requirement requirementListOldRequirement : requirementListOld) {
                if (!requirementListNew.contains(requirementListOldRequirement)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Requirement " + requirementListOldRequirement + " since its requirementTypeId field is not nullable.");
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
            requirementType.setRequirementList(requirementListNew);
            requirementType = em.merge(requirementType);
            for (Requirement requirementListNewRequirement : requirementListNew) {
                if (!requirementListOld.contains(requirementListNewRequirement)) {
                    RequirementType oldRequirementTypeIdOfRequirementListNewRequirement = requirementListNewRequirement.getRequirementTypeId();
                    requirementListNewRequirement.setRequirementTypeId(requirementType);
                    requirementListNewRequirement = em.merge(requirementListNewRequirement);
                    if (oldRequirementTypeIdOfRequirementListNewRequirement != null && !oldRequirementTypeIdOfRequirementListNewRequirement.equals(requirementType)) {
                        oldRequirementTypeIdOfRequirementListNewRequirement.getRequirementList().remove(requirementListNewRequirement);
                        oldRequirementTypeIdOfRequirementListNewRequirement = em.merge(oldRequirementTypeIdOfRequirementListNewRequirement);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = requirementType.getId();
                if (findRequirementType(id) == null) {
                    throw new NonexistentEntityException("The requirementType with id " + id + " no longer exists.");
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
            RequirementType requirementType;
            try {
                requirementType = em.getReference(RequirementType.class, id);
                requirementType.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The requirementType with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Requirement> requirementListOrphanCheck = requirementType.getRequirementList();
            for (Requirement requirementListOrphanCheckRequirement : requirementListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This RequirementType (" + requirementType + ") cannot be destroyed since the Requirement " + requirementListOrphanCheckRequirement + " in its requirementList field has a non-nullable requirementTypeId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(requirementType);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RequirementType> findRequirementTypeEntities() {
        return findRequirementTypeEntities(true, -1, -1);
    }

    public List<RequirementType> findRequirementTypeEntities(int maxResults, int firstResult) {
        return findRequirementTypeEntities(false, maxResults, firstResult);
    }

    private List<RequirementType> findRequirementTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RequirementType.class));
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

    public RequirementType findRequirementType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RequirementType.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getRequirementTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RequirementType> rt = cq.from(RequirementType.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
