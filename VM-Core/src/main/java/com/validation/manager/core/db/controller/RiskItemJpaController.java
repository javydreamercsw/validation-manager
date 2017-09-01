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
import com.validation.manager.core.db.Fmea;
import com.validation.manager.core.db.RiskItemHasHazard;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.RiskControl;
import com.validation.manager.core.db.RiskControlHasResidualRiskItem;
import com.validation.manager.core.db.RiskItem;
import com.validation.manager.core.db.RiskItemPK;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class RiskItemJpaController implements Serializable {

    public RiskItemJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RiskItem riskItem) throws PreexistingEntityException, Exception {
        if (riskItem.getRiskItemPK() == null) {
            riskItem.setRiskItemPK(new RiskItemPK());
        }
        if (riskItem.getRiskItemHasHazardList() == null) {
            riskItem.setRiskItemHasHazardList(new ArrayList<>());
        }
        if (riskItem.getRiskControlList() == null) {
            riskItem.setRiskControlList(new ArrayList<>());
        }
        if (riskItem.getRiskControlHasResidualRiskItemList() == null) {
            riskItem.setRiskControlHasResidualRiskItemList(new ArrayList<>());
        }
        riskItem.getRiskItemPK().setFMEAid(riskItem.getFmea().getFmeaPK().getId());
        riskItem.getRiskItemPK().setFMEAprojectid(riskItem.getFmea().getFmeaPK().getProjectId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Fmea fmea = riskItem.getFmea();
            if (fmea != null) {
                fmea = em.getReference(fmea.getClass(), fmea.getFmeaPK());
                riskItem.setFmea(fmea);
            }
            List<RiskItemHasHazard> attachedRiskItemHasHazardList = new ArrayList<>();
            for (RiskItemHasHazard riskItemHasHazardListRiskItemHasHazardToAttach : riskItem.getRiskItemHasHazardList()) {
                riskItemHasHazardListRiskItemHasHazardToAttach = em.getReference(riskItemHasHazardListRiskItemHasHazardToAttach.getClass(), riskItemHasHazardListRiskItemHasHazardToAttach.getRiskItemHasHazardPK());
                attachedRiskItemHasHazardList.add(riskItemHasHazardListRiskItemHasHazardToAttach);
            }
            riskItem.setRiskItemHasHazardList(attachedRiskItemHasHazardList);
            List<RiskControl> attachedRiskControlList = new ArrayList<>();
            for (RiskControl riskControlListRiskControlToAttach : riskItem.getRiskControlList()) {
                riskControlListRiskControlToAttach = em.getReference(riskControlListRiskControlToAttach.getClass(), riskControlListRiskControlToAttach.getRiskControlPK());
                attachedRiskControlList.add(riskControlListRiskControlToAttach);
            }
            riskItem.setRiskControlList(attachedRiskControlList);
            List<RiskControlHasResidualRiskItem> attachedRiskControlHasResidualRiskItemList = new ArrayList<>();
            for (RiskControlHasResidualRiskItem riskControlHasResidualRiskItemListRiskControlHasResidualRiskItemToAttach : riskItem.getRiskControlHasResidualRiskItemList()) {
                riskControlHasResidualRiskItemListRiskControlHasResidualRiskItemToAttach = em.getReference(riskControlHasResidualRiskItemListRiskControlHasResidualRiskItemToAttach.getClass(), riskControlHasResidualRiskItemListRiskControlHasResidualRiskItemToAttach.getRiskControlHasResidualRiskItemPK());
                attachedRiskControlHasResidualRiskItemList.add(riskControlHasResidualRiskItemListRiskControlHasResidualRiskItemToAttach);
            }
            riskItem.setRiskControlHasResidualRiskItemList(attachedRiskControlHasResidualRiskItemList);
            em.persist(riskItem);
            if (fmea != null) {
                fmea.getRiskItemList().add(riskItem);
                fmea = em.merge(fmea);
            }
            for (RiskItemHasHazard riskItemHasHazardListRiskItemHasHazard : riskItem.getRiskItemHasHazardList()) {
                RiskItem oldRiskItemOfRiskItemHasHazardListRiskItemHasHazard = riskItemHasHazardListRiskItemHasHazard.getRiskItem();
                riskItemHasHazardListRiskItemHasHazard.setRiskItem(riskItem);
                riskItemHasHazardListRiskItemHasHazard = em.merge(riskItemHasHazardListRiskItemHasHazard);
                if (oldRiskItemOfRiskItemHasHazardListRiskItemHasHazard != null) {
                    oldRiskItemOfRiskItemHasHazardListRiskItemHasHazard.getRiskItemHasHazardList().remove(riskItemHasHazardListRiskItemHasHazard);
                    oldRiskItemOfRiskItemHasHazardListRiskItemHasHazard = em.merge(oldRiskItemOfRiskItemHasHazardListRiskItemHasHazard);
                }
            }
            for (RiskControl riskControlListRiskControl : riskItem.getRiskControlList()) {
                riskControlListRiskControl.getRiskItemList().add(riskItem);
                riskControlListRiskControl = em.merge(riskControlListRiskControl);
            }
            for (RiskControlHasResidualRiskItem riskControlHasResidualRiskItemListRiskControlHasResidualRiskItem : riskItem.getRiskControlHasResidualRiskItemList()) {
                RiskItem oldRiskItemOfRiskControlHasResidualRiskItemListRiskControlHasResidualRiskItem = riskControlHasResidualRiskItemListRiskControlHasResidualRiskItem.getRiskItem();
                riskControlHasResidualRiskItemListRiskControlHasResidualRiskItem.setRiskItem(riskItem);
                riskControlHasResidualRiskItemListRiskControlHasResidualRiskItem = em.merge(riskControlHasResidualRiskItemListRiskControlHasResidualRiskItem);
                if (oldRiskItemOfRiskControlHasResidualRiskItemListRiskControlHasResidualRiskItem != null) {
                    oldRiskItemOfRiskControlHasResidualRiskItemListRiskControlHasResidualRiskItem.getRiskControlHasResidualRiskItemList().remove(riskControlHasResidualRiskItemListRiskControlHasResidualRiskItem);
                    oldRiskItemOfRiskControlHasResidualRiskItemListRiskControlHasResidualRiskItem = em.merge(oldRiskItemOfRiskControlHasResidualRiskItemListRiskControlHasResidualRiskItem);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findRiskItem(riskItem.getRiskItemPK()) != null) {
                throw new PreexistingEntityException("RiskItem " + riskItem + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RiskItem riskItem) throws IllegalOrphanException, NonexistentEntityException, Exception {
        riskItem.getRiskItemPK().setFMEAid(riskItem.getFmea().getFmeaPK().getId());
        riskItem.getRiskItemPK().setFMEAprojectid(riskItem.getFmea().getFmeaPK().getProjectId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskItem persistentRiskItem = em.find(RiskItem.class, riskItem.getRiskItemPK());
            Fmea fmeaOld = persistentRiskItem.getFmea();
            Fmea fmeaNew = riskItem.getFmea();
            List<RiskItemHasHazard> riskItemHasHazardListOld = persistentRiskItem.getRiskItemHasHazardList();
            List<RiskItemHasHazard> riskItemHasHazardListNew = riskItem.getRiskItemHasHazardList();
            List<RiskControl> riskControlListOld = persistentRiskItem.getRiskControlList();
            List<RiskControl> riskControlListNew = riskItem.getRiskControlList();
            List<RiskControlHasResidualRiskItem> riskControlHasResidualRiskItemListOld = persistentRiskItem.getRiskControlHasResidualRiskItemList();
            List<RiskControlHasResidualRiskItem> riskControlHasResidualRiskItemListNew = riskItem.getRiskControlHasResidualRiskItemList();
            List<String> illegalOrphanMessages = null;
            for (RiskItemHasHazard riskItemHasHazardListOldRiskItemHasHazard : riskItemHasHazardListOld) {
                if (!riskItemHasHazardListNew.contains(riskItemHasHazardListOldRiskItemHasHazard)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain RiskItemHasHazard " + riskItemHasHazardListOldRiskItemHasHazard + " since its riskItem field is not nullable.");
                }
            }
            for (RiskControlHasResidualRiskItem riskControlHasResidualRiskItemListOldRiskControlHasResidualRiskItem : riskControlHasResidualRiskItemListOld) {
                if (!riskControlHasResidualRiskItemListNew.contains(riskControlHasResidualRiskItemListOldRiskControlHasResidualRiskItem)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain RiskControlHasResidualRiskItem " + riskControlHasResidualRiskItemListOldRiskControlHasResidualRiskItem + " since its riskItem field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (fmeaNew != null) {
                fmeaNew = em.getReference(fmeaNew.getClass(), fmeaNew.getFmeaPK());
                riskItem.setFmea(fmeaNew);
            }
            List<RiskItemHasHazard> attachedRiskItemHasHazardListNew = new ArrayList<>();
            for (RiskItemHasHazard riskItemHasHazardListNewRiskItemHasHazardToAttach : riskItemHasHazardListNew) {
                riskItemHasHazardListNewRiskItemHasHazardToAttach = em.getReference(riskItemHasHazardListNewRiskItemHasHazardToAttach.getClass(), riskItemHasHazardListNewRiskItemHasHazardToAttach.getRiskItemHasHazardPK());
                attachedRiskItemHasHazardListNew.add(riskItemHasHazardListNewRiskItemHasHazardToAttach);
            }
            riskItemHasHazardListNew = attachedRiskItemHasHazardListNew;
            riskItem.setRiskItemHasHazardList(riskItemHasHazardListNew);
            List<RiskControl> attachedRiskControlListNew = new ArrayList<>();
            for (RiskControl riskControlListNewRiskControlToAttach : riskControlListNew) {
                riskControlListNewRiskControlToAttach = em.getReference(riskControlListNewRiskControlToAttach.getClass(), riskControlListNewRiskControlToAttach.getRiskControlPK());
                attachedRiskControlListNew.add(riskControlListNewRiskControlToAttach);
            }
            riskControlListNew = attachedRiskControlListNew;
            riskItem.setRiskControlList(riskControlListNew);
            List<RiskControlHasResidualRiskItem> attachedRiskControlHasResidualRiskItemListNew = new ArrayList<>();
            for (RiskControlHasResidualRiskItem riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItemToAttach : riskControlHasResidualRiskItemListNew) {
                riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItemToAttach = em.getReference(riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItemToAttach.getClass(), riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItemToAttach.getRiskControlHasResidualRiskItemPK());
                attachedRiskControlHasResidualRiskItemListNew.add(riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItemToAttach);
            }
            riskControlHasResidualRiskItemListNew = attachedRiskControlHasResidualRiskItemListNew;
            riskItem.setRiskControlHasResidualRiskItemList(riskControlHasResidualRiskItemListNew);
            riskItem = em.merge(riskItem);
            if (fmeaOld != null && !fmeaOld.equals(fmeaNew)) {
                fmeaOld.getRiskItemList().remove(riskItem);
                fmeaOld = em.merge(fmeaOld);
            }
            if (fmeaNew != null && !fmeaNew.equals(fmeaOld)) {
                fmeaNew.getRiskItemList().add(riskItem);
                fmeaNew = em.merge(fmeaNew);
            }
            for (RiskItemHasHazard riskItemHasHazardListNewRiskItemHasHazard : riskItemHasHazardListNew) {
                if (!riskItemHasHazardListOld.contains(riskItemHasHazardListNewRiskItemHasHazard)) {
                    RiskItem oldRiskItemOfRiskItemHasHazardListNewRiskItemHasHazard = riskItemHasHazardListNewRiskItemHasHazard.getRiskItem();
                    riskItemHasHazardListNewRiskItemHasHazard.setRiskItem(riskItem);
                    riskItemHasHazardListNewRiskItemHasHazard = em.merge(riskItemHasHazardListNewRiskItemHasHazard);
                    if (oldRiskItemOfRiskItemHasHazardListNewRiskItemHasHazard != null && !oldRiskItemOfRiskItemHasHazardListNewRiskItemHasHazard.equals(riskItem)) {
                        oldRiskItemOfRiskItemHasHazardListNewRiskItemHasHazard.getRiskItemHasHazardList().remove(riskItemHasHazardListNewRiskItemHasHazard);
                        oldRiskItemOfRiskItemHasHazardListNewRiskItemHasHazard = em.merge(oldRiskItemOfRiskItemHasHazardListNewRiskItemHasHazard);
                    }
                }
            }
            for (RiskControl riskControlListOldRiskControl : riskControlListOld) {
                if (!riskControlListNew.contains(riskControlListOldRiskControl)) {
                    riskControlListOldRiskControl.getRiskItemList().remove(riskItem);
                    riskControlListOldRiskControl = em.merge(riskControlListOldRiskControl);
                }
            }
            for (RiskControl riskControlListNewRiskControl : riskControlListNew) {
                if (!riskControlListOld.contains(riskControlListNewRiskControl)) {
                    riskControlListNewRiskControl.getRiskItemList().add(riskItem);
                    riskControlListNewRiskControl = em.merge(riskControlListNewRiskControl);
                }
            }
            for (RiskControlHasResidualRiskItem riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem : riskControlHasResidualRiskItemListNew) {
                if (!riskControlHasResidualRiskItemListOld.contains(riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem)) {
                    RiskItem oldRiskItemOfRiskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem = riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem.getRiskItem();
                    riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem.setRiskItem(riskItem);
                    riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem = em.merge(riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem);
                    if (oldRiskItemOfRiskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem != null && !oldRiskItemOfRiskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem.equals(riskItem)) {
                        oldRiskItemOfRiskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem.getRiskControlHasResidualRiskItemList().remove(riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem);
                        oldRiskItemOfRiskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem = em.merge(oldRiskItemOfRiskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RiskItemPK id = riskItem.getRiskItemPK();
                if (findRiskItem(id) == null) {
                    throw new NonexistentEntityException("The riskItem with id " + id + " no longer exists.");
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

    public void destroy(RiskItemPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskItem riskItem;
            try {
                riskItem = em.getReference(RiskItem.class, id);
                riskItem.getRiskItemPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The riskItem with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RiskItemHasHazard> riskItemHasHazardListOrphanCheck = riskItem.getRiskItemHasHazardList();
            for (RiskItemHasHazard riskItemHasHazardListOrphanCheckRiskItemHasHazard : riskItemHasHazardListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This RiskItem (" + riskItem + ") cannot be destroyed since the RiskItemHasHazard " + riskItemHasHazardListOrphanCheckRiskItemHasHazard + " in its riskItemHasHazardList field has a non-nullable riskItem field.");
            }
            List<RiskControlHasResidualRiskItem> riskControlHasResidualRiskItemListOrphanCheck = riskItem.getRiskControlHasResidualRiskItemList();
            for (RiskControlHasResidualRiskItem riskControlHasResidualRiskItemListOrphanCheckRiskControlHasResidualRiskItem : riskControlHasResidualRiskItemListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This RiskItem (" + riskItem + ") cannot be destroyed since the RiskControlHasResidualRiskItem " + riskControlHasResidualRiskItemListOrphanCheckRiskControlHasResidualRiskItem + " in its riskControlHasResidualRiskItemList field has a non-nullable riskItem field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Fmea fmea = riskItem.getFmea();
            if (fmea != null) {
                fmea.getRiskItemList().remove(riskItem);
                fmea = em.merge(fmea);
            }
            List<RiskControl> riskControlList = riskItem.getRiskControlList();
            for (RiskControl riskControlListRiskControl : riskControlList) {
                riskControlListRiskControl.getRiskItemList().remove(riskItem);
                riskControlListRiskControl = em.merge(riskControlListRiskControl);
            }
            em.remove(riskItem);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RiskItem> findRiskItemEntities() {
        return findRiskItemEntities(true, -1, -1);
    }

    public List<RiskItem> findRiskItemEntities(int maxResults, int firstResult) {
        return findRiskItemEntities(false, maxResults, firstResult);
    }

    private List<RiskItem> findRiskItemEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RiskItem.class));
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

    public RiskItem findRiskItem(RiskItemPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RiskItem.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getRiskItemCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RiskItem> rt = cq.from(RiskItem.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
