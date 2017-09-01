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
import com.validation.manager.core.db.Cause;
import com.validation.manager.core.db.FailureModeHasCause;
import com.validation.manager.core.db.HazardHasFailureMode;
import com.validation.manager.core.db.RiskCategory;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.RiskControl;
import com.validation.manager.core.db.FailureModeHasCauseHasRiskCategory;
import com.validation.manager.core.db.FailureModeHasCausePK;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class FailureModeHasCauseJpaController implements Serializable {

    public FailureModeHasCauseJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(FailureModeHasCause failureModeHasCause) throws PreexistingEntityException, Exception {
        if (failureModeHasCause.getFailureModeHasCausePK() == null) {
            failureModeHasCause.setFailureModeHasCausePK(new FailureModeHasCausePK());
        }
        if (failureModeHasCause.getRiskCategoryList() == null) {
            failureModeHasCause.setRiskCategoryList(new ArrayList<>());
        }
        if (failureModeHasCause.getRiskControlList() == null) {
            failureModeHasCause.setRiskControlList(new ArrayList<>());
        }
        if (failureModeHasCause.getFailureModeHasCauseHasRiskCategoryList() == null) {
            failureModeHasCause.setFailureModeHasCauseHasRiskCategoryList(new ArrayList<>());
        }
        failureModeHasCause.getFailureModeHasCausePK().setRiskItemId(failureModeHasCause.getHazardHasFailureMode().getHazardHasFailureModePK().getRiskItemId());
        failureModeHasCause.getFailureModeHasCausePK().setFailureModeId(failureModeHasCause.getHazardHasFailureMode().getHazardHasFailureModePK().getFailureModeId());
        failureModeHasCause.getFailureModeHasCausePK().setHazardId(failureModeHasCause.getHazardHasFailureMode().getHazardHasFailureModePK().getHazardId());
        failureModeHasCause.getFailureModeHasCausePK().setProjectId(failureModeHasCause.getHazardHasFailureMode().getHazardHasFailureModePK().getFMEAprojectid());
        failureModeHasCause.getFailureModeHasCausePK().setFMEAid(failureModeHasCause.getHazardHasFailureMode().getHazardHasFailureModePK().getFMEAid());
        failureModeHasCause.getFailureModeHasCausePK().setCauseId(failureModeHasCause.getCause().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cause cause = failureModeHasCause.getCause();
            if (cause != null) {
                cause = em.getReference(cause.getClass(), cause.getId());
                failureModeHasCause.setCause(cause);
            }
            HazardHasFailureMode hazardHasFailureMode = failureModeHasCause.getHazardHasFailureMode();
            if (hazardHasFailureMode != null) {
                hazardHasFailureMode = em.getReference(hazardHasFailureMode.getClass(), hazardHasFailureMode.getHazardHasFailureModePK());
                failureModeHasCause.setHazardHasFailureMode(hazardHasFailureMode);
            }
            List<RiskCategory> attachedRiskCategoryList = new ArrayList<>();
            for (RiskCategory riskCategoryListRiskCategoryToAttach : failureModeHasCause.getRiskCategoryList()) {
                riskCategoryListRiskCategoryToAttach = em.getReference(riskCategoryListRiskCategoryToAttach.getClass(), riskCategoryListRiskCategoryToAttach.getId());
                attachedRiskCategoryList.add(riskCategoryListRiskCategoryToAttach);
            }
            failureModeHasCause.setRiskCategoryList(attachedRiskCategoryList);
            List<RiskControl> attachedRiskControlList = new ArrayList<>();
            for (RiskControl riskControlListRiskControlToAttach : failureModeHasCause.getRiskControlList()) {
                riskControlListRiskControlToAttach = em.getReference(riskControlListRiskControlToAttach.getClass(), riskControlListRiskControlToAttach.getRiskControlPK());
                attachedRiskControlList.add(riskControlListRiskControlToAttach);
            }
            failureModeHasCause.setRiskControlList(attachedRiskControlList);
            List<FailureModeHasCauseHasRiskCategory> attachedFailureModeHasCauseHasRiskCategoryList = new ArrayList<>();
            for (FailureModeHasCauseHasRiskCategory failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategoryToAttach : failureModeHasCause.getFailureModeHasCauseHasRiskCategoryList()) {
                failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategoryToAttach = em.getReference(failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategoryToAttach.getClass(), failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategoryToAttach.getFailureModeHasCauseHasRiskCategoryPK());
                attachedFailureModeHasCauseHasRiskCategoryList.add(failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategoryToAttach);
            }
            failureModeHasCause.setFailureModeHasCauseHasRiskCategoryList(attachedFailureModeHasCauseHasRiskCategoryList);
            em.persist(failureModeHasCause);
            if (cause != null) {
                cause.getFailureModeHasCauseList().add(failureModeHasCause);
                cause = em.merge(cause);
            }
            if (hazardHasFailureMode != null) {
                hazardHasFailureMode.getFailureModeHasCauseList().add(failureModeHasCause);
                hazardHasFailureMode = em.merge(hazardHasFailureMode);
            }
            for (RiskCategory riskCategoryListRiskCategory : failureModeHasCause.getRiskCategoryList()) {
                riskCategoryListRiskCategory.getFailureModeHasCauseList().add(failureModeHasCause);
                riskCategoryListRiskCategory = em.merge(riskCategoryListRiskCategory);
            }
            for (RiskControl riskControlListRiskControl : failureModeHasCause.getRiskControlList()) {
                riskControlListRiskControl.getFailureModeHasCauseList().add(failureModeHasCause);
                riskControlListRiskControl = em.merge(riskControlListRiskControl);
            }
            for (FailureModeHasCauseHasRiskCategory failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory : failureModeHasCause.getFailureModeHasCauseHasRiskCategoryList()) {
                FailureModeHasCause oldFailureModeHasCauseOfFailureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory = failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory.getFailureModeHasCause();
                failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory.setFailureModeHasCause(failureModeHasCause);
                failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory = em.merge(failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory);
                if (oldFailureModeHasCauseOfFailureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory != null) {
                    oldFailureModeHasCauseOfFailureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryList().remove(failureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory);
                    oldFailureModeHasCauseOfFailureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory = em.merge(oldFailureModeHasCauseOfFailureModeHasCauseHasRiskCategoryListFailureModeHasCauseHasRiskCategory);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findFailureModeHasCause(failureModeHasCause.getFailureModeHasCausePK()) != null) {
                throw new PreexistingEntityException("FailureModeHasCause " + failureModeHasCause + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(FailureModeHasCause failureModeHasCause) throws IllegalOrphanException, NonexistentEntityException, Exception {
        failureModeHasCause.getFailureModeHasCausePK().setRiskItemId(failureModeHasCause.getHazardHasFailureMode().getHazardHasFailureModePK().getRiskItemId());
        failureModeHasCause.getFailureModeHasCausePK().setFailureModeId(failureModeHasCause.getHazardHasFailureMode().getHazardHasFailureModePK().getFailureModeId());
        failureModeHasCause.getFailureModeHasCausePK().setHazardId(failureModeHasCause.getHazardHasFailureMode().getHazardHasFailureModePK().getHazardId());
        failureModeHasCause.getFailureModeHasCausePK().setProjectId(failureModeHasCause.getHazardHasFailureMode().getHazardHasFailureModePK().getFMEAprojectid());
        failureModeHasCause.getFailureModeHasCausePK().setFMEAid(failureModeHasCause.getHazardHasFailureMode().getHazardHasFailureModePK().getFMEAid());
        failureModeHasCause.getFailureModeHasCausePK().setCauseId(failureModeHasCause.getCause().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FailureModeHasCause persistentFailureModeHasCause = em.find(FailureModeHasCause.class, failureModeHasCause.getFailureModeHasCausePK());
            Cause causeOld = persistentFailureModeHasCause.getCause();
            Cause causeNew = failureModeHasCause.getCause();
            HazardHasFailureMode hazardHasFailureModeOld = persistentFailureModeHasCause.getHazardHasFailureMode();
            HazardHasFailureMode hazardHasFailureModeNew = failureModeHasCause.getHazardHasFailureMode();
            List<RiskCategory> riskCategoryListOld = persistentFailureModeHasCause.getRiskCategoryList();
            List<RiskCategory> riskCategoryListNew = failureModeHasCause.getRiskCategoryList();
            List<RiskControl> riskControlListOld = persistentFailureModeHasCause.getRiskControlList();
            List<RiskControl> riskControlListNew = failureModeHasCause.getRiskControlList();
            List<FailureModeHasCauseHasRiskCategory> failureModeHasCauseHasRiskCategoryListOld = persistentFailureModeHasCause.getFailureModeHasCauseHasRiskCategoryList();
            List<FailureModeHasCauseHasRiskCategory> failureModeHasCauseHasRiskCategoryListNew = failureModeHasCause.getFailureModeHasCauseHasRiskCategoryList();
            List<String> illegalOrphanMessages = null;
            for (FailureModeHasCauseHasRiskCategory failureModeHasCauseHasRiskCategoryListOldFailureModeHasCauseHasRiskCategory : failureModeHasCauseHasRiskCategoryListOld) {
                if (!failureModeHasCauseHasRiskCategoryListNew.contains(failureModeHasCauseHasRiskCategoryListOldFailureModeHasCauseHasRiskCategory)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain FailureModeHasCauseHasRiskCategory " + failureModeHasCauseHasRiskCategoryListOldFailureModeHasCauseHasRiskCategory + " since its failureModeHasCause field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (causeNew != null) {
                causeNew = em.getReference(causeNew.getClass(), causeNew.getId());
                failureModeHasCause.setCause(causeNew);
            }
            if (hazardHasFailureModeNew != null) {
                hazardHasFailureModeNew = em.getReference(hazardHasFailureModeNew.getClass(), hazardHasFailureModeNew.getHazardHasFailureModePK());
                failureModeHasCause.setHazardHasFailureMode(hazardHasFailureModeNew);
            }
            List<RiskCategory> attachedRiskCategoryListNew = new ArrayList<>();
            for (RiskCategory riskCategoryListNewRiskCategoryToAttach : riskCategoryListNew) {
                riskCategoryListNewRiskCategoryToAttach = em.getReference(riskCategoryListNewRiskCategoryToAttach.getClass(), riskCategoryListNewRiskCategoryToAttach.getId());
                attachedRiskCategoryListNew.add(riskCategoryListNewRiskCategoryToAttach);
            }
            riskCategoryListNew = attachedRiskCategoryListNew;
            failureModeHasCause.setRiskCategoryList(riskCategoryListNew);
            List<RiskControl> attachedRiskControlListNew = new ArrayList<>();
            for (RiskControl riskControlListNewRiskControlToAttach : riskControlListNew) {
                riskControlListNewRiskControlToAttach = em.getReference(riskControlListNewRiskControlToAttach.getClass(), riskControlListNewRiskControlToAttach.getRiskControlPK());
                attachedRiskControlListNew.add(riskControlListNewRiskControlToAttach);
            }
            riskControlListNew = attachedRiskControlListNew;
            failureModeHasCause.setRiskControlList(riskControlListNew);
            List<FailureModeHasCauseHasRiskCategory> attachedFailureModeHasCauseHasRiskCategoryListNew = new ArrayList<>();
            for (FailureModeHasCauseHasRiskCategory failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategoryToAttach : failureModeHasCauseHasRiskCategoryListNew) {
                failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategoryToAttach = em.getReference(failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategoryToAttach.getClass(), failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategoryToAttach.getFailureModeHasCauseHasRiskCategoryPK());
                attachedFailureModeHasCauseHasRiskCategoryListNew.add(failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategoryToAttach);
            }
            failureModeHasCauseHasRiskCategoryListNew = attachedFailureModeHasCauseHasRiskCategoryListNew;
            failureModeHasCause.setFailureModeHasCauseHasRiskCategoryList(failureModeHasCauseHasRiskCategoryListNew);
            failureModeHasCause = em.merge(failureModeHasCause);
            if (causeOld != null && !causeOld.equals(causeNew)) {
                causeOld.getFailureModeHasCauseList().remove(failureModeHasCause);
                causeOld = em.merge(causeOld);
            }
            if (causeNew != null && !causeNew.equals(causeOld)) {
                causeNew.getFailureModeHasCauseList().add(failureModeHasCause);
                causeNew = em.merge(causeNew);
            }
            if (hazardHasFailureModeOld != null && !hazardHasFailureModeOld.equals(hazardHasFailureModeNew)) {
                hazardHasFailureModeOld.getFailureModeHasCauseList().remove(failureModeHasCause);
                hazardHasFailureModeOld = em.merge(hazardHasFailureModeOld);
            }
            if (hazardHasFailureModeNew != null && !hazardHasFailureModeNew.equals(hazardHasFailureModeOld)) {
                hazardHasFailureModeNew.getFailureModeHasCauseList().add(failureModeHasCause);
                hazardHasFailureModeNew = em.merge(hazardHasFailureModeNew);
            }
            for (RiskCategory riskCategoryListOldRiskCategory : riskCategoryListOld) {
                if (!riskCategoryListNew.contains(riskCategoryListOldRiskCategory)) {
                    riskCategoryListOldRiskCategory.getFailureModeHasCauseList().remove(failureModeHasCause);
                    riskCategoryListOldRiskCategory = em.merge(riskCategoryListOldRiskCategory);
                }
            }
            for (RiskCategory riskCategoryListNewRiskCategory : riskCategoryListNew) {
                if (!riskCategoryListOld.contains(riskCategoryListNewRiskCategory)) {
                    riskCategoryListNewRiskCategory.getFailureModeHasCauseList().add(failureModeHasCause);
                    riskCategoryListNewRiskCategory = em.merge(riskCategoryListNewRiskCategory);
                }
            }
            for (RiskControl riskControlListOldRiskControl : riskControlListOld) {
                if (!riskControlListNew.contains(riskControlListOldRiskControl)) {
                    riskControlListOldRiskControl.getFailureModeHasCauseList().remove(failureModeHasCause);
                    riskControlListOldRiskControl = em.merge(riskControlListOldRiskControl);
                }
            }
            for (RiskControl riskControlListNewRiskControl : riskControlListNew) {
                if (!riskControlListOld.contains(riskControlListNewRiskControl)) {
                    riskControlListNewRiskControl.getFailureModeHasCauseList().add(failureModeHasCause);
                    riskControlListNewRiskControl = em.merge(riskControlListNewRiskControl);
                }
            }
            for (FailureModeHasCauseHasRiskCategory failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory : failureModeHasCauseHasRiskCategoryListNew) {
                if (!failureModeHasCauseHasRiskCategoryListOld.contains(failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory)) {
                    FailureModeHasCause oldFailureModeHasCauseOfFailureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory = failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory.getFailureModeHasCause();
                    failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory.setFailureModeHasCause(failureModeHasCause);
                    failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory = em.merge(failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory);
                    if (oldFailureModeHasCauseOfFailureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory != null && !oldFailureModeHasCauseOfFailureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory.equals(failureModeHasCause)) {
                        oldFailureModeHasCauseOfFailureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory.getFailureModeHasCauseHasRiskCategoryList().remove(failureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory);
                        oldFailureModeHasCauseOfFailureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory = em.merge(oldFailureModeHasCauseOfFailureModeHasCauseHasRiskCategoryListNewFailureModeHasCauseHasRiskCategory);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                FailureModeHasCausePK id = failureModeHasCause.getFailureModeHasCausePK();
                if (findFailureModeHasCause(id) == null) {
                    throw new NonexistentEntityException("The failureModeHasCause with id " + id + " no longer exists.");
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

    public void destroy(FailureModeHasCausePK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FailureModeHasCause failureModeHasCause;
            try {
                failureModeHasCause = em.getReference(FailureModeHasCause.class, id);
                failureModeHasCause.getFailureModeHasCausePK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The failureModeHasCause with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<FailureModeHasCauseHasRiskCategory> failureModeHasCauseHasRiskCategoryListOrphanCheck = failureModeHasCause.getFailureModeHasCauseHasRiskCategoryList();
            for (FailureModeHasCauseHasRiskCategory failureModeHasCauseHasRiskCategoryListOrphanCheckFailureModeHasCauseHasRiskCategory : failureModeHasCauseHasRiskCategoryListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This FailureModeHasCause (" + failureModeHasCause + ") cannot be destroyed since the FailureModeHasCauseHasRiskCategory " + failureModeHasCauseHasRiskCategoryListOrphanCheckFailureModeHasCauseHasRiskCategory + " in its failureModeHasCauseHasRiskCategoryList field has a non-nullable failureModeHasCause field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Cause cause = failureModeHasCause.getCause();
            if (cause != null) {
                cause.getFailureModeHasCauseList().remove(failureModeHasCause);
                cause = em.merge(cause);
            }
            HazardHasFailureMode hazardHasFailureMode = failureModeHasCause.getHazardHasFailureMode();
            if (hazardHasFailureMode != null) {
                hazardHasFailureMode.getFailureModeHasCauseList().remove(failureModeHasCause);
                hazardHasFailureMode = em.merge(hazardHasFailureMode);
            }
            List<RiskCategory> riskCategoryList = failureModeHasCause.getRiskCategoryList();
            for (RiskCategory riskCategoryListRiskCategory : riskCategoryList) {
                riskCategoryListRiskCategory.getFailureModeHasCauseList().remove(failureModeHasCause);
                riskCategoryListRiskCategory = em.merge(riskCategoryListRiskCategory);
            }
            List<RiskControl> riskControlList = failureModeHasCause.getRiskControlList();
            for (RiskControl riskControlListRiskControl : riskControlList) {
                riskControlListRiskControl.getFailureModeHasCauseList().remove(failureModeHasCause);
                riskControlListRiskControl = em.merge(riskControlListRiskControl);
            }
            em.remove(failureModeHasCause);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<FailureModeHasCause> findFailureModeHasCauseEntities() {
        return findFailureModeHasCauseEntities(true, -1, -1);
    }

    public List<FailureModeHasCause> findFailureModeHasCauseEntities(int maxResults, int firstResult) {
        return findFailureModeHasCauseEntities(false, maxResults, firstResult);
    }

    private List<FailureModeHasCause> findFailureModeHasCauseEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(FailureModeHasCause.class));
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

    public FailureModeHasCause findFailureModeHasCause(FailureModeHasCausePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(FailureModeHasCause.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getFailureModeHasCauseCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<FailureModeHasCause> rt = cq.from(FailureModeHasCause.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
