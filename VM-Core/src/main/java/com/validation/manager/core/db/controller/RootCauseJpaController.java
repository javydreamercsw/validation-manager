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
import com.validation.manager.core.db.RootCauseType;
import com.validation.manager.core.db.VmUser;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.ExceptionHasRootCause;
import com.validation.manager.core.db.RootCause;
import com.validation.manager.core.db.RootCausePK;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class RootCauseJpaController implements Serializable {

    public RootCauseJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RootCause rootCause) throws PreexistingEntityException, Exception {
        if (rootCause.getRootCausePK() == null) {
            rootCause.setRootCausePK(new RootCausePK());
        }
        if (rootCause.getVmUserList() == null) {
            rootCause.setVmUserList(new ArrayList<>());
        }
        if (rootCause.getExceptionHasRootCauseList() == null) {
            rootCause.setExceptionHasRootCauseList(new ArrayList<>());
        }
        rootCause.getRootCausePK().setRootCauseTypeId(rootCause.getRootCauseType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RootCauseType rootCauseType = rootCause.getRootCauseType();
            if (rootCauseType != null) {
                rootCauseType = em.getReference(rootCauseType.getClass(), rootCauseType.getId());
                rootCause.setRootCauseType(rootCauseType);
            }
            List<VmUser> attachedVmUserList = new ArrayList<>();
            for (VmUser vmUserListVmUserToAttach : rootCause.getVmUserList()) {
                vmUserListVmUserToAttach = em.getReference(vmUserListVmUserToAttach.getClass(), vmUserListVmUserToAttach.getId());
                attachedVmUserList.add(vmUserListVmUserToAttach);
            }
            rootCause.setVmUserList(attachedVmUserList);
            List<ExceptionHasRootCause> attachedExceptionHasRootCauseList = new ArrayList<>();
            for (ExceptionHasRootCause exceptionHasRootCauseListExceptionHasRootCauseToAttach : rootCause.getExceptionHasRootCauseList()) {
                exceptionHasRootCauseListExceptionHasRootCauseToAttach = em.getReference(exceptionHasRootCauseListExceptionHasRootCauseToAttach.getClass(), exceptionHasRootCauseListExceptionHasRootCauseToAttach.getExceptionHasRootCausePK());
                attachedExceptionHasRootCauseList.add(exceptionHasRootCauseListExceptionHasRootCauseToAttach);
            }
            rootCause.setExceptionHasRootCauseList(attachedExceptionHasRootCauseList);
            em.persist(rootCause);
            if (rootCauseType != null) {
                rootCauseType.getRootCauseList().add(rootCause);
                rootCauseType = em.merge(rootCauseType);
            }
            for (VmUser vmUserListVmUser : rootCause.getVmUserList()) {
                vmUserListVmUser.getRootCauseList().add(rootCause);
                vmUserListVmUser = em.merge(vmUserListVmUser);
            }
            for (ExceptionHasRootCause exceptionHasRootCauseListExceptionHasRootCause : rootCause.getExceptionHasRootCauseList()) {
                RootCause oldRootCauseOfExceptionHasRootCauseListExceptionHasRootCause = exceptionHasRootCauseListExceptionHasRootCause.getRootCause();
                exceptionHasRootCauseListExceptionHasRootCause.setRootCause(rootCause);
                exceptionHasRootCauseListExceptionHasRootCause = em.merge(exceptionHasRootCauseListExceptionHasRootCause);
                if (oldRootCauseOfExceptionHasRootCauseListExceptionHasRootCause != null) {
                    oldRootCauseOfExceptionHasRootCauseListExceptionHasRootCause.getExceptionHasRootCauseList().remove(exceptionHasRootCauseListExceptionHasRootCause);
                    oldRootCauseOfExceptionHasRootCauseListExceptionHasRootCause = em.merge(oldRootCauseOfExceptionHasRootCauseListExceptionHasRootCause);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findRootCause(rootCause.getRootCausePK()) != null) {
                throw new PreexistingEntityException("RootCause " + rootCause + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RootCause rootCause) throws IllegalOrphanException, NonexistentEntityException, Exception {
        rootCause.getRootCausePK().setRootCauseTypeId(rootCause.getRootCauseType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RootCause persistentRootCause = em.find(RootCause.class, rootCause.getRootCausePK());
            RootCauseType rootCauseTypeOld = persistentRootCause.getRootCauseType();
            RootCauseType rootCauseTypeNew = rootCause.getRootCauseType();
            List<VmUser> vmUserListOld = persistentRootCause.getVmUserList();
            List<VmUser> vmUserListNew = rootCause.getVmUserList();
            List<ExceptionHasRootCause> exceptionHasRootCauseListOld = persistentRootCause.getExceptionHasRootCauseList();
            List<ExceptionHasRootCause> exceptionHasRootCauseListNew = rootCause.getExceptionHasRootCauseList();
            List<String> illegalOrphanMessages = null;
            for (ExceptionHasRootCause exceptionHasRootCauseListOldExceptionHasRootCause : exceptionHasRootCauseListOld) {
                if (!exceptionHasRootCauseListNew.contains(exceptionHasRootCauseListOldExceptionHasRootCause)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain ExceptionHasRootCause " + exceptionHasRootCauseListOldExceptionHasRootCause + " since its rootCause field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (rootCauseTypeNew != null) {
                rootCauseTypeNew = em.getReference(rootCauseTypeNew.getClass(), rootCauseTypeNew.getId());
                rootCause.setRootCauseType(rootCauseTypeNew);
            }
            List<VmUser> attachedVmUserListNew = new ArrayList<>();
            for (VmUser vmUserListNewVmUserToAttach : vmUserListNew) {
                vmUserListNewVmUserToAttach = em.getReference(vmUserListNewVmUserToAttach.getClass(), vmUserListNewVmUserToAttach.getId());
                attachedVmUserListNew.add(vmUserListNewVmUserToAttach);
            }
            vmUserListNew = attachedVmUserListNew;
            rootCause.setVmUserList(vmUserListNew);
            List<ExceptionHasRootCause> attachedExceptionHasRootCauseListNew = new ArrayList<>();
            for (ExceptionHasRootCause exceptionHasRootCauseListNewExceptionHasRootCauseToAttach : exceptionHasRootCauseListNew) {
                exceptionHasRootCauseListNewExceptionHasRootCauseToAttach = em.getReference(exceptionHasRootCauseListNewExceptionHasRootCauseToAttach.getClass(), exceptionHasRootCauseListNewExceptionHasRootCauseToAttach.getExceptionHasRootCausePK());
                attachedExceptionHasRootCauseListNew.add(exceptionHasRootCauseListNewExceptionHasRootCauseToAttach);
            }
            exceptionHasRootCauseListNew = attachedExceptionHasRootCauseListNew;
            rootCause.setExceptionHasRootCauseList(exceptionHasRootCauseListNew);
            rootCause = em.merge(rootCause);
            if (rootCauseTypeOld != null && !rootCauseTypeOld.equals(rootCauseTypeNew)) {
                rootCauseTypeOld.getRootCauseList().remove(rootCause);
                rootCauseTypeOld = em.merge(rootCauseTypeOld);
            }
            if (rootCauseTypeNew != null && !rootCauseTypeNew.equals(rootCauseTypeOld)) {
                rootCauseTypeNew.getRootCauseList().add(rootCause);
                rootCauseTypeNew = em.merge(rootCauseTypeNew);
            }
            for (VmUser vmUserListOldVmUser : vmUserListOld) {
                if (!vmUserListNew.contains(vmUserListOldVmUser)) {
                    vmUserListOldVmUser.getRootCauseList().remove(rootCause);
                    vmUserListOldVmUser = em.merge(vmUserListOldVmUser);
                }
            }
            for (VmUser vmUserListNewVmUser : vmUserListNew) {
                if (!vmUserListOld.contains(vmUserListNewVmUser)) {
                    vmUserListNewVmUser.getRootCauseList().add(rootCause);
                    vmUserListNewVmUser = em.merge(vmUserListNewVmUser);
                }
            }
            for (ExceptionHasRootCause exceptionHasRootCauseListNewExceptionHasRootCause : exceptionHasRootCauseListNew) {
                if (!exceptionHasRootCauseListOld.contains(exceptionHasRootCauseListNewExceptionHasRootCause)) {
                    RootCause oldRootCauseOfExceptionHasRootCauseListNewExceptionHasRootCause = exceptionHasRootCauseListNewExceptionHasRootCause.getRootCause();
                    exceptionHasRootCauseListNewExceptionHasRootCause.setRootCause(rootCause);
                    exceptionHasRootCauseListNewExceptionHasRootCause = em.merge(exceptionHasRootCauseListNewExceptionHasRootCause);
                    if (oldRootCauseOfExceptionHasRootCauseListNewExceptionHasRootCause != null && !oldRootCauseOfExceptionHasRootCauseListNewExceptionHasRootCause.equals(rootCause)) {
                        oldRootCauseOfExceptionHasRootCauseListNewExceptionHasRootCause.getExceptionHasRootCauseList().remove(exceptionHasRootCauseListNewExceptionHasRootCause);
                        oldRootCauseOfExceptionHasRootCauseListNewExceptionHasRootCause = em.merge(oldRootCauseOfExceptionHasRootCauseListNewExceptionHasRootCause);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RootCausePK id = rootCause.getRootCausePK();
                if (findRootCause(id) == null) {
                    throw new NonexistentEntityException("The rootCause with id " + id + " no longer exists.");
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

    public void destroy(RootCausePK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RootCause rootCause;
            try {
                rootCause = em.getReference(RootCause.class, id);
                rootCause.getRootCausePK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rootCause with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<ExceptionHasRootCause> exceptionHasRootCauseListOrphanCheck = rootCause.getExceptionHasRootCauseList();
            for (ExceptionHasRootCause exceptionHasRootCauseListOrphanCheckExceptionHasRootCause : exceptionHasRootCauseListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This RootCause (" + rootCause + ") cannot be destroyed since the ExceptionHasRootCause " + exceptionHasRootCauseListOrphanCheckExceptionHasRootCause + " in its exceptionHasRootCauseList field has a non-nullable rootCause field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            RootCauseType rootCauseType = rootCause.getRootCauseType();
            if (rootCauseType != null) {
                rootCauseType.getRootCauseList().remove(rootCause);
                rootCauseType = em.merge(rootCauseType);
            }
            List<VmUser> vmUserList = rootCause.getVmUserList();
            for (VmUser vmUserListVmUser : vmUserList) {
                vmUserListVmUser.getRootCauseList().remove(rootCause);
                vmUserListVmUser = em.merge(vmUserListVmUser);
            }
            em.remove(rootCause);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RootCause> findRootCauseEntities() {
        return findRootCauseEntities(true, -1, -1);
    }

    public List<RootCause> findRootCauseEntities(int maxResults, int firstResult) {
        return findRootCauseEntities(false, maxResults, firstResult);
    }

    private List<RootCause> findRootCauseEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RootCause.class));
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

    public RootCause findRootCause(RootCausePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RootCause.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getRootCauseCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RootCause> rt = cq.from(RootCause.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
