/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.FailureMode;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.RiskItem;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class FailureModeJpaController implements Serializable {

    public FailureModeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(FailureMode failureMode) {
        if (failureMode.getRiskItemList() == null) {
            failureMode.setRiskItemList(new ArrayList<RiskItem>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<RiskItem> attachedRiskItemList = new ArrayList<RiskItem>();
            for (RiskItem riskItemListRiskItemToAttach : failureMode.getRiskItemList()) {
                riskItemListRiskItemToAttach = em.getReference(riskItemListRiskItemToAttach.getClass(), riskItemListRiskItemToAttach.getRiskItemPK());
                attachedRiskItemList.add(riskItemListRiskItemToAttach);
            }
            failureMode.setRiskItemList(attachedRiskItemList);
            em.persist(failureMode);
            for (RiskItem riskItemListRiskItem : failureMode.getRiskItemList()) {
                riskItemListRiskItem.getFailureModeList().add(failureMode);
                riskItemListRiskItem = em.merge(riskItemListRiskItem);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(FailureMode failureMode) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FailureMode persistentFailureMode = em.find(FailureMode.class, failureMode.getId());
            List<RiskItem> riskItemListOld = persistentFailureMode.getRiskItemList();
            List<RiskItem> riskItemListNew = failureMode.getRiskItemList();
            List<RiskItem> attachedRiskItemListNew = new ArrayList<RiskItem>();
            for (RiskItem riskItemListNewRiskItemToAttach : riskItemListNew) {
                riskItemListNewRiskItemToAttach = em.getReference(riskItemListNewRiskItemToAttach.getClass(), riskItemListNewRiskItemToAttach.getRiskItemPK());
                attachedRiskItemListNew.add(riskItemListNewRiskItemToAttach);
            }
            riskItemListNew = attachedRiskItemListNew;
            failureMode.setRiskItemList(riskItemListNew);
            failureMode = em.merge(failureMode);
            for (RiskItem riskItemListOldRiskItem : riskItemListOld) {
                if (!riskItemListNew.contains(riskItemListOldRiskItem)) {
                    riskItemListOldRiskItem.getFailureModeList().remove(failureMode);
                    riskItemListOldRiskItem = em.merge(riskItemListOldRiskItem);
                }
            }
            for (RiskItem riskItemListNewRiskItem : riskItemListNew) {
                if (!riskItemListOld.contains(riskItemListNewRiskItem)) {
                    riskItemListNewRiskItem.getFailureModeList().add(failureMode);
                    riskItemListNewRiskItem = em.merge(riskItemListNewRiskItem);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = failureMode.getId();
                if (findFailureMode(id) == null) {
                    throw new NonexistentEntityException("The failureMode with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FailureMode failureMode;
            try {
                failureMode = em.getReference(FailureMode.class, id);
                failureMode.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The failureMode with id " + id + " no longer exists.", enfe);
            }
            List<RiskItem> riskItemList = failureMode.getRiskItemList();
            for (RiskItem riskItemListRiskItem : riskItemList) {
                riskItemListRiskItem.getFailureModeList().remove(failureMode);
                riskItemListRiskItem = em.merge(riskItemListRiskItem);
            }
            em.remove(failureMode);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<FailureMode> findFailureModeEntities() {
        return findFailureModeEntities(true, -1, -1);
    }

    public List<FailureMode> findFailureModeEntities(int maxResults, int firstResult) {
        return findFailureModeEntities(false, maxResults, firstResult);
    }

    private List<FailureMode> findFailureModeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(FailureMode.class));
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

    public FailureMode findFailureMode(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(FailureMode.class, id);
        } finally {
            em.close();
        }
    }

    public int getFailureModeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<FailureMode> rt = cq.from(FailureMode.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
