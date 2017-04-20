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
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.RootCause;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.CorrectiveAction;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.Investigation;
import com.validation.manager.core.db.VmException;
import com.validation.manager.core.db.VmExceptionPK;
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
        if (vmException.getRootCauseList() == null) {
            vmException.setRootCauseList(new ArrayList<RootCause>());
        }
        if (vmException.getRequirementList() == null) {
            vmException.setRequirementList(new ArrayList<Requirement>());
        }
        if (vmException.getCorrectiveActionList() == null) {
            vmException.setCorrectiveActionList(new ArrayList<CorrectiveAction>());
        }
        if (vmException.getStepList() == null) {
            vmException.setStepList(new ArrayList<Step>());
        }
        if (vmException.getExecutionStepList() == null) {
            vmException.setExecutionStepList(new ArrayList<ExecutionStep>());
        }
        if (vmException.getInvestigationList() == null) {
            vmException.setInvestigationList(new ArrayList<Investigation>());
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
            List<RootCause> attachedRootCauseList = new ArrayList<RootCause>();
            for (RootCause rootCauseListRootCauseToAttach : vmException.getRootCauseList()) {
                rootCauseListRootCauseToAttach = em.getReference(rootCauseListRootCauseToAttach.getClass(), rootCauseListRootCauseToAttach.getRootCausePK());
                attachedRootCauseList.add(rootCauseListRootCauseToAttach);
            }
            vmException.setRootCauseList(attachedRootCauseList);
            List<Requirement> attachedRequirementList = new ArrayList<Requirement>();
            for (Requirement requirementListRequirementToAttach : vmException.getRequirementList()) {
                requirementListRequirementToAttach = em.getReference(requirementListRequirementToAttach.getClass(), requirementListRequirementToAttach.getId());
                attachedRequirementList.add(requirementListRequirementToAttach);
            }
            vmException.setRequirementList(attachedRequirementList);
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
            List<ExecutionStep> attachedExecutionStepList = new ArrayList<ExecutionStep>();
            for (ExecutionStep executionStepListExecutionStepToAttach : vmException.getExecutionStepList()) {
                executionStepListExecutionStepToAttach = em.getReference(executionStepListExecutionStepToAttach.getClass(), executionStepListExecutionStepToAttach.getExecutionStepPK());
                attachedExecutionStepList.add(executionStepListExecutionStepToAttach);
            }
            vmException.setExecutionStepList(attachedExecutionStepList);
            List<Investigation> attachedInvestigationList = new ArrayList<Investigation>();
            for (Investigation investigationListInvestigationToAttach : vmException.getInvestigationList()) {
                investigationListInvestigationToAttach = em.getReference(investigationListInvestigationToAttach.getClass(), investigationListInvestigationToAttach.getId());
                attachedInvestigationList.add(investigationListInvestigationToAttach);
            }
            vmException.setInvestigationList(attachedInvestigationList);
            em.persist(vmException);
            if (vmUser != null) {
                vmUser.getVmExceptionList().add(vmException);
                vmUser = em.merge(vmUser);
            }
            for (RootCause rootCauseListRootCause : vmException.getRootCauseList()) {
                rootCauseListRootCause.getVmExceptionList().add(vmException);
                rootCauseListRootCause = em.merge(rootCauseListRootCause);
            }
            for (Requirement requirementListRequirement : vmException.getRequirementList()) {
                requirementListRequirement.getVmExceptionList().add(vmException);
                requirementListRequirement = em.merge(requirementListRequirement);
            }
            for (CorrectiveAction correctiveActionListCorrectiveAction : vmException.getCorrectiveActionList()) {
                correctiveActionListCorrectiveAction.getVmExceptionList().add(vmException);
                correctiveActionListCorrectiveAction = em.merge(correctiveActionListCorrectiveAction);
            }
            for (Step stepListStep : vmException.getStepList()) {
                stepListStep.getVmExceptionList().add(vmException);
                stepListStep = em.merge(stepListStep);
            }
            for (ExecutionStep executionStepListExecutionStep : vmException.getExecutionStepList()) {
                executionStepListExecutionStep.getVmExceptionList().add(vmException);
                executionStepListExecutionStep = em.merge(executionStepListExecutionStep);
            }
            for (Investigation investigationListInvestigation : vmException.getInvestigationList()) {
                investigationListInvestigation.getVmExceptionList().add(vmException);
                investigationListInvestigation = em.merge(investigationListInvestigation);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findVmException(vmException.getVmExceptionPK()) != null) {
                throw new PreexistingEntityException("VmException " + vmException + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(VmException vmException) throws NonexistentEntityException, Exception {
        vmException.getVmExceptionPK().setReporterId(vmException.getVmUser().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            VmException persistentVmException = em.find(VmException.class, vmException.getVmExceptionPK());
            VmUser vmUserOld = persistentVmException.getVmUser();
            VmUser vmUserNew = vmException.getVmUser();
            List<RootCause> rootCauseListOld = persistentVmException.getRootCauseList();
            List<RootCause> rootCauseListNew = vmException.getRootCauseList();
            List<Requirement> requirementListOld = persistentVmException.getRequirementList();
            List<Requirement> requirementListNew = vmException.getRequirementList();
            List<CorrectiveAction> correctiveActionListOld = persistentVmException.getCorrectiveActionList();
            List<CorrectiveAction> correctiveActionListNew = vmException.getCorrectiveActionList();
            List<Step> stepListOld = persistentVmException.getStepList();
            List<Step> stepListNew = vmException.getStepList();
            List<ExecutionStep> executionStepListOld = persistentVmException.getExecutionStepList();
            List<ExecutionStep> executionStepListNew = vmException.getExecutionStepList();
            List<Investigation> investigationListOld = persistentVmException.getInvestigationList();
            List<Investigation> investigationListNew = vmException.getInvestigationList();
            if (vmUserNew != null) {
                vmUserNew = em.getReference(vmUserNew.getClass(), vmUserNew.getId());
                vmException.setVmUser(vmUserNew);
            }
            List<RootCause> attachedRootCauseListNew = new ArrayList<RootCause>();
            for (RootCause rootCauseListNewRootCauseToAttach : rootCauseListNew) {
                rootCauseListNewRootCauseToAttach = em.getReference(rootCauseListNewRootCauseToAttach.getClass(), rootCauseListNewRootCauseToAttach.getRootCausePK());
                attachedRootCauseListNew.add(rootCauseListNewRootCauseToAttach);
            }
            rootCauseListNew = attachedRootCauseListNew;
            vmException.setRootCauseList(rootCauseListNew);
            List<Requirement> attachedRequirementListNew = new ArrayList<Requirement>();
            for (Requirement requirementListNewRequirementToAttach : requirementListNew) {
                requirementListNewRequirementToAttach = em.getReference(requirementListNewRequirementToAttach.getClass(), requirementListNewRequirementToAttach.getId());
                attachedRequirementListNew.add(requirementListNewRequirementToAttach);
            }
            requirementListNew = attachedRequirementListNew;
            vmException.setRequirementList(requirementListNew);
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
            List<ExecutionStep> attachedExecutionStepListNew = new ArrayList<ExecutionStep>();
            for (ExecutionStep executionStepListNewExecutionStepToAttach : executionStepListNew) {
                executionStepListNewExecutionStepToAttach = em.getReference(executionStepListNewExecutionStepToAttach.getClass(), executionStepListNewExecutionStepToAttach.getExecutionStepPK());
                attachedExecutionStepListNew.add(executionStepListNewExecutionStepToAttach);
            }
            executionStepListNew = attachedExecutionStepListNew;
            vmException.setExecutionStepList(executionStepListNew);
            List<Investigation> attachedInvestigationListNew = new ArrayList<Investigation>();
            for (Investigation investigationListNewInvestigationToAttach : investigationListNew) {
                investigationListNewInvestigationToAttach = em.getReference(investigationListNewInvestigationToAttach.getClass(), investigationListNewInvestigationToAttach.getId());
                attachedInvestigationListNew.add(investigationListNewInvestigationToAttach);
            }
            investigationListNew = attachedInvestigationListNew;
            vmException.setInvestigationList(investigationListNew);
            vmException = em.merge(vmException);
            if (vmUserOld != null && !vmUserOld.equals(vmUserNew)) {
                vmUserOld.getVmExceptionList().remove(vmException);
                vmUserOld = em.merge(vmUserOld);
            }
            if (vmUserNew != null && !vmUserNew.equals(vmUserOld)) {
                vmUserNew.getVmExceptionList().add(vmException);
                vmUserNew = em.merge(vmUserNew);
            }
            for (RootCause rootCauseListOldRootCause : rootCauseListOld) {
                if (!rootCauseListNew.contains(rootCauseListOldRootCause)) {
                    rootCauseListOldRootCause.getVmExceptionList().remove(vmException);
                    rootCauseListOldRootCause = em.merge(rootCauseListOldRootCause);
                }
            }
            for (RootCause rootCauseListNewRootCause : rootCauseListNew) {
                if (!rootCauseListOld.contains(rootCauseListNewRootCause)) {
                    rootCauseListNewRootCause.getVmExceptionList().add(vmException);
                    rootCauseListNewRootCause = em.merge(rootCauseListNewRootCause);
                }
            }
            for (Requirement requirementListOldRequirement : requirementListOld) {
                if (!requirementListNew.contains(requirementListOldRequirement)) {
                    requirementListOldRequirement.getVmExceptionList().remove(vmException);
                    requirementListOldRequirement = em.merge(requirementListOldRequirement);
                }
            }
            for (Requirement requirementListNewRequirement : requirementListNew) {
                if (!requirementListOld.contains(requirementListNewRequirement)) {
                    requirementListNewRequirement.getVmExceptionList().add(vmException);
                    requirementListNewRequirement = em.merge(requirementListNewRequirement);
                }
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
            for (ExecutionStep executionStepListOldExecutionStep : executionStepListOld) {
                if (!executionStepListNew.contains(executionStepListOldExecutionStep)) {
                    executionStepListOldExecutionStep.getVmExceptionList().remove(vmException);
                    executionStepListOldExecutionStep = em.merge(executionStepListOldExecutionStep);
                }
            }
            for (ExecutionStep executionStepListNewExecutionStep : executionStepListNew) {
                if (!executionStepListOld.contains(executionStepListNewExecutionStep)) {
                    executionStepListNewExecutionStep.getVmExceptionList().add(vmException);
                    executionStepListNewExecutionStep = em.merge(executionStepListNewExecutionStep);
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
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                VmExceptionPK id = vmException.getVmExceptionPK();
                if (findVmException(id) == null) {
                    throw new NonexistentEntityException("The vmException with id " + id + " no longer exists.");
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

    public void destroy(VmExceptionPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            VmException vmException;
            try {
                vmException = em.getReference(VmException.class, id);
                vmException.getVmExceptionPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The vmException with id " + id + " no longer exists.", enfe);
            }
            VmUser vmUser = vmException.getVmUser();
            if (vmUser != null) {
                vmUser.getVmExceptionList().remove(vmException);
                vmUser = em.merge(vmUser);
            }
            List<RootCause> rootCauseList = vmException.getRootCauseList();
            for (RootCause rootCauseListRootCause : rootCauseList) {
                rootCauseListRootCause.getVmExceptionList().remove(vmException);
                rootCauseListRootCause = em.merge(rootCauseListRootCause);
            }
            List<Requirement> requirementList = vmException.getRequirementList();
            for (Requirement requirementListRequirement : requirementList) {
                requirementListRequirement.getVmExceptionList().remove(vmException);
                requirementListRequirement = em.merge(requirementListRequirement);
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
            List<ExecutionStep> executionStepList = vmException.getExecutionStepList();
            for (ExecutionStep executionStepListExecutionStep : executionStepList) {
                executionStepListExecutionStep.getVmExceptionList().remove(vmException);
                executionStepListExecutionStep = em.merge(executionStepListExecutionStep);
            }
            List<Investigation> investigationList = vmException.getInvestigationList();
            for (Investigation investigationListInvestigation : investigationList) {
                investigationListInvestigation.getVmExceptionList().remove(vmException);
                investigationListInvestigation = em.merge(investigationListInvestigation);
            }
            em.remove(vmException);
            em.getTransaction().commit();
        }
        finally {
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
        }
        finally {
            em.close();
        }
    }

    public VmException findVmException(VmExceptionPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(VmException.class, id);
        }
        finally {
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
        }
        finally {
            em.close();
        }
    }

}
