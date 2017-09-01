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
import com.validation.manager.core.db.Hazard;
import com.validation.manager.core.db.RiskItem;
import com.validation.manager.core.db.HazardHasFailureMode;
import com.validation.manager.core.db.RiskItemHasHazard;
import com.validation.manager.core.db.RiskItemHasHazardPK;
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
public class RiskItemHasHazardJpaController implements Serializable {

    public RiskItemHasHazardJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RiskItemHasHazard riskItemHasHazard) throws PreexistingEntityException, Exception {
        if (riskItemHasHazard.getRiskItemHasHazardPK() == null) {
            riskItemHasHazard.setRiskItemHasHazardPK(new RiskItemHasHazardPK());
        }
        if (riskItemHasHazard.getHazardHasFailureModeList() == null) {
            riskItemHasHazard.setHazardHasFailureModeList(new ArrayList<>());
        }
        riskItemHasHazard.getRiskItemHasHazardPK().setHazardId(riskItemHasHazard.getHazard().getId());
        riskItemHasHazard.getRiskItemHasHazardPK().setRiskitemFMEAid(riskItemHasHazard.getRiskItem().getRiskItemPK().getFMEAid());
        riskItemHasHazard.getRiskItemHasHazardPK().setRiskItemId(riskItemHasHazard.getRiskItem().getRiskItemPK().getId());
        riskItemHasHazard.getRiskItemHasHazardPK().setRiskitemFMEAprojectid(riskItemHasHazard.getRiskItem().getRiskItemPK().getFMEAprojectid());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Hazard hazard = riskItemHasHazard.getHazard();
            if (hazard != null) {
                hazard = em.getReference(hazard.getClass(), hazard.getId());
                riskItemHasHazard.setHazard(hazard);
            }
            RiskItem riskItem = riskItemHasHazard.getRiskItem();
            if (riskItem != null) {
                riskItem = em.getReference(riskItem.getClass(), riskItem.getRiskItemPK());
                riskItemHasHazard.setRiskItem(riskItem);
            }
            List<HazardHasFailureMode> attachedHazardHasFailureModeList = new ArrayList<>();
            for (HazardHasFailureMode hazardHasFailureModeListHazardHasFailureModeToAttach : riskItemHasHazard.getHazardHasFailureModeList()) {
                hazardHasFailureModeListHazardHasFailureModeToAttach = em.getReference(hazardHasFailureModeListHazardHasFailureModeToAttach.getClass(), hazardHasFailureModeListHazardHasFailureModeToAttach.getHazardHasFailureModePK());
                attachedHazardHasFailureModeList.add(hazardHasFailureModeListHazardHasFailureModeToAttach);
            }
            riskItemHasHazard.setHazardHasFailureModeList(attachedHazardHasFailureModeList);
            em.persist(riskItemHasHazard);
            if (hazard != null) {
                hazard.getRiskItemHasHazardList().add(riskItemHasHazard);
                hazard = em.merge(hazard);
            }
            if (riskItem != null) {
                riskItem.getRiskItemHasHazardList().add(riskItemHasHazard);
                riskItem = em.merge(riskItem);
            }
            for (HazardHasFailureMode hazardHasFailureModeListHazardHasFailureMode : riskItemHasHazard.getHazardHasFailureModeList()) {
                RiskItemHasHazard oldRiskItemHasHazardOfHazardHasFailureModeListHazardHasFailureMode = hazardHasFailureModeListHazardHasFailureMode.getRiskItemHasHazard();
                hazardHasFailureModeListHazardHasFailureMode.setRiskItemHasHazard(riskItemHasHazard);
                hazardHasFailureModeListHazardHasFailureMode = em.merge(hazardHasFailureModeListHazardHasFailureMode);
                if (oldRiskItemHasHazardOfHazardHasFailureModeListHazardHasFailureMode != null) {
                    oldRiskItemHasHazardOfHazardHasFailureModeListHazardHasFailureMode.getHazardHasFailureModeList().remove(hazardHasFailureModeListHazardHasFailureMode);
                    oldRiskItemHasHazardOfHazardHasFailureModeListHazardHasFailureMode = em.merge(oldRiskItemHasHazardOfHazardHasFailureModeListHazardHasFailureMode);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findRiskItemHasHazard(riskItemHasHazard.getRiskItemHasHazardPK()) != null) {
                throw new PreexistingEntityException("RiskItemHasHazard " + riskItemHasHazard + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RiskItemHasHazard riskItemHasHazard) throws IllegalOrphanException, NonexistentEntityException, Exception {
        riskItemHasHazard.getRiskItemHasHazardPK().setHazardId(riskItemHasHazard.getHazard().getId());
        riskItemHasHazard.getRiskItemHasHazardPK().setRiskitemFMEAid(riskItemHasHazard.getRiskItem().getRiskItemPK().getFMEAid());
        riskItemHasHazard.getRiskItemHasHazardPK().setRiskItemId(riskItemHasHazard.getRiskItem().getRiskItemPK().getId());
        riskItemHasHazard.getRiskItemHasHazardPK().setRiskitemFMEAprojectid(riskItemHasHazard.getRiskItem().getRiskItemPK().getFMEAprojectid());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskItemHasHazard persistentRiskItemHasHazard = em.find(RiskItemHasHazard.class, riskItemHasHazard.getRiskItemHasHazardPK());
            Hazard hazardOld = persistentRiskItemHasHazard.getHazard();
            Hazard hazardNew = riskItemHasHazard.getHazard();
            RiskItem riskItemOld = persistentRiskItemHasHazard.getRiskItem();
            RiskItem riskItemNew = riskItemHasHazard.getRiskItem();
            List<HazardHasFailureMode> hazardHasFailureModeListOld = persistentRiskItemHasHazard.getHazardHasFailureModeList();
            List<HazardHasFailureMode> hazardHasFailureModeListNew = riskItemHasHazard.getHazardHasFailureModeList();
            List<String> illegalOrphanMessages = null;
            for (HazardHasFailureMode hazardHasFailureModeListOldHazardHasFailureMode : hazardHasFailureModeListOld) {
                if (!hazardHasFailureModeListNew.contains(hazardHasFailureModeListOldHazardHasFailureMode)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain HazardHasFailureMode " + hazardHasFailureModeListOldHazardHasFailureMode + " since its riskItemHasHazard field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (hazardNew != null) {
                hazardNew = em.getReference(hazardNew.getClass(), hazardNew.getId());
                riskItemHasHazard.setHazard(hazardNew);
            }
            if (riskItemNew != null) {
                riskItemNew = em.getReference(riskItemNew.getClass(), riskItemNew.getRiskItemPK());
                riskItemHasHazard.setRiskItem(riskItemNew);
            }
            List<HazardHasFailureMode> attachedHazardHasFailureModeListNew = new ArrayList<>();
            for (HazardHasFailureMode hazardHasFailureModeListNewHazardHasFailureModeToAttach : hazardHasFailureModeListNew) {
                hazardHasFailureModeListNewHazardHasFailureModeToAttach = em.getReference(hazardHasFailureModeListNewHazardHasFailureModeToAttach.getClass(), hazardHasFailureModeListNewHazardHasFailureModeToAttach.getHazardHasFailureModePK());
                attachedHazardHasFailureModeListNew.add(hazardHasFailureModeListNewHazardHasFailureModeToAttach);
            }
            hazardHasFailureModeListNew = attachedHazardHasFailureModeListNew;
            riskItemHasHazard.setHazardHasFailureModeList(hazardHasFailureModeListNew);
            riskItemHasHazard = em.merge(riskItemHasHazard);
            if (hazardOld != null && !hazardOld.equals(hazardNew)) {
                hazardOld.getRiskItemHasHazardList().remove(riskItemHasHazard);
                hazardOld = em.merge(hazardOld);
            }
            if (hazardNew != null && !hazardNew.equals(hazardOld)) {
                hazardNew.getRiskItemHasHazardList().add(riskItemHasHazard);
                hazardNew = em.merge(hazardNew);
            }
            if (riskItemOld != null && !riskItemOld.equals(riskItemNew)) {
                riskItemOld.getRiskItemHasHazardList().remove(riskItemHasHazard);
                riskItemOld = em.merge(riskItemOld);
            }
            if (riskItemNew != null && !riskItemNew.equals(riskItemOld)) {
                riskItemNew.getRiskItemHasHazardList().add(riskItemHasHazard);
                riskItemNew = em.merge(riskItemNew);
            }
            for (HazardHasFailureMode hazardHasFailureModeListNewHazardHasFailureMode : hazardHasFailureModeListNew) {
                if (!hazardHasFailureModeListOld.contains(hazardHasFailureModeListNewHazardHasFailureMode)) {
                    RiskItemHasHazard oldRiskItemHasHazardOfHazardHasFailureModeListNewHazardHasFailureMode = hazardHasFailureModeListNewHazardHasFailureMode.getRiskItemHasHazard();
                    hazardHasFailureModeListNewHazardHasFailureMode.setRiskItemHasHazard(riskItemHasHazard);
                    hazardHasFailureModeListNewHazardHasFailureMode = em.merge(hazardHasFailureModeListNewHazardHasFailureMode);
                    if (oldRiskItemHasHazardOfHazardHasFailureModeListNewHazardHasFailureMode != null && !oldRiskItemHasHazardOfHazardHasFailureModeListNewHazardHasFailureMode.equals(riskItemHasHazard)) {
                        oldRiskItemHasHazardOfHazardHasFailureModeListNewHazardHasFailureMode.getHazardHasFailureModeList().remove(hazardHasFailureModeListNewHazardHasFailureMode);
                        oldRiskItemHasHazardOfHazardHasFailureModeListNewHazardHasFailureMode = em.merge(oldRiskItemHasHazardOfHazardHasFailureModeListNewHazardHasFailureMode);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RiskItemHasHazardPK id = riskItemHasHazard.getRiskItemHasHazardPK();
                if (findRiskItemHasHazard(id) == null) {
                    throw new NonexistentEntityException("The riskItemHasHazard with id " + id + " no longer exists.");
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

    public void destroy(RiskItemHasHazardPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskItemHasHazard riskItemHasHazard;
            try {
                riskItemHasHazard = em.getReference(RiskItemHasHazard.class, id);
                riskItemHasHazard.getRiskItemHasHazardPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The riskItemHasHazard with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<HazardHasFailureMode> hazardHasFailureModeListOrphanCheck = riskItemHasHazard.getHazardHasFailureModeList();
            for (HazardHasFailureMode hazardHasFailureModeListOrphanCheckHazardHasFailureMode : hazardHasFailureModeListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This RiskItemHasHazard (" + riskItemHasHazard + ") cannot be destroyed since the HazardHasFailureMode " + hazardHasFailureModeListOrphanCheckHazardHasFailureMode + " in its hazardHasFailureModeList field has a non-nullable riskItemHasHazard field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Hazard hazard = riskItemHasHazard.getHazard();
            if (hazard != null) {
                hazard.getRiskItemHasHazardList().remove(riskItemHasHazard);
                hazard = em.merge(hazard);
            }
            RiskItem riskItem = riskItemHasHazard.getRiskItem();
            if (riskItem != null) {
                riskItem.getRiskItemHasHazardList().remove(riskItemHasHazard);
                riskItem = em.merge(riskItem);
            }
            em.remove(riskItemHasHazard);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RiskItemHasHazard> findRiskItemHasHazardEntities() {
        return findRiskItemHasHazardEntities(true, -1, -1);
    }

    public List<RiskItemHasHazard> findRiskItemHasHazardEntities(int maxResults, int firstResult) {
        return findRiskItemHasHazardEntities(false, maxResults, firstResult);
    }

    private List<RiskItemHasHazard> findRiskItemHasHazardEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RiskItemHasHazard.class));
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

    public RiskItemHasHazard findRiskItemHasHazard(RiskItemHasHazardPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RiskItemHasHazard.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getRiskItemHasHazardCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RiskItemHasHazard> rt = cq.from(RiskItemHasHazard.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
