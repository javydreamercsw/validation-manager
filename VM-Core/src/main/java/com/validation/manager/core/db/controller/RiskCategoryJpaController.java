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

import com.validation.manager.core.db.RiskCategory;
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
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
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
        if (riskCategory.getRiskItemList() == null) {
            riskCategory.setRiskItemList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<RiskItem> attachedRiskItemList = new ArrayList<>();
            for (RiskItem riskItemListRiskItemToAttach : riskCategory.getRiskItemList()) {
                riskItemListRiskItemToAttach = em.getReference(riskItemListRiskItemToAttach.getClass(), riskItemListRiskItemToAttach.getRiskItemPK());
                attachedRiskItemList.add(riskItemListRiskItemToAttach);
            }
            riskCategory.setRiskItemList(attachedRiskItemList);
            em.persist(riskCategory);
            for (RiskItem riskItemListRiskItem : riskCategory.getRiskItemList()) {
                riskItemListRiskItem.getRiskCategoryList().add(riskCategory);
                riskItemListRiskItem = em.merge(riskItemListRiskItem);
            }
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RiskCategory riskCategory) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskCategory persistentRiskCategory = em.find(RiskCategory.class, riskCategory.getId());
            List<RiskItem> riskItemListOld = persistentRiskCategory.getRiskItemList();
            List<RiskItem> riskItemListNew = riskCategory.getRiskItemList();
            List<RiskItem> attachedRiskItemListNew = new ArrayList<>();
            for (RiskItem riskItemListNewRiskItemToAttach : riskItemListNew) {
                riskItemListNewRiskItemToAttach = em.getReference(riskItemListNewRiskItemToAttach.getClass(), riskItemListNewRiskItemToAttach.getRiskItemPK());
                attachedRiskItemListNew.add(riskItemListNewRiskItemToAttach);
            }
            riskItemListNew = attachedRiskItemListNew;
            riskCategory.setRiskItemList(riskItemListNew);
            riskCategory = em.merge(riskCategory);
            for (RiskItem riskItemListOldRiskItem : riskItemListOld) {
                if (!riskItemListNew.contains(riskItemListOldRiskItem)) {
                    riskItemListOldRiskItem.getRiskCategoryList().remove(riskCategory);
                    riskItemListOldRiskItem = em.merge(riskItemListOldRiskItem);
                }
            }
            for (RiskItem riskItemListNewRiskItem : riskItemListNew) {
                if (!riskItemListOld.contains(riskItemListNewRiskItem)) {
                    riskItemListNewRiskItem.getRiskCategoryList().add(riskCategory);
                    riskItemListNewRiskItem = em.merge(riskItemListNewRiskItem);
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

    public void destroy(Integer id) throws NonexistentEntityException {
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
            List<RiskItem> riskItemList = riskCategory.getRiskItemList();
            for (RiskItem riskItemListRiskItem : riskItemList) {
                riskItemListRiskItem.getRiskCategoryList().remove(riskCategory);
                riskItemListRiskItem = em.merge(riskItemListRiskItem);
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
