/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.Fmea;
import com.validation.manager.core.db.RiskCategory;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.RiskItemHasRiskCategory;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
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
        if (riskCategory.getFmeaList() == null) {
            riskCategory.setFmeaList(new ArrayList<Fmea>());
        }
        if (riskCategory.getRiskItemHasRiskCategoryList() == null) {
            riskCategory.setRiskItemHasRiskCategoryList(new ArrayList<RiskItemHasRiskCategory>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Fmea> attachedFmeaList = new ArrayList<Fmea>();
            for (Fmea fmeaListFmeaToAttach : riskCategory.getFmeaList()) {
                fmeaListFmeaToAttach = em.getReference(fmeaListFmeaToAttach.getClass(), fmeaListFmeaToAttach.getId());
                attachedFmeaList.add(fmeaListFmeaToAttach);
            }
            riskCategory.setFmeaList(attachedFmeaList);
            List<RiskItemHasRiskCategory> attachedRiskItemHasRiskCategoryList = new ArrayList<RiskItemHasRiskCategory>();
            for (RiskItemHasRiskCategory riskItemHasRiskCategoryListRiskItemHasRiskCategoryToAttach : riskCategory.getRiskItemHasRiskCategoryList()) {
                riskItemHasRiskCategoryListRiskItemHasRiskCategoryToAttach = em.getReference(riskItemHasRiskCategoryListRiskItemHasRiskCategoryToAttach.getClass(), riskItemHasRiskCategoryListRiskItemHasRiskCategoryToAttach.getRiskItemHasRiskCategoryPK());
                attachedRiskItemHasRiskCategoryList.add(riskItemHasRiskCategoryListRiskItemHasRiskCategoryToAttach);
            }
            riskCategory.setRiskItemHasRiskCategoryList(attachedRiskItemHasRiskCategoryList);
            em.persist(riskCategory);
            for (Fmea fmeaListFmea : riskCategory.getFmeaList()) {
                fmeaListFmea.getRiskCategoryList().add(riskCategory);
                fmeaListFmea = em.merge(fmeaListFmea);
            }
            for (RiskItemHasRiskCategory riskItemHasRiskCategoryListRiskItemHasRiskCategory : riskCategory.getRiskItemHasRiskCategoryList()) {
                RiskCategory oldRiskCategoryOfRiskItemHasRiskCategoryListRiskItemHasRiskCategory = riskItemHasRiskCategoryListRiskItemHasRiskCategory.getRiskCategory();
                riskItemHasRiskCategoryListRiskItemHasRiskCategory.setRiskCategory(riskCategory);
                riskItemHasRiskCategoryListRiskItemHasRiskCategory = em.merge(riskItemHasRiskCategoryListRiskItemHasRiskCategory);
                if (oldRiskCategoryOfRiskItemHasRiskCategoryListRiskItemHasRiskCategory != null) {
                    oldRiskCategoryOfRiskItemHasRiskCategoryListRiskItemHasRiskCategory.getRiskItemHasRiskCategoryList().remove(riskItemHasRiskCategoryListRiskItemHasRiskCategory);
                    oldRiskCategoryOfRiskItemHasRiskCategoryListRiskItemHasRiskCategory = em.merge(oldRiskCategoryOfRiskItemHasRiskCategoryListRiskItemHasRiskCategory);
                }
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
            List<Fmea> fmeaListOld = persistentRiskCategory.getFmeaList();
            List<Fmea> fmeaListNew = riskCategory.getFmeaList();
            List<RiskItemHasRiskCategory> riskItemHasRiskCategoryListOld = persistentRiskCategory.getRiskItemHasRiskCategoryList();
            List<RiskItemHasRiskCategory> riskItemHasRiskCategoryListNew = riskCategory.getRiskItemHasRiskCategoryList();
            List<String> illegalOrphanMessages = null;
            for (RiskItemHasRiskCategory riskItemHasRiskCategoryListOldRiskItemHasRiskCategory : riskItemHasRiskCategoryListOld) {
                if (!riskItemHasRiskCategoryListNew.contains(riskItemHasRiskCategoryListOldRiskItemHasRiskCategory)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RiskItemHasRiskCategory " + riskItemHasRiskCategoryListOldRiskItemHasRiskCategory + " since its riskCategory field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Fmea> attachedFmeaListNew = new ArrayList<Fmea>();
            for (Fmea fmeaListNewFmeaToAttach : fmeaListNew) {
                fmeaListNewFmeaToAttach = em.getReference(fmeaListNewFmeaToAttach.getClass(), fmeaListNewFmeaToAttach.getId());
                attachedFmeaListNew.add(fmeaListNewFmeaToAttach);
            }
            fmeaListNew = attachedFmeaListNew;
            riskCategory.setFmeaList(fmeaListNew);
            List<RiskItemHasRiskCategory> attachedRiskItemHasRiskCategoryListNew = new ArrayList<RiskItemHasRiskCategory>();
            for (RiskItemHasRiskCategory riskItemHasRiskCategoryListNewRiskItemHasRiskCategoryToAttach : riskItemHasRiskCategoryListNew) {
                riskItemHasRiskCategoryListNewRiskItemHasRiskCategoryToAttach = em.getReference(riskItemHasRiskCategoryListNewRiskItemHasRiskCategoryToAttach.getClass(), riskItemHasRiskCategoryListNewRiskItemHasRiskCategoryToAttach.getRiskItemHasRiskCategoryPK());
                attachedRiskItemHasRiskCategoryListNew.add(riskItemHasRiskCategoryListNewRiskItemHasRiskCategoryToAttach);
            }
            riskItemHasRiskCategoryListNew = attachedRiskItemHasRiskCategoryListNew;
            riskCategory.setRiskItemHasRiskCategoryList(riskItemHasRiskCategoryListNew);
            riskCategory = em.merge(riskCategory);
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
            for (RiskItemHasRiskCategory riskItemHasRiskCategoryListNewRiskItemHasRiskCategory : riskItemHasRiskCategoryListNew) {
                if (!riskItemHasRiskCategoryListOld.contains(riskItemHasRiskCategoryListNewRiskItemHasRiskCategory)) {
                    RiskCategory oldRiskCategoryOfRiskItemHasRiskCategoryListNewRiskItemHasRiskCategory = riskItemHasRiskCategoryListNewRiskItemHasRiskCategory.getRiskCategory();
                    riskItemHasRiskCategoryListNewRiskItemHasRiskCategory.setRiskCategory(riskCategory);
                    riskItemHasRiskCategoryListNewRiskItemHasRiskCategory = em.merge(riskItemHasRiskCategoryListNewRiskItemHasRiskCategory);
                    if (oldRiskCategoryOfRiskItemHasRiskCategoryListNewRiskItemHasRiskCategory != null && !oldRiskCategoryOfRiskItemHasRiskCategoryListNewRiskItemHasRiskCategory.equals(riskCategory)) {
                        oldRiskCategoryOfRiskItemHasRiskCategoryListNewRiskItemHasRiskCategory.getRiskItemHasRiskCategoryList().remove(riskItemHasRiskCategoryListNewRiskItemHasRiskCategory);
                        oldRiskCategoryOfRiskItemHasRiskCategoryListNewRiskItemHasRiskCategory = em.merge(oldRiskCategoryOfRiskItemHasRiskCategoryListNewRiskItemHasRiskCategory);
                    }
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
            List<RiskItemHasRiskCategory> riskItemHasRiskCategoryListOrphanCheck = riskCategory.getRiskItemHasRiskCategoryList();
            for (RiskItemHasRiskCategory riskItemHasRiskCategoryListOrphanCheckRiskItemHasRiskCategory : riskItemHasRiskCategoryListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This RiskCategory (" + riskCategory + ") cannot be destroyed since the RiskItemHasRiskCategory " + riskItemHasRiskCategoryListOrphanCheckRiskItemHasRiskCategory + " in its riskItemHasRiskCategoryList field has a non-nullable riskCategory field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
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
