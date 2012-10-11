/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.Investigation;
import com.validation.manager.core.db.UserHasInvestigation;
import com.validation.manager.core.db.VmException;
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
public class InvestigationJpaController implements Serializable {

    public InvestigationJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Investigation investigation) {
        if (investigation.getVmExceptionList() == null) {
            investigation.setVmExceptionList(new ArrayList<VmException>());
        }
        if (investigation.getUserHasInvestigationList() == null) {
            investigation.setUserHasInvestigationList(new ArrayList<UserHasInvestigation>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<VmException> attachedVmExceptionList = new ArrayList<VmException>();
            for (VmException vmExceptionListVmExceptionToAttach : investigation.getVmExceptionList()) {
                vmExceptionListVmExceptionToAttach = em.getReference(vmExceptionListVmExceptionToAttach.getClass(), vmExceptionListVmExceptionToAttach.getVmExceptionPK());
                attachedVmExceptionList.add(vmExceptionListVmExceptionToAttach);
            }
            investigation.setVmExceptionList(attachedVmExceptionList);
            List<UserHasInvestigation> attachedUserHasInvestigationList = new ArrayList<UserHasInvestigation>();
            for (UserHasInvestigation userHasInvestigationListUserHasInvestigationToAttach : investigation.getUserHasInvestigationList()) {
                userHasInvestigationListUserHasInvestigationToAttach = em.getReference(userHasInvestigationListUserHasInvestigationToAttach.getClass(), userHasInvestigationListUserHasInvestigationToAttach.getUserHasInvestigationPK());
                attachedUserHasInvestigationList.add(userHasInvestigationListUserHasInvestigationToAttach);
            }
            investigation.setUserHasInvestigationList(attachedUserHasInvestigationList);
            em.persist(investigation);
            for (VmException vmExceptionListVmException : investigation.getVmExceptionList()) {
                vmExceptionListVmException.getInvestigationList().add(investigation);
                vmExceptionListVmException = em.merge(vmExceptionListVmException);
            }
            for (UserHasInvestigation userHasInvestigationListUserHasInvestigation : investigation.getUserHasInvestigationList()) {
                Investigation oldInvestigationOfUserHasInvestigationListUserHasInvestigation = userHasInvestigationListUserHasInvestigation.getInvestigation();
                userHasInvestigationListUserHasInvestigation.setInvestigation(investigation);
                userHasInvestigationListUserHasInvestigation = em.merge(userHasInvestigationListUserHasInvestigation);
                if (oldInvestigationOfUserHasInvestigationListUserHasInvestigation != null) {
                    oldInvestigationOfUserHasInvestigationListUserHasInvestigation.getUserHasInvestigationList().remove(userHasInvestigationListUserHasInvestigation);
                    oldInvestigationOfUserHasInvestigationListUserHasInvestigation = em.merge(oldInvestigationOfUserHasInvestigationListUserHasInvestigation);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Investigation investigation) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Investigation persistentInvestigation = em.find(Investigation.class, investigation.getId());
            List<VmException> vmExceptionListOld = persistentInvestigation.getVmExceptionList();
            List<VmException> vmExceptionListNew = investigation.getVmExceptionList();
            List<UserHasInvestigation> userHasInvestigationListOld = persistentInvestigation.getUserHasInvestigationList();
            List<UserHasInvestigation> userHasInvestigationListNew = investigation.getUserHasInvestigationList();
            List<String> illegalOrphanMessages = null;
            for (UserHasInvestigation userHasInvestigationListOldUserHasInvestigation : userHasInvestigationListOld) {
                if (!userHasInvestigationListNew.contains(userHasInvestigationListOldUserHasInvestigation)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain UserHasInvestigation " + userHasInvestigationListOldUserHasInvestigation + " since its investigation field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<VmException> attachedVmExceptionListNew = new ArrayList<VmException>();
            for (VmException vmExceptionListNewVmExceptionToAttach : vmExceptionListNew) {
                vmExceptionListNewVmExceptionToAttach = em.getReference(vmExceptionListNewVmExceptionToAttach.getClass(), vmExceptionListNewVmExceptionToAttach.getVmExceptionPK());
                attachedVmExceptionListNew.add(vmExceptionListNewVmExceptionToAttach);
            }
            vmExceptionListNew = attachedVmExceptionListNew;
            investigation.setVmExceptionList(vmExceptionListNew);
            List<UserHasInvestigation> attachedUserHasInvestigationListNew = new ArrayList<UserHasInvestigation>();
            for (UserHasInvestigation userHasInvestigationListNewUserHasInvestigationToAttach : userHasInvestigationListNew) {
                userHasInvestigationListNewUserHasInvestigationToAttach = em.getReference(userHasInvestigationListNewUserHasInvestigationToAttach.getClass(), userHasInvestigationListNewUserHasInvestigationToAttach.getUserHasInvestigationPK());
                attachedUserHasInvestigationListNew.add(userHasInvestigationListNewUserHasInvestigationToAttach);
            }
            userHasInvestigationListNew = attachedUserHasInvestigationListNew;
            investigation.setUserHasInvestigationList(userHasInvestigationListNew);
            investigation = em.merge(investigation);
            for (VmException vmExceptionListOldVmException : vmExceptionListOld) {
                if (!vmExceptionListNew.contains(vmExceptionListOldVmException)) {
                    vmExceptionListOldVmException.getInvestigationList().remove(investigation);
                    vmExceptionListOldVmException = em.merge(vmExceptionListOldVmException);
                }
            }
            for (VmException vmExceptionListNewVmException : vmExceptionListNew) {
                if (!vmExceptionListOld.contains(vmExceptionListNewVmException)) {
                    vmExceptionListNewVmException.getInvestigationList().add(investigation);
                    vmExceptionListNewVmException = em.merge(vmExceptionListNewVmException);
                }
            }
            for (UserHasInvestigation userHasInvestigationListNewUserHasInvestigation : userHasInvestigationListNew) {
                if (!userHasInvestigationListOld.contains(userHasInvestigationListNewUserHasInvestigation)) {
                    Investigation oldInvestigationOfUserHasInvestigationListNewUserHasInvestigation = userHasInvestigationListNewUserHasInvestigation.getInvestigation();
                    userHasInvestigationListNewUserHasInvestigation.setInvestigation(investigation);
                    userHasInvestigationListNewUserHasInvestigation = em.merge(userHasInvestigationListNewUserHasInvestigation);
                    if (oldInvestigationOfUserHasInvestigationListNewUserHasInvestigation != null && !oldInvestigationOfUserHasInvestigationListNewUserHasInvestigation.equals(investigation)) {
                        oldInvestigationOfUserHasInvestigationListNewUserHasInvestigation.getUserHasInvestigationList().remove(userHasInvestigationListNewUserHasInvestigation);
                        oldInvestigationOfUserHasInvestigationListNewUserHasInvestigation = em.merge(oldInvestigationOfUserHasInvestigationListNewUserHasInvestigation);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = investigation.getId();
                if (findInvestigation(id) == null) {
                    throw new NonexistentEntityException("The investigation with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
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
            Investigation investigation;
            try {
                investigation = em.getReference(Investigation.class, id);
                investigation.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The investigation with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<UserHasInvestigation> userHasInvestigationListOrphanCheck = investigation.getUserHasInvestigationList();
            for (UserHasInvestigation userHasInvestigationListOrphanCheckUserHasInvestigation : userHasInvestigationListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Investigation (" + investigation + ") cannot be destroyed since the UserHasInvestigation " + userHasInvestigationListOrphanCheckUserHasInvestigation + " in its userHasInvestigationList field has a non-nullable investigation field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<VmException> vmExceptionList = investigation.getVmExceptionList();
            for (VmException vmExceptionListVmException : vmExceptionList) {
                vmExceptionListVmException.getInvestigationList().remove(investigation);
                vmExceptionListVmException = em.merge(vmExceptionListVmException);
            }
            em.remove(investigation);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Investigation> findInvestigationEntities() {
        return findInvestigationEntities(true, -1, -1);
    }

    public List<Investigation> findInvestigationEntities(int maxResults, int firstResult) {
        return findInvestigationEntities(false, maxResults, firstResult);
    }

    private List<Investigation> findInvestigationEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Investigation.class));
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

    public Investigation findInvestigation(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Investigation.class, id);
        } finally {
            em.close();
        }
    }

    public int getInvestigationCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Investigation> rt = cq.from(Investigation.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
