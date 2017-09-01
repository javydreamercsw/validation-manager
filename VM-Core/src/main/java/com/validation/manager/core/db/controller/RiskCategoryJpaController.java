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
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.FailureModeHasCauseHasRiskCategory;
import com.validation.manager.core.db.Fmea;
import com.validation.manager.core.db.RiskCategory;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class RiskCategoryJpaController implements Serializable {

    public RiskCategoryJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RiskCategory riskCategory) {
        if (riskCategory.getFailureModeHasCauseList() == null) {
            riskCategory.setFailureModeHasCauseList(new ArrayList<>());
        }
        if (riskCategory.getFailureModeHasCauseHasRiskCategoryList() == null) {
            riskCategory.setFailureModeHasCauseHasRiskCategoryList(new ArrayList<>());
        }
        if (riskCategory.getFmeaList() == null) {
            riskCategory.setFmeaList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<FailureModeHasCause> attachedFailureModeHasCauseList = new ArrayList<>();
            for (FailureModeHasCause failureModeHasCauseListFailureModeHasCauseToAttach : riskCategory.getFailureModeHasCauseList()) {
                failureModeHasCauseListFailureModeHasCauseToAttach = em.getReference(failureModeHasCauseListFailureModeHasCauseToAttach.getClass(), failureModeHasCauseListFailureModeHasCauseToAttach.getFailureModeHasCausePK());
                attachedFailureModeHasCauseList.add(failureModeHasCauseListFailureModeHasCauseToAttach);
            }
            riskCategory.setFailureModeHasCauseList(attachedFailureModeHasCauseList);
            List<FailureModeHasCauseHasRiskCategory> attachedFailureModeHasCauseHasRiskCategoryList = new ArrayList<>();
            for (FailureModeHasCauseHasRiskCategory failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategoryToAttach : riskCategory.getFailureModeHasCauseHasRiskCategoryList()) {
                failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategoryToAttach = em.getReference(failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategoryToAttach.getClass(), failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategoryToAttach.getFailureModeHasCauseHasRiskCategoryPK());
                attachedFailureModeHasCauseHasRiskCategoryList.add(failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategoryToAttach);
            }
            riskCategory.setFailureModeHasCauseHasRiskCategoryList(attachedFailureModeHasCauseHasRiskCategoryList);
            List<Fmea> attachedFmeaList = new ArrayList<>();
            for (Fmea fmeaListFmeaToAttach : riskCategory.getFmeaList()) {
                fmeaListFmeaToAttach = em.getReference(fmeaListFmeaToAttach.getClass(), fmeaListFmeaToAttach.getFmeaPK());
                attachedFmeaList.add(fmeaListFmeaToAttach);
            }
            riskCategory.setFmeaList(attachedFmeaList);
            em.persist(riskCategory);
            for (FailureModeHasCause failureModeHasCauseListFailureModeHasCause : riskCategory.getFailureModeHasCauseList()) {
                failureModeHasCauseListFailureModeHasCause.getRiskCategoryList().add(riskCategory);
                failureModeHasCauseListFailureModeHasCause = em.merge(failureModeHasCauseListFailureModeHasCause);
            }
            for (FailureModeHasCauseHasRiskCategory failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory : riskCategory.getFailureModeHasCauseHasRiskCategoryList()) {
                RiskCategory oldRiskCategoryOfFailureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory = failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory.getRiskCategory();
                failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory.setRiskCategory(riskCategory);
                failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory = em.merge(failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory);
                if (oldRiskCategoryOfFailureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory != null) {
                    oldRiskCategoryOfFailureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryList().remove(failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory);
                    oldRiskCategoryOfFailureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory = em.merge(oldRiskCategoryOfFailureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory);
                }
            }
            for (Fmea fmeaListFmea : riskCategory.getFmeaList()) {
                fmeaListFmea.getRiskCategoryList().add(riskCategory);
                fmeaListFmea = em.merge(fmeaListFmea);
            }
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RiskCategory riskCategory) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskCategory persistentRiskCategory = em.find(RiskCategory.class, riskCategory.getId());
            List<FailureModeHasCause> failureModeHasCauseListOld = persistentRiskCategory.getFailureModeHasCauseList();
            List<FailureModeHasCause> failureModeHasCauseListNew = riskCategory.getFailureModeHasCauseList();
            List<FailureModeHasCauseHasRiskCategory> failureModeHasCauseHasRiskCategoryListOld = persistentRiskCategory.getFailureModeHasCauseHasRiskCategoryList();
            List<FailureModeHasCauseHasRiskCategory> failureModeHasCauseHasRiskCategoryListNew = riskCategory.getFailureModeHasCauseHasRiskCategoryList();
            List<Fmea> fmeaListOld = persistentRiskCategory.getFmeaList();
            List<Fmea> fmeaListNew = riskCategory.getFmeaList();
            List<String> illegalOrphanMessages = null;
            for (FailureModeHasCauseHasRiskCategory failureModeHasCauseHasRiskCategoryListOldFailureModeHasCauseHasRiskCategory : failureModeHasCauseHasRiskCategoryListOld) {
                if (!failureModeHasCauseHasRiskCategoryListNew.contains(failureModeHasCauseHasRiskCategoryListOldFailureModeHasCauseHasRiskCategory)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain FailureModeHasCauseHasRiskCategory " + failureModeHasCauseHasRiskCategoryListOldFailureModeHasCauseHasRiskCategory + " since its riskCategory field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<FailureModeHasCause> attachedFailureModeHasCauseListNew = new ArrayList<>();
            for (FailureModeHasCause failureModeHasCauseListNewFailureModeHasCauseToAttach : failureModeHasCauseListNew) {
                failureModeHasCauseListNewFailureModeHasCauseToAttach = em.getReference(failureModeHasCauseListNewFailureModeHasCauseToAttach.getClass(), failureModeHasCauseListNewFailureModeHasCauseToAttach.getFailureModeHasCausePK());
                attachedFailureModeHasCauseListNew.add(failureModeHasCauseListNewFailureModeHasCauseToAttach);
            }
            failureModeHasCauseListNew = attachedFailureModeHasCauseListNew;
            riskCategory.setFailureModeHasCauseList(failureModeHasCauseListNew);
            List<FailureModeHasCauseHasRiskCategory> attachedFailureModeHasCauseHasRiskCategoryListNew = new ArrayList<>();
            for (FailureModeHasCauseHasRiskCategory failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategoryToAttach : failureModeHasCauseHasRiskCategoryListNew) {
                failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategoryToAttach = em.getReference(failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategoryToAttach.getClass(), failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategoryToAttach.getFailureModeHasCauseHasRiskCategoryPK());
                attachedFailureModeHasCauseHasRiskCategoryListNew.add(failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategoryToAttach);
            }
            failureModeHasCauseHasRiskCategoryListNew = attachedFailureModeHasCauseHasRiskCategoryListNew;
            riskCategory.setFailureModeHasCauseHasRiskCategoryList(failureModeHasCauseHasRiskCategoryListNew);
            List<Fmea> attachedFmeaListNew = new ArrayList<>();
            for (Fmea fmeaListNewFmeaToAttach : fmeaListNew) {
                fmeaListNewFmeaToAttach = em.getReference(fmeaListNewFmeaToAttach.getClass(), fmeaListNewFmeaToAttach.getFmeaPK());
                attachedFmeaListNew.add(fmeaListNewFmeaToAttach);
            }
            fmeaListNew = attachedFmeaListNew;
            riskCategory.setFmeaList(fmeaListNew);
            riskCategory = em.merge(riskCategory);
            for (FailureModeHasCause failureModeHasCauseListOldFailureModeHasCause : failureModeHasCauseListOld) {
                if (!failureModeHasCauseListNew.contains(failureModeHasCauseListOldFailureModeHasCause)) {
                    failureModeHasCauseListOldFailureModeHasCause.getRiskCategoryList().remove(riskCategory);
                    failureModeHasCauseListOldFailureModeHasCause = em.merge(failureModeHasCauseListOldFailureModeHasCause);
                }
            }
            for (FailureModeHasCause failureModeHasCauseListNewFailureModeHasCause : failureModeHasCauseListNew) {
                if (!failureModeHasCauseListOld.contains(failureModeHasCauseListNewFailureModeHasCause)) {
                    failureModeHasCauseListNewFailureModeHasCause.getRiskCategoryList().add(riskCategory);
                    failureModeHasCauseListNewFailureModeHasCause = em.merge(failureModeHasCauseListNewFailureModeHasCause);
                }
            }
            for (FailureModeHasCauseHasRiskCategory failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory : failureModeHasCauseHasRiskCategoryListNew) {
                if (!failureModeHasCauseHasRiskCategoryListOld.contains(failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory)) {
                    RiskCategory oldRiskCategoryOfFailureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory = failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory.getRiskCategory();
                    failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory.setRiskCategory(riskCategory);
                    failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory = em.merge(failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory);
                    if (oldRiskCategoryOfFailureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory != null && !oldRiskCategoryOfFailureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory.equals(riskCategory)) {
                        oldRiskCategoryOfFailureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryList().remove(failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory);
                        oldRiskCategoryOfFailureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory = em.merge(oldRiskCategoryOfFailureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory);
                    }
                }
            }
            for (Fmea fmeaListOldFmea : fmeaListOld) {
                if (!fmeaListNew.contains(fmeaListOldFmea)) {
                    fmeaListOldFmea.getRiskCategoryList().remove(riskCategory);
                    fmeaListOldFmea = em.merge(fmeaListOldFmea);
                }
            }
            for (Fmea fmeaListNewFmea : fmeaListNew) {
                if (!fmeaListOld.contains(fmeaListNewFmea)) {
                    fmeaListNewFmea.getRiskCategoryList().add(riskCategory);
                    fmeaListNewFmea = em.merge(fmeaListNewFmea);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = riskCategory.getId();
                if (findRiskCategory(id) == null) {
                    throw new NonexistentEntityException("The riskCategory with id " + id + " no longer exists.");
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
            RiskCategory riskCategory;
            try {
                riskCategory = em.getReference(RiskCategory.class, id);
                riskCategory.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The riskCategory with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<FailureModeHasCauseHasRiskCategory> failureModeHasCauseHasRiskCategoryListOrphanCheck = riskCategory.getFailureModeHasCauseHasRiskCategoryList();
            for (FailureModeHasCauseHasRiskCategory failureModeHasCauseHasRiskCategoryListOrphanCheckFailureModeHasCauseHasRiskCategory : failureModeHasCauseHasRiskCategoryListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This RiskCategory (" + riskCategory + ") cannot be destroyed since the FailureModeHasCauseHasRiskCategory " + failureModeHasCauseHasRiskCategoryListOrphanCheckFailureModeHasCauseHasRiskCategory + " in its failureModeHasCauseHasRiskCategoryList field has a non-nullable riskCategory field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<FailureModeHasCause> failureModeHasCauseList = riskCategory.getFailureModeHasCauseList();
            for (FailureModeHasCause failureModeHasCauseListFailureModeHasCause : failureModeHasCauseList) {
                failureModeHasCauseListFailureModeHasCause.getRiskCategoryList().remove(riskCategory);
                failureModeHasCauseListFailureModeHasCause = em.merge(failureModeHasCauseListFailureModeHasCause);
            }
            List<Fmea> fmeaList = riskCategory.getFmeaList();
            for (Fmea fmeaListFmea : fmeaList) {
                fmeaListFmea.getRiskCategoryList().remove(riskCategory);
                fmeaListFmea = em.merge(fmeaListFmea);
            }
            em.remove(riskCategory);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RiskCategory> findRiskCategoryEntities() {
        return findRiskCategoryEntities(true, -1, -1);
    }

    public List<RiskCategory> findRiskCategoryEntities(int maxResults, int firstResult) {
        return findRiskCategoryEntities(false, maxResults, firstResult);
    }

    private List<RiskCategory> findRiskCategoryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RiskCategory.class));
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

    public RiskCategory findRiskCategory(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RiskCategory.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getRiskCategoryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RiskCategory> rt = cq.from(RiskCategory.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
