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

import com.validation.manager.core.db.Cause;
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
public class CauseJpaController implements Serializable {

    public CauseJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cause cause) {
        if (cause.getRiskItemList() == null) {
            cause.setRiskItemList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<RiskItem> attachedRiskItemList = new ArrayList<>();
            for (RiskItem riskItemListRiskItemToAttach : cause.getRiskItemList()) {
                riskItemListRiskItemToAttach = em.getReference(riskItemListRiskItemToAttach.getClass(), riskItemListRiskItemToAttach.getRiskItemPK());
                attachedRiskItemList.add(riskItemListRiskItemToAttach);
            }
            cause.setRiskItemList(attachedRiskItemList);
            em.persist(cause);
            for (RiskItem riskItemListRiskItem : cause.getRiskItemList()) {
                riskItemListRiskItem.getCauseList().add(cause);
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

    public void edit(Cause cause) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cause persistentCause = em.find(Cause.class, cause.getId());
            List<RiskItem> riskItemListOld = persistentCause.getRiskItemList();
            List<RiskItem> riskItemListNew = cause.getRiskItemList();
            List<RiskItem> attachedRiskItemListNew = new ArrayList<>();
            for (RiskItem riskItemListNewRiskItemToAttach : riskItemListNew) {
                riskItemListNewRiskItemToAttach = em.getReference(riskItemListNewRiskItemToAttach.getClass(), riskItemListNewRiskItemToAttach.getRiskItemPK());
                attachedRiskItemListNew.add(riskItemListNewRiskItemToAttach);
            }
            riskItemListNew = attachedRiskItemListNew;
            cause.setRiskItemList(riskItemListNew);
            cause = em.merge(cause);
            for (RiskItem riskItemListOldRiskItem : riskItemListOld) {
                if (!riskItemListNew.contains(riskItemListOldRiskItem)) {
                    riskItemListOldRiskItem.getCauseList().remove(cause);
                    riskItemListOldRiskItem = em.merge(riskItemListOldRiskItem);
                }
            }
            for (RiskItem riskItemListNewRiskItem : riskItemListNew) {
                if (!riskItemListOld.contains(riskItemListNewRiskItem)) {
                    riskItemListNewRiskItem.getCauseList().add(cause);
                    riskItemListNewRiskItem = em.merge(riskItemListNewRiskItem);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cause.getId();
                if (findCause(id) == null) {
                    throw new NonexistentEntityException("The cause with id " + id + " no longer exists.");
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
            Cause cause;
            try {
                cause = em.getReference(Cause.class, id);
                cause.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cause with id " + id + " no longer exists.", enfe);
            }
            List<RiskItem> riskItemList = cause.getRiskItemList();
            for (RiskItem riskItemListRiskItem : riskItemList) {
                riskItemListRiskItem.getCauseList().remove(cause);
                riskItemListRiskItem = em.merge(riskItemListRiskItem);
            }
            em.remove(cause);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cause> findCauseEntities() {
        return findCauseEntities(true, -1, -1);
    }

    public List<Cause> findCauseEntities(int maxResults, int firstResult) {
        return findCauseEntities(false, maxResults, firstResult);
    }

    private List<Cause> findCauseEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cause.class));
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

    public Cause findCause(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cause.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getCauseCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cause> rt = cq.from(Cause.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
