/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.fmea.FMEA;
import com.validation.manager.core.db.fmea.RiskCategory;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class FMEAJpaController implements Serializable {

    public FMEAJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(FMEA FMEA) {
        if (FMEA.getRiskCategoryList() == null) {
            FMEA.setRiskCategoryList(new ArrayList<RiskCategory>());
        }
        if (FMEA.getFMEAList() == null) {
            FMEA.setFMEAList(new ArrayList<FMEA>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FMEA parent = FMEA.getParent();
            if (parent != null) {
                parent = em.getReference(parent.getClass(), parent.getId());
                FMEA.setParent(parent);
            }
            List<RiskCategory> attachedRiskCategoryList = new ArrayList<RiskCategory>();
            for (RiskCategory riskCategoryListRiskCategoryToAttach : FMEA.getRiskCategoryList()) {
                riskCategoryListRiskCategoryToAttach = em.getReference(riskCategoryListRiskCategoryToAttach.getClass(), riskCategoryListRiskCategoryToAttach.getId());
                attachedRiskCategoryList.add(riskCategoryListRiskCategoryToAttach);
            }
            FMEA.setRiskCategoryList(attachedRiskCategoryList);
            List<FMEA> attachedFMEAList = new ArrayList<FMEA>();
            for (FMEA FMEAListFMEAToAttach : FMEA.getFMEAList()) {
                FMEAListFMEAToAttach = em.getReference(FMEAListFMEAToAttach.getClass(), FMEAListFMEAToAttach.getId());
                attachedFMEAList.add(FMEAListFMEAToAttach);
            }
            FMEA.setFMEAList(attachedFMEAList);
            em.persist(FMEA);
            if (parent != null) {
                parent.getFMEAList().add(FMEA);
                parent = em.merge(parent);
            }
            for (RiskCategory riskCategoryListRiskCategory : FMEA.getRiskCategoryList()) {
                riskCategoryListRiskCategory.getFMEAList().add(FMEA);
                riskCategoryListRiskCategory = em.merge(riskCategoryListRiskCategory);
            }
            for (FMEA FMEAListFMEA : FMEA.getFMEAList()) {
                FMEA oldParentOfFMEAListFMEA = FMEAListFMEA.getParent();
                FMEAListFMEA.setParent(FMEA);
                FMEAListFMEA = em.merge(FMEAListFMEA);
                if (oldParentOfFMEAListFMEA != null) {
                    oldParentOfFMEAListFMEA.getFMEAList().remove(FMEAListFMEA);
                    oldParentOfFMEAListFMEA = em.merge(oldParentOfFMEAListFMEA);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(FMEA FMEA) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FMEA persistentFMEA = em.find(FMEA.class, FMEA.getId());
            FMEA parentOld = persistentFMEA.getParent();
            FMEA parentNew = FMEA.getParent();
            List<RiskCategory> riskCategoryListOld = persistentFMEA.getRiskCategoryList();
            List<RiskCategory> riskCategoryListNew = FMEA.getRiskCategoryList();
            List<FMEA> FMEAListOld = persistentFMEA.getFMEAList();
            List<FMEA> FMEAListNew = FMEA.getFMEAList();
            if (parentNew != null) {
                parentNew = em.getReference(parentNew.getClass(), parentNew.getId());
                FMEA.setParent(parentNew);
            }
            List<RiskCategory> attachedRiskCategoryListNew = new ArrayList<RiskCategory>();
            for (RiskCategory riskCategoryListNewRiskCategoryToAttach : riskCategoryListNew) {
                riskCategoryListNewRiskCategoryToAttach = em.getReference(riskCategoryListNewRiskCategoryToAttach.getClass(), riskCategoryListNewRiskCategoryToAttach.getId());
                attachedRiskCategoryListNew.add(riskCategoryListNewRiskCategoryToAttach);
            }
            riskCategoryListNew = attachedRiskCategoryListNew;
            FMEA.setRiskCategoryList(riskCategoryListNew);
            List<FMEA> attachedFMEAListNew = new ArrayList<FMEA>();
            for (FMEA FMEAListNewFMEAToAttach : FMEAListNew) {
                FMEAListNewFMEAToAttach = em.getReference(FMEAListNewFMEAToAttach.getClass(), FMEAListNewFMEAToAttach.getId());
                attachedFMEAListNew.add(FMEAListNewFMEAToAttach);
            }
            FMEAListNew = attachedFMEAListNew;
            FMEA.setFMEAList(FMEAListNew);
            FMEA = em.merge(FMEA);
            if (parentOld != null && !parentOld.equals(parentNew)) {
                parentOld.getFMEAList().remove(FMEA);
                parentOld = em.merge(parentOld);
            }
            if (parentNew != null && !parentNew.equals(parentOld)) {
                parentNew.getFMEAList().add(FMEA);
                parentNew = em.merge(parentNew);
            }
            for (RiskCategory riskCategoryListOldRiskCategory : riskCategoryListOld) {
                if (!riskCategoryListNew.contains(riskCategoryListOldRiskCategory)) {
                    riskCategoryListOldRiskCategory.getFMEAList().remove(FMEA);
                    riskCategoryListOldRiskCategory = em.merge(riskCategoryListOldRiskCategory);
                }
            }
            for (RiskCategory riskCategoryListNewRiskCategory : riskCategoryListNew) {
                if (!riskCategoryListOld.contains(riskCategoryListNewRiskCategory)) {
                    riskCategoryListNewRiskCategory.getFMEAList().add(FMEA);
                    riskCategoryListNewRiskCategory = em.merge(riskCategoryListNewRiskCategory);
                }
            }
            for (FMEA FMEAListOldFMEA : FMEAListOld) {
                if (!FMEAListNew.contains(FMEAListOldFMEA)) {
                    FMEAListOldFMEA.setParent(null);
                    FMEAListOldFMEA = em.merge(FMEAListOldFMEA);
                }
            }
            for (FMEA FMEAListNewFMEA : FMEAListNew) {
                if (!FMEAListOld.contains(FMEAListNewFMEA)) {
                    FMEA oldParentOfFMEAListNewFMEA = FMEAListNewFMEA.getParent();
                    FMEAListNewFMEA.setParent(FMEA);
                    FMEAListNewFMEA = em.merge(FMEAListNewFMEA);
                    if (oldParentOfFMEAListNewFMEA != null && !oldParentOfFMEAListNewFMEA.equals(FMEA)) {
                        oldParentOfFMEAListNewFMEA.getFMEAList().remove(FMEAListNewFMEA);
                        oldParentOfFMEAListNewFMEA = em.merge(oldParentOfFMEAListNewFMEA);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = FMEA.getId();
                if (findFMEA(id) == null) {
                    throw new NonexistentEntityException("The fMEA with id " + id + " no longer exists.");
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
            FMEA FMEA;
            try {
                FMEA = em.getReference(FMEA.class, id);
                FMEA.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The FMEA with id " + id + " no longer exists.", enfe);
            }
            FMEA parent = FMEA.getParent();
            if (parent != null) {
                parent.getFMEAList().remove(FMEA);
                parent = em.merge(parent);
            }
            List<RiskCategory> riskCategoryList = FMEA.getRiskCategoryList();
            for (RiskCategory riskCategoryListRiskCategory : riskCategoryList) {
                riskCategoryListRiskCategory.getFMEAList().remove(FMEA);
                riskCategoryListRiskCategory = em.merge(riskCategoryListRiskCategory);
            }
            List<FMEA> FMEAList = FMEA.getFMEAList();
            for (FMEA FMEAListFMEA : FMEAList) {
                FMEAListFMEA.setParent(null);
                FMEAListFMEA = em.merge(FMEAListFMEA);
            }
            em.remove(FMEA);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<FMEA> findFMEAEntities() {
        return findFMEAEntities(true, -1, -1);
    }

    public List<FMEA> findFMEAEntities(int maxResults, int firstResult) {
        return findFMEAEntities(false, maxResults, firstResult);
    }

    private List<FMEA> findFMEAEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(FMEA.class));
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

    public FMEA findFMEA(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(FMEA.class, id);
        } finally {
            em.close();
        }
    }

    public int getFMEACount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<FMEA> rt = cq.from(FMEA.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
