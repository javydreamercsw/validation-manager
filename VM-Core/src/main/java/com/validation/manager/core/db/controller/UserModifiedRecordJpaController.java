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

import com.validation.manager.core.db.UserModifiedRecord;
import com.validation.manager.core.db.UserModifiedRecordPK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
public class UserModifiedRecordJpaController implements Serializable {

    public UserModifiedRecordJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UserModifiedRecord userModifiedRecord) throws PreexistingEntityException, Exception {
        if (userModifiedRecord.getUserModifiedRecordPK() == null) {
            userModifiedRecord.setUserModifiedRecordPK(new UserModifiedRecordPK());
        }
        userModifiedRecord.getUserModifiedRecordPK().setUserId(userModifiedRecord.getVmUser().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            VmUser vmUser = userModifiedRecord.getVmUser();
            if (vmUser != null) {
                vmUser = em.getReference(vmUser.getClass(), vmUser.getId());
                userModifiedRecord.setVmUser(vmUser);
            }
            em.persist(userModifiedRecord);
            if (vmUser != null) {
                vmUser.getUserModifiedRecordList().add(userModifiedRecord);
                vmUser = em.merge(vmUser);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findUserModifiedRecord(userModifiedRecord.getUserModifiedRecordPK()) != null) {
                throw new PreexistingEntityException("UserModifiedRecord " + userModifiedRecord + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UserModifiedRecord userModifiedRecord) throws NonexistentEntityException, Exception {
        userModifiedRecord.getUserModifiedRecordPK().setUserId(userModifiedRecord.getVmUser().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserModifiedRecord persistentUserModifiedRecord = em.find(UserModifiedRecord.class, userModifiedRecord.getUserModifiedRecordPK());
            VmUser vmUserOld = persistentUserModifiedRecord.getVmUser();
            VmUser vmUserNew = userModifiedRecord.getVmUser();
            if (vmUserNew != null) {
                vmUserNew = em.getReference(vmUserNew.getClass(), vmUserNew.getId());
                userModifiedRecord.setVmUser(vmUserNew);
            }
            userModifiedRecord = em.merge(userModifiedRecord);
            if (vmUserOld != null && !vmUserOld.equals(vmUserNew)) {
                vmUserOld.getUserModifiedRecordList().remove(userModifiedRecord);
                vmUserOld = em.merge(vmUserOld);
            }
            if (vmUserNew != null && !vmUserNew.equals(vmUserOld)) {
                vmUserNew.getUserModifiedRecordList().add(userModifiedRecord);
                vmUserNew = em.merge(vmUserNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                UserModifiedRecordPK id = userModifiedRecord.getUserModifiedRecordPK();
                if (findUserModifiedRecord(id) == null) {
                    throw new NonexistentEntityException("The userModifiedRecord with id " + id + " no longer exists.");
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

    public void destroy(UserModifiedRecordPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserModifiedRecord userModifiedRecord;
            try {
                userModifiedRecord = em.getReference(UserModifiedRecord.class, id);
                userModifiedRecord.getUserModifiedRecordPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userModifiedRecord with id " + id + " no longer exists.", enfe);
            }
            VmUser vmUser = userModifiedRecord.getVmUser();
            if (vmUser != null) {
                vmUser.getUserModifiedRecordList().remove(userModifiedRecord);
                vmUser = em.merge(vmUser);
            }
            em.remove(userModifiedRecord);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<UserModifiedRecord> findUserModifiedRecordEntities() {
        return findUserModifiedRecordEntities(true, -1, -1);
    }

    public List<UserModifiedRecord> findUserModifiedRecordEntities(int maxResults, int firstResult) {
        return findUserModifiedRecordEntities(false, maxResults, firstResult);
    }

    private List<UserModifiedRecord> findUserModifiedRecordEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UserModifiedRecord.class));
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

    public UserModifiedRecord findUserModifiedRecord(UserModifiedRecordPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UserModifiedRecord.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getUserModifiedRecordCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UserModifiedRecord> rt = cq.from(UserModifiedRecord.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
