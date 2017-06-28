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

import com.validation.manager.core.db.Hazard;
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
public class HazardJpaController implements Serializable {

    public HazardJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Hazard hazard) {
        if (hazard.getRiskItemList() == null) {
            hazard.setRiskItemList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<RiskItem> attachedRiskItemList = new ArrayList<>();
            for (RiskItem riskItemListRiskItemToAttach : hazard.getRiskItemList()) {
                riskItemListRiskItemToAttach = em.getReference(riskItemListRiskItemToAttach.getClass(), riskItemListRiskItemToAttach.getRiskItemPK());
                attachedRiskItemList.add(riskItemListRiskItemToAttach);
            }
            hazard.setRiskItemList(attachedRiskItemList);
            em.persist(hazard);
            for (RiskItem riskItemListRiskItem : hazard.getRiskItemList()) {
                riskItemListRiskItem.getHazardList().add(hazard);
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

    public void edit(Hazard hazard) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Hazard persistentHazard = em.find(Hazard.class, hazard.getId());
            List<RiskItem> riskItemListOld = persistentHazard.getRiskItemList();
            List<RiskItem> riskItemListNew = hazard.getRiskItemList();
            List<RiskItem> attachedRiskItemListNew = new ArrayList<>();
            for (RiskItem riskItemListNewRiskItemToAttach : riskItemListNew) {
                riskItemListNewRiskItemToAttach = em.getReference(riskItemListNewRiskItemToAttach.getClass(), riskItemListNewRiskItemToAttach.getRiskItemPK());
                attachedRiskItemListNew.add(riskItemListNewRiskItemToAttach);
            }
            riskItemListNew = attachedRiskItemListNew;
            hazard.setRiskItemList(riskItemListNew);
            hazard = em.merge(hazard);
            for (RiskItem riskItemListOldRiskItem : riskItemListOld) {
                if (!riskItemListNew.contains(riskItemListOldRiskItem)) {
                    riskItemListOldRiskItem.getHazardList().remove(hazard);
                    riskItemListOldRiskItem = em.merge(riskItemListOldRiskItem);
                }
            }
            for (RiskItem riskItemListNewRiskItem : riskItemListNew) {
                if (!riskItemListOld.contains(riskItemListNewRiskItem)) {
                    riskItemListNewRiskItem.getHazardList().add(hazard);
                    riskItemListNewRiskItem = em.merge(riskItemListNewRiskItem);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = hazard.getId();
                if (findHazard(id) == null) {
                    throw new NonexistentEntityException("The hazard with id " + id + " no longer exists.");
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
            Hazard hazard;
            try {
                hazard = em.getReference(Hazard.class, id);
                hazard.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The hazard with id " + id + " no longer exists.", enfe);
            }
            List<RiskItem> riskItemList = hazard.getRiskItemList();
            for (RiskItem riskItemListRiskItem : riskItemList) {
                riskItemListRiskItem.getHazardList().remove(hazard);
                riskItemListRiskItem = em.merge(riskItemListRiskItem);
            }
            em.remove(hazard);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Hazard> findHazardEntities() {
        return findHazardEntities(true, -1, -1);
    }

    public List<Hazard> findHazardEntities(int maxResults, int firstResult) {
        return findHazardEntities(false, maxResults, firstResult);
    }

    private List<Hazard> findHazardEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Hazard.class));
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

    public Hazard findHazard(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Hazard.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getHazardCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Hazard> rt = cq.from(Hazard.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
