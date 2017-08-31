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
import com.validation.manager.core.db.Cause;
import com.validation.manager.core.db.HazardHasFailureMode;
import com.validation.manager.core.db.HazardHasFailureModePK;
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
        if (hazardHasFailureMode.getCauseList() == null) {
            hazardHasFailureMode.setCauseList(new ArrayList<>());
        }
        hazardHasFailureMode.getHazardHasFailureModePK().setRiskItemId(hazardHasFailureMode.getRiskItemHasHazard().getRiskItemHasHazardPK().getRiskItemId());
        hazardHasFailureMode.getHazardHasFailureModePK().setFMEAid(hazardHasFailureMode.getRiskItemHasHazard().getRiskItemHasHazardPK().getRiskitemFMEAid());
        hazardHasFailureMode.getHazardHasFailureModePK().setFMEAprojectid(hazardHasFailureMode.getRiskItemHasHazard().getRiskItemHasHazardPK().getRiskitemFMEAprojectid());
        hazardHasFailureMode.getHazardHasFailureModePK().setFailureModeId(hazardHasFailureMode.getFailureMode().getId());
        hazardHasFailureMode.getHazardHasFailureModePK().setHazardId(hazardHasFailureMode.getRiskItemHasHazard().getRiskItemHasHazardPK().getHazardId());
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
            List<Cause> attachedCauseList = new ArrayList<>();
            for (Cause causeListCauseToAttach : hazardHasFailureMode.getCauseList()) {
                causeListCauseToAttach = em.getReference(causeListCauseToAttach.getClass(), causeListCauseToAttach.getId());
                attachedCauseList.add(causeListCauseToAttach);
            }
            hazardHasFailureMode.setCauseList(attachedCauseList);
            em.persist(hazardHasFailureMode);
            if (failureMode != null) {
                failureMode.getHazardHasFailureModeList().add(hazardHasFailureMode);
                failureMode = em.merge(failureMode);
            }
            if (riskItemHasHazard != null) {
                riskItemHasHazard.getHazardHasFailureModeList().add(hazardHasFailureMode);
                riskItemHasHazard = em.merge(riskItemHasHazard);
            }
            for (Cause causeListCause : hazardHasFailureMode.getCauseList()) {
                causeListCause.getHazardHasFailureModeList().add(hazardHasFailureMode);
                causeListCause = em.merge(causeListCause);
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

    public void edit(HazardHasFailureMode hazardHasFailureMode) throws NonexistentEntityException, Exception {
        hazardHasFailureMode.getHazardHasFailureModePK().setRiskItemId(hazardHasFailureMode.getRiskItemHasHazard().getRiskItemHasHazardPK().getRiskItemId());
        hazardHasFailureMode.getHazardHasFailureModePK().setFMEAid(hazardHasFailureMode.getRiskItemHasHazard().getRiskItemHasHazardPK().getRiskitemFMEAid());
        hazardHasFailureMode.getHazardHasFailureModePK().setFMEAprojectid(hazardHasFailureMode.getRiskItemHasHazard().getRiskItemHasHazardPK().getRiskitemFMEAprojectid());
        hazardHasFailureMode.getHazardHasFailureModePK().setFailureModeId(hazardHasFailureMode.getFailureMode().getId());
        hazardHasFailureMode.getHazardHasFailureModePK().setHazardId(hazardHasFailureMode.getRiskItemHasHazard().getRiskItemHasHazardPK().getHazardId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            HazardHasFailureMode persistentHazardHasFailureMode = em.find(HazardHasFailureMode.class, hazardHasFailureMode.getHazardHasFailureModePK());
            FailureMode failureModeOld = persistentHazardHasFailureMode.getFailureMode();
            FailureMode failureModeNew = hazardHasFailureMode.getFailureMode();
            RiskItemHasHazard riskItemHasHazardOld = persistentHazardHasFailureMode.getRiskItemHasHazard();
            RiskItemHasHazard riskItemHasHazardNew = hazardHasFailureMode.getRiskItemHasHazard();
            List<Cause> causeListOld = persistentHazardHasFailureMode.getCauseList();
            List<Cause> causeListNew = hazardHasFailureMode.getCauseList();
            if (failureModeNew != null) {
                failureModeNew = em.getReference(failureModeNew.getClass(), failureModeNew.getId());
                hazardHasFailureMode.setFailureMode(failureModeNew);
            }
            if (riskItemHasHazardNew != null) {
                riskItemHasHazardNew = em.getReference(riskItemHasHazardNew.getClass(), riskItemHasHazardNew.getRiskItemHasHazardPK());
                hazardHasFailureMode.setRiskItemHasHazard(riskItemHasHazardNew);
            }
            List<Cause> attachedCauseListNew = new ArrayList<>();
            for (Cause causeListNewCauseToAttach : causeListNew) {
                causeListNewCauseToAttach = em.getReference(causeListNewCauseToAttach.getClass(), causeListNewCauseToAttach.getId());
                attachedCauseListNew.add(causeListNewCauseToAttach);
            }
            causeListNew = attachedCauseListNew;
            hazardHasFailureMode.setCauseList(causeListNew);
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
            for (Cause causeListOldCause : causeListOld) {
                if (!causeListNew.contains(causeListOldCause)) {
                    causeListOldCause.getHazardHasFailureModeList().remove(hazardHasFailureMode);
                    causeListOldCause = em.merge(causeListOldCause);
                }
            }
            for (Cause causeListNewCause : causeListNew) {
                if (!causeListOld.contains(causeListNewCause)) {
                    causeListNewCause.getHazardHasFailureModeList().add(hazardHasFailureMode);
                    causeListNewCause = em.merge(causeListNewCause);
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

    public void destroy(HazardHasFailureModePK id) throws NonexistentEntityException {
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
            List<Cause> causeList = hazardHasFailureMode.getCauseList();
            for (Cause causeListCause : causeList) {
                causeListCause.getHazardHasFailureModeList().remove(hazardHasFailureMode);
                causeListCause = em.merge(causeListCause);
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
