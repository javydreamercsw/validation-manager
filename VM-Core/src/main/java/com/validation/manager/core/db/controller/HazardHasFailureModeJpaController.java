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
import com.validation.manager.core.db.FailureMode;
import com.validation.manager.core.db.RiskItemHasHazard;
import com.validation.manager.core.db.FailureModeHasCause;
import com.validation.manager.core.db.HazardHasFailureMode;
import com.validation.manager.core.db.HazardHasFailureModePK;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class HazardHasFailureModeJpaController implements Serializable {

    public HazardHasFailureModeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(HazardHasFailureMode hazardHasFailureMode) throws PreexistingEntityException, Exception {
        if (hazardHasFailureMode.getHazardHasFailureModePK() == null) {
            hazardHasFailureMode.setHazardHasFailureModePK(new HazardHasFailureModePK());
        }
        if (hazardHasFailureMode.getFailureModeHasCauseList() == null) {
            hazardHasFailureMode.setFailureModeHasCauseList(new ArrayList<>());
        }
        hazardHasFailureMode.getHazardHasFailureModePK().setFMEAid(hazardHasFailureMode.getRiskItemHasHazard().getRiskItemHasHazardPK().getRiskitemFMEAid());
        hazardHasFailureMode.getHazardHasFailureModePK().setHazardId(hazardHasFailureMode.getRiskItemHasHazard().getRiskItemHasHazardPK().getHazardId());
        hazardHasFailureMode.getHazardHasFailureModePK().setFailureModeId(hazardHasFailureMode.getFailureMode().getId());
        hazardHasFailureMode.getHazardHasFailureModePK().setRiskItemId(hazardHasFailureMode.getRiskItemHasHazard().getRiskItemHasHazardPK().getRiskItemId());
        hazardHasFailureMode.getHazardHasFailureModePK().setFMEAprojectid(hazardHasFailureMode.getRiskItemHasHazard().getRiskItemHasHazardPK().getRiskitemFMEAprojectid());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FailureMode failureMode = hazardHasFailureMode.getFailureMode();
            if (failureMode != null) {
                failureMode = em.getReference(failureMode.getClass(), failureMode.getId());
                hazardHasFailureMode.setFailureMode(failureMode);
            }
            RiskItemHasHazard riskItemHasHazard = hazardHasFailureMode.getRiskItemHasHazard();
            if (riskItemHasHazard != null) {
                riskItemHasHazard = em.getReference(riskItemHasHazard.getClass(), riskItemHasHazard.getRiskItemHasHazardPK());
                hazardHasFailureMode.setRiskItemHasHazard(riskItemHasHazard);
            }
            List<FailureModeHasCause> attachedFailureModeHasCauseList = new ArrayList<>();
            for (FailureModeHasCause failureModeHasCauseListFailureModeHasCauseToAttach : hazardHasFailureMode.getFailureModeHasCauseList()) {
                failureModeHasCauseListFailureModeHasCauseToAttach = em.getReference(failureModeHasCauseListFailureModeHasCauseToAttach.getClass(), failureModeHasCauseListFailureModeHasCauseToAttach.getFailureModeHasCausePK());
                attachedFailureModeHasCauseList.add(failureModeHasCauseListFailureModeHasCauseToAttach);
            }
            hazardHasFailureMode.setFailureModeHasCauseList(attachedFailureModeHasCauseList);
            em.persist(hazardHasFailureMode);
            if (failureMode != null) {
                failureMode.getHazardHasFailureModeList().add(hazardHasFailureMode);
                failureMode = em.merge(failureMode);
            }
            if (riskItemHasHazard != null) {
                riskItemHasHazard.getHazardHasFailureModeList().add(hazardHasFailureMode);
                riskItemHasHazard = em.merge(riskItemHasHazard);
            }
            for (FailureModeHasCause failureModeHasCauseListFailureModeHasCause : hazardHasFailureMode.getFailureModeHasCauseList()) {
                HazardHasFailureMode oldHazardHasFailureModeOfFailureModeHasCauseListFailureModeHasCause = failureModeHasCauseListFailureModeHasCause.getHazardHasFailureMode();
                failureModeHasCauseListFailureModeHasCause.setHazardHasFailureMode(hazardHasFailureMode);
                failureModeHasCauseListFailureModeHasCause = em.merge(failureModeHasCauseListFailureModeHasCause);
                if (oldHazardHasFailureModeOfFailureModeHasCauseListFailureModeHasCause != null) {
                    oldHazardHasFailureModeOfFailureModeHasCauseListFailureModeHasCause.getFailureModeHasCauseList().remove(failureModeHasCauseListFailureModeHasCause);
                    oldHazardHasFailureModeOfFailureModeHasCauseListFailureModeHasCause = em.merge(oldHazardHasFailureModeOfFailureModeHasCauseListFailureModeHasCause);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findHazardHasFailureMode(hazardHasFailureMode.getHazardHasFailureModePK()) != null) {
                throw new PreexistingEntityException("HazardHasFailureMode " + hazardHasFailureMode + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(HazardHasFailureMode hazardHasFailureMode) throws IllegalOrphanException, NonexistentEntityException, Exception {
        hazardHasFailureMode.getHazardHasFailureModePK().setFMEAid(hazardHasFailureMode.getRiskItemHasHazard().getRiskItemHasHazardPK().getRiskitemFMEAid());
        hazardHasFailureMode.getHazardHasFailureModePK().setHazardId(hazardHasFailureMode.getRiskItemHasHazard().getRiskItemHasHazardPK().getHazardId());
        hazardHasFailureMode.getHazardHasFailureModePK().setFailureModeId(hazardHasFailureMode.getFailureMode().getId());
        hazardHasFailureMode.getHazardHasFailureModePK().setRiskItemId(hazardHasFailureMode.getRiskItemHasHazard().getRiskItemHasHazardPK().getRiskItemId());
        hazardHasFailureMode.getHazardHasFailureModePK().setFMEAprojectid(hazardHasFailureMode.getRiskItemHasHazard().getRiskItemHasHazardPK().getRiskitemFMEAprojectid());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            HazardHasFailureMode persistentHazardHasFailureMode = em.find(HazardHasFailureMode.class, hazardHasFailureMode.getHazardHasFailureModePK());
            FailureMode failureModeOld = persistentHazardHasFailureMode.getFailureMode();
            FailureMode failureModeNew = hazardHasFailureMode.getFailureMode();
            RiskItemHasHazard riskItemHasHazardOld = persistentHazardHasFailureMode.getRiskItemHasHazard();
            RiskItemHasHazard riskItemHasHazardNew = hazardHasFailureMode.getRiskItemHasHazard();
            List<FailureModeHasCause> failureModeHasCauseListOld = persistentHazardHasFailureMode.getFailureModeHasCauseList();
            List<FailureModeHasCause> failureModeHasCauseListNew = hazardHasFailureMode.getFailureModeHasCauseList();
            List<String> illegalOrphanMessages = null;
            for (FailureModeHasCause failureModeHasCauseListOldFailureModeHasCause : failureModeHasCauseListOld) {
                if (!failureModeHasCauseListNew.contains(failureModeHasCauseListOldFailureModeHasCause)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain FailureModeHasCause " + failureModeHasCauseListOldFailureModeHasCause + " since its hazardHasFailureMode field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (failureModeNew != null) {
                failureModeNew = em.getReference(failureModeNew.getClass(), failureModeNew.getId());
                hazardHasFailureMode.setFailureMode(failureModeNew);
            }
            if (riskItemHasHazardNew != null) {
                riskItemHasHazardNew = em.getReference(riskItemHasHazardNew.getClass(), riskItemHasHazardNew.getRiskItemHasHazardPK());
                hazardHasFailureMode.setRiskItemHasHazard(riskItemHasHazardNew);
            }
            List<FailureModeHasCause> attachedFailureModeHasCauseListNew = new ArrayList<>();
            for (FailureModeHasCause failureModeHasCauseListNewFailureModeHasCauseToAttach : failureModeHasCauseListNew) {
                failureModeHasCauseListNewFailureModeHasCauseToAttach = em.getReference(failureModeHasCauseListNewFailureModeHasCauseToAttach.getClass(), failureModeHasCauseListNewFailureModeHasCauseToAttach.getFailureModeHasCausePK());
                attachedFailureModeHasCauseListNew.add(failureModeHasCauseListNewFailureModeHasCauseToAttach);
            }
            failureModeHasCauseListNew = attachedFailureModeHasCauseListNew;
            hazardHasFailureMode.setFailureModeHasCauseList(failureModeHasCauseListNew);
            hazardHasFailureMode = em.merge(hazardHasFailureMode);
            if (failureModeOld != null && !failureModeOld.equals(failureModeNew)) {
                failureModeOld.getHazardHasFailureModeList().remove(hazardHasFailureMode);
                failureModeOld = em.merge(failureModeOld);
            }
            if (failureModeNew != null && !failureModeNew.equals(failureModeOld)) {
                failureModeNew.getHazardHasFailureModeList().add(hazardHasFailureMode);
                failureModeNew = em.merge(failureModeNew);
            }
            if (riskItemHasHazardOld != null && !riskItemHasHazardOld.equals(riskItemHasHazardNew)) {
                riskItemHasHazardOld.getHazardHasFailureModeList().remove(hazardHasFailureMode);
                riskItemHasHazardOld = em.merge(riskItemHasHazardOld);
            }
            if (riskItemHasHazardNew != null && !riskItemHasHazardNew.equals(riskItemHasHazardOld)) {
                riskItemHasHazardNew.getHazardHasFailureModeList().add(hazardHasFailureMode);
                riskItemHasHazardNew = em.merge(riskItemHasHazardNew);
            }
            for (FailureModeHasCause failureModeHasCauseListNewFailureModeHasCause : failureModeHasCauseListNew) {
                if (!failureModeHasCauseListOld.contains(failureModeHasCauseListNewFailureModeHasCause)) {
                    HazardHasFailureMode oldHazardHasFailureModeOfFailureModeHasCauseListNewFailureModeHasCause = failureModeHasCauseListNewFailureModeHasCause.getHazardHasFailureMode();
                    failureModeHasCauseListNewFailureModeHasCause.setHazardHasFailureMode(hazardHasFailureMode);
                    failureModeHasCauseListNewFailureModeHasCause = em.merge(failureModeHasCauseListNewFailureModeHasCause);
                    if (oldHazardHasFailureModeOfFailureModeHasCauseListNewFailureModeHasCause != null && !oldHazardHasFailureModeOfFailureModeHasCauseListNewFailureModeHasCause.equals(hazardHasFailureMode)) {
                        oldHazardHasFailureModeOfFailureModeHasCauseListNewFailureModeHasCause.getFailureModeHasCauseList().remove(failureModeHasCauseListNewFailureModeHasCause);
                        oldHazardHasFailureModeOfFailureModeHasCauseListNewFailureModeHasCause = em.merge(oldHazardHasFailureModeOfFailureModeHasCauseListNewFailureModeHasCause);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                HazardHasFailureModePK id = hazardHasFailureMode.getHazardHasFailureModePK();
                if (findHazardHasFailureMode(id) == null) {
                    throw new NonexistentEntityException("The hazardHasFailureMode with id " + id + " no longer exists.");
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

    public void destroy(HazardHasFailureModePK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            HazardHasFailureMode hazardHasFailureMode;
            try {
                hazardHasFailureMode = em.getReference(HazardHasFailureMode.class, id);
                hazardHasFailureMode.getHazardHasFailureModePK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The hazardHasFailureMode with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<FailureModeHasCause> failureModeHasCauseListOrphanCheck = hazardHasFailureMode.getFailureModeHasCauseList();
            for (FailureModeHasCause failureModeHasCauseListOrphanCheckFailureModeHasCause : failureModeHasCauseListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This HazardHasFailureMode (" + hazardHasFailureMode + ") cannot be destroyed since the FailureModeHasCause " + failureModeHasCauseListOrphanCheckFailureModeHasCause + " in its failureModeHasCauseList field has a non-nullable hazardHasFailureMode field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            FailureMode failureMode = hazardHasFailureMode.getFailureMode();
            if (failureMode != null) {
                failureMode.getHazardHasFailureModeList().remove(hazardHasFailureMode);
                failureMode = em.merge(failureMode);
            }
            RiskItemHasHazard riskItemHasHazard = hazardHasFailureMode.getRiskItemHasHazard();
            if (riskItemHasHazard != null) {
                riskItemHasHazard.getHazardHasFailureModeList().remove(hazardHasFailureMode);
                riskItemHasHazard = em.merge(riskItemHasHazard);
            }
            em.remove(hazardHasFailureMode);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<HazardHasFailureMode> findHazardHasFailureModeEntities() {
        return findHazardHasFailureModeEntities(true, -1, -1);
    }

    public List<HazardHasFailureMode> findHazardHasFailureModeEntities(int maxResults, int firstResult) {
        return findHazardHasFailureModeEntities(false, maxResults, firstResult);
    }

    private List<HazardHasFailureMode> findHazardHasFailureModeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(HazardHasFailureMode.class));
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

    public HazardHasFailureMode findHazardHasFailureMode(HazardHasFailureModePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(HazardHasFailureMode.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getHazardHasFailureModeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<HazardHasFailureMode> rt = cq.from(HazardHasFailureMode.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
