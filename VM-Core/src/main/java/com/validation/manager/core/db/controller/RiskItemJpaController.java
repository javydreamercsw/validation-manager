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
import com.validation.manager.core.db.Hazard;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.RiskControl;
import com.validation.manager.core.db.Cause;
import com.validation.manager.core.db.RiskCategory;
import com.validation.manager.core.db.RiskItem;
import com.validation.manager.core.db.RiskItemPK;
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
        if (riskItem.getHazardList() == null) {
            riskItem.setHazardList(new ArrayList<>());
        }
        if (riskItem.getRiskControlList() == null) {
            riskItem.setRiskControlList(new ArrayList<>());
        }
        if (riskItem.getRiskControlList1() == null) {
            riskItem.setRiskControlList1(new ArrayList<>());
        }
        if (riskItem.getCauseList() == null) {
            riskItem.setCauseList(new ArrayList<>());
        }
        if (riskItem.getRiskCategoryList() == null) {
            riskItem.setRiskCategoryList(new ArrayList<>());
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
            List<Hazard> attachedHazardList = new ArrayList<>();
            for (Hazard hazardListHazardToAttach : riskItem.getHazardList()) {
                hazardListHazardToAttach = em.getReference(hazardListHazardToAttach.getClass(), hazardListHazardToAttach.getId());
                attachedHazardList.add(hazardListHazardToAttach);
            }
            riskItem.setHazardList(attachedHazardList);
            List<RiskControl> attachedRiskControlList = new ArrayList<>();
            for (RiskControl riskControlListRiskControlToAttach : riskItem.getRiskControlList()) {
                riskControlListRiskControlToAttach = em.getReference(riskControlListRiskControlToAttach.getClass(), riskControlListRiskControlToAttach.getRiskControlPK());
                attachedRiskControlList.add(riskControlListRiskControlToAttach);
            }
            riskItem.setRiskControlList(attachedRiskControlList);
            List<RiskControl> attachedRiskControlList1 = new ArrayList<>();
            for (RiskControl riskControlList1RiskControlToAttach : riskItem.getRiskControlList1()) {
                riskControlList1RiskControlToAttach = em.getReference(riskControlList1RiskControlToAttach.getClass(), riskControlList1RiskControlToAttach.getRiskControlPK());
                attachedRiskControlList1.add(riskControlList1RiskControlToAttach);
            }
            riskItem.setRiskControlList1(attachedRiskControlList1);
            List<Cause> attachedCauseList = new ArrayList<>();
            for (Cause causeListCauseToAttach : riskItem.getCauseList()) {
                causeListCauseToAttach = em.getReference(causeListCauseToAttach.getClass(), causeListCauseToAttach.getId());
                attachedCauseList.add(causeListCauseToAttach);
            }
            riskItem.setCauseList(attachedCauseList);
            List<RiskCategory> attachedRiskCategoryList = new ArrayList<>();
            for (RiskCategory riskCategoryListRiskCategoryToAttach : riskItem.getRiskCategoryList()) {
                riskCategoryListRiskCategoryToAttach = em.getReference(riskCategoryListRiskCategoryToAttach.getClass(), riskCategoryListRiskCategoryToAttach.getId());
                attachedRiskCategoryList.add(riskCategoryListRiskCategoryToAttach);
            }
            riskItem.setRiskCategoryList(attachedRiskCategoryList);
            em.persist(riskItem);
            if (fmea != null) {
                fmea.getRiskItemList().add(riskItem);
                fmea = em.merge(fmea);
            }
            for (Hazard hazardListHazard : riskItem.getHazardList()) {
                hazardListHazard.getRiskItemList().add(riskItem);
                hazardListHazard = em.merge(hazardListHazard);
            }
            for (RiskControl riskControlListRiskControl : riskItem.getRiskControlList()) {
                riskControlListRiskControl.getRiskItemList().add(riskItem);
                riskControlListRiskControl = em.merge(riskControlListRiskControl);
            }
            for (RiskControl riskControlList1RiskControl : riskItem.getRiskControlList1()) {
                riskControlList1RiskControl.getRiskItemList1().add(riskItem);
                riskControlList1RiskControl = em.merge(riskControlList1RiskControl);
            }
            for (Cause causeListCause : riskItem.getCauseList()) {
                causeListCause.getRiskItemList().add(riskItem);
                causeListCause = em.merge(causeListCause);
            }
            for (RiskCategory riskCategoryListRiskCategory : riskItem.getRiskCategoryList()) {
                riskCategoryListRiskCategory.getRiskItemList().add(riskItem);
                riskCategoryListRiskCategory = em.merge(riskCategoryListRiskCategory);
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

    public void edit(RiskItem riskItem) throws NonexistentEntityException, Exception {
        riskItem.getRiskItemPK().setFMEAid(riskItem.getFmea().getFmeaPK().getId());
        riskItem.getRiskItemPK().setFMEAprojectid(riskItem.getFmea().getFmeaPK().getProjectId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskItem persistentRiskItem = em.find(RiskItem.class, riskItem.getRiskItemPK());
            Fmea fmeaOld = persistentRiskItem.getFmea();
            Fmea fmeaNew = riskItem.getFmea();
            List<Hazard> hazardListOld = persistentRiskItem.getHazardList();
            List<Hazard> hazardListNew = riskItem.getHazardList();
            List<RiskControl> riskControlListOld = persistentRiskItem.getRiskControlList();
            List<RiskControl> riskControlListNew = riskItem.getRiskControlList();
            List<RiskControl> riskControlList1Old = persistentRiskItem.getRiskControlList1();
            List<RiskControl> riskControlList1New = riskItem.getRiskControlList1();
            List<Cause> causeListOld = persistentRiskItem.getCauseList();
            List<Cause> causeListNew = riskItem.getCauseList();
            List<RiskCategory> riskCategoryListOld = persistentRiskItem.getRiskCategoryList();
            List<RiskCategory> riskCategoryListNew = riskItem.getRiskCategoryList();
            if (fmeaNew != null) {
                fmeaNew = em.getReference(fmeaNew.getClass(), fmeaNew.getFmeaPK());
                riskItem.setFmea(fmeaNew);
            }
            List<Hazard> attachedHazardListNew = new ArrayList<>();
            for (Hazard hazardListNewHazardToAttach : hazardListNew) {
                hazardListNewHazardToAttach = em.getReference(hazardListNewHazardToAttach.getClass(), hazardListNewHazardToAttach.getId());
                attachedHazardListNew.add(hazardListNewHazardToAttach);
            }
            hazardListNew = attachedHazardListNew;
            riskItem.setHazardList(hazardListNew);
            List<RiskControl> attachedRiskControlListNew = new ArrayList<>();
            for (RiskControl riskControlListNewRiskControlToAttach : riskControlListNew) {
                riskControlListNewRiskControlToAttach = em.getReference(riskControlListNewRiskControlToAttach.getClass(), riskControlListNewRiskControlToAttach.getRiskControlPK());
                attachedRiskControlListNew.add(riskControlListNewRiskControlToAttach);
            }
            riskControlListNew = attachedRiskControlListNew;
            riskItem.setRiskControlList(riskControlListNew);
            List<RiskControl> attachedRiskControlList1New = new ArrayList<>();
            for (RiskControl riskControlList1NewRiskControlToAttach : riskControlList1New) {
                riskControlList1NewRiskControlToAttach = em.getReference(riskControlList1NewRiskControlToAttach.getClass(), riskControlList1NewRiskControlToAttach.getRiskControlPK());
                attachedRiskControlList1New.add(riskControlList1NewRiskControlToAttach);
            }
            riskControlList1New = attachedRiskControlList1New;
            riskItem.setRiskControlList1(riskControlList1New);
            List<Cause> attachedCauseListNew = new ArrayList<>();
            for (Cause causeListNewCauseToAttach : causeListNew) {
                causeListNewCauseToAttach = em.getReference(causeListNewCauseToAttach.getClass(), causeListNewCauseToAttach.getId());
                attachedCauseListNew.add(causeListNewCauseToAttach);
            }
            causeListNew = attachedCauseListNew;
            riskItem.setCauseList(causeListNew);
            List<RiskCategory> attachedRiskCategoryListNew = new ArrayList<>();
            for (RiskCategory riskCategoryListNewRiskCategoryToAttach : riskCategoryListNew) {
                riskCategoryListNewRiskCategoryToAttach = em.getReference(riskCategoryListNewRiskCategoryToAttach.getClass(), riskCategoryListNewRiskCategoryToAttach.getId());
                attachedRiskCategoryListNew.add(riskCategoryListNewRiskCategoryToAttach);
            }
            riskCategoryListNew = attachedRiskCategoryListNew;
            riskItem.setRiskCategoryList(riskCategoryListNew);
            riskItem = em.merge(riskItem);
            if (fmeaOld != null && !fmeaOld.equals(fmeaNew)) {
                fmeaOld.getRiskItemList().remove(riskItem);
                fmeaOld = em.merge(fmeaOld);
            }
            if (fmeaNew != null && !fmeaNew.equals(fmeaOld)) {
                fmeaNew.getRiskItemList().add(riskItem);
                fmeaNew = em.merge(fmeaNew);
            }
            for (Hazard hazardListOldHazard : hazardListOld) {
                if (!hazardListNew.contains(hazardListOldHazard)) {
                    hazardListOldHazard.getRiskItemList().remove(riskItem);
                    hazardListOldHazard = em.merge(hazardListOldHazard);
                }
            }
            for (Hazard hazardListNewHazard : hazardListNew) {
                if (!hazardListOld.contains(hazardListNewHazard)) {
                    hazardListNewHazard.getRiskItemList().add(riskItem);
                    hazardListNewHazard = em.merge(hazardListNewHazard);
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
            for (RiskControl riskControlList1OldRiskControl : riskControlList1Old) {
                if (!riskControlList1New.contains(riskControlList1OldRiskControl)) {
                    riskControlList1OldRiskControl.getRiskItemList1().remove(riskItem);
                    riskControlList1OldRiskControl = em.merge(riskControlList1OldRiskControl);
                }
            }
            for (RiskControl riskControlList1NewRiskControl : riskControlList1New) {
                if (!riskControlList1Old.contains(riskControlList1NewRiskControl)) {
                    riskControlList1NewRiskControl.getRiskItemList1().add(riskItem);
                    riskControlList1NewRiskControl = em.merge(riskControlList1NewRiskControl);
                }
            }
            for (Cause causeListOldCause : causeListOld) {
                if (!causeListNew.contains(causeListOldCause)) {
                    causeListOldCause.getRiskItemList().remove(riskItem);
                    causeListOldCause = em.merge(causeListOldCause);
                }
            }
            for (Cause causeListNewCause : causeListNew) {
                if (!causeListOld.contains(causeListNewCause)) {
                    causeListNewCause.getRiskItemList().add(riskItem);
                    causeListNewCause = em.merge(causeListNewCause);
                }
            }
            for (RiskCategory riskCategoryListOldRiskCategory : riskCategoryListOld) {
                if (!riskCategoryListNew.contains(riskCategoryListOldRiskCategory)) {
                    riskCategoryListOldRiskCategory.getRiskItemList().remove(riskItem);
                    riskCategoryListOldRiskCategory = em.merge(riskCategoryListOldRiskCategory);
                }
            }
            for (RiskCategory riskCategoryListNewRiskCategory : riskCategoryListNew) {
                if (!riskCategoryListOld.contains(riskCategoryListNewRiskCategory)) {
                    riskCategoryListNewRiskCategory.getRiskItemList().add(riskItem);
                    riskCategoryListNewRiskCategory = em.merge(riskCategoryListNewRiskCategory);
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

    public void destroy(RiskItemPK id) throws NonexistentEntityException {
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
            Fmea fmea = riskItem.getFmea();
            if (fmea != null) {
                fmea.getRiskItemList().remove(riskItem);
                fmea = em.merge(fmea);
            }
            List<Hazard> hazardList = riskItem.getHazardList();
            for (Hazard hazardListHazard : hazardList) {
                hazardListHazard.getRiskItemList().remove(riskItem);
                hazardListHazard = em.merge(hazardListHazard);
            }
            List<RiskControl> riskControlList = riskItem.getRiskControlList();
            for (RiskControl riskControlListRiskControl : riskControlList) {
                riskControlListRiskControl.getRiskItemList().remove(riskItem);
                riskControlListRiskControl = em.merge(riskControlListRiskControl);
            }
            List<RiskControl> riskControlList1 = riskItem.getRiskControlList1();
            for (RiskControl riskControlList1RiskControl : riskControlList1) {
                riskControlList1RiskControl.getRiskItemList1().remove(riskItem);
                riskControlList1RiskControl = em.merge(riskControlList1RiskControl);
            }
            List<Cause> causeList = riskItem.getCauseList();
            for (Cause causeListCause : causeList) {
                causeListCause.getRiskItemList().remove(riskItem);
                causeListCause = em.merge(causeListCause);
            }
            List<RiskCategory> riskCategoryList = riskItem.getRiskCategoryList();
            for (RiskCategory riskCategoryListRiskCategory : riskCategoryList) {
                riskCategoryListRiskCategory.getRiskItemList().remove(riskItem);
                riskCategoryListRiskCategory = em.merge(riskCategoryListRiskCategory);
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
