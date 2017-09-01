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

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.RiskControl;
import com.validation.manager.core.db.RiskControlHasResidualRiskItem;
import com.validation.manager.core.db.RiskControlHasResidualRiskItemPK;
import com.validation.manager.core.db.RiskItem;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class RiskControlHasResidualRiskItemJpaController implements Serializable {

    public RiskControlHasResidualRiskItemJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RiskControlHasResidualRiskItem riskControlHasResidualRiskItem) throws PreexistingEntityException, Exception {
        if (riskControlHasResidualRiskItem.getRiskControlHasResidualRiskItemPK() == null) {
            riskControlHasResidualRiskItem.setRiskControlHasResidualRiskItemPK(new RiskControlHasResidualRiskItemPK());
        }
        riskControlHasResidualRiskItem.getRiskControlHasResidualRiskItemPK().setRiskItemId(riskControlHasResidualRiskItem.getRiskItem().getRiskItemPK().getId());
        riskControlHasResidualRiskItem.getRiskControlHasResidualRiskItemPK().setRiskControlId(riskControlHasResidualRiskItem.getRiskControl().getRiskControlPK().getId());
        riskControlHasResidualRiskItem.getRiskControlHasResidualRiskItemPK().setRiskitemFMEAid(riskControlHasResidualRiskItem.getRiskItem().getRiskItemPK().getFMEAid());
        riskControlHasResidualRiskItem.getRiskControlHasResidualRiskItemPK().setRiskControlRiskControlTypeId(riskControlHasResidualRiskItem.getRiskControl().getRiskControlPK().getRiskControlTypeId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskControl riskControl = riskControlHasResidualRiskItem.getRiskControl();
            if (riskControl != null) {
                riskControl = em.getReference(riskControl.getClass(), riskControl.getRiskControlPK());
                riskControlHasResidualRiskItem.setRiskControl(riskControl);
            }
            RiskItem riskItem = riskControlHasResidualRiskItem.getRiskItem();
            if (riskItem != null) {
                riskItem = em.getReference(riskItem.getClass(), riskItem.getRiskItemPK());
                riskControlHasResidualRiskItem.setRiskItem(riskItem);
            }
            em.persist(riskControlHasResidualRiskItem);
            if (riskControl != null) {
                riskControl.getRiskControlHasResidualRiskItemList().add(riskControlHasResidualRiskItem);
                riskControl = em.merge(riskControl);
            }
            if (riskItem != null) {
                riskItem.getRiskControlHasResidualRiskItemList().add(riskControlHasResidualRiskItem);
                riskItem = em.merge(riskItem);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findRiskControlHasResidualRiskItem(riskControlHasResidualRiskItem.getRiskControlHasResidualRiskItemPK()) != null) {
                throw new PreexistingEntityException("RiskControlHasResidualRiskItem " + riskControlHasResidualRiskItem + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RiskControlHasResidualRiskItem riskControlHasResidualRiskItem) throws NonexistentEntityException, Exception {
        riskControlHasResidualRiskItem.getRiskControlHasResidualRiskItemPK().setRiskItemId(riskControlHasResidualRiskItem.getRiskItem().getRiskItemPK().getId());
        riskControlHasResidualRiskItem.getRiskControlHasResidualRiskItemPK().setRiskControlId(riskControlHasResidualRiskItem.getRiskControl().getRiskControlPK().getId());
        riskControlHasResidualRiskItem.getRiskControlHasResidualRiskItemPK().setRiskitemFMEAid(riskControlHasResidualRiskItem.getRiskItem().getRiskItemPK().getFMEAid());
        riskControlHasResidualRiskItem.getRiskControlHasResidualRiskItemPK().setRiskControlRiskControlTypeId(riskControlHasResidualRiskItem.getRiskControl().getRiskControlPK().getRiskControlTypeId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskControlHasResidualRiskItem persistentRiskControlHasResidualRiskItem = em.find(RiskControlHasResidualRiskItem.class, riskControlHasResidualRiskItem.getRiskControlHasResidualRiskItemPK());
            RiskControl riskControlOld = persistentRiskControlHasResidualRiskItem.getRiskControl();
            RiskControl riskControlNew = riskControlHasResidualRiskItem.getRiskControl();
            RiskItem riskItemOld = persistentRiskControlHasResidualRiskItem.getRiskItem();
            RiskItem riskItemNew = riskControlHasResidualRiskItem.getRiskItem();
            if (riskControlNew != null) {
                riskControlNew = em.getReference(riskControlNew.getClass(), riskControlNew.getRiskControlPK());
                riskControlHasResidualRiskItem.setRiskControl(riskControlNew);
            }
            if (riskItemNew != null) {
                riskItemNew = em.getReference(riskItemNew.getClass(), riskItemNew.getRiskItemPK());
                riskControlHasResidualRiskItem.setRiskItem(riskItemNew);
            }
            riskControlHasResidualRiskItem = em.merge(riskControlHasResidualRiskItem);
            if (riskControlOld != null && !riskControlOld.equals(riskControlNew)) {
                riskControlOld.getRiskControlHasResidualRiskItemList().remove(riskControlHasResidualRiskItem);
                riskControlOld = em.merge(riskControlOld);
            }
            if (riskControlNew != null && !riskControlNew.equals(riskControlOld)) {
                riskControlNew.getRiskControlHasResidualRiskItemList().add(riskControlHasResidualRiskItem);
                riskControlNew = em.merge(riskControlNew);
            }
            if (riskItemOld != null && !riskItemOld.equals(riskItemNew)) {
                riskItemOld.getRiskControlHasResidualRiskItemList().remove(riskControlHasResidualRiskItem);
                riskItemOld = em.merge(riskItemOld);
            }
            if (riskItemNew != null && !riskItemNew.equals(riskItemOld)) {
                riskItemNew.getRiskControlHasResidualRiskItemList().add(riskControlHasResidualRiskItem);
                riskItemNew = em.merge(riskItemNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RiskControlHasResidualRiskItemPK id = riskControlHasResidualRiskItem.getRiskControlHasResidualRiskItemPK();
                if (findRiskControlHasResidualRiskItem(id) == null) {
                    throw new NonexistentEntityException("The riskControlHasResidualRiskItem with id " + id + " no longer exists.");
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

    public void destroy(RiskControlHasResidualRiskItemPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskControlHasResidualRiskItem riskControlHasResidualRiskItem;
            try {
                riskControlHasResidualRiskItem = em.getReference(RiskControlHasResidualRiskItem.class, id);
                riskControlHasResidualRiskItem.getRiskControlHasResidualRiskItemPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The riskControlHasResidualRiskItem with id " + id + " no longer exists.", enfe);
            }
            RiskControl riskControl = riskControlHasResidualRiskItem.getRiskControl();
            if (riskControl != null) {
                riskControl.getRiskControlHasResidualRiskItemList().remove(riskControlHasResidualRiskItem);
                riskControl = em.merge(riskControl);
            }
            RiskItem riskItem = riskControlHasResidualRiskItem.getRiskItem();
            if (riskItem != null) {
                riskItem.getRiskControlHasResidualRiskItemList().remove(riskControlHasResidualRiskItem);
                riskItem = em.merge(riskItem);
            }
            em.remove(riskControlHasResidualRiskItem);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RiskControlHasResidualRiskItem> findRiskControlHasResidualRiskItemEntities() {
        return findRiskControlHasResidualRiskItemEntities(true, -1, -1);
    }

    public List<RiskControlHasResidualRiskItem> findRiskControlHasResidualRiskItemEntities(int maxResults, int firstResult) {
        return findRiskControlHasResidualRiskItemEntities(false, maxResults, firstResult);
    }

    private List<RiskControlHasResidualRiskItem> findRiskControlHasResidualRiskItemEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RiskControlHasResidualRiskItem.class));
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

    public RiskControlHasResidualRiskItem findRiskControlHasResidualRiskItem(RiskControlHasResidualRiskItemPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RiskControlHasResidualRiskItem.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getRiskControlHasResidualRiskItemCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RiskControlHasResidualRiskItem> rt = cq.from(RiskControlHasResidualRiskItem.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
