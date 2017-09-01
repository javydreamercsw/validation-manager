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
import com.validation.manager.core.db.FailureModeHasCause;
import com.validation.manager.core.db.FailureModeHasCauseHasRiskCategory;
import com.validation.manager.core.db.FailureModeHasCauseHasRiskCategoryPK;
import com.validation.manager.core.db.RiskCategory;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class FailureModeHasCauseHasRiskCategoryJpaController implements Serializable {

    public FailureModeHasCauseHasRiskCategoryJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(FailureModeHasCauseHasRiskCategory failureModeHasCauseHasRiskCategory) throws PreexistingEntityException, Exception {
        if (failureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryPK() == null) {
            failureModeHasCauseHasRiskCategory.setFailureModeHasCauseHasRiskCategoryPK(new FailureModeHasCauseHasRiskCategoryPK());
        }
        failureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryPK().setProjectId(failureModeHasCauseHasRiskCategory.getFailureModeHasCause().getFailureModeHasCausePK().getProjectId());
        failureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryPK().setCauseId(failureModeHasCauseHasRiskCategory.getFailureModeHasCause().getFailureModeHasCausePK().getCauseId());
        failureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryPK().setHazardId(failureModeHasCauseHasRiskCategory.getFailureModeHasCause().getFailureModeHasCausePK().getHazardId());
        failureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryPK().setRiskItemId(failureModeHasCauseHasRiskCategory.getFailureModeHasCause().getFailureModeHasCausePK().getRiskItemId());
        failureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryPK().setFailureModeId(failureModeHasCauseHasRiskCategory.getFailureModeHasCause().getFailureModeHasCausePK().getFailureModeId());
        failureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryPK().setFMEAid(failureModeHasCauseHasRiskCategory.getFailureModeHasCause().getFailureModeHasCausePK().getFMEAid());
        failureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryPK().setRiskCategoryId(failureModeHasCauseHasRiskCategory.getRiskCategory().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FailureModeHasCause failureModeHasCause = failureModeHasCauseHasRiskCategory.getFailureModeHasCause();
            if (failureModeHasCause != null) {
                failureModeHasCause = em.getReference(failureModeHasCause.getClass(), failureModeHasCause.getFailureModeHasCausePK());
                failureModeHasCauseHasRiskCategory.setFailureModeHasCause(failureModeHasCause);
            }
            RiskCategory riskCategory = failureModeHasCauseHasRiskCategory.getRiskCategory();
            if (riskCategory != null) {
                riskCategory = em.getReference(riskCategory.getClass(), riskCategory.getId());
                failureModeHasCauseHasRiskCategory.setRiskCategory(riskCategory);
            }
            em.persist(failureModeHasCauseHasRiskCategory);
            if (failureModeHasCause != null) {
                failureModeHasCause.getFailureModeHasCauseHasRiskCategoryList().add(failureModeHasCauseHasRiskCategory);
                failureModeHasCause = em.merge(failureModeHasCause);
            }
            if (riskCategory != null) {
                riskCategory.getFailureModeHasCauseHasRiskCategoryList().add(failureModeHasCauseHasRiskCategory);
                riskCategory = em.merge(riskCategory);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findFailureModeHasCauseHasRiskCategory(failureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryPK()) != null) {
                throw new PreexistingEntityException("FailureModeHasCauseHasRiskCategory " + failureModeHasCauseHasRiskCategory + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(FailureModeHasCauseHasRiskCategory failureModeHasCauseHasRiskCategory) throws NonexistentEntityException, Exception {
        failureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryPK().setProjectId(failureModeHasCauseHasRiskCategory.getFailureModeHasCause().getFailureModeHasCausePK().getProjectId());
        failureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryPK().setCauseId(failureModeHasCauseHasRiskCategory.getFailureModeHasCause().getFailureModeHasCausePK().getCauseId());
        failureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryPK().setHazardId(failureModeHasCauseHasRiskCategory.getFailureModeHasCause().getFailureModeHasCausePK().getHazardId());
        failureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryPK().setRiskItemId(failureModeHasCauseHasRiskCategory.getFailureModeHasCause().getFailureModeHasCausePK().getRiskItemId());
        failureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryPK().setFailureModeId(failureModeHasCauseHasRiskCategory.getFailureModeHasCause().getFailureModeHasCausePK().getFailureModeId());
        failureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryPK().setFMEAid(failureModeHasCauseHasRiskCategory.getFailureModeHasCause().getFailureModeHasCausePK().getFMEAid());
        failureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryPK().setRiskCategoryId(failureModeHasCauseHasRiskCategory.getRiskCategory().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FailureModeHasCauseHasRiskCategory persistentFailureModeHasCauseHasRiskCategory = em.find(FailureModeHasCauseHasRiskCategory.class, failureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryPK());
            FailureModeHasCause failureModeHasCauseOld = persistentFailureModeHasCauseHasRiskCategory.getFailureModeHasCause();
            FailureModeHasCause failureModeHasCauseNew = failureModeHasCauseHasRiskCategory.getFailureModeHasCause();
            RiskCategory riskCategoryOld = persistentFailureModeHasCauseHasRiskCategory.getRiskCategory();
            RiskCategory riskCategoryNew = failureModeHasCauseHasRiskCategory.getRiskCategory();
            if (failureModeHasCauseNew != null) {
                failureModeHasCauseNew = em.getReference(failureModeHasCauseNew.getClass(), failureModeHasCauseNew.getFailureModeHasCausePK());
                failureModeHasCauseHasRiskCategory.setFailureModeHasCause(failureModeHasCauseNew);
            }
            if (riskCategoryNew != null) {
                riskCategoryNew = em.getReference(riskCategoryNew.getClass(), riskCategoryNew.getId());
                failureModeHasCauseHasRiskCategory.setRiskCategory(riskCategoryNew);
            }
            failureModeHasCauseHasRiskCategory = em.merge(failureModeHasCauseHasRiskCategory);
            if (failureModeHasCauseOld != null && !failureModeHasCauseOld.equals(failureModeHasCauseNew)) {
                failureModeHasCauseOld.getFailureModeHasCauseHasRiskCategoryList().remove(failureModeHasCauseHasRiskCategory);
                failureModeHasCauseOld = em.merge(failureModeHasCauseOld);
            }
            if (failureModeHasCauseNew != null && !failureModeHasCauseNew.equals(failureModeHasCauseOld)) {
                failureModeHasCauseNew.getFailureModeHasCauseHasRiskCategoryList().add(failureModeHasCauseHasRiskCategory);
                failureModeHasCauseNew = em.merge(failureModeHasCauseNew);
            }
            if (riskCategoryOld != null && !riskCategoryOld.equals(riskCategoryNew)) {
                riskCategoryOld.getFailureModeHasCauseHasRiskCategoryList().remove(failureModeHasCauseHasRiskCategory);
                riskCategoryOld = em.merge(riskCategoryOld);
            }
            if (riskCategoryNew != null && !riskCategoryNew.equals(riskCategoryOld)) {
                riskCategoryNew.getFailureModeHasCauseHasRiskCategoryList().add(failureModeHasCauseHasRiskCategory);
                riskCategoryNew = em.merge(riskCategoryNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                FailureModeHasCauseHasRiskCategoryPK id = failureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryPK();
                if (findFailureModeHasCauseHasRiskCategory(id) == null) {
                    throw new NonexistentEntityException("The failureModeHasCauseHasRiskCategory with id " + id + " no longer exists.");
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

    public void destroy(FailureModeHasCauseHasRiskCategoryPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FailureModeHasCauseHasRiskCategory failureModeHasCauseHasRiskCategory;
            try {
                failureModeHasCauseHasRiskCategory = em.getReference(FailureModeHasCauseHasRiskCategory.class, id);
                failureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The failureModeHasCauseHasRiskCategory with id " + id + " no longer exists.", enfe);
            }
            FailureModeHasCause failureModeHasCause = failureModeHasCauseHasRiskCategory.getFailureModeHasCause();
            if (failureModeHasCause != null) {
                failureModeHasCause.getFailureModeHasCauseHasRiskCategoryList().remove(failureModeHasCauseHasRiskCategory);
                failureModeHasCause = em.merge(failureModeHasCause);
            }
            RiskCategory riskCategory = failureModeHasCauseHasRiskCategory.getRiskCategory();
            if (riskCategory != null) {
                riskCategory.getFailureModeHasCauseHasRiskCategoryList().remove(failureModeHasCauseHasRiskCategory);
                riskCategory = em.merge(riskCategory);
            }
            em.remove(failureModeHasCauseHasRiskCategory);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<FailureModeHasCauseHasRiskCategory> findFailureModeHasCauseHasRiskCategoryEntities() {
        return findFailureModeHasCauseHasRiskCategoryEntities(true, -1, -1);
    }

    public List<FailureModeHasCauseHasRiskCategory> findFailureModeHasCauseHasRiskCategoryEntities(int maxResults, int firstResult) {
        return findFailureModeHasCauseHasRiskCategoryEntities(false, maxResults, firstResult);
    }

    private List<FailureModeHasCauseHasRiskCategory> findFailureModeHasCauseHasRiskCategoryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(FailureModeHasCauseHasRiskCategory.class));
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

    public FailureModeHasCauseHasRiskCategory findFailureModeHasCauseHasRiskCategory(FailureModeHasCauseHasRiskCategoryPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(FailureModeHasCauseHasRiskCategory.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getFailureModeHasCauseHasRiskCategoryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<FailureModeHasCauseHasRiskCategory> rt = cq.from(FailureModeHasCauseHasRiskCategory.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
