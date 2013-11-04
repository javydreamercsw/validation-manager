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
import com.validation.manager.core.db.RiskItem;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
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

    public void create(Fmea fmea) throws PreexistingEntityException, Exception {
        if (fmea.getRiskCategoryList() == null) {
            fmea.setRiskCategoryList(new ArrayList<RiskCategory>());
        }
        if (fmea.getRiskItemList() == null) {
            fmea.setRiskItemList(new ArrayList<RiskItem>());
        }
        if (fmea.getFmeaList() == null) {
            fmea.setFmeaList(new ArrayList<Fmea>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Fmea parent = fmea.getParent();
            if (parent != null) {
                parent = em.getReference(parent.getClass(), parent.getId());
                fmea.setParent(parent);
            }
            List<RiskCategory> attachedRiskCategoryList = new ArrayList<RiskCategory>();
            for (RiskCategory riskCategoryListRiskCategoryToAttach : fmea.getRiskCategoryList()) {
                riskCategoryListRiskCategoryToAttach = em.getReference(riskCategoryListRiskCategoryToAttach.getClass(), riskCategoryListRiskCategoryToAttach.getId());
                attachedRiskCategoryList.add(riskCategoryListRiskCategoryToAttach);
            }
            fmea.setRiskCategoryList(attachedRiskCategoryList);
            List<RiskItem> attachedRiskItemList = new ArrayList<RiskItem>();
            for (RiskItem riskItemListRiskItemToAttach : fmea.getRiskItemList()) {
                riskItemListRiskItemToAttach = em.getReference(riskItemListRiskItemToAttach.getClass(), riskItemListRiskItemToAttach.getRiskItemPK());
                attachedRiskItemList.add(riskItemListRiskItemToAttach);
            }
            fmea.setRiskItemList(attachedRiskItemList);
            List<Fmea> attachedFmeaList = new ArrayList<Fmea>();
            for (Fmea fmeaListFmeaToAttach : fmea.getFmeaList()) {
                fmeaListFmeaToAttach = em.getReference(fmeaListFmeaToAttach.getClass(), fmeaListFmeaToAttach.getId());
                attachedFmeaList.add(fmeaListFmeaToAttach);
            }
            fmea.setFmeaList(attachedFmeaList);
            em.persist(fmea);
            if (parent != null) {
                parent.getFmeaList().add(fmea);
                parent = em.merge(parent);
            }
            for (RiskCategory riskCategoryListRiskCategory : fmea.getRiskCategoryList()) {
                riskCategoryListRiskCategory.getFmeaList().add(fmea);
                riskCategoryListRiskCategory = em.merge(riskCategoryListRiskCategory);
            }
            for (RiskItem riskItemListRiskItem : fmea.getRiskItemList()) {
                Fmea oldFmeaOfRiskItemListRiskItem = riskItemListRiskItem.getFmea();
                riskItemListRiskItem.setFmea(fmea);
                riskItemListRiskItem = em.merge(riskItemListRiskItem);
                if (oldFmeaOfRiskItemListRiskItem != null) {
                    oldFmeaOfRiskItemListRiskItem.getRiskItemList().remove(riskItemListRiskItem);
                    oldFmeaOfRiskItemListRiskItem = em.merge(oldFmeaOfRiskItemListRiskItem);
                }
            }
            for (Fmea fmeaListFmea : fmea.getFmeaList()) {
                Fmea oldParentOfFmeaListFmea = fmeaListFmea.getParent();
                fmeaListFmea.setParent(fmea);
                fmeaListFmea = em.merge(fmeaListFmea);
                if (oldParentOfFmeaListFmea != null) {
                    oldParentOfFmeaListFmea.getFmeaList().remove(fmeaListFmea);
                    oldParentOfFmeaListFmea = em.merge(oldParentOfFmeaListFmea);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findFmea(fmea.getId()) != null) {
                throw new PreexistingEntityException("Fmea " + fmea + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Fmea fmea) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Fmea persistentFmea = em.find(Fmea.class, fmea.getId());
            Fmea parentOld = persistentFmea.getParent();
            Fmea parentNew = fmea.getParent();
            List<RiskCategory> riskCategoryListOld = persistentFmea.getRiskCategoryList();
            List<RiskCategory> riskCategoryListNew = fmea.getRiskCategoryList();
            List<RiskItem> riskItemListOld = persistentFmea.getRiskItemList();
            List<RiskItem> riskItemListNew = fmea.getRiskItemList();
            List<Fmea> fmeaListOld = persistentFmea.getFmeaList();
            List<Fmea> fmeaListNew = fmea.getFmeaList();
            List<String> illegalOrphanMessages = null;
            for (RiskItem riskItemListOldRiskItem : riskItemListOld) {
                if (!riskItemListNew.contains(riskItemListOldRiskItem)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RiskItem " + riskItemListOldRiskItem + " since its fmea field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (parentNew != null) {
                parentNew = em.getReference(parentNew.getClass(), parentNew.getId());
                fmea.setParent(parentNew);
            }
            List<RiskCategory> attachedRiskCategoryListNew = new ArrayList<RiskCategory>();
            for (RiskCategory riskCategoryListNewRiskCategoryToAttach : riskCategoryListNew) {
                riskCategoryListNewRiskCategoryToAttach = em.getReference(riskCategoryListNewRiskCategoryToAttach.getClass(), riskCategoryListNewRiskCategoryToAttach.getId());
                attachedRiskCategoryListNew.add(riskCategoryListNewRiskCategoryToAttach);
            }
            riskCategoryListNew = attachedRiskCategoryListNew;
            fmea.setRiskCategoryList(riskCategoryListNew);
            List<RiskItem> attachedRiskItemListNew = new ArrayList<RiskItem>();
            for (RiskItem riskItemListNewRiskItemToAttach : riskItemListNew) {
                riskItemListNewRiskItemToAttach = em.getReference(riskItemListNewRiskItemToAttach.getClass(), riskItemListNewRiskItemToAttach.getRiskItemPK());
                attachedRiskItemListNew.add(riskItemListNewRiskItemToAttach);
            }
            riskItemListNew = attachedRiskItemListNew;
            fmea.setRiskItemList(riskItemListNew);
            List<Fmea> attachedFmeaListNew = new ArrayList<Fmea>();
            for (Fmea fmeaListNewFmeaToAttach : fmeaListNew) {
                fmeaListNewFmeaToAttach = em.getReference(fmeaListNewFmeaToAttach.getClass(), fmeaListNewFmeaToAttach.getId());
                attachedFmeaListNew.add(fmeaListNewFmeaToAttach);
            }
            fmeaListNew = attachedFmeaListNew;
            fmea.setFmeaList(fmeaListNew);
            fmea = em.merge(fmea);
            if (parentOld != null && !parentOld.equals(parentNew)) {
                parentOld.getFmeaList().remove(fmea);
                parentOld = em.merge(parentOld);
            }
            if (parentNew != null && !parentNew.equals(parentOld)) {
                parentNew.getFmeaList().add(fmea);
                parentNew = em.merge(parentNew);
            }
            for (RiskCategory riskCategoryListOldRiskCategory : riskCategoryListOld) {
                if (!riskCategoryListNew.contains(riskCategoryListOldRiskCategory)) {
                    riskCategoryListOldRiskCategory.getFmeaList().remove(fmea);
                    riskCategoryListOldRiskCategory = em.merge(riskCategoryListOldRiskCategory);
                }
            }
            for (RiskCategory riskCategoryListNewRiskCategory : riskCategoryListNew) {
                if (!riskCategoryListOld.contains(riskCategoryListNewRiskCategory)) {
                    riskCategoryListNewRiskCategory.getFmeaList().add(fmea);
                    riskCategoryListNewRiskCategory = em.merge(riskCategoryListNewRiskCategory);
                }
            }
            for (RiskItem riskItemListNewRiskItem : riskItemListNew) {
                if (!riskItemListOld.contains(riskItemListNewRiskItem)) {
                    Fmea oldFmeaOfRiskItemListNewRiskItem = riskItemListNewRiskItem.getFmea();
                    riskItemListNewRiskItem.setFmea(fmea);
                    riskItemListNewRiskItem = em.merge(riskItemListNewRiskItem);
                    if (oldFmeaOfRiskItemListNewRiskItem != null && !oldFmeaOfRiskItemListNewRiskItem.equals(fmea)) {
                        oldFmeaOfRiskItemListNewRiskItem.getRiskItemList().remove(riskItemListNewRiskItem);
                        oldFmeaOfRiskItemListNewRiskItem = em.merge(oldFmeaOfRiskItemListNewRiskItem);
                    }
                }
            }
            for (Fmea fmeaListOldFmea : fmeaListOld) {
                if (!fmeaListNew.contains(fmeaListOldFmea)) {
                    fmeaListOldFmea.setParent(null);
                    fmeaListOldFmea = em.merge(fmeaListOldFmea);
                }
            }
            for (Fmea fmeaListNewFmea : fmeaListNew) {
                if (!fmeaListOld.contains(fmeaListNewFmea)) {
                    Fmea oldParentOfFmeaListNewFmea = fmeaListNewFmea.getParent();
                    fmeaListNewFmea.setParent(fmea);
                    fmeaListNewFmea = em.merge(fmeaListNewFmea);
                    if (oldParentOfFmeaListNewFmea != null && !oldParentOfFmeaListNewFmea.equals(fmea)) {
                        oldParentOfFmeaListNewFmea.getFmeaList().remove(fmeaListNewFmea);
                        oldParentOfFmeaListNewFmea = em.merge(oldParentOfFmeaListNewFmea);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = fmea.getId();
                if (findFmea(id) == null) {
                    throw new NonexistentEntityException("The fmea with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
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
            Fmea fmea;
            try {
                fmea = em.getReference(Fmea.class, id);
                fmea.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The fmea with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RiskItem> riskItemListOrphanCheck = fmea.getRiskItemList();
            for (RiskItem riskItemListOrphanCheckRiskItem : riskItemListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Fmea (" + fmea + ") cannot be destroyed since the RiskItem " + riskItemListOrphanCheckRiskItem + " in its riskItemList field has a non-nullable fmea field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Fmea parent = fmea.getParent();
            if (parent != null) {
                parent.getFmeaList().remove(fmea);
                parent = em.merge(parent);
            }
            List<RiskCategory> riskCategoryList = fmea.getRiskCategoryList();
            for (RiskCategory riskCategoryListRiskCategory : riskCategoryList) {
                riskCategoryListRiskCategory.getFmeaList().remove(fmea);
                riskCategoryListRiskCategory = em.merge(riskCategoryListRiskCategory);
            }
            List<Fmea> fmeaList = fmea.getFmeaList();
            for (Fmea fmeaListFmea : fmeaList) {
                fmeaListFmea.setParent(null);
                fmeaListFmea = em.merge(fmeaListFmea);
            }
            em.remove(fmea);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Fmea> findFmeaEntities() {
        return findFmeaEntities(true, -1, -1);
    }

    public List<Fmea> findFmeaEntities(int maxResults, int firstResult) {
        return findFmeaEntities(false, maxResults, firstResult);
    }

    private List<Fmea> findFmeaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Fmea.class));
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

    public Fmea findFmea(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Fmea.class, id);
        } finally {
            em.close();
        }
    }

    public int getFmeaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Fmea> rt = cq.from(Fmea.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
