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
import com.validation.manager.core.db.RiskItemHasHazard;
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
public class HazardJpaController implements Serializable {

    public HazardJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Hazard hazard) {
        if (hazard.getRiskItemHasHazardList() == null) {
            hazard.setRiskItemHasHazardList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<RiskItemHasHazard> attachedRiskItemHasHazardList = new ArrayList<>();
            for (RiskItemHasHazard riskItemHasHazardListRiskItemHasHazardToAttach : hazard.getRiskItemHasHazardList()) {
                riskItemHasHazardListRiskItemHasHazardToAttach = em.getReference(riskItemHasHazardListRiskItemHasHazardToAttach.getClass(), riskItemHasHazardListRiskItemHasHazardToAttach.getRiskItemHasHazardPK());
                attachedRiskItemHasHazardList.add(riskItemHasHazardListRiskItemHasHazardToAttach);
            }
            hazard.setRiskItemHasHazardList(attachedRiskItemHasHazardList);
            em.persist(hazard);
            for (RiskItemHasHazard riskItemHasHazardListRiskItemHasHazard : hazard.getRiskItemHasHazardList()) {
                Hazard oldHazardOfRiskItemHasHazardListRiskItemHasHazard = riskItemHasHazardListRiskItemHasHazard.getHazard();
                riskItemHasHazardListRiskItemHasHazard.setHazard(hazard);
                riskItemHasHazardListRiskItemHasHazard = em.merge(riskItemHasHazardListRiskItemHasHazard);
                if (oldHazardOfRiskItemHasHazardListRiskItemHasHazard != null) {
                    oldHazardOfRiskItemHasHazardListRiskItemHasHazard.getRiskItemHasHazardList().remove(riskItemHasHazardListRiskItemHasHazard);
                    oldHazardOfRiskItemHasHazardListRiskItemHasHazard = em.merge(oldHazardOfRiskItemHasHazardListRiskItemHasHazard);
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

    public void edit(Hazard hazard) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Hazard persistentHazard = em.find(Hazard.class, hazard.getId());
            List<RiskItemHasHazard> riskItemHasHazardListOld = persistentHazard.getRiskItemHasHazardList();
            List<RiskItemHasHazard> riskItemHasHazardListNew = hazard.getRiskItemHasHazardList();
            List<String> illegalOrphanMessages = null;
            for (RiskItemHasHazard riskItemHasHazardListOldRiskItemHasHazard : riskItemHasHazardListOld) {
                if (!riskItemHasHazardListNew.contains(riskItemHasHazardListOldRiskItemHasHazard)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain RiskItemHasHazard " + riskItemHasHazardListOldRiskItemHasHazard + " since its hazard field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<RiskItemHasHazard> attachedRiskItemHasHazardListNew = new ArrayList<>();
            for (RiskItemHasHazard riskItemHasHazardListNewRiskItemHasHazardToAttach : riskItemHasHazardListNew) {
                riskItemHasHazardListNewRiskItemHasHazardToAttach = em.getReference(riskItemHasHazardListNewRiskItemHasHazardToAttach.getClass(), riskItemHasHazardListNewRiskItemHasHazardToAttach.getRiskItemHasHazardPK());
                attachedRiskItemHasHazardListNew.add(riskItemHasHazardListNewRiskItemHasHazardToAttach);
            }
            riskItemHasHazardListNew = attachedRiskItemHasHazardListNew;
            hazard.setRiskItemHasHazardList(riskItemHasHazardListNew);
            hazard = em.merge(hazard);
            for (RiskItemHasHazard riskItemHasHazardListNewRiskItemHasHazard : riskItemHasHazardListNew) {
                if (!riskItemHasHazardListOld.contains(riskItemHasHazardListNewRiskItemHasHazard)) {
                    Hazard oldHazardOfRiskItemHasHazardListNewRiskItemHasHazard = riskItemHasHazardListNewRiskItemHasHazard.getHazard();
                    riskItemHasHazardListNewRiskItemHasHazard.setHazard(hazard);
                    riskItemHasHazardListNewRiskItemHasHazard = em.merge(riskItemHasHazardListNewRiskItemHasHazard);
                    if (oldHazardOfRiskItemHasHazardListNewRiskItemHasHazard != null && !oldHazardOfRiskItemHasHazardListNewRiskItemHasHazard.equals(hazard)) {
                        oldHazardOfRiskItemHasHazardListNewRiskItemHasHazard.getRiskItemHasHazardList().remove(riskItemHasHazardListNewRiskItemHasHazard);
                        oldHazardOfRiskItemHasHazardListNewRiskItemHasHazard = em.merge(oldHazardOfRiskItemHasHazardListNewRiskItemHasHazard);
                    }
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

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
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
            List<String> illegalOrphanMessages = null;
            List<RiskItemHasHazard> riskItemHasHazardListOrphanCheck = hazard.getRiskItemHasHazardList();
            for (RiskItemHasHazard riskItemHasHazardListOrphanCheckRiskItemHasHazard : riskItemHasHazardListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Hazard (" + hazard + ") cannot be destroyed since the RiskItemHasHazard " + riskItemHasHazardListOrphanCheckRiskItemHasHazard + " in its riskItemHasHazardList field has a non-nullable hazard field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
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
