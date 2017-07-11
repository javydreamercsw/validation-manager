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

import com.validation.manager.core.db.CorrectiveAction;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.VmUser;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.ExceptionHasCorrectiveAction;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class CorrectiveActionJpaController implements Serializable {

    public CorrectiveActionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CorrectiveAction correctiveAction) {
        if (correctiveAction.getVmUserList() == null) {
            correctiveAction.setVmUserList(new ArrayList<>());
        }
        if (correctiveAction.getExceptionHasCorrectiveActionList() == null) {
            correctiveAction.setExceptionHasCorrectiveActionList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<VmUser> attachedVmUserList = new ArrayList<>();
            for (VmUser vmUserListVmUserToAttach : correctiveAction.getVmUserList()) {
                vmUserListVmUserToAttach = em.getReference(vmUserListVmUserToAttach.getClass(), vmUserListVmUserToAttach.getId());
                attachedVmUserList.add(vmUserListVmUserToAttach);
            }
            correctiveAction.setVmUserList(attachedVmUserList);
            List<ExceptionHasCorrectiveAction> attachedExceptionHasCorrectiveActionList = new ArrayList<>();
            for (ExceptionHasCorrectiveAction exceptionHasCorrectiveActionListExceptionHasCorrectiveActionToAttach : correctiveAction.getExceptionHasCorrectiveActionList()) {
                exceptionHasCorrectiveActionListExceptionHasCorrectiveActionToAttach = em.getReference(exceptionHasCorrectiveActionListExceptionHasCorrectiveActionToAttach.getClass(), exceptionHasCorrectiveActionListExceptionHasCorrectiveActionToAttach.getExceptionHasCorrectiveActionPK());
                attachedExceptionHasCorrectiveActionList.add(exceptionHasCorrectiveActionListExceptionHasCorrectiveActionToAttach);
            }
            correctiveAction.setExceptionHasCorrectiveActionList(attachedExceptionHasCorrectiveActionList);
            em.persist(correctiveAction);
            for (VmUser vmUserListVmUser : correctiveAction.getVmUserList()) {
                vmUserListVmUser.getCorrectiveActionList().add(correctiveAction);
                vmUserListVmUser = em.merge(vmUserListVmUser);
            }
            for (ExceptionHasCorrectiveAction exceptionHasCorrectiveActionListExceptionHasCorrectiveAction : correctiveAction.getExceptionHasCorrectiveActionList()) {
                CorrectiveAction oldCorrectiveActionOfExceptionHasCorrectiveActionListExceptionHasCorrectiveAction = exceptionHasCorrectiveActionListExceptionHasCorrectiveAction.getCorrectiveAction();
                exceptionHasCorrectiveActionListExceptionHasCorrectiveAction.setCorrectiveAction(correctiveAction);
                exceptionHasCorrectiveActionListExceptionHasCorrectiveAction = em.merge(exceptionHasCorrectiveActionListExceptionHasCorrectiveAction);
                if (oldCorrectiveActionOfExceptionHasCorrectiveActionListExceptionHasCorrectiveAction != null) {
                    oldCorrectiveActionOfExceptionHasCorrectiveActionListExceptionHasCorrectiveAction.getExceptionHasCorrectiveActionList().remove(exceptionHasCorrectiveActionListExceptionHasCorrectiveAction);
                    oldCorrectiveActionOfExceptionHasCorrectiveActionListExceptionHasCorrectiveAction = em.merge(oldCorrectiveActionOfExceptionHasCorrectiveActionListExceptionHasCorrectiveAction);
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

    public void edit(CorrectiveAction correctiveAction) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CorrectiveAction persistentCorrectiveAction = em.find(CorrectiveAction.class, correctiveAction.getId());
            List<VmUser> vmUserListOld = persistentCorrectiveAction.getVmUserList();
            List<VmUser> vmUserListNew = correctiveAction.getVmUserList();
            List<ExceptionHasCorrectiveAction> exceptionHasCorrectiveActionListOld = persistentCorrectiveAction.getExceptionHasCorrectiveActionList();
            List<ExceptionHasCorrectiveAction> exceptionHasCorrectiveActionListNew = correctiveAction.getExceptionHasCorrectiveActionList();
            List<String> illegalOrphanMessages = null;
            for (ExceptionHasCorrectiveAction exceptionHasCorrectiveActionListOldExceptionHasCorrectiveAction : exceptionHasCorrectiveActionListOld) {
                if (!exceptionHasCorrectiveActionListNew.contains(exceptionHasCorrectiveActionListOldExceptionHasCorrectiveAction)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain ExceptionHasCorrectiveAction " + exceptionHasCorrectiveActionListOldExceptionHasCorrectiveAction + " since its correctiveAction field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<VmUser> attachedVmUserListNew = new ArrayList<>();
            for (VmUser vmUserListNewVmUserToAttach : vmUserListNew) {
                vmUserListNewVmUserToAttach = em.getReference(vmUserListNewVmUserToAttach.getClass(), vmUserListNewVmUserToAttach.getId());
                attachedVmUserListNew.add(vmUserListNewVmUserToAttach);
            }
            vmUserListNew = attachedVmUserListNew;
            correctiveAction.setVmUserList(vmUserListNew);
            List<ExceptionHasCorrectiveAction> attachedExceptionHasCorrectiveActionListNew = new ArrayList<>();
            for (ExceptionHasCorrectiveAction exceptionHasCorrectiveActionListNewExceptionHasCorrectiveActionToAttach : exceptionHasCorrectiveActionListNew) {
                exceptionHasCorrectiveActionListNewExceptionHasCorrectiveActionToAttach = em.getReference(exceptionHasCorrectiveActionListNewExceptionHasCorrectiveActionToAttach.getClass(), exceptionHasCorrectiveActionListNewExceptionHasCorrectiveActionToAttach.getExceptionHasCorrectiveActionPK());
                attachedExceptionHasCorrectiveActionListNew.add(exceptionHasCorrectiveActionListNewExceptionHasCorrectiveActionToAttach);
            }
            exceptionHasCorrectiveActionListNew = attachedExceptionHasCorrectiveActionListNew;
            correctiveAction.setExceptionHasCorrectiveActionList(exceptionHasCorrectiveActionListNew);
            correctiveAction = em.merge(correctiveAction);
            for (VmUser vmUserListOldVmUser : vmUserListOld) {
                if (!vmUserListNew.contains(vmUserListOldVmUser)) {
                    vmUserListOldVmUser.getCorrectiveActionList().remove(correctiveAction);
                    vmUserListOldVmUser = em.merge(vmUserListOldVmUser);
                }
            }
            for (VmUser vmUserListNewVmUser : vmUserListNew) {
                if (!vmUserListOld.contains(vmUserListNewVmUser)) {
                    vmUserListNewVmUser.getCorrectiveActionList().add(correctiveAction);
                    vmUserListNewVmUser = em.merge(vmUserListNewVmUser);
                }
            }
            for (ExceptionHasCorrectiveAction exceptionHasCorrectiveActionListNewExceptionHasCorrectiveAction : exceptionHasCorrectiveActionListNew) {
                if (!exceptionHasCorrectiveActionListOld.contains(exceptionHasCorrectiveActionListNewExceptionHasCorrectiveAction)) {
                    CorrectiveAction oldCorrectiveActionOfExceptionHasCorrectiveActionListNewExceptionHasCorrectiveAction = exceptionHasCorrectiveActionListNewExceptionHasCorrectiveAction.getCorrectiveAction();
                    exceptionHasCorrectiveActionListNewExceptionHasCorrectiveAction.setCorrectiveAction(correctiveAction);
                    exceptionHasCorrectiveActionListNewExceptionHasCorrectiveAction = em.merge(exceptionHasCorrectiveActionListNewExceptionHasCorrectiveAction);
                    if (oldCorrectiveActionOfExceptionHasCorrectiveActionListNewExceptionHasCorrectiveAction != null && !oldCorrectiveActionOfExceptionHasCorrectiveActionListNewExceptionHasCorrectiveAction.equals(correctiveAction)) {
                        oldCorrectiveActionOfExceptionHasCorrectiveActionListNewExceptionHasCorrectiveAction.getExceptionHasCorrectiveActionList().remove(exceptionHasCorrectiveActionListNewExceptionHasCorrectiveAction);
                        oldCorrectiveActionOfExceptionHasCorrectiveActionListNewExceptionHasCorrectiveAction = em.merge(oldCorrectiveActionOfExceptionHasCorrectiveActionListNewExceptionHasCorrectiveAction);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = correctiveAction.getId();
                if (findCorrectiveAction(id) == null) {
                    throw new NonexistentEntityException("The correctiveAction with id " + id + " no longer exists.");
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
            CorrectiveAction correctiveAction;
            try {
                correctiveAction = em.getReference(CorrectiveAction.class, id);
                correctiveAction.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The correctiveAction with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<ExceptionHasCorrectiveAction> exceptionHasCorrectiveActionListOrphanCheck = correctiveAction.getExceptionHasCorrectiveActionList();
            for (ExceptionHasCorrectiveAction exceptionHasCorrectiveActionListOrphanCheckExceptionHasCorrectiveAction : exceptionHasCorrectiveActionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This CorrectiveAction (" + correctiveAction + ") cannot be destroyed since the ExceptionHasCorrectiveAction " + exceptionHasCorrectiveActionListOrphanCheckExceptionHasCorrectiveAction + " in its exceptionHasCorrectiveActionList field has a non-nullable correctiveAction field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<VmUser> vmUserList = correctiveAction.getVmUserList();
            for (VmUser vmUserListVmUser : vmUserList) {
                vmUserListVmUser.getCorrectiveActionList().remove(correctiveAction);
                vmUserListVmUser = em.merge(vmUserListVmUser);
            }
            em.remove(correctiveAction);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CorrectiveAction> findCorrectiveActionEntities() {
        return findCorrectiveActionEntities(true, -1, -1);
    }

    public List<CorrectiveAction> findCorrectiveActionEntities(int maxResults, int firstResult) {
        return findCorrectiveActionEntities(false, maxResults, firstResult);
    }

    private List<CorrectiveAction> findCorrectiveActionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CorrectiveAction.class));
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

    public CorrectiveAction findCorrectiveAction(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CorrectiveAction.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getCorrectiveActionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CorrectiveAction> rt = cq.from(CorrectiveAction.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
