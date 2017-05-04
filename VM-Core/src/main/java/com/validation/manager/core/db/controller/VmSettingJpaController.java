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
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.VmSetting;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
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
            vmSetting.setHistoryList(new ArrayList<History>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<History> attachedHistoryList = new ArrayList<History>();
            for (History historyListHistoryToAttach : vmSetting.getHistoryList()) {
                historyListHistoryToAttach = em.getReference(historyListHistoryToAttach.getClass(), historyListHistoryToAttach.getId());
                attachedHistoryList.add(historyListHistoryToAttach);
            }
            vmSetting.setHistoryList(attachedHistoryList);
            em.persist(vmSetting);
            for (History historyListHistory : vmSetting.getHistoryList()) {
                historyListHistory.getVmSettingList().add(vmSetting);
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

    public void edit(VmSetting vmSetting) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            VmSetting persistentVmSetting = em.find(VmSetting.class, vmSetting.getId());
            List<History> historyListOld = persistentVmSetting.getHistoryList();
            List<History> historyListNew = vmSetting.getHistoryList();
            List<History> attachedHistoryListNew = new ArrayList<History>();
            for (History historyListNewHistoryToAttach : historyListNew) {
                historyListNewHistoryToAttach = em.getReference(historyListNewHistoryToAttach.getClass(), historyListNewHistoryToAttach.getId());
                attachedHistoryListNew.add(historyListNewHistoryToAttach);
            }
            historyListNew = attachedHistoryListNew;
            vmSetting.setHistoryList(historyListNew);
            vmSetting = em.merge(vmSetting);
            for (History historyListOldHistory : historyListOld) {
                if (!historyListNew.contains(historyListOldHistory)) {
                    historyListOldHistory.getVmSettingList().remove(vmSetting);
                    historyListOldHistory = em.merge(historyListOldHistory);
                }
            }
            for (History historyListNewHistory : historyListNew) {
                if (!historyListOld.contains(historyListNewHistory)) {
                    historyListNewHistory.getVmSettingList().add(vmSetting);
                    historyListNewHistory = em.merge(historyListNewHistory);
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

    public void destroy(Integer id) throws NonexistentEntityException {
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
            List<History> historyList = vmSetting.getHistoryList();
            for (History historyListHistory : historyList) {
                historyListHistory.getVmSettingList().remove(vmSetting);
                historyListHistory = em.merge(historyListHistory);
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
