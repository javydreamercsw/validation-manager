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
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.VmSetting;
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
public class VmSettingJpaController implements Serializable {

    public VmSettingJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(VmSetting vmSetting) {
        if (vmSetting.getHistoryList() == null) {
            vmSetting.setHistoryList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<History> attachedHistoryList = new ArrayList<>();
            for (History historyListHistoryToAttach : vmSetting.getHistoryList()) {
                historyListHistoryToAttach = em.getReference(historyListHistoryToAttach.getClass(), historyListHistoryToAttach.getId());
                attachedHistoryList.add(historyListHistoryToAttach);
            }
            vmSetting.setHistoryList(attachedHistoryList);
            em.persist(vmSetting);
            for (History historyListHistory : vmSetting.getHistoryList()) {
                VmSetting oldVmSettingIdOfHistoryListHistory = historyListHistory.getVmSettingId();
                historyListHistory.setVmSettingId(vmSetting);
                historyListHistory = em.merge(historyListHistory);
                if (oldVmSettingIdOfHistoryListHistory != null) {
                    oldVmSettingIdOfHistoryListHistory.getHistoryList().remove(historyListHistory);
                    oldVmSettingIdOfHistoryListHistory = em.merge(oldVmSettingIdOfHistoryListHistory);
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

    public void edit(VmSetting vmSetting) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            VmSetting persistentVmSetting = em.find(VmSetting.class, vmSetting.getId());
            List<History> historyListOld = persistentVmSetting.getHistoryList();
            List<History> historyListNew = vmSetting.getHistoryList();
            List<String> illegalOrphanMessages = null;
            for (History historyListOldHistory : historyListOld) {
                if (!historyListNew.contains(historyListOldHistory)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain History " + historyListOldHistory + " since its vmSettingId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<History> attachedHistoryListNew = new ArrayList<>();
            for (History historyListNewHistoryToAttach : historyListNew) {
                historyListNewHistoryToAttach = em.getReference(historyListNewHistoryToAttach.getClass(), historyListNewHistoryToAttach.getId());
                attachedHistoryListNew.add(historyListNewHistoryToAttach);
            }
            historyListNew = attachedHistoryListNew;
            vmSetting.setHistoryList(historyListNew);
            vmSetting = em.merge(vmSetting);
            for (History historyListNewHistory : historyListNew) {
                if (!historyListOld.contains(historyListNewHistory)) {
                    VmSetting oldVmSettingIdOfHistoryListNewHistory = historyListNewHistory.getVmSettingId();
                    historyListNewHistory.setVmSettingId(vmSetting);
                    historyListNewHistory = em.merge(historyListNewHistory);
                    if (oldVmSettingIdOfHistoryListNewHistory != null && !oldVmSettingIdOfHistoryListNewHistory.equals(vmSetting)) {
                        oldVmSettingIdOfHistoryListNewHistory.getHistoryList().remove(historyListNewHistory);
                        oldVmSettingIdOfHistoryListNewHistory = em.merge(oldVmSettingIdOfHistoryListNewHistory);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = vmSetting.getId();
                if (findVmSetting(id) == null) {
                    throw new NonexistentEntityException("The vmSetting with id " + id + " no longer exists.");
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
            VmSetting vmSetting;
            try {
                vmSetting = em.getReference(VmSetting.class, id);
                vmSetting.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The vmSetting with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<History> historyListOrphanCheck = vmSetting.getHistoryList();
            for (History historyListOrphanCheckHistory : historyListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This VmSetting (" + vmSetting + ") cannot be destroyed since the History " + historyListOrphanCheckHistory + " in its historyList field has a non-nullable vmSettingId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(vmSetting);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<VmSetting> findVmSettingEntities() {
        return findVmSettingEntities(true, -1, -1);
    }

    public List<VmSetting> findVmSettingEntities(int maxResults, int firstResult) {
        return findVmSettingEntities(false, maxResults, firstResult);
    }

    private List<VmSetting> findVmSettingEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(VmSetting.class));
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

    public VmSetting findVmSetting(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(VmSetting.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getVmSettingCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<VmSetting> rt = cq.from(VmSetting.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
