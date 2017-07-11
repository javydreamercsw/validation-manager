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

import com.validation.manager.core.db.Baseline;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class BaselineJpaController implements Serializable {

    public BaselineJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Baseline baseline) {
        if (baseline.getHistoryList() == null) {
            baseline.setHistoryList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RequirementSpec requirementSpec = baseline.getRequirementSpec();
            if (requirementSpec != null) {
                requirementSpec = em.getReference(requirementSpec.getClass(), requirementSpec.getRequirementSpecPK());
                baseline.setRequirementSpec(requirementSpec);
            }
            List<History> attachedHistoryList = new ArrayList<>();
            for (History historyListHistoryToAttach : baseline.getHistoryList()) {
                historyListHistoryToAttach = em.getReference(historyListHistoryToAttach.getClass(), historyListHistoryToAttach.getId());
                attachedHistoryList.add(historyListHistoryToAttach);
            }
            baseline.setHistoryList(attachedHistoryList);
            em.persist(baseline);
            if (requirementSpec != null) {
                requirementSpec.getBaselineList().add(baseline);
                requirementSpec = em.merge(requirementSpec);
            }
            for (History historyListHistory : baseline.getHistoryList()) {
                historyListHistory.getBaselineList().add(baseline);
                historyListHistory = em.merge(historyListHistory);
            }
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Baseline baseline) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Baseline persistentBaseline = em.find(Baseline.class, baseline.getId());
            RequirementSpec requirementSpecOld = persistentBaseline.getRequirementSpec();
            RequirementSpec requirementSpecNew = baseline.getRequirementSpec();
            List<History> historyListOld = persistentBaseline.getHistoryList();
            List<History> historyListNew = baseline.getHistoryList();
            if (requirementSpecNew != null) {
                requirementSpecNew = em.getReference(requirementSpecNew.getClass(), requirementSpecNew.getRequirementSpecPK());
                baseline.setRequirementSpec(requirementSpecNew);
            }
            List<History> attachedHistoryListNew = new ArrayList<>();
            for (History historyListNewHistoryToAttach : historyListNew) {
                historyListNewHistoryToAttach = em.getReference(historyListNewHistoryToAttach.getClass(), historyListNewHistoryToAttach.getId());
                attachedHistoryListNew.add(historyListNewHistoryToAttach);
            }
            historyListNew = attachedHistoryListNew;
            baseline.setHistoryList(historyListNew);
            baseline = em.merge(baseline);
            if (requirementSpecOld != null && !requirementSpecOld.equals(requirementSpecNew)) {
                requirementSpecOld.getBaselineList().remove(baseline);
                requirementSpecOld = em.merge(requirementSpecOld);
            }
            if (requirementSpecNew != null && !requirementSpecNew.equals(requirementSpecOld)) {
                requirementSpecNew.getBaselineList().add(baseline);
                requirementSpecNew = em.merge(requirementSpecNew);
            }
            for (History historyListOldHistory : historyListOld) {
                if (!historyListNew.contains(historyListOldHistory)) {
                    historyListOldHistory.getBaselineList().remove(baseline);
                    historyListOldHistory = em.merge(historyListOldHistory);
                }
            }
            for (History historyListNewHistory : historyListNew) {
                if (!historyListOld.contains(historyListNewHistory)) {
                    historyListNewHistory.getBaselineList().add(baseline);
                    historyListNewHistory = em.merge(historyListNewHistory);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = baseline.getId();
                if (findBaseline(id) == null) {
                    throw new NonexistentEntityException("The baseline with id " + id + " no longer exists.");
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
            Baseline baseline;
            try {
                baseline = em.getReference(Baseline.class, id);
                baseline.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The baseline with id " + id + " no longer exists.", enfe);
            }
            RequirementSpec requirementSpec = baseline.getRequirementSpec();
            if (requirementSpec != null) {
                requirementSpec.getBaselineList().remove(baseline);
                requirementSpec = em.merge(requirementSpec);
            }
            List<History> historyList = baseline.getHistoryList();
            for (History historyListHistory : historyList) {
                historyListHistory.getBaselineList().remove(baseline);
                historyListHistory = em.merge(historyListHistory);
            }
            em.remove(baseline);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Baseline> findBaselineEntities() {
        return findBaselineEntities(true, -1, -1);
    }

    public List<Baseline> findBaselineEntities(int maxResults, int firstResult) {
        return findBaselineEntities(false, maxResults, firstResult);
    }

    private List<Baseline> findBaselineEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Baseline.class));
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

    public Baseline findBaseline(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Baseline.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getBaselineCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Baseline> rt = cq.from(Baseline.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
