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
import com.validation.manager.core.db.RiskControlType;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class RiskControlTypeJpaController implements Serializable {

    public RiskControlTypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RiskControlType riskControlType) {
        if (riskControlType.getRiskControlList() == null) {
            riskControlType.setRiskControlList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<RiskControl> attachedRiskControlList = new ArrayList<>();
            for (RiskControl riskControlListRiskControlToAttach : riskControlType.getRiskControlList()) {
                riskControlListRiskControlToAttach = em.getReference(riskControlListRiskControlToAttach.getClass(), riskControlListRiskControlToAttach.getRiskControlPK());
                attachedRiskControlList.add(riskControlListRiskControlToAttach);
            }
            riskControlType.setRiskControlList(attachedRiskControlList);
            em.persist(riskControlType);
            for (RiskControl riskControlListRiskControl : riskControlType.getRiskControlList()) {
                RiskControlType oldRiskControlTypeOfRiskControlListRiskControl = riskControlListRiskControl.getRiskControlType();
                riskControlListRiskControl.setRiskControlType(riskControlType);
                riskControlListRiskControl = em.merge(riskControlListRiskControl);
                if (oldRiskControlTypeOfRiskControlListRiskControl != null) {
                    oldRiskControlTypeOfRiskControlListRiskControl.getRiskControlList().remove(riskControlListRiskControl);
                    oldRiskControlTypeOfRiskControlListRiskControl = em.merge(oldRiskControlTypeOfRiskControlListRiskControl);
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

    public void edit(RiskControlType riskControlType) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskControlType persistentRiskControlType = em.find(RiskControlType.class, riskControlType.getId());
            List<RiskControl> riskControlListOld = persistentRiskControlType.getRiskControlList();
            List<RiskControl> riskControlListNew = riskControlType.getRiskControlList();
            List<String> illegalOrphanMessages = null;
            for (RiskControl riskControlListOldRiskControl : riskControlListOld) {
                if (!riskControlListNew.contains(riskControlListOldRiskControl)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain RiskControl " + riskControlListOldRiskControl + " since its riskControlType field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<RiskControl> attachedRiskControlListNew = new ArrayList<>();
            for (RiskControl riskControlListNewRiskControlToAttach : riskControlListNew) {
                riskControlListNewRiskControlToAttach = em.getReference(riskControlListNewRiskControlToAttach.getClass(), riskControlListNewRiskControlToAttach.getRiskControlPK());
                attachedRiskControlListNew.add(riskControlListNewRiskControlToAttach);
            }
            riskControlListNew = attachedRiskControlListNew;
            riskControlType.setRiskControlList(riskControlListNew);
            riskControlType = em.merge(riskControlType);
            for (RiskControl riskControlListNewRiskControl : riskControlListNew) {
                if (!riskControlListOld.contains(riskControlListNewRiskControl)) {
                    RiskControlType oldRiskControlTypeOfRiskControlListNewRiskControl = riskControlListNewRiskControl.getRiskControlType();
                    riskControlListNewRiskControl.setRiskControlType(riskControlType);
                    riskControlListNewRiskControl = em.merge(riskControlListNewRiskControl);
                    if (oldRiskControlTypeOfRiskControlListNewRiskControl != null && !oldRiskControlTypeOfRiskControlListNewRiskControl.equals(riskControlType)) {
                        oldRiskControlTypeOfRiskControlListNewRiskControl.getRiskControlList().remove(riskControlListNewRiskControl);
                        oldRiskControlTypeOfRiskControlListNewRiskControl = em.merge(oldRiskControlTypeOfRiskControlListNewRiskControl);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = riskControlType.getId();
                if (findRiskControlType(id) == null) {
                    throw new NonexistentEntityException("The riskControlType with id " + id + " no longer exists.");
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
            RiskControlType riskControlType;
            try {
                riskControlType = em.getReference(RiskControlType.class, id);
                riskControlType.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The riskControlType with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RiskControl> riskControlListOrphanCheck = riskControlType.getRiskControlList();
            for (RiskControl riskControlListOrphanCheckRiskControl : riskControlListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This RiskControlType (" + riskControlType + ") cannot be destroyed since the RiskControl " + riskControlListOrphanCheckRiskControl + " in its riskControlList field has a non-nullable riskControlType field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(riskControlType);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RiskControlType> findRiskControlTypeEntities() {
        return findRiskControlTypeEntities(true, -1, -1);
    }

    public List<RiskControlType> findRiskControlTypeEntities(int maxResults, int firstResult) {
        return findRiskControlTypeEntities(false, maxResults, firstResult);
    }

    private List<RiskControlType> findRiskControlTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RiskControlType.class));
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

    public RiskControlType findRiskControlType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RiskControlType.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getRiskControlTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RiskControlType> rt = cq.from(RiskControlType.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
