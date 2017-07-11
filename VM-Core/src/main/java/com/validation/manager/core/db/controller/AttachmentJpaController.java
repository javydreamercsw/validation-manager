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

import com.validation.manager.core.db.Attachment;
import com.validation.manager.core.db.AttachmentPK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.AttachmentType;
import com.validation.manager.core.db.ExecutionStepHasAttachment;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class AttachmentJpaController implements Serializable {

    public AttachmentJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Attachment attachment) throws PreexistingEntityException, Exception {
        if (attachment.getAttachmentPK() == null) {
            attachment.setAttachmentPK(new AttachmentPK());
        }
        if (attachment.getExecutionStepHasAttachmentList() == null) {
            attachment.setExecutionStepHasAttachmentList(new ArrayList<>());
        }
        attachment.getAttachmentPK().setAttachmentTypeId(attachment.getAttachmentType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            AttachmentType attachmentType = attachment.getAttachmentType();
            if (attachmentType != null) {
                attachmentType = em.getReference(attachmentType.getClass(), attachmentType.getId());
                attachment.setAttachmentType(attachmentType);
            }
            List<ExecutionStepHasAttachment> attachedExecutionStepHasAttachmentList = new ArrayList<>();
            for (ExecutionStepHasAttachment executionStepHasAttachmentListExecutionStepHasAttachmentToAttach : attachment.getExecutionStepHasAttachmentList()) {
                executionStepHasAttachmentListExecutionStepHasAttachmentToAttach = em.getReference(executionStepHasAttachmentListExecutionStepHasAttachmentToAttach.getClass(), executionStepHasAttachmentListExecutionStepHasAttachmentToAttach.getExecutionStepHasAttachmentPK());
                attachedExecutionStepHasAttachmentList.add(executionStepHasAttachmentListExecutionStepHasAttachmentToAttach);
            }
            attachment.setExecutionStepHasAttachmentList(attachedExecutionStepHasAttachmentList);
            em.persist(attachment);
            if (attachmentType != null) {
                attachmentType.getAttachmentList().add(attachment);
                attachmentType = em.merge(attachmentType);
            }
            for (ExecutionStepHasAttachment executionStepHasAttachmentListExecutionStepHasAttachment : attachment.getExecutionStepHasAttachmentList()) {
                Attachment oldAttachmentOfExecutionStepHasAttachmentListExecutionStepHasAttachment = executionStepHasAttachmentListExecutionStepHasAttachment.getAttachment();
                executionStepHasAttachmentListExecutionStepHasAttachment.setAttachment(attachment);
                executionStepHasAttachmentListExecutionStepHasAttachment = em.merge(executionStepHasAttachmentListExecutionStepHasAttachment);
                if (oldAttachmentOfExecutionStepHasAttachmentListExecutionStepHasAttachment != null) {
                    oldAttachmentOfExecutionStepHasAttachmentListExecutionStepHasAttachment.getExecutionStepHasAttachmentList().remove(executionStepHasAttachmentListExecutionStepHasAttachment);
                    oldAttachmentOfExecutionStepHasAttachmentListExecutionStepHasAttachment = em.merge(oldAttachmentOfExecutionStepHasAttachmentListExecutionStepHasAttachment);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findAttachment(attachment.getAttachmentPK()) != null) {
                throw new PreexistingEntityException("Attachment " + attachment + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Attachment attachment) throws IllegalOrphanException, NonexistentEntityException, Exception {
        attachment.getAttachmentPK().setAttachmentTypeId(attachment.getAttachmentType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Attachment persistentAttachment = em.find(Attachment.class, attachment.getAttachmentPK());
            AttachmentType attachmentTypeOld = persistentAttachment.getAttachmentType();
            AttachmentType attachmentTypeNew = attachment.getAttachmentType();
            List<ExecutionStepHasAttachment> executionStepHasAttachmentListOld = persistentAttachment.getExecutionStepHasAttachmentList();
            List<ExecutionStepHasAttachment> executionStepHasAttachmentListNew = attachment.getExecutionStepHasAttachmentList();
            List<String> illegalOrphanMessages = null;
            for (ExecutionStepHasAttachment executionStepHasAttachmentListOldExecutionStepHasAttachment : executionStepHasAttachmentListOld) {
                if (!executionStepHasAttachmentListNew.contains(executionStepHasAttachmentListOldExecutionStepHasAttachment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain ExecutionStepHasAttachment " + executionStepHasAttachmentListOldExecutionStepHasAttachment + " since its attachment field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (attachmentTypeNew != null) {
                attachmentTypeNew = em.getReference(attachmentTypeNew.getClass(), attachmentTypeNew.getId());
                attachment.setAttachmentType(attachmentTypeNew);
            }
            List<ExecutionStepHasAttachment> attachedExecutionStepHasAttachmentListNew = new ArrayList<>();
            for (ExecutionStepHasAttachment executionStepHasAttachmentListNewExecutionStepHasAttachmentToAttach : executionStepHasAttachmentListNew) {
                executionStepHasAttachmentListNewExecutionStepHasAttachmentToAttach = em.getReference(executionStepHasAttachmentListNewExecutionStepHasAttachmentToAttach.getClass(), executionStepHasAttachmentListNewExecutionStepHasAttachmentToAttach.getExecutionStepHasAttachmentPK());
                attachedExecutionStepHasAttachmentListNew.add(executionStepHasAttachmentListNewExecutionStepHasAttachmentToAttach);
            }
            executionStepHasAttachmentListNew = attachedExecutionStepHasAttachmentListNew;
            attachment.setExecutionStepHasAttachmentList(executionStepHasAttachmentListNew);
            attachment = em.merge(attachment);
            if (attachmentTypeOld != null && !attachmentTypeOld.equals(attachmentTypeNew)) {
                attachmentTypeOld.getAttachmentList().remove(attachment);
                attachmentTypeOld = em.merge(attachmentTypeOld);
            }
            if (attachmentTypeNew != null && !attachmentTypeNew.equals(attachmentTypeOld)) {
                attachmentTypeNew.getAttachmentList().add(attachment);
                attachmentTypeNew = em.merge(attachmentTypeNew);
            }
            for (ExecutionStepHasAttachment executionStepHasAttachmentListNewExecutionStepHasAttachment : executionStepHasAttachmentListNew) {
                if (!executionStepHasAttachmentListOld.contains(executionStepHasAttachmentListNewExecutionStepHasAttachment)) {
                    Attachment oldAttachmentOfExecutionStepHasAttachmentListNewExecutionStepHasAttachment = executionStepHasAttachmentListNewExecutionStepHasAttachment.getAttachment();
                    executionStepHasAttachmentListNewExecutionStepHasAttachment.setAttachment(attachment);
                    executionStepHasAttachmentListNewExecutionStepHasAttachment = em.merge(executionStepHasAttachmentListNewExecutionStepHasAttachment);
                    if (oldAttachmentOfExecutionStepHasAttachmentListNewExecutionStepHasAttachment != null && !oldAttachmentOfExecutionStepHasAttachmentListNewExecutionStepHasAttachment.equals(attachment)) {
                        oldAttachmentOfExecutionStepHasAttachmentListNewExecutionStepHasAttachment.getExecutionStepHasAttachmentList().remove(executionStepHasAttachmentListNewExecutionStepHasAttachment);
                        oldAttachmentOfExecutionStepHasAttachmentListNewExecutionStepHasAttachment = em.merge(oldAttachmentOfExecutionStepHasAttachmentListNewExecutionStepHasAttachment);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                AttachmentPK id = attachment.getAttachmentPK();
                if (findAttachment(id) == null) {
                    throw new NonexistentEntityException("The attachment with id " + id + " no longer exists.");
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

    public void destroy(AttachmentPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Attachment attachment;
            try {
                attachment = em.getReference(Attachment.class, id);
                attachment.getAttachmentPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The attachment with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<ExecutionStepHasAttachment> executionStepHasAttachmentListOrphanCheck = attachment.getExecutionStepHasAttachmentList();
            for (ExecutionStepHasAttachment executionStepHasAttachmentListOrphanCheckExecutionStepHasAttachment : executionStepHasAttachmentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Attachment (" + attachment + ") cannot be destroyed since the ExecutionStepHasAttachment " + executionStepHasAttachmentListOrphanCheckExecutionStepHasAttachment + " in its executionStepHasAttachmentList field has a non-nullable attachment field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            AttachmentType attachmentType = attachment.getAttachmentType();
            if (attachmentType != null) {
                attachmentType.getAttachmentList().remove(attachment);
                attachmentType = em.merge(attachmentType);
            }
            em.remove(attachment);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Attachment> findAttachmentEntities() {
        return findAttachmentEntities(true, -1, -1);
    }

    public List<Attachment> findAttachmentEntities(int maxResults, int firstResult) {
        return findAttachmentEntities(false, maxResults, firstResult);
    }

    private List<Attachment> findAttachmentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Attachment.class));
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

    public Attachment findAttachment(AttachmentPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Attachment.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getAttachmentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Attachment> rt = cq.from(Attachment.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
