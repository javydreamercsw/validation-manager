/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.fmea.FailureMode;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.fmea.Cause;
import com.validation.manager.core.db.fmea.RiskControl;
import com.validation.manager.core.db.fmea.Hazard;
import com.validation.manager.core.db.fmea.RiskItem;
import com.validation.manager.core.db.fmea.RiskItemHasRiskCategory;
import com.validation.manager.core.db.fmea.RiskItemPK;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
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
        if (riskItem.getFailureModeList() == null) {
            riskItem.setFailureModeList(new ArrayList<FailureMode>());
        }
        if (riskItem.getCauseList() == null) {
            riskItem.setCauseList(new ArrayList<Cause>());
        }
        if (riskItem.getRiskControlList() == null) {
            riskItem.setRiskControlList(new ArrayList<RiskControl>());
        }
        if (riskItem.getHazardList() == null) {
            riskItem.setHazardList(new ArrayList<Hazard>());
        }
        if (riskItem.getRiskControlList1() == null) {
            riskItem.setRiskControlList1(new ArrayList<RiskControl>());
        }
        if (riskItem.getRiskItemHasRiskCategoryList() == null) {
            riskItem.setRiskItemHasRiskCategoryList(new ArrayList<RiskItemHasRiskCategory>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<FailureMode> attachedFailureModeList = new ArrayList<FailureMode>();
            for (FailureMode failureModeListFailureModeToAttach : riskItem.getFailureModeList()) {
                failureModeListFailureModeToAttach = em.getReference(failureModeListFailureModeToAttach.getClass(), failureModeListFailureModeToAttach.getId());
                attachedFailureModeList.add(failureModeListFailureModeToAttach);
            }
            riskItem.setFailureModeList(attachedFailureModeList);
            List<Cause> attachedCauseList = new ArrayList<Cause>();
            for (Cause causeListCauseToAttach : riskItem.getCauseList()) {
                causeListCauseToAttach = em.getReference(causeListCauseToAttach.getClass(), causeListCauseToAttach.getId());
                attachedCauseList.add(causeListCauseToAttach);
            }
            riskItem.setCauseList(attachedCauseList);
            List<RiskControl> attachedRiskControlList = new ArrayList<RiskControl>();
            for (RiskControl riskControlListRiskControlToAttach : riskItem.getRiskControlList()) {
                riskControlListRiskControlToAttach = em.getReference(riskControlListRiskControlToAttach.getClass(), riskControlListRiskControlToAttach.getRiskControlPK());
                attachedRiskControlList.add(riskControlListRiskControlToAttach);
            }
            riskItem.setRiskControlList(attachedRiskControlList);
            List<Hazard> attachedHazardList = new ArrayList<Hazard>();
            for (Hazard hazardListHazardToAttach : riskItem.getHazardList()) {
                hazardListHazardToAttach = em.getReference(hazardListHazardToAttach.getClass(), hazardListHazardToAttach.getId());
                attachedHazardList.add(hazardListHazardToAttach);
            }
            riskItem.setHazardList(attachedHazardList);
            List<RiskControl> attachedRiskControlList1 = new ArrayList<RiskControl>();
            for (RiskControl riskControlList1RiskControlToAttach : riskItem.getRiskControlList1()) {
                riskControlList1RiskControlToAttach = em.getReference(riskControlList1RiskControlToAttach.getClass(), riskControlList1RiskControlToAttach.getRiskControlPK());
                attachedRiskControlList1.add(riskControlList1RiskControlToAttach);
            }
            riskItem.setRiskControlList1(attachedRiskControlList1);
            List<RiskItemHasRiskCategory> attachedRiskItemHasRiskCategoryList = new ArrayList<RiskItemHasRiskCategory>();
            for (RiskItemHasRiskCategory riskItemHasRiskCategoryListRiskItemHasRiskCategoryToAttach : riskItem.getRiskItemHasRiskCategoryList()) {
                riskItemHasRiskCategoryListRiskItemHasRiskCategoryToAttach = em.getReference(riskItemHasRiskCategoryListRiskItemHasRiskCategoryToAttach.getClass(), riskItemHasRiskCategoryListRiskItemHasRiskCategoryToAttach.getRiskItemHasRiskCategoryPK());
                attachedRiskItemHasRiskCategoryList.add(riskItemHasRiskCategoryListRiskItemHasRiskCategoryToAttach);
            }
            riskItem.setRiskItemHasRiskCategoryList(attachedRiskItemHasRiskCategoryList);
            em.persist(riskItem);
            for (FailureMode failureModeListFailureMode : riskItem.getFailureModeList()) {
                failureModeListFailureMode.getRiskItemList().add(riskItem);
                failureModeListFailureMode = em.merge(failureModeListFailureMode);
            }
            for (Cause causeListCause : riskItem.getCauseList()) {
                causeListCause.getRiskItemList().add(riskItem);
                causeListCause = em.merge(causeListCause);
            }
            for (RiskControl riskControlListRiskControl : riskItem.getRiskControlList()) {
                riskControlListRiskControl.getRiskItemList().add(riskItem);
                riskControlListRiskControl = em.merge(riskControlListRiskControl);
            }
            for (Hazard hazardListHazard : riskItem.getHazardList()) {
                hazardListHazard.getRiskItemList().add(riskItem);
                hazardListHazard = em.merge(hazardListHazard);
            }
            for (RiskControl riskControlList1RiskControl : riskItem.getRiskControlList1()) {
                riskControlList1RiskControl.getRiskItemList().add(riskItem);
                riskControlList1RiskControl = em.merge(riskControlList1RiskControl);
            }
            for (RiskItemHasRiskCategory riskItemHasRiskCategoryListRiskItemHasRiskCategory : riskItem.getRiskItemHasRiskCategoryList()) {
                RiskItem oldRiskItemOfRiskItemHasRiskCategoryListRiskItemHasRiskCategory = riskItemHasRiskCategoryListRiskItemHasRiskCategory.getRiskItem();
                riskItemHasRiskCategoryListRiskItemHasRiskCategory.setRiskItem(riskItem);
                riskItemHasRiskCategoryListRiskItemHasRiskCategory = em.merge(riskItemHasRiskCategoryListRiskItemHasRiskCategory);
                if (oldRiskItemOfRiskItemHasRiskCategoryListRiskItemHasRiskCategory != null) {
                    oldRiskItemOfRiskItemHasRiskCategoryListRiskItemHasRiskCategory.getRiskItemHasRiskCategoryList().remove(riskItemHasRiskCategoryListRiskItemHasRiskCategory);
                    oldRiskItemOfRiskItemHasRiskCategoryListRiskItemHasRiskCategory = em.merge(oldRiskItemOfRiskItemHasRiskCategoryListRiskItemHasRiskCategory);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRiskItem(riskItem.getRiskItemPK()) != null) {
                throw new PreexistingEntityException("RiskItem " + riskItem + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RiskItem riskItem) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskItem persistentRiskItem = em.find(RiskItem.class, riskItem.getRiskItemPK());
            List<FailureMode> failureModeListOld = persistentRiskItem.getFailureModeList();
            List<FailureMode> failureModeListNew = riskItem.getFailureModeList();
            List<Cause> causeListOld = persistentRiskItem.getCauseList();
            List<Cause> causeListNew = riskItem.getCauseList();
            List<RiskControl> riskControlListOld = persistentRiskItem.getRiskControlList();
            List<RiskControl> riskControlListNew = riskItem.getRiskControlList();
            List<Hazard> hazardListOld = persistentRiskItem.getHazardList();
            List<Hazard> hazardListNew = riskItem.getHazardList();
            List<RiskControl> riskControlList1Old = persistentRiskItem.getRiskControlList1();
            List<RiskControl> riskControlList1New = riskItem.getRiskControlList1();
            List<RiskItemHasRiskCategory> riskItemHasRiskCategoryListOld = persistentRiskItem.getRiskItemHasRiskCategoryList();
            List<RiskItemHasRiskCategory> riskItemHasRiskCategoryListNew = riskItem.getRiskItemHasRiskCategoryList();
            List<String> illegalOrphanMessages = null;
            for (RiskItemHasRiskCategory riskItemHasRiskCategoryListOldRiskItemHasRiskCategory : riskItemHasRiskCategoryListOld) {
                if (!riskItemHasRiskCategoryListNew.contains(riskItemHasRiskCategoryListOldRiskItemHasRiskCategory)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RiskItemHasRiskCategory " + riskItemHasRiskCategoryListOldRiskItemHasRiskCategory + " since its riskItem field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<FailureMode> attachedFailureModeListNew = new ArrayList<FailureMode>();
            for (FailureMode failureModeListNewFailureModeToAttach : failureModeListNew) {
                failureModeListNewFailureModeToAttach = em.getReference(failureModeListNewFailureModeToAttach.getClass(), failureModeListNewFailureModeToAttach.getId());
                attachedFailureModeListNew.add(failureModeListNewFailureModeToAttach);
            }
            failureModeListNew = attachedFailureModeListNew;
            riskItem.setFailureModeList(failureModeListNew);
            List<Cause> attachedCauseListNew = new ArrayList<Cause>();
            for (Cause causeListNewCauseToAttach : causeListNew) {
                causeListNewCauseToAttach = em.getReference(causeListNewCauseToAttach.getClass(), causeListNewCauseToAttach.getId());
                attachedCauseListNew.add(causeListNewCauseToAttach);
            }
            causeListNew = attachedCauseListNew;
            riskItem.setCauseList(causeListNew);
            List<RiskControl> attachedRiskControlListNew = new ArrayList<RiskControl>();
            for (RiskControl riskControlListNewRiskControlToAttach : riskControlListNew) {
                riskControlListNewRiskControlToAttach = em.getReference(riskControlListNewRiskControlToAttach.getClass(), riskControlListNewRiskControlToAttach.getRiskControlPK());
                attachedRiskControlListNew.add(riskControlListNewRiskControlToAttach);
            }
            riskControlListNew = attachedRiskControlListNew;
            riskItem.setRiskControlList(riskControlListNew);
            List<Hazard> attachedHazardListNew = new ArrayList<Hazard>();
            for (Hazard hazardListNewHazardToAttach : hazardListNew) {
                hazardListNewHazardToAttach = em.getReference(hazardListNewHazardToAttach.getClass(), hazardListNewHazardToAttach.getId());
                attachedHazardListNew.add(hazardListNewHazardToAttach);
            }
            hazardListNew = attachedHazardListNew;
            riskItem.setHazardList(hazardListNew);
            List<RiskControl> attachedRiskControlList1New = new ArrayList<RiskControl>();
            for (RiskControl riskControlList1NewRiskControlToAttach : riskControlList1New) {
                riskControlList1NewRiskControlToAttach = em.getReference(riskControlList1NewRiskControlToAttach.getClass(), riskControlList1NewRiskControlToAttach.getRiskControlPK());
                attachedRiskControlList1New.add(riskControlList1NewRiskControlToAttach);
            }
            riskControlList1New = attachedRiskControlList1New;
            riskItem.setRiskControlList1(riskControlList1New);
            List<RiskItemHasRiskCategory> attachedRiskItemHasRiskCategoryListNew = new ArrayList<RiskItemHasRiskCategory>();
            for (RiskItemHasRiskCategory riskItemHasRiskCategoryListNewRiskItemHasRiskCategoryToAttach : riskItemHasRiskCategoryListNew) {
                riskItemHasRiskCategoryListNewRiskItemHasRiskCategoryToAttach = em.getReference(riskItemHasRiskCategoryListNewRiskItemHasRiskCategoryToAttach.getClass(), riskItemHasRiskCategoryListNewRiskItemHasRiskCategoryToAttach.getRiskItemHasRiskCategoryPK());
                attachedRiskItemHasRiskCategoryListNew.add(riskItemHasRiskCategoryListNewRiskItemHasRiskCategoryToAttach);
            }
            riskItemHasRiskCategoryListNew = attachedRiskItemHasRiskCategoryListNew;
            riskItem.setRiskItemHasRiskCategoryList(riskItemHasRiskCategoryListNew);
            riskItem = em.merge(riskItem);
            for (FailureMode failureModeListOldFailureMode : failureModeListOld) {
                if (!failureModeListNew.contains(failureModeListOldFailureMode)) {
                    failureModeListOldFailureMode.getRiskItemList().remove(riskItem);
                    failureModeListOldFailureMode = em.merge(failureModeListOldFailureMode);
                }
            }
            for (FailureMode failureModeListNewFailureMode : failureModeListNew) {
                if (!failureModeListOld.contains(failureModeListNewFailureMode)) {
                    failureModeListNewFailureMode.getRiskItemList().add(riskItem);
                    failureModeListNewFailureMode = em.merge(failureModeListNewFailureMode);
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
            for (RiskControl riskControlList1OldRiskControl : riskControlList1Old) {
                if (!riskControlList1New.contains(riskControlList1OldRiskControl)) {
                    riskControlList1OldRiskControl.getRiskItemList().remove(riskItem);
                    riskControlList1OldRiskControl = em.merge(riskControlList1OldRiskControl);
                }
            }
            for (RiskControl riskControlList1NewRiskControl : riskControlList1New) {
                if (!riskControlList1Old.contains(riskControlList1NewRiskControl)) {
                    riskControlList1NewRiskControl.getRiskItemList().add(riskItem);
                    riskControlList1NewRiskControl = em.merge(riskControlList1NewRiskControl);
                }
            }
            for (RiskItemHasRiskCategory riskItemHasRiskCategoryListNewRiskItemHasRiskCategory : riskItemHasRiskCategoryListNew) {
                if (!riskItemHasRiskCategoryListOld.contains(riskItemHasRiskCategoryListNewRiskItemHasRiskCategory)) {
                    RiskItem oldRiskItemOfRiskItemHasRiskCategoryListNewRiskItemHasRiskCategory = riskItemHasRiskCategoryListNewRiskItemHasRiskCategory.getRiskItem();
                    riskItemHasRiskCategoryListNewRiskItemHasRiskCategory.setRiskItem(riskItem);
                    riskItemHasRiskCategoryListNewRiskItemHasRiskCategory = em.merge(riskItemHasRiskCategoryListNewRiskItemHasRiskCategory);
                    if (oldRiskItemOfRiskItemHasRiskCategoryListNewRiskItemHasRiskCategory != null && !oldRiskItemOfRiskItemHasRiskCategoryListNewRiskItemHasRiskCategory.equals(riskItem)) {
                        oldRiskItemOfRiskItemHasRiskCategoryListNewRiskItemHasRiskCategory.getRiskItemHasRiskCategoryList().remove(riskItemHasRiskCategoryListNewRiskItemHasRiskCategory);
                        oldRiskItemOfRiskItemHasRiskCategoryListNewRiskItemHasRiskCategory = em.merge(oldRiskItemOfRiskItemHasRiskCategoryListNewRiskItemHasRiskCategory);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RiskItemPK id = riskItem.getRiskItemPK();
                if (findRiskItem(id) == null) {
                    throw new NonexistentEntityException("The riskItem with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
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
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The riskItem with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RiskItemHasRiskCategory> riskItemHasRiskCategoryListOrphanCheck = riskItem.getRiskItemHasRiskCategoryList();
            for (RiskItemHasRiskCategory riskItemHasRiskCategoryListOrphanCheckRiskItemHasRiskCategory : riskItemHasRiskCategoryListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This RiskItem (" + riskItem + ") cannot be destroyed since the RiskItemHasRiskCategory " + riskItemHasRiskCategoryListOrphanCheckRiskItemHasRiskCategory + " in its riskItemHasRiskCategoryList field has a non-nullable riskItem field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<FailureMode> failureModeList = riskItem.getFailureModeList();
            for (FailureMode failureModeListFailureMode : failureModeList) {
                failureModeListFailureMode.getRiskItemList().remove(riskItem);
                failureModeListFailureMode = em.merge(failureModeListFailureMode);
            }
            List<Cause> causeList = riskItem.getCauseList();
            for (Cause causeListCause : causeList) {
                causeListCause.getRiskItemList().remove(riskItem);
                causeListCause = em.merge(causeListCause);
            }
            List<RiskControl> riskControlList = riskItem.getRiskControlList();
            for (RiskControl riskControlListRiskControl : riskControlList) {
                riskControlListRiskControl.getRiskItemList().remove(riskItem);
                riskControlListRiskControl = em.merge(riskControlListRiskControl);
            }
            List<Hazard> hazardList = riskItem.getHazardList();
            for (Hazard hazardListHazard : hazardList) {
                hazardListHazard.getRiskItemList().remove(riskItem);
                hazardListHazard = em.merge(hazardListHazard);
            }
            List<RiskControl> riskControlList1 = riskItem.getRiskControlList1();
            for (RiskControl riskControlList1RiskControl : riskControlList1) {
                riskControlList1RiskControl.getRiskItemList().remove(riskItem);
                riskControlList1RiskControl = em.merge(riskControlList1RiskControl);
            }
            em.remove(riskItem);
            em.getTransaction().commit();
        } finally {
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
        } finally {
            em.close();
        }
    }

    public RiskItem findRiskItem(RiskItemPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RiskItem.class, id);
        } finally {
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
        } finally {
            em.close();
        }
    }
    
}
