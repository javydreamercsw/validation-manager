/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.CorrectiveAction;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.Investigation;
import com.validation.manager.core.db.RequirementHasException;
import com.validation.manager.core.db.VmException;
import com.validation.manager.core.db.VmExceptionPK;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class VmExceptionJpaController implements Serializable {

    public VmExceptionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(VmException vmException) throws PreexistingEntityException, Exception {
        if (vmException.getVmExceptionPK() == null) {
            vmException.setVmExceptionPK(new VmExceptionPK());
        }
        if (vmException.getCorrectiveActionList() == null) {
            vmException.setCorrectiveActionList(new ArrayList<CorrectiveAction>());
        }
        if (vmException.getStepList() == null) {
            vmException.setStepList(new ArrayList<Step>());
        }
        if (vmException.getInvestigationList() == null) {
            vmException.setInvestigationList(new ArrayList<Investigation>());
        }
        if (vmException.getRequirementHasExceptionList() == null) {
            vmException.setRequirementHasExceptionList(new ArrayList<RequirementHasException>());
        }
        vmException.getVmExceptionPK().setReporterId(vmException.getVmUser().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            VmUser vmUser = vmException.getVmUser();
            if (vmUser != null) {
                vmUser = em.getReference(vmUser.getClass(), vmUser.getId());
                vmException.setVmUser(vmUser);
            }
            List<CorrectiveAction> attachedCorrectiveActionList = new ArrayList<CorrectiveAction>();
            for (CorrectiveAction correctiveActionListCorrectiveActionToAttach : vmException.getCorrectiveActionList()) {
                correctiveActionListCorrectiveActionToAttach = em.getReference(correctiveActionListCorrectiveActionToAttach.getClass(), correctiveActionListCorrectiveActionToAttach.getId());
                attachedCorrectiveActionList.add(correctiveActionListCorrectiveActionToAttach);
            }
            vmException.setCorrectiveActionList(attachedCorrectiveActionList);
            List<Step> attachedStepList = new ArrayList<Step>();
            for (Step stepListStepToAttach : vmException.getStepList()) {
                stepListStepToAttach = em.getReference(stepListStepToAttach.getClass(), stepListStepToAttach.getStepPK());
                attachedStepList.add(stepListStepToAttach);
            }
            vmException.setStepList(attachedStepList);
            List<Investigation> attachedInvestigationList = new ArrayList<Investigation>();
            for (Investigation investigationListInvestigationToAttach : vmException.getInvestigationList()) {
                investigationListInvestigationToAttach = em.getReference(investigationListInvestigationToAttach.getClass(), investigationListInvestigationToAttach.getId());
                attachedInvestigationList.add(investigationListInvestigationToAttach);
            }
            vmException.setInvestigationList(attachedInvestigationList);
            List<RequirementHasException> attachedRequirementHasExceptionList = new ArrayList<RequirementHasException>();
            for (RequirementHasException requirementHasExceptionListRequirementHasExceptionToAttach : vmException.getRequirementHasExceptionList()) {
                requirementHasExceptionListRequirementHasExceptionToAttach = em.getReference(requirementHasExceptionListRequirementHasExceptionToAttach.getClass(), requirementHasExceptionListRequirementHasExceptionToAttach.getRequirementHasExceptionPK());
                attachedRequirementHasExceptionList.add(requirementHasExceptionListRequirementHasExceptionToAttach);
            }
            vmException.setRequirementHasExceptionList(attachedRequirementHasExceptionList);
            em.persist(vmException);
            if (vmUser != null) {
                vmUser.getVmExceptionList().add(vmException);
                vmUser = em.merge(vmUser);
            }
            for (CorrectiveAction correctiveActionListCorrectiveAction : vmException.getCorrectiveActionList()) {
                correctiveActionListCorrectiveAction.getVmExceptionList().add(vmException);
                correctiveActionListCorrectiveAction = em.merge(correctiveActionListCorrectiveAction);
            }
            for (Step stepListStep : vmException.getStepList()) {
                stepListStep.getVmExceptionList().add(vmException);
                stepListStep = em.merge(stepListStep);
            }
            for (Investigation investigationListInvestigation : vmException.getInvestigationList()) {
                investigationListInvestigation.getVmExceptionList().add(vmException);
                investigationListInvestigation = em.merge(investigationListInvestigation);
            }
            for (RequirementHasException requirementHasExceptionListRequirementHasException : vmException.getRequirementHasExceptionList()) {
                VmException oldVmExceptionOfRequirementHasExceptionListRequirementHasException = requirementHasExceptionListRequirementHasException.getVmException();
                requirementHasExceptionListRequirementHasException.setVmException(vmException);
                requirementHasExceptionListRequirementHasException = em.merge(requirementHasExceptionListRequirementHasException);
                if (oldVmExceptionOfRequirementHasExceptionListRequirementHasException != null) {
                    oldVmExceptionOfRequirementHasExceptionListRequirementHasException.getRequirementHasExceptionList().remove(requirementHasExceptionListRequirementHasException);
                    oldVmExceptionOfRequirementHasExceptionListRequirementHasException = em.merge(oldVmExceptionOfRequirementHasExceptionListRequirementHasException);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findVmException(vmException.getVmExceptionPK()) != null) {
                throw new PreexistingEntityException("VmException " + vmException + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(VmException vmException) throws IllegalOrphanException, NonexistentEntityException, Exception {
        vmException.getVmExceptionPK().setReporterId(vmException.getVmUser().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            VmException persistentVmException = em.find(VmException.class, vmException.getVmExceptionPK());
            VmUser vmUserOld = persistentVmException.getVmUser();
            VmUser vmUserNew = vmException.getVmUser();
            List<CorrectiveAction> correctiveActionListOld = persistentVmException.getCorrectiveActionList();
            List<CorrectiveAction> correctiveActionListNew = vmException.getCorrectiveActionList();
            List<Step> stepListOld = persistentVmException.getStepList();
            List<Step> stepListNew = vmException.getStepList();
            List<Investigation> investigationListOld = persistentVmException.getInvestigationList();
            List<Investigation> investigationListNew = vmException.getInvestigationList();
            List<RequirementHasException> requirementHasExceptionListOld = persistentVmException.getRequirementHasExceptionList();
            List<RequirementHasException> requirementHasExceptionListNew = vmException.getRequirementHasExceptionList();
            List<String> illegalOrphanMessages = null;
            for (RequirementHasException requirementHasExceptionListOldRequirementHasException : requirementHasExceptionListOld) {
                if (!requirementHasExceptionListNew.contains(requirementHasExceptionListOldRequirementHasException)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RequirementHasException " + requirementHasExceptionListOldRequirementHasException + " since its vmException field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (vmUserNew != null) {
                vmUserNew = em.getReference(vmUserNew.getClass(), vmUserNew.getId());
                vmException.setVmUser(vmUserNew);
            }
            List<CorrectiveAction> attachedCorrectiveActionListNew = new ArrayList<CorrectiveAction>();
            for (CorrectiveAction correctiveActionListNewCorrectiveActionToAttach : correctiveActionListNew) {
                correctiveActionListNewCorrectiveActionToAttach = em.getReference(correctiveActionListNewCorrectiveActionToAttach.getClass(), correctiveActionListNewCorrectiveActionToAttach.getId());
                attachedCorrectiveActionListNew.add(correctiveActionListNewCorrectiveActionToAttach);
            }
            correctiveActionListNew = attachedCorrectiveActionListNew;
            vmException.setCorrectiveActionList(correctiveActionListNew);
            List<Step> attachedStepListNew = new ArrayList<Step>();
            for (Step stepListNewStepToAttach : stepListNew) {
                stepListNewStepToAttach = em.getReference(stepListNewStepToAttach.getClass(), stepListNewStepToAttach.getStepPK());
                attachedStepListNew.add(stepListNewStepToAttach);
            }
            stepListNew = attachedStepListNew;
            vmException.setStepList(stepListNew);
            List<Investigation> attachedInvestigationListNew = new ArrayList<Investigation>();
            for (Investigation investigationListNewInvestigationToAttach : investigationListNew) {
                investigationListNewInvestigationToAttach = em.getReference(investigationListNewInvestigationToAttach.getClass(), investigationListNewInvestigationToAttach.getId());
                attachedInvestigationListNew.add(investigationListNewInvestigationToAttach);
            }
            investigationListNew = attachedInvestigationListNew;
            vmException.setInvestigationList(investigationListNew);
            List<RequirementHasException> attachedRequirementHasExceptionListNew = new ArrayList<RequirementHasException>();
            for (RequirementHasException requirementHasExceptionListNewRequirementHasExceptionToAttach : requirementHasExceptionListNew) {
                requirementHasExceptionListNewRequirementHasExceptionToAttach = em.getReference(requirementHasExceptionListNewRequirementHasExceptionToAttach.getClass(), requirementHasExceptionListNewRequirementHasExceptionToAttach.getRequirementHasExceptionPK());
                attachedRequirementHasExceptionListNew.add(requirementHasExceptionListNewRequirementHasExceptionToAttach);
            }
            requirementHasExceptionListNew = attachedRequirementHasExceptionListNew;
            vmException.setRequirementHasExceptionList(requirementHasExceptionListNew);
            vmException = em.merge(vmException);
            if (vmUserOld != null && !vmUserOld.equals(vmUserNew)) {
                vmUserOld.getVmExceptionList().remove(vmException);
                vmUserOld = em.merge(vmUserOld);
            }
            if (vmUserNew != null && !vmUserNew.equals(vmUserOld)) {
                vmUserNew.getVmExceptionList().add(vmException);
                vmUserNew = em.merge(vmUserNew);
            }
            for (CorrectiveAction correctiveActionListOldCorrectiveAction : correctiveActionListOld) {
                if (!correctiveActionListNew.contains(correctiveActionListOldCorrectiveAction)) {
                    correctiveActionListOldCorrectiveAction.getVmExceptionList().remove(vmException);
                    correctiveActionListOldCorrectiveAction = em.merge(correctiveActionListOldCorrectiveAction);
                }
            }
            for (CorrectiveAction correctiveActionListNewCorrectiveAction : correctiveActionListNew) {
                if (!correctiveActionListOld.contains(correctiveActionListNewCorrectiveAction)) {
                    correctiveActionListNewCorrectiveAction.getVmExceptionList().add(vmException);
                    correctiveActionListNewCorrectiveAction = em.merge(correctiveActionListNewCorrectiveAction);
                }
            }
            for (Step stepListOldStep : stepListOld) {
                if (!stepListNew.contains(stepListOldStep)) {
                    stepListOldStep.getVmExceptionList().remove(vmException);
                    stepListOldStep = em.merge(stepListOldStep);
                }
            }
            for (Step stepListNewStep : stepListNew) {
                if (!stepListOld.contains(stepListNewStep)) {
                    stepListNewStep.getVmExceptionList().add(vmException);
                    stepListNewStep = em.merge(stepListNewStep);
                }
            }
            for (Investigation investigationListOldInvestigation : investigationListOld) {
                if (!investigationListNew.contains(investigationListOldInvestigation)) {
                    investigationListOldInvestigation.getVmExceptionList().remove(vmException);
                    investigationListOldInvestigation = em.merge(investigationListOldInvestigation);
                }
            }
            for (Investigation investigationListNewInvestigation : investigationListNew) {
                if (!investigationListOld.contains(investigationListNewInvestigation)) {
                    investigationListNewInvestigation.getVmExceptionList().add(vmException);
                    investigationListNewInvestigation = em.merge(investigationListNewInvestigation);
                }
            }
            for (RequirementHasException requirementHasExceptionListNewRequirementHasException : requirementHasExceptionListNew) {
                if (!requirementHasExceptionListOld.contains(requirementHasExceptionListNewRequirementHasException)) {
                    VmException oldVmExceptionOfRequirementHasExceptionListNewRequirementHasException = requirementHasExceptionListNewRequirementHasException.getVmException();
                    requirementHasExceptionListNewRequirementHasException.setVmException(vmException);
                    requirementHasExceptionListNewRequirementHasException = em.merge(requirementHasExceptionListNewRequirementHasException);
                    if (oldVmExceptionOfRequirementHasExceptionListNewRequirementHasException != null && !oldVmExceptionOfRequirementHasExceptionListNewRequirementHasException.equals(vmException)) {
                        oldVmExceptionOfRequirementHasExceptionListNewRequirementHasException.getRequirementHasExceptionList().remove(requirementHasExceptionListNewRequirementHasException);
                        oldVmExceptionOfRequirementHasExceptionListNewRequirementHasException = em.merge(oldVmExceptionOfRequirementHasExceptionListNewRequirementHasException);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                VmExceptionPK id = vmException.getVmExceptionPK();
                if (findVmException(id) == null) {
                    throw new NonexistentEntityException("The vmException with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(VmExceptionPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            VmException vmException;
            try {
                vmException = em.getReference(VmException.class, id);
                vmException.getVmExceptionPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The vmException with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RequirementHasException> requirementHasExceptionListOrphanCheck = vmException.getRequirementHasExceptionList();
            for (RequirementHasException requirementHasExceptionListOrphanCheckRequirementHasException : requirementHasExceptionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This VmException (" + vmException + ") cannot be destroyed since the RequirementHasException " + requirementHasExceptionListOrphanCheckRequirementHasException + " in its requirementHasExceptionList field has a non-nullable vmException field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            VmUser vmUser = vmException.getVmUser();
            if (vmUser != null) {
                vmUser.getVmExceptionList().remove(vmException);
                vmUser = em.merge(vmUser);
            }
            List<CorrectiveAction> correctiveActionList = vmException.getCorrectiveActionList();
            for (CorrectiveAction correctiveActionListCorrectiveAction : correctiveActionList) {
                correctiveActionListCorrectiveAction.getVmExceptionList().remove(vmException);
                correctiveActionListCorrectiveAction = em.merge(correctiveActionListCorrectiveAction);
            }
            List<Step> stepList = vmException.getStepList();
            for (Step stepListStep : stepList) {
                stepListStep.getVmExceptionList().remove(vmException);
                stepListStep = em.merge(stepListStep);
            }
            List<Investigation> investigationList = vmException.getInvestigationList();
            for (Investigation investigationListInvestigation : investigationList) {
                investigationListInvestigation.getVmExceptionList().remove(vmException);
                investigationListInvestigation = em.merge(investigationListInvestigation);
            }
            em.remove(vmException);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<VmException> findVmExceptionEntities() {
        return findVmExceptionEntities(true, -1, -1);
    }

    public List<VmException> findVmExceptionEntities(int maxResults, int firstResult) {
        return findVmExceptionEntities(false, maxResults, firstResult);
    }

    private List<VmException> findVmExceptionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(VmException.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public VmException findVmException(VmExceptionPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(VmException.class, id);
        } finally {
            em.close();
        }
    }

    public int getVmExceptionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<VmException> rt = cq.from(VmException.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}