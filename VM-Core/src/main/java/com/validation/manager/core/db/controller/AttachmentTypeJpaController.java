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
import com.validation.manager.core.db.AttachmentType;
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
public class AttachmentTypeJpaController implements Serializable {

    public AttachmentTypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(AttachmentType attachmentType) throws PreexistingEntityException, Exception {
        if (attachmentType.getAttachmentList() == null) {
            attachmentType.setAttachmentList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Attachment> attachedAttachmentList = new ArrayList<>();
            for (Attachment attachmentListAttachmentToAttach : attachmentType.getAttachmentList()) {
                attachmentListAttachmentToAttach = em.getReference(attachmentListAttachmentToAttach.getClass(), attachmentListAttachmentToAttach.getAttachmentPK());
                attachedAttachmentList.add(attachmentListAttachmentToAttach);
            }
            attachmentType.setAttachmentList(attachedAttachmentList);
            em.persist(attachmentType);
            for (Attachment attachmentListAttachment : attachmentType.getAttachmentList()) {
                AttachmentType oldAttachmentTypeOfAttachmentListAttachment = attachmentListAttachment.getAttachmentType();
                attachmentListAttachment.setAttachmentType(attachmentType);
                attachmentListAttachment = em.merge(attachmentListAttachment);
                if (oldAttachmentTypeOfAttachmentListAttachment != null) {
                    oldAttachmentTypeOfAttachmentListAttachment.getAttachmentList().remove(attachmentListAttachment);
                    oldAttachmentTypeOfAttachmentListAttachment = em.merge(oldAttachmentTypeOfAttachmentListAttachment);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findAttachmentType(attachmentType.getId()) != null) {
                throw new PreexistingEntityException("AttachmentType " + attachmentType + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(AttachmentType attachmentType) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            AttachmentType persistentAttachmentType = em.find(AttachmentType.class, attachmentType.getId());
            List<Attachment> attachmentListOld = persistentAttachmentType.getAttachmentList();
            List<Attachment> attachmentListNew = attachmentType.getAttachmentList();
            List<String> illegalOrphanMessages = null;
            for (Attachment attachmentListOldAttachment : attachmentListOld) {
                if (!attachmentListNew.contains(attachmentListOldAttachment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Attachment " + attachmentListOldAttachment + " since its attachmentType field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Attachment> attachedAttachmentListNew = new ArrayList<>();
            for (Attachment attachmentListNewAttachmentToAttach : attachmentListNew) {
                attachmentListNewAttachmentToAttach = em.getReference(attachmentListNewAttachmentToAttach.getClass(), attachmentListNewAttachmentToAttach.getAttachmentPK());
                attachedAttachmentListNew.add(attachmentListNewAttachmentToAttach);
            }
            attachmentListNew = attachedAttachmentListNew;
            attachmentType.setAttachmentList(attachmentListNew);
            attachmentType = em.merge(attachmentType);
            for (Attachment attachmentListNewAttachment : attachmentListNew) {
                if (!attachmentListOld.contains(attachmentListNewAttachment)) {
                    AttachmentType oldAttachmentTypeOfAttachmentListNewAttachment = attachmentListNewAttachment.getAttachmentType();
                    attachmentListNewAttachment.setAttachmentType(attachmentType);
                    attachmentListNewAttachment = em.merge(attachmentListNewAttachment);
                    if (oldAttachmentTypeOfAttachmentListNewAttachment != null && !oldAttachmentTypeOfAttachmentListNewAttachment.equals(attachmentType)) {
                        oldAttachmentTypeOfAttachmentListNewAttachment.getAttachmentList().remove(attachmentListNewAttachment);
                        oldAttachmentTypeOfAttachmentListNewAttachment = em.merge(oldAttachmentTypeOfAttachmentListNewAttachment);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = attachmentType.getId();
                if (findAttachmentType(id) == null) {
                    throw new NonexistentEntityException("The attachmentType with id " + id + " no longer exists.");
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
            AttachmentType attachmentType;
            try {
                attachmentType = em.getReference(AttachmentType.class, id);
                attachmentType.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The attachmentType with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Attachment> attachmentListOrphanCheck = attachmentType.getAttachmentList();
            for (Attachment attachmentListOrphanCheckAttachment : attachmentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This AttachmentType (" + attachmentType + ") cannot be destroyed since the Attachment " + attachmentListOrphanCheckAttachment + " in its attachmentList field has a non-nullable attachmentType field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(attachmentType);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<AttachmentType> findAttachmentTypeEntities() {
        return findAttachmentTypeEntities(true, -1, -1);
    }

    public List<AttachmentType> findAttachmentTypeEntities(int maxResults, int firstResult) {
        return findAttachmentTypeEntities(false, maxResults, firstResult);
    }

    private List<AttachmentType> findAttachmentTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(AttachmentType.class));
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

    public AttachmentType findAttachmentType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(AttachmentType.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getAttachmentTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<AttachmentType> rt = cq.from(AttachmentType.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
