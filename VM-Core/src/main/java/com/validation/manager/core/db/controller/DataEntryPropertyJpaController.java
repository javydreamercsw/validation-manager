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
import com.validation.manager.core.db.DataEntry;
import com.validation.manager.core.db.DataEntryProperty;
import com.validation.manager.core.db.DataEntryPropertyPK;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class DataEntryPropertyJpaController implements Serializable {

    public DataEntryPropertyJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DataEntryProperty dataEntryProperty) throws PreexistingEntityException, Exception {
        if (dataEntryProperty.getDataEntryPropertyPK() == null) {
            dataEntryProperty.setDataEntryPropertyPK(new DataEntryPropertyPK());
        }
        dataEntryProperty.getDataEntryPropertyPK().setDataEntryId(dataEntryProperty.getDataEntry().getDataEntryPK().getId());
        dataEntryProperty.getDataEntryPropertyPK().setDataEntryStepTestCaseId(dataEntryProperty.getDataEntry().getDataEntryPK().getStepTestCaseId());
        dataEntryProperty.getDataEntryPropertyPK().setDataEntryDataEntryTypeId(dataEntryProperty.getDataEntry().getDataEntryPK().getDataEntryTypeId());
        dataEntryProperty.getDataEntryPropertyPK().setDataEntryStepId(dataEntryProperty.getDataEntry().getDataEntryPK().getStepId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DataEntry dataEntry = dataEntryProperty.getDataEntry();
            if (dataEntry != null) {
                dataEntry = em.getReference(dataEntry.getClass(), dataEntry.getDataEntryPK());
                dataEntryProperty.setDataEntry(dataEntry);
            }
            em.persist(dataEntryProperty);
            if (dataEntry != null) {
                dataEntry.getDataEntryPropertyList().add(dataEntryProperty);
                dataEntry = em.merge(dataEntry);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findDataEntryProperty(dataEntryProperty.getDataEntryPropertyPK()) != null) {
                throw new PreexistingEntityException("DataEntryProperty " + dataEntryProperty + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DataEntryProperty dataEntryProperty) throws NonexistentEntityException, Exception {
        dataEntryProperty.getDataEntryPropertyPK().setDataEntryId(dataEntryProperty.getDataEntry().getDataEntryPK().getId());
        dataEntryProperty.getDataEntryPropertyPK().setDataEntryStepTestCaseId(dataEntryProperty.getDataEntry().getDataEntryPK().getStepTestCaseId());
        dataEntryProperty.getDataEntryPropertyPK().setDataEntryDataEntryTypeId(dataEntryProperty.getDataEntry().getDataEntryPK().getDataEntryTypeId());
        dataEntryProperty.getDataEntryPropertyPK().setDataEntryStepId(dataEntryProperty.getDataEntry().getDataEntryPK().getStepId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DataEntryProperty persistentDataEntryProperty = em.find(DataEntryProperty.class, dataEntryProperty.getDataEntryPropertyPK());
            DataEntry dataEntryOld = persistentDataEntryProperty.getDataEntry();
            DataEntry dataEntryNew = dataEntryProperty.getDataEntry();
            if (dataEntryNew != null) {
                dataEntryNew = em.getReference(dataEntryNew.getClass(), dataEntryNew.getDataEntryPK());
                dataEntryProperty.setDataEntry(dataEntryNew);
            }
            dataEntryProperty = em.merge(dataEntryProperty);
            if (dataEntryOld != null && !dataEntryOld.equals(dataEntryNew)) {
                dataEntryOld.getDataEntryPropertyList().remove(dataEntryProperty);
                dataEntryOld = em.merge(dataEntryOld);
            }
            if (dataEntryNew != null && !dataEntryNew.equals(dataEntryOld)) {
                dataEntryNew.getDataEntryPropertyList().add(dataEntryProperty);
                dataEntryNew = em.merge(dataEntryNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                DataEntryPropertyPK id = dataEntryProperty.getDataEntryPropertyPK();
                if (findDataEntryProperty(id) == null) {
                    throw new NonexistentEntityException("The dataEntryProperty with id " + id + " no longer exists.");
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

    public void destroy(DataEntryPropertyPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DataEntryProperty dataEntryProperty;
            try {
                dataEntryProperty = em.getReference(DataEntryProperty.class, id);
                dataEntryProperty.getDataEntryPropertyPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The dataEntryProperty with id " + id + " no longer exists.", enfe);
            }
            DataEntry dataEntry = dataEntryProperty.getDataEntry();
            if (dataEntry != null) {
                dataEntry.getDataEntryPropertyList().remove(dataEntryProperty);
                dataEntry = em.merge(dataEntry);
            }
            em.remove(dataEntryProperty);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DataEntryProperty> findDataEntryPropertyEntities() {
        return findDataEntryPropertyEntities(true, -1, -1);
    }

    public List<DataEntryProperty> findDataEntryPropertyEntities(int maxResults, int firstResult) {
        return findDataEntryPropertyEntities(false, maxResults, firstResult);
    }

    private List<DataEntryProperty> findDataEntryPropertyEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DataEntryProperty.class));
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

    public DataEntryProperty findDataEntryProperty(DataEntryPropertyPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DataEntryProperty.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getDataEntryPropertyCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DataEntryProperty> rt = cq.from(DataEntryProperty.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
