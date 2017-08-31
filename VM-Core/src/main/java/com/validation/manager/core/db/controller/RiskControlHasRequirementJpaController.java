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
import com.validation.manager.core.db.RiskControl;
import com.validation.manager.core.db.RiskControlHasRequirement;
import com.validation.manager.core.db.RiskControlHasRequirementPK;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class RiskControlHasRequirementJpaController implements Serializable {

    public RiskControlHasRequirementJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RiskControlHasRequirement riskControlHasRequirement) throws PreexistingEntityException, Exception {
        if (riskControlHasRequirement.getRiskControlHasRequirementPK() == null) {
            riskControlHasRequirement.setRiskControlHasRequirementPK(new RiskControlHasRequirementPK());
        }
        riskControlHasRequirement.getRiskControlHasRequirementPK().setRequirementId(riskControlHasRequirement.getRequirement().getId());
        riskControlHasRequirement.getRiskControlHasRequirementPK().setRiskControlRiskControlTypeId(riskControlHasRequirement.getRiskControl().getRiskControlPK().getRiskControlTypeId());
        riskControlHasRequirement.getRiskControlHasRequirementPK().setRiskControlId(riskControlHasRequirement.getRiskControl().getRiskControlPK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Requirement requirement = riskControlHasRequirement.getRequirement();
            if (requirement != null) {
                requirement = em.getReference(requirement.getClass(), requirement.getId());
                riskControlHasRequirement.setRequirement(requirement);
            }
            RiskControl riskControl = riskControlHasRequirement.getRiskControl();
            if (riskControl != null) {
                riskControl = em.getReference(riskControl.getClass(), riskControl.getRiskControlPK());
                riskControlHasRequirement.setRiskControl(riskControl);
            }
            em.persist(riskControlHasRequirement);
            if (requirement != null) {
                requirement.getRiskControlHasRequirementList().add(riskControlHasRequirement);
                requirement = em.merge(requirement);
            }
            if (riskControl != null) {
                riskControl.getRiskControlHasRequirementList().add(riskControlHasRequirement);
                riskControl = em.merge(riskControl);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findRiskControlHasRequirement(riskControlHasRequirement.getRiskControlHasRequirementPK()) != null) {
                throw new PreexistingEntityException("RiskControlHasRequirement " + riskControlHasRequirement + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RiskControlHasRequirement riskControlHasRequirement) throws NonexistentEntityException, Exception {
        riskControlHasRequirement.getRiskControlHasRequirementPK().setRequirementId(riskControlHasRequirement.getRequirement().getId());
        riskControlHasRequirement.getRiskControlHasRequirementPK().setRiskControlRiskControlTypeId(riskControlHasRequirement.getRiskControl().getRiskControlPK().getRiskControlTypeId());
        riskControlHasRequirement.getRiskControlHasRequirementPK().setRiskControlId(riskControlHasRequirement.getRiskControl().getRiskControlPK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskControlHasRequirement persistentRiskControlHasRequirement = em.find(RiskControlHasRequirement.class, riskControlHasRequirement.getRiskControlHasRequirementPK());
            Requirement requirementOld = persistentRiskControlHasRequirement.getRequirement();
            Requirement requirementNew = riskControlHasRequirement.getRequirement();
            RiskControl riskControlOld = persistentRiskControlHasRequirement.getRiskControl();
            RiskControl riskControlNew = riskControlHasRequirement.getRiskControl();
            if (requirementNew != null) {
                requirementNew = em.getReference(requirementNew.getClass(), requirementNew.getId());
                riskControlHasRequirement.setRequirement(requirementNew);
            }
            if (riskControlNew != null) {
                riskControlNew = em.getReference(riskControlNew.getClass(), riskControlNew.getRiskControlPK());
                riskControlHasRequirement.setRiskControl(riskControlNew);
            }
            riskControlHasRequirement = em.merge(riskControlHasRequirement);
            if (requirementOld != null && !requirementOld.equals(requirementNew)) {
                requirementOld.getRiskControlHasRequirementList().remove(riskControlHasRequirement);
                requirementOld = em.merge(requirementOld);
            }
            if (requirementNew != null && !requirementNew.equals(requirementOld)) {
                requirementNew.getRiskControlHasRequirementList().add(riskControlHasRequirement);
                requirementNew = em.merge(requirementNew);
            }
            if (riskControlOld != null && !riskControlOld.equals(riskControlNew)) {
                riskControlOld.getRiskControlHasRequirementList().remove(riskControlHasRequirement);
                riskControlOld = em.merge(riskControlOld);
            }
            if (riskControlNew != null && !riskControlNew.equals(riskControlOld)) {
                riskControlNew.getRiskControlHasRequirementList().add(riskControlHasRequirement);
                riskControlNew = em.merge(riskControlNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RiskControlHasRequirementPK id = riskControlHasRequirement.getRiskControlHasRequirementPK();
                if (findRiskControlHasRequirement(id) == null) {
                    throw new NonexistentEntityException("The riskControlHasRequirement with id " + id + " no longer exists.");
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

    public void destroy(RiskControlHasRequirementPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskControlHasRequirement riskControlHasRequirement;
            try {
                riskControlHasRequirement = em.getReference(RiskControlHasRequirement.class, id);
                riskControlHasRequirement.getRiskControlHasRequirementPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The riskControlHasRequirement with id " + id + " no longer exists.", enfe);
            }
            Requirement requirement = riskControlHasRequirement.getRequirement();
            if (requirement != null) {
                requirement.getRiskControlHasRequirementList().remove(riskControlHasRequirement);
                requirement = em.merge(requirement);
            }
            RiskControl riskControl = riskControlHasRequirement.getRiskControl();
            if (riskControl != null) {
                riskControl.getRiskControlHasRequirementList().remove(riskControlHasRequirement);
                riskControl = em.merge(riskControl);
            }
            em.remove(riskControlHasRequirement);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RiskControlHasRequirement> findRiskControlHasRequirementEntities() {
        return findRiskControlHasRequirementEntities(true, -1, -1);
    }

    public List<RiskControlHasRequirement> findRiskControlHasRequirementEntities(int maxResults, int firstResult) {
        return findRiskControlHasRequirementEntities(false, maxResults, firstResult);
    }

    private List<RiskControlHasRequirement> findRiskControlHasRequirementEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RiskControlHasRequirement.class));
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

    public RiskControlHasRequirement findRiskControlHasRequirement(RiskControlHasRequirementPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RiskControlHasRequirement.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getRiskControlHasRequirementCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RiskControlHasRequirement> rt = cq.from(RiskControlHasRequirement.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
