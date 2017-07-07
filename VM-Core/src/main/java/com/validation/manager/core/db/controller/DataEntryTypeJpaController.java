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
import com.validation.manager.core.db.DataEntryType;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class DataEntryTypeJpaController implements Serializable {

    public DataEntryTypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DataEntryType dataEntryType) {
        if (dataEntryType.getDataEntryList() == null) {
            dataEntryType.setDataEntryList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<DataEntry> attachedDataEntryList = new ArrayList<>();
            for (DataEntry dataEntryListDataEntryToAttach : dataEntryType.getDataEntryList()) {
                dataEntryListDataEntryToAttach = em.getReference(dataEntryListDataEntryToAttach.getClass(), dataEntryListDataEntryToAttach.getDataEntryPK());
                attachedDataEntryList.add(dataEntryListDataEntryToAttach);
            }
            dataEntryType.setDataEntryList(attachedDataEntryList);
            em.persist(dataEntryType);
            for (DataEntry dataEntryListDataEntry : dataEntryType.getDataEntryList()) {
                DataEntryType oldDataEntryTypeOfDataEntryListDataEntry = dataEntryListDataEntry.getDataEntryType();
                dataEntryListDataEntry.setDataEntryType(dataEntryType);
                dataEntryListDataEntry = em.merge(dataEntryListDataEntry);
                if (oldDataEntryTypeOfDataEntryListDataEntry != null) {
                    oldDataEntryTypeOfDataEntryListDataEntry.getDataEntryList().remove(dataEntryListDataEntry);
                    oldDataEntryTypeOfDataEntryListDataEntry = em.merge(oldDataEntryTypeOfDataEntryListDataEntry);
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

    public void edit(DataEntryType dataEntryType) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DataEntryType persistentDataEntryType = em.find(DataEntryType.class, dataEntryType.getId());
            List<DataEntry> dataEntryListOld = persistentDataEntryType.getDataEntryList();
            List<DataEntry> dataEntryListNew = dataEntryType.getDataEntryList();
            List<String> illegalOrphanMessages = null;
            for (DataEntry dataEntryListOldDataEntry : dataEntryListOld) {
                if (!dataEntryListNew.contains(dataEntryListOldDataEntry)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain DataEntry " + dataEntryListOldDataEntry + " since its dataEntryType field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<DataEntry> attachedDataEntryListNew = new ArrayList<>();
            for (DataEntry dataEntryListNewDataEntryToAttach : dataEntryListNew) {
                dataEntryListNewDataEntryToAttach = em.getReference(dataEntryListNewDataEntryToAttach.getClass(), dataEntryListNewDataEntryToAttach.getDataEntryPK());
                attachedDataEntryListNew.add(dataEntryListNewDataEntryToAttach);
            }
            dataEntryListNew = attachedDataEntryListNew;
            dataEntryType.setDataEntryList(dataEntryListNew);
            dataEntryType = em.merge(dataEntryType);
            for (DataEntry dataEntryListNewDataEntry : dataEntryListNew) {
                if (!dataEntryListOld.contains(dataEntryListNewDataEntry)) {
                    DataEntryType oldDataEntryTypeOfDataEntryListNewDataEntry = dataEntryListNewDataEntry.getDataEntryType();
                    dataEntryListNewDataEntry.setDataEntryType(dataEntryType);
                    dataEntryListNewDataEntry = em.merge(dataEntryListNewDataEntry);
                    if (oldDataEntryTypeOfDataEntryListNewDataEntry != null && !oldDataEntryTypeOfDataEntryListNewDataEntry.equals(dataEntryType)) {
                        oldDataEntryTypeOfDataEntryListNewDataEntry.getDataEntryList().remove(dataEntryListNewDataEntry);
                        oldDataEntryTypeOfDataEntryListNewDataEntry = em.merge(oldDataEntryTypeOfDataEntryListNewDataEntry);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = dataEntryType.getId();
                if (findDataEntryType(id) == null) {
                    throw new NonexistentEntityException("The dataEntryType with id " + id + " no longer exists.");
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
            DataEntryType dataEntryType;
            try {
                dataEntryType = em.getReference(DataEntryType.class, id);
                dataEntryType.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The dataEntryType with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<DataEntry> dataEntryListOrphanCheck = dataEntryType.getDataEntryList();
            for (DataEntry dataEntryListOrphanCheckDataEntry : dataEntryListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This DataEntryType (" + dataEntryType + ") cannot be destroyed since the DataEntry " + dataEntryListOrphanCheckDataEntry + " in its dataEntryList field has a non-nullable dataEntryType field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(dataEntryType);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DataEntryType> findDataEntryTypeEntities() {
        return findDataEntryTypeEntities(true, -1, -1);
    }

    public List<DataEntryType> findDataEntryTypeEntities(int maxResults, int firstResult) {
        return findDataEntryTypeEntities(false, maxResults, firstResult);
    }

    private List<DataEntryType> findDataEntryTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DataEntryType.class));
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

    public DataEntryType findDataEntryType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DataEntryType.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getDataEntryTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DataEntryType> rt = cq.from(DataEntryType.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
