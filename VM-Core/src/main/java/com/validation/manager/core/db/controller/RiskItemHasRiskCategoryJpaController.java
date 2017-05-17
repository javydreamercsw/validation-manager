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
import com.validation.manager.core.db.RiskCategory;
import com.validation.manager.core.db.RiskItem;
import com.validation.manager.core.db.RiskItemHasRiskCategory;
import com.validation.manager.core.db.RiskItemHasRiskCategoryPK;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RiskItemHasRiskCategoryJpaController implements Serializable {

    public RiskItemHasRiskCategoryJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RiskItemHasRiskCategory riskItemHasRiskCategory) throws PreexistingEntityException, Exception {
        if (riskItemHasRiskCategory.getRiskItemHasRiskCategoryPK() == null) {
            riskItemHasRiskCategory.setRiskItemHasRiskCategoryPK(new RiskItemHasRiskCategoryPK());
        }
        riskItemHasRiskCategory.getRiskItemHasRiskCategoryPK().setRiskItemId(riskItemHasRiskCategory.getRiskItem().getRiskItemPK().getId());
        riskItemHasRiskCategory.getRiskItemHasRiskCategoryPK().setRiskitemFMEAid(riskItemHasRiskCategory.getRiskItem().getRiskItemPK().getFMEAid());
        riskItemHasRiskCategory.getRiskItemHasRiskCategoryPK().setRiskCategoryId(riskItemHasRiskCategory.getRiskCategory().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskCategory riskCategory = riskItemHasRiskCategory.getRiskCategory();
            if (riskCategory != null) {
                riskCategory = em.getReference(riskCategory.getClass(), riskCategory.getId());
                riskItemHasRiskCategory.setRiskCategory(riskCategory);
            }
            RiskItem riskItem = riskItemHasRiskCategory.getRiskItem();
            if (riskItem != null) {
                riskItem = em.getReference(riskItem.getClass(), riskItem.getRiskItemPK());
                riskItemHasRiskCategory.setRiskItem(riskItem);
            }
            em.persist(riskItemHasRiskCategory);
            if (riskCategory != null) {
                riskCategory.getRiskItemHasRiskCategoryList().add(riskItemHasRiskCategory);
                riskCategory = em.merge(riskCategory);
            }
            if (riskItem != null) {
                riskItem.getRiskItemHasRiskCategoryList().add(riskItemHasRiskCategory);
                riskItem = em.merge(riskItem);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findRiskItemHasRiskCategory(riskItemHasRiskCategory.getRiskItemHasRiskCategoryPK()) != null) {
                throw new PreexistingEntityException("RiskItemHasRiskCategory " + riskItemHasRiskCategory + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RiskItemHasRiskCategory riskItemHasRiskCategory) throws NonexistentEntityException, Exception {
        riskItemHasRiskCategory.getRiskItemHasRiskCategoryPK().setRiskItemId(riskItemHasRiskCategory.getRiskItem().getRiskItemPK().getId());
        riskItemHasRiskCategory.getRiskItemHasRiskCategoryPK().setRiskitemFMEAid(riskItemHasRiskCategory.getRiskItem().getRiskItemPK().getFMEAid());
        riskItemHasRiskCategory.getRiskItemHasRiskCategoryPK().setRiskCategoryId(riskItemHasRiskCategory.getRiskCategory().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskItemHasRiskCategory persistentRiskItemHasRiskCategory = em.find(RiskItemHasRiskCategory.class, riskItemHasRiskCategory.getRiskItemHasRiskCategoryPK());
            RiskCategory riskCategoryOld = persistentRiskItemHasRiskCategory.getRiskCategory();
            RiskCategory riskCategoryNew = riskItemHasRiskCategory.getRiskCategory();
            RiskItem riskItemOld = persistentRiskItemHasRiskCategory.getRiskItem();
            RiskItem riskItemNew = riskItemHasRiskCategory.getRiskItem();
            if (riskCategoryNew != null) {
                riskCategoryNew = em.getReference(riskCategoryNew.getClass(), riskCategoryNew.getId());
                riskItemHasRiskCategory.setRiskCategory(riskCategoryNew);
            }
            if (riskItemNew != null) {
                riskItemNew = em.getReference(riskItemNew.getClass(), riskItemNew.getRiskItemPK());
                riskItemHasRiskCategory.setRiskItem(riskItemNew);
            }
            riskItemHasRiskCategory = em.merge(riskItemHasRiskCategory);
            if (riskCategoryOld != null && !riskCategoryOld.equals(riskCategoryNew)) {
                riskCategoryOld.getRiskItemHasRiskCategoryList().remove(riskItemHasRiskCategory);
                riskCategoryOld = em.merge(riskCategoryOld);
            }
            if (riskCategoryNew != null && !riskCategoryNew.equals(riskCategoryOld)) {
                riskCategoryNew.getRiskItemHasRiskCategoryList().add(riskItemHasRiskCategory);
                riskCategoryNew = em.merge(riskCategoryNew);
            }
            if (riskItemOld != null && !riskItemOld.equals(riskItemNew)) {
                riskItemOld.getRiskItemHasRiskCategoryList().remove(riskItemHasRiskCategory);
                riskItemOld = em.merge(riskItemOld);
            }
            if (riskItemNew != null && !riskItemNew.equals(riskItemOld)) {
                riskItemNew.getRiskItemHasRiskCategoryList().add(riskItemHasRiskCategory);
                riskItemNew = em.merge(riskItemNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RiskItemHasRiskCategoryPK id = riskItemHasRiskCategory.getRiskItemHasRiskCategoryPK();
                if (findRiskItemHasRiskCategory(id) == null) {
                    throw new NonexistentEntityException("The riskItemHasRiskCategory with id " + id + " no longer exists.");
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

    public void destroy(RiskItemHasRiskCategoryPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskItemHasRiskCategory riskItemHasRiskCategory;
            try {
                riskItemHasRiskCategory = em.getReference(RiskItemHasRiskCategory.class, id);
                riskItemHasRiskCategory.getRiskItemHasRiskCategoryPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The riskItemHasRiskCategory with id " + id + " no longer exists.", enfe);
            }
            RiskCategory riskCategory = riskItemHasRiskCategory.getRiskCategory();
            if (riskCategory != null) {
                riskCategory.getRiskItemHasRiskCategoryList().remove(riskItemHasRiskCategory);
                riskCategory = em.merge(riskCategory);
            }
            RiskItem riskItem = riskItemHasRiskCategory.getRiskItem();
            if (riskItem != null) {
                riskItem.getRiskItemHasRiskCategoryList().remove(riskItemHasRiskCategory);
                riskItem = em.merge(riskItem);
            }
            em.remove(riskItemHasRiskCategory);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RiskItemHasRiskCategory> findRiskItemHasRiskCategoryEntities() {
        return findRiskItemHasRiskCategoryEntities(true, -1, -1);
    }

    public List<RiskItemHasRiskCategory> findRiskItemHasRiskCategoryEntities(int maxResults, int firstResult) {
        return findRiskItemHasRiskCategoryEntities(false, maxResults, firstResult);
    }

    private List<RiskItemHasRiskCategory> findRiskItemHasRiskCategoryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RiskItemHasRiskCategory.class));
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

    public RiskItemHasRiskCategory findRiskItemHasRiskCategory(RiskItemHasRiskCategoryPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RiskItemHasRiskCategory.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getRiskItemHasRiskCategoryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RiskItemHasRiskCategory> rt = cq.from(RiskItemHasRiskCategory.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
