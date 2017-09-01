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
import com.validation.manager.core.db.Attachment;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.ExecutionStepHasAttachment;
import com.validation.manager.core.db.ExecutionStepHasAttachmentPK;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ExecutionStepHasAttachmentJpaController implements Serializable {

    public ExecutionStepHasAttachmentJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ExecutionStepHasAttachment executionStepHasAttachment) throws PreexistingEntityException, Exception {
        if (executionStepHasAttachment.getExecutionStepHasAttachmentPK() == null) {
            executionStepHasAttachment.setExecutionStepHasAttachmentPK(new ExecutionStepHasAttachmentPK());
        }
        executionStepHasAttachment.getExecutionStepHasAttachmentPK().setExecutionStepTestCaseExecutionId(executionStepHasAttachment.getExecutionStep().getExecutionStepPK().getTestCaseExecutionId());
        executionStepHasAttachment.getExecutionStepHasAttachmentPK().setAttachmentId(executionStepHasAttachment.getAttachment().getAttachmentPK().getId());
        executionStepHasAttachment.getExecutionStepHasAttachmentPK().setExecutionStepStepTestCaseId(executionStepHasAttachment.getExecutionStep().getExecutionStepPK().getStepTestCaseId());
        executionStepHasAttachment.getExecutionStepHasAttachmentPK().setExecutionStepStepId(executionStepHasAttachment.getExecutionStep().getExecutionStepPK().getStepId());
        executionStepHasAttachment.getExecutionStepHasAttachmentPK().setAttachmentAttachmentTypeId(executionStepHasAttachment.getAttachment().getAttachmentPK().getAttachmentTypeId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Attachment attachment = executionStepHasAttachment.getAttachment();
            if (attachment != null) {
                attachment = em.getReference(attachment.getClass(), attachment.getAttachmentPK());
                executionStepHasAttachment.setAttachment(attachment);
            }
            ExecutionStep executionStep = executionStepHasAttachment.getExecutionStep();
            if (executionStep != null) {
                executionStep = em.getReference(executionStep.getClass(), executionStep.getExecutionStepPK());
                executionStepHasAttachment.setExecutionStep(executionStep);
            }
            em.persist(executionStepHasAttachment);
            if (attachment != null) {
                attachment.getExecutionStepHasAttachmentList().add(executionStepHasAttachment);
                attachment = em.merge(attachment);
            }
            if (executionStep != null) {
                executionStep.getExecutionStepHasAttachmentList().add(executionStepHasAttachment);
                executionStep = em.merge(executionStep);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findExecutionStepHasAttachment(executionStepHasAttachment.getExecutionStepHasAttachmentPK()) != null) {
                throw new PreexistingEntityException("ExecutionStepHasAttachment " + executionStepHasAttachment + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ExecutionStepHasAttachment executionStepHasAttachment) throws NonexistentEntityException, Exception {
        executionStepHasAttachment.getExecutionStepHasAttachmentPK().setExecutionStepTestCaseExecutionId(executionStepHasAttachment.getExecutionStep().getExecutionStepPK().getTestCaseExecutionId());
        executionStepHasAttachment.getExecutionStepHasAttachmentPK().setAttachmentId(executionStepHasAttachment.getAttachment().getAttachmentPK().getId());
        executionStepHasAttachment.getExecutionStepHasAttachmentPK().setExecutionStepStepTestCaseId(executionStepHasAttachment.getExecutionStep().getExecutionStepPK().getStepTestCaseId());
        executionStepHasAttachment.getExecutionStepHasAttachmentPK().setExecutionStepStepId(executionStepHasAttachment.getExecutionStep().getExecutionStepPK().getStepId());
        executionStepHasAttachment.getExecutionStepHasAttachmentPK().setAttachmentAttachmentTypeId(executionStepHasAttachment.getAttachment().getAttachmentPK().getAttachmentTypeId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExecutionStepHasAttachment persistentExecutionStepHasAttachment = em.find(ExecutionStepHasAttachment.class, executionStepHasAttachment.getExecutionStepHasAttachmentPK());
            Attachment attachmentOld = persistentExecutionStepHasAttachment.getAttachment();
            Attachment attachmentNew = executionStepHasAttachment.getAttachment();
            ExecutionStep executionStepOld = persistentExecutionStepHasAttachment.getExecutionStep();
            ExecutionStep executionStepNew = executionStepHasAttachment.getExecutionStep();
            if (attachmentNew != null) {
                attachmentNew = em.getReference(attachmentNew.getClass(), attachmentNew.getAttachmentPK());
                executionStepHasAttachment.setAttachment(attachmentNew);
            }
            if (executionStepNew != null) {
                executionStepNew = em.getReference(executionStepNew.getClass(), executionStepNew.getExecutionStepPK());
                executionStepHasAttachment.setExecutionStep(executionStepNew);
            }
            executionStepHasAttachment = em.merge(executionStepHasAttachment);
            if (attachmentOld != null && !attachmentOld.equals(attachmentNew)) {
                attachmentOld.getExecutionStepHasAttachmentList().remove(executionStepHasAttachment);
                attachmentOld = em.merge(attachmentOld);
            }
            if (attachmentNew != null && !attachmentNew.equals(attachmentOld)) {
                attachmentNew.getExecutionStepHasAttachmentList().add(executionStepHasAttachment);
                attachmentNew = em.merge(attachmentNew);
            }
            if (executionStepOld != null && !executionStepOld.equals(executionStepNew)) {
                executionStepOld.getExecutionStepHasAttachmentList().remove(executionStepHasAttachment);
                executionStepOld = em.merge(executionStepOld);
            }
            if (executionStepNew != null && !executionStepNew.equals(executionStepOld)) {
                executionStepNew.getExecutionStepHasAttachmentList().add(executionStepHasAttachment);
                executionStepNew = em.merge(executionStepNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ExecutionStepHasAttachmentPK id = executionStepHasAttachment.getExecutionStepHasAttachmentPK();
                if (findExecutionStepHasAttachment(id) == null) {
                    throw new NonexistentEntityException("The executionStepHasAttachment with id " + id + " no longer exists.");
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

    public void destroy(ExecutionStepHasAttachmentPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExecutionStepHasAttachment executionStepHasAttachment;
            try {
                executionStepHasAttachment = em.getReference(ExecutionStepHasAttachment.class, id);
                executionStepHasAttachment.getExecutionStepHasAttachmentPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The executionStepHasAttachment with id " + id + " no longer exists.", enfe);
            }
            Attachment attachment = executionStepHasAttachment.getAttachment();
            if (attachment != null) {
                attachment.getExecutionStepHasAttachmentList().remove(executionStepHasAttachment);
                attachment = em.merge(attachment);
            }
            ExecutionStep executionStep = executionStepHasAttachment.getExecutionStep();
            if (executionStep != null) {
                executionStep.getExecutionStepHasAttachmentList().remove(executionStepHasAttachment);
                executionStep = em.merge(executionStep);
            }
            em.remove(executionStepHasAttachment);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ExecutionStepHasAttachment> findExecutionStepHasAttachmentEntities() {
        return findExecutionStepHasAttachmentEntities(true, -1, -1);
    }

    public List<ExecutionStepHasAttachment> findExecutionStepHasAttachmentEntities(int maxResults, int firstResult) {
        return findExecutionStepHasAttachmentEntities(false, maxResults, firstResult);
    }

    private List<ExecutionStepHasAttachment> findExecutionStepHasAttachmentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ExecutionStepHasAttachment.class));
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

    public ExecutionStepHasAttachment findExecutionStepHasAttachment(ExecutionStepHasAttachmentPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ExecutionStepHasAttachment.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getExecutionStepHasAttachmentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ExecutionStepHasAttachment> rt = cq.from(ExecutionStepHasAttachment.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
