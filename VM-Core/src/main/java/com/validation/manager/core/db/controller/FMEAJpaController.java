package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.Fmea;
import com.validation.manager.core.db.RiskCategory;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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

    public void create(Fmea Fmea) {
        if (Fmea.getRiskCategoryList() == null) {
            Fmea.setRiskCategoryList(new ArrayList<RiskCategory>());
        }
        if (Fmea.getFmeaList() == null) {
            Fmea.setFmeaList(new ArrayList<Fmea>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Fmea parent = Fmea.getParent();
            if (parent != null) {
                parent = em.getReference(parent.getClass(), parent.getId());
                Fmea.setParent(parent);
            }
            List<RiskCategory> attachedRiskCategoryList = new ArrayList<RiskCategory>();
            for (RiskCategory riskCategoryListRiskCategoryToAttach : Fmea.getRiskCategoryList()) {
                riskCategoryListRiskCategoryToAttach = em.getReference(riskCategoryListRiskCategoryToAttach.getClass(), riskCategoryListRiskCategoryToAttach.getId());
                attachedRiskCategoryList.add(riskCategoryListRiskCategoryToAttach);
            }
            Fmea.setRiskCategoryList(attachedRiskCategoryList);
            List<Fmea> attachedFmeaList = new ArrayList<Fmea>();
            for (Fmea FmeaListFmeaToAttach : Fmea.getFmeaList()) {
                FmeaListFmeaToAttach = em.getReference(FmeaListFmeaToAttach.getClass(), FmeaListFmeaToAttach.getId());
                attachedFmeaList.add(FmeaListFmeaToAttach);
            }
            Fmea.setFmeaList(attachedFmeaList);
            em.persist(Fmea);
            if (parent != null) {
                parent.getFmeaList().add(Fmea);
                parent = em.merge(parent);
            }
            for (RiskCategory riskCategoryListRiskCategory : Fmea.getRiskCategoryList()) {
                riskCategoryListRiskCategory.getFmeaList().add(Fmea);
                riskCategoryListRiskCategory = em.merge(riskCategoryListRiskCategory);
            }
            for (Fmea FmeaListFmea : Fmea.getFmeaList()) {
                Fmea oldParentOfFmeaListFmea = FmeaListFmea.getParent();
                FmeaListFmea.setParent(Fmea);
                FmeaListFmea = em.merge(FmeaListFmea);
                if (oldParentOfFmeaListFmea != null) {
                    oldParentOfFmeaListFmea.getFmeaList().remove(FmeaListFmea);
                    oldParentOfFmeaListFmea = em.merge(oldParentOfFmeaListFmea);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Fmea Fmea) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Fmea persistentFmea = em.find(Fmea.class, Fmea.getId());
            Fmea parentOld = persistentFmea.getParent();
            Fmea parentNew = Fmea.getParent();
            List<RiskCategory> riskCategoryListOld = persistentFmea.getRiskCategoryList();
            List<RiskCategory> riskCategoryListNew = Fmea.getRiskCategoryList();
            List<Fmea> FmeaListOld = persistentFmea.getFmeaList();
            List<Fmea> FmeaListNew = Fmea.getFmeaList();
            if (parentNew != null) {
                parentNew = em.getReference(parentNew.getClass(), parentNew.getId());
                Fmea.setParent(parentNew);
            }
            List<RiskCategory> attachedRiskCategoryListNew = new ArrayList<RiskCategory>();
            for (RiskCategory riskCategoryListNewRiskCategoryToAttach : riskCategoryListNew) {
                riskCategoryListNewRiskCategoryToAttach = em.getReference(riskCategoryListNewRiskCategoryToAttach.getClass(), riskCategoryListNewRiskCategoryToAttach.getId());
                attachedRiskCategoryListNew.add(riskCategoryListNewRiskCategoryToAttach);
            }
            riskCategoryListNew = attachedRiskCategoryListNew;
            Fmea.setRiskCategoryList(riskCategoryListNew);
            List<Fmea> attachedFmeaListNew = new ArrayList<Fmea>();
            for (Fmea FmeaListNewFmeaToAttach : FmeaListNew) {
                FmeaListNewFmeaToAttach = em.getReference(FmeaListNewFmeaToAttach.getClass(), FmeaListNewFmeaToAttach.getId());
                attachedFmeaListNew.add(FmeaListNewFmeaToAttach);
            }
            FmeaListNew = attachedFmeaListNew;
            Fmea.setFmeaList(FmeaListNew);
            Fmea = em.merge(Fmea);
            if (parentOld != null && !parentOld.equals(parentNew)) {
                parentOld.getFmeaList().remove(Fmea);
                parentOld = em.merge(parentOld);
            }
            if (parentNew != null && !parentNew.equals(parentOld)) {
                parentNew.getFmeaList().add(Fmea);
                parentNew = em.merge(parentNew);
            }
            for (RiskCategory riskCategoryListOldRiskCategory : riskCategoryListOld) {
                if (!riskCategoryListNew.contains(riskCategoryListOldRiskCategory)) {
                    riskCategoryListOldRiskCategory.getFmeaList().remove(Fmea);
                    riskCategoryListOldRiskCategory = em.merge(riskCategoryListOldRiskCategory);
                }
            }
            for (RiskCategory riskCategoryListNewRiskCategory : riskCategoryListNew) {
                if (!riskCategoryListOld.contains(riskCategoryListNewRiskCategory)) {
                    riskCategoryListNewRiskCategory.getFmeaList().add(Fmea);
                    riskCategoryListNewRiskCategory = em.merge(riskCategoryListNewRiskCategory);
                }
            }
            for (Fmea FmeaListOldFmea : FmeaListOld) {
                if (!FmeaListNew.contains(FmeaListOldFmea)) {
                    FmeaListOldFmea.setParent(null);
                    FmeaListOldFmea = em.merge(FmeaListOldFmea);
                }
            }
            for (Fmea FmeaListNewFmea : FmeaListNew) {
                if (!FmeaListOld.contains(FmeaListNewFmea)) {
                    Fmea oldParentOfFmeaListNewFmea = FmeaListNewFmea.getParent();
                    FmeaListNewFmea.setParent(Fmea);
                    FmeaListNewFmea = em.merge(FmeaListNewFmea);
                    if (oldParentOfFmeaListNewFmea != null && !oldParentOfFmeaListNewFmea.equals(Fmea)) {
                        oldParentOfFmeaListNewFmea.getFmeaList().remove(FmeaListNewFmea);
                        oldParentOfFmeaListNewFmea = em.merge(oldParentOfFmeaListNewFmea);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = Fmea.getId();
                if (findFmea(id) == null) {
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
            Fmea Fmea;
            try {
                Fmea = em.getReference(Fmea.class, id);
                Fmea.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The Fmea with id " + id + " no longer exists.", enfe);
            }
            Fmea parent = Fmea.getParent();
            if (parent != null) {
                parent.getFmeaList().remove(Fmea);
                parent = em.merge(parent);
            }
            List<RiskCategory> riskCategoryList = Fmea.getRiskCategoryList();
            for (RiskCategory riskCategoryListRiskCategory : riskCategoryList) {
                riskCategoryListRiskCategory.getFmeaList().remove(Fmea);
                riskCategoryListRiskCategory = em.merge(riskCategoryListRiskCategory);
            }
            List<Fmea> FmeaList = Fmea.getFmeaList();
            for (Fmea FmeaListFmea : FmeaList) {
                FmeaListFmea.setParent(null);
                FmeaListFmea = em.merge(FmeaListFmea);
            }
            em.remove(Fmea);
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
