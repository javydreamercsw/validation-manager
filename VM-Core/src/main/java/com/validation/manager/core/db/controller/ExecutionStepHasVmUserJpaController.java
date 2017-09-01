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
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.ExecutionStepHasVmUser;
import com.validation.manager.core.db.ExecutionStepHasVmUserPK;
import com.validation.manager.core.db.Role;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ExecutionStepHasVmUserJpaController implements Serializable {

    public ExecutionStepHasVmUserJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ExecutionStepHasVmUser executionStepHasVmUser) throws PreexistingEntityException, Exception {
        if (executionStepHasVmUser.getExecutionStepHasVmUserPK() == null) {
            executionStepHasVmUser.setExecutionStepHasVmUserPK(new ExecutionStepHasVmUserPK());
        }
        executionStepHasVmUser.getExecutionStepHasVmUserPK().setExecutionStepStepId(executionStepHasVmUser.getExecutionStep().getExecutionStepPK().getStepId());
        executionStepHasVmUser.getExecutionStepHasVmUserPK().setExecutionStepStepTestCaseId(executionStepHasVmUser.getExecutionStep().getExecutionStepPK().getStepTestCaseId());
        executionStepHasVmUser.getExecutionStepHasVmUserPK().setExecutionStepTestCaseExecutionId(executionStepHasVmUser.getExecutionStep().getExecutionStepPK().getTestCaseExecutionId());
        executionStepHasVmUser.getExecutionStepHasVmUserPK().setRoleId(executionStepHasVmUser.getRole().getId());
        executionStepHasVmUser.getExecutionStepHasVmUserPK().setVmUserId(executionStepHasVmUser.getVmUser().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExecutionStep executionStep = executionStepHasVmUser.getExecutionStep();
            if (executionStep != null) {
                executionStep = em.getReference(executionStep.getClass(), executionStep.getExecutionStepPK());
                executionStepHasVmUser.setExecutionStep(executionStep);
            }
            Role role = executionStepHasVmUser.getRole();
            if (role != null) {
                role = em.getReference(role.getClass(), role.getId());
                executionStepHasVmUser.setRole(role);
            }
            VmUser vmUser = executionStepHasVmUser.getVmUser();
            if (vmUser != null) {
                vmUser = em.getReference(vmUser.getClass(), vmUser.getId());
                executionStepHasVmUser.setVmUser(vmUser);
            }
            em.persist(executionStepHasVmUser);
            if (executionStep != null) {
                executionStep.getExecutionStepHasVmUserList().add(executionStepHasVmUser);
                executionStep = em.merge(executionStep);
            }
            if (role != null) {
                role.getExecutionStepHasVmUserList().add(executionStepHasVmUser);
                role = em.merge(role);
            }
            if (vmUser != null) {
                vmUser.getExecutionStepHasVmUserList().add(executionStepHasVmUser);
                vmUser = em.merge(vmUser);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findExecutionStepHasVmUser(executionStepHasVmUser.getExecutionStepHasVmUserPK()) != null) {
                throw new PreexistingEntityException("ExecutionStepHasVmUser " + executionStepHasVmUser + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ExecutionStepHasVmUser executionStepHasVmUser) throws NonexistentEntityException, Exception {
        executionStepHasVmUser.getExecutionStepHasVmUserPK().setExecutionStepStepId(executionStepHasVmUser.getExecutionStep().getExecutionStepPK().getStepId());
        executionStepHasVmUser.getExecutionStepHasVmUserPK().setExecutionStepStepTestCaseId(executionStepHasVmUser.getExecutionStep().getExecutionStepPK().getStepTestCaseId());
        executionStepHasVmUser.getExecutionStepHasVmUserPK().setExecutionStepTestCaseExecutionId(executionStepHasVmUser.getExecutionStep().getExecutionStepPK().getTestCaseExecutionId());
        executionStepHasVmUser.getExecutionStepHasVmUserPK().setRoleId(executionStepHasVmUser.getRole().getId());
        executionStepHasVmUser.getExecutionStepHasVmUserPK().setVmUserId(executionStepHasVmUser.getVmUser().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExecutionStepHasVmUser persistentExecutionStepHasVmUser = em.find(ExecutionStepHasVmUser.class, executionStepHasVmUser.getExecutionStepHasVmUserPK());
            ExecutionStep executionStepOld = persistentExecutionStepHasVmUser.getExecutionStep();
            ExecutionStep executionStepNew = executionStepHasVmUser.getExecutionStep();
            Role roleOld = persistentExecutionStepHasVmUser.getRole();
            Role roleNew = executionStepHasVmUser.getRole();
            VmUser vmUserOld = persistentExecutionStepHasVmUser.getVmUser();
            VmUser vmUserNew = executionStepHasVmUser.getVmUser();
            if (executionStepNew != null) {
                executionStepNew = em.getReference(executionStepNew.getClass(), executionStepNew.getExecutionStepPK());
                executionStepHasVmUser.setExecutionStep(executionStepNew);
            }
            if (roleNew != null) {
                roleNew = em.getReference(roleNew.getClass(), roleNew.getId());
                executionStepHasVmUser.setRole(roleNew);
            }
            if (vmUserNew != null) {
                vmUserNew = em.getReference(vmUserNew.getClass(), vmUserNew.getId());
                executionStepHasVmUser.setVmUser(vmUserNew);
            }
            executionStepHasVmUser = em.merge(executionStepHasVmUser);
            if (executionStepOld != null && !executionStepOld.equals(executionStepNew)) {
                executionStepOld.getExecutionStepHasVmUserList().remove(executionStepHasVmUser);
                executionStepOld = em.merge(executionStepOld);
            }
            if (executionStepNew != null && !executionStepNew.equals(executionStepOld)) {
                executionStepNew.getExecutionStepHasVmUserList().add(executionStepHasVmUser);
                executionStepNew = em.merge(executionStepNew);
            }
            if (roleOld != null && !roleOld.equals(roleNew)) {
                roleOld.getExecutionStepHasVmUserList().remove(executionStepHasVmUser);
                roleOld = em.merge(roleOld);
            }
            if (roleNew != null && !roleNew.equals(roleOld)) {
                roleNew.getExecutionStepHasVmUserList().add(executionStepHasVmUser);
                roleNew = em.merge(roleNew);
            }
            if (vmUserOld != null && !vmUserOld.equals(vmUserNew)) {
                vmUserOld.getExecutionStepHasVmUserList().remove(executionStepHasVmUser);
                vmUserOld = em.merge(vmUserOld);
            }
            if (vmUserNew != null && !vmUserNew.equals(vmUserOld)) {
                vmUserNew.getExecutionStepHasVmUserList().add(executionStepHasVmUser);
                vmUserNew = em.merge(vmUserNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ExecutionStepHasVmUserPK id = executionStepHasVmUser.getExecutionStepHasVmUserPK();
                if (findExecutionStepHasVmUser(id) == null) {
                    throw new NonexistentEntityException("The executionStepHasVmUser with id " + id + " no longer exists.");
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

    public void destroy(ExecutionStepHasVmUserPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExecutionStepHasVmUser executionStepHasVmUser;
            try {
                executionStepHasVmUser = em.getReference(ExecutionStepHasVmUser.class, id);
                executionStepHasVmUser.getExecutionStepHasVmUserPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The executionStepHasVmUser with id " + id + " no longer exists.", enfe);
            }
            ExecutionStep executionStep = executionStepHasVmUser.getExecutionStep();
            if (executionStep != null) {
                executionStep.getExecutionStepHasVmUserList().remove(executionStepHasVmUser);
                executionStep = em.merge(executionStep);
            }
            Role role = executionStepHasVmUser.getRole();
            if (role != null) {
                role.getExecutionStepHasVmUserList().remove(executionStepHasVmUser);
                role = em.merge(role);
            }
            VmUser vmUser = executionStepHasVmUser.getVmUser();
            if (vmUser != null) {
                vmUser.getExecutionStepHasVmUserList().remove(executionStepHasVmUser);
                vmUser = em.merge(vmUser);
            }
            em.remove(executionStepHasVmUser);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ExecutionStepHasVmUser> findExecutionStepHasVmUserEntities() {
        return findExecutionStepHasVmUserEntities(true, -1, -1);
    }

    public List<ExecutionStepHasVmUser> findExecutionStepHasVmUserEntities(int maxResults, int firstResult) {
        return findExecutionStepHasVmUserEntities(false, maxResults, firstResult);
    }

    private List<ExecutionStepHasVmUser> findExecutionStepHasVmUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ExecutionStepHasVmUser.class));
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

    public ExecutionStepHasVmUser findExecutionStepHasVmUser(ExecutionStepHasVmUserPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ExecutionStepHasVmUser.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getExecutionStepHasVmUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ExecutionStepHasVmUser> rt = cq.from(ExecutionStepHasVmUser.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
