/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.History;
import com.validation.manager.core.db.HistoryField;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class HistoryJpaController implements Serializable {

    public HistoryJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(History history) {
        if (history.getHistoryFieldList() == null) {
            history.setHistoryFieldList(new ArrayList<HistoryField>());
        }
        if (history.getRequirementList() == null) {
            history.setRequirementList(new ArrayList<Requirement>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            VmUser modifierId = history.getModifierId();
            if (modifierId != null) {
                modifierId = em.getReference(modifierId.getClass(), modifierId.getId());
                history.setModifierId(modifierId);
            }
            List<HistoryField> attachedHistoryFieldList = new ArrayList<HistoryField>();
            for (HistoryField historyFieldListHistoryFieldToAttach : history.getHistoryFieldList()) {
                historyFieldListHistoryFieldToAttach = em.getReference(historyFieldListHistoryFieldToAttach.getClass(), historyFieldListHistoryFieldToAttach.getHistoryFieldPK());
                attachedHistoryFieldList.add(historyFieldListHistoryFieldToAttach);
            }
            history.setHistoryFieldList(attachedHistoryFieldList);
            List<Requirement> attachedRequirementList = new ArrayList<Requirement>();
            for (Requirement requirementListRequirementToAttach : history.getRequirementList()) {
                requirementListRequirementToAttach = em.getReference(requirementListRequirementToAttach.getClass(), requirementListRequirementToAttach.getId());
                attachedRequirementList.add(requirementListRequirementToAttach);
            }
            history.setRequirementList(attachedRequirementList);
            em.persist(history);
            if (modifierId != null) {
                modifierId.getHistoryModificationList().add(history);
                modifierId = em.merge(modifierId);
            }
            for (HistoryField historyFieldListHistoryField : history.getHistoryFieldList()) {
                History oldHistoryOfHistoryFieldListHistoryField = historyFieldListHistoryField.getHistory();
                historyFieldListHistoryField.setHistory(history);
                historyFieldListHistoryField = em.merge(historyFieldListHistoryField);
                if (oldHistoryOfHistoryFieldListHistoryField != null) {
                    oldHistoryOfHistoryFieldListHistoryField.getHistoryFieldList().remove(historyFieldListHistoryField);
                    oldHistoryOfHistoryFieldListHistoryField = em.merge(oldHistoryOfHistoryFieldListHistoryField);
                }
            }
            for (Requirement requirementListRequirement : history.getRequirementList()) {
                requirementListRequirement.getHistoryList().add(history);
                requirementListRequirement = em.merge(requirementListRequirement);
            }
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(History history) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            History persistentHistory = em.find(History.class, history.getId());
            VmUser modifierIdOld = persistentHistory.getModifierId();
            VmUser modifierIdNew = history.getModifierId();
            List<HistoryField> historyFieldListOld = persistentHistory.getHistoryFieldList();
            List<HistoryField> historyFieldListNew = history.getHistoryFieldList();
            List<Requirement> requirementListOld = persistentHistory.getRequirementList();
            List<Requirement> requirementListNew = history.getRequirementList();
            List<String> illegalOrphanMessages = null;
            for (HistoryField historyFieldListOldHistoryField : historyFieldListOld) {
                if (!historyFieldListNew.contains(historyFieldListOldHistoryField)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain HistoryField " + historyFieldListOldHistoryField + " since its history field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (modifierIdNew != null) {
                modifierIdNew = em.getReference(modifierIdNew.getClass(), modifierIdNew.getId());
                history.setModifierId(modifierIdNew);
            }
            List<HistoryField> attachedHistoryFieldListNew = new ArrayList<HistoryField>();
            for (HistoryField historyFieldListNewHistoryFieldToAttach : historyFieldListNew) {
                historyFieldListNewHistoryFieldToAttach = em.getReference(historyFieldListNewHistoryFieldToAttach.getClass(), historyFieldListNewHistoryFieldToAttach.getHistoryFieldPK());
                attachedHistoryFieldListNew.add(historyFieldListNewHistoryFieldToAttach);
            }
            historyFieldListNew = attachedHistoryFieldListNew;
            history.setHistoryFieldList(historyFieldListNew);
            List<Requirement> attachedRequirementListNew = new ArrayList<Requirement>();
            for (Requirement requirementListNewRequirementToAttach : requirementListNew) {
                requirementListNewRequirementToAttach = em.getReference(requirementListNewRequirementToAttach.getClass(), requirementListNewRequirementToAttach.getId());
                attachedRequirementListNew.add(requirementListNewRequirementToAttach);
            }
            requirementListNew = attachedRequirementListNew;
            history.setRequirementList(requirementListNew);
            history = em.merge(history);
            if (modifierIdOld != null && !modifierIdOld.equals(modifierIdNew)) {
                modifierIdOld.getHistoryModificationList().remove(history);
                modifierIdOld = em.merge(modifierIdOld);
            }
            if (modifierIdNew != null && !modifierIdNew.equals(modifierIdOld)) {
                modifierIdNew.getHistoryModificationList().add(history);
                modifierIdNew = em.merge(modifierIdNew);
            }
            for (HistoryField historyFieldListNewHistoryField : historyFieldListNew) {
                if (!historyFieldListOld.contains(historyFieldListNewHistoryField)) {
                    History oldHistoryOfHistoryFieldListNewHistoryField = historyFieldListNewHistoryField.getHistory();
                    historyFieldListNewHistoryField.setHistory(history);
                    historyFieldListNewHistoryField = em.merge(historyFieldListNewHistoryField);
                    if (oldHistoryOfHistoryFieldListNewHistoryField != null && !oldHistoryOfHistoryFieldListNewHistoryField.equals(history)) {
                        oldHistoryOfHistoryFieldListNewHistoryField.getHistoryFieldList().remove(historyFieldListNewHistoryField);
                        oldHistoryOfHistoryFieldListNewHistoryField = em.merge(oldHistoryOfHistoryFieldListNewHistoryField);
                    }
                }
            }
            for (Requirement requirementListOldRequirement : requirementListOld) {
                if (!requirementListNew.contains(requirementListOldRequirement)) {
                    requirementListOldRequirement.getHistoryList().remove(history);
                    requirementListOldRequirement = em.merge(requirementListOldRequirement);
                }
            }
            for (Requirement requirementListNewRequirement : requirementListNew) {
                if (!requirementListOld.contains(requirementListNewRequirement)) {
                    requirementListNewRequirement.getHistoryList().add(history);
                    requirementListNewRequirement = em.merge(requirementListNewRequirement);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = history.getId();
                if (findHistory(id) == null) {
                    throw new NonexistentEntityException("The history with id " + id + " no longer exists.");
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
            History history;
            try {
                history = em.getReference(History.class, id);
                history.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The history with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<HistoryField> historyFieldListOrphanCheck = history.getHistoryFieldList();
            for (HistoryField historyFieldListOrphanCheckHistoryField : historyFieldListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This History (" + history + ") cannot be destroyed since the HistoryField " + historyFieldListOrphanCheckHistoryField + " in its historyFieldList field has a non-nullable history field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            VmUser modifierId = history.getModifierId();
            if (modifierId != null) {
                modifierId.getHistoryModificationList().remove(history);
                modifierId = em.merge(modifierId);
            }
            List<Requirement> requirementList = history.getRequirementList();
            for (Requirement requirementListRequirement : requirementList) {
                requirementListRequirement.getHistoryList().remove(history);
                requirementListRequirement = em.merge(requirementListRequirement);
            }
            em.remove(history);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<History> findHistoryEntities() {
        return findHistoryEntities(true, -1, -1);
    }

    public List<History> findHistoryEntities(int maxResults, int firstResult) {
        return findHistoryEntities(false, maxResults, firstResult);
    }

    private List<History> findHistoryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(History.class));
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

    public History findHistory(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(History.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getHistoryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<History> rt = cq.from(History.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
