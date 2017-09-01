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

import com.validation.manager.core.db.DataEntry;
import com.validation.manager.core.db.DataEntryPK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.DataEntryType;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.DataEntryProperty;
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
public class DataEntryJpaController implements Serializable {

    public DataEntryJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DataEntry dataEntry) throws PreexistingEntityException, Exception {
        if (dataEntry.getDataEntryPK() == null) {
            dataEntry.setDataEntryPK(new DataEntryPK());
        }
        if (dataEntry.getDataEntryPropertyList() == null) {
            dataEntry.setDataEntryPropertyList(new ArrayList<>());
        }
        dataEntry.getDataEntryPK().setDataEntryTypeId(dataEntry.getDataEntryType().getId());
        dataEntry.getDataEntryPK().setStepTestCaseId(dataEntry.getStep().getStepPK().getTestCaseId());
        dataEntry.getDataEntryPK().setStepId(dataEntry.getStep().getStepPK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DataEntryType dataEntryType = dataEntry.getDataEntryType();
            if (dataEntryType != null) {
                dataEntryType = em.getReference(dataEntryType.getClass(), dataEntryType.getId());
                dataEntry.setDataEntryType(dataEntryType);
            }
            Step step = dataEntry.getStep();
            if (step != null) {
                step = em.getReference(step.getClass(), step.getStepPK());
                dataEntry.setStep(step);
            }
            List<DataEntryProperty> attachedDataEntryPropertyList = new ArrayList<>();
            for (DataEntryProperty dataEntryPropertyListDataEntryPropertyToAttach : dataEntry.getDataEntryPropertyList()) {
                dataEntryPropertyListDataEntryPropertyToAttach = em.getReference(dataEntryPropertyListDataEntryPropertyToAttach.getClass(), dataEntryPropertyListDataEntryPropertyToAttach.getDataEntryPropertyPK());
                attachedDataEntryPropertyList.add(dataEntryPropertyListDataEntryPropertyToAttach);
            }
            dataEntry.setDataEntryPropertyList(attachedDataEntryPropertyList);
            em.persist(dataEntry);
            if (dataEntryType != null) {
                dataEntryType.getDataEntryList().add(dataEntry);
                dataEntryType = em.merge(dataEntryType);
            }
            if (step != null) {
                step.getDataEntryList().add(dataEntry);
                step = em.merge(step);
            }
            for (DataEntryProperty dataEntryPropertyListDataEntryProperty : dataEntry.getDataEntryPropertyList()) {
                DataEntry oldDataEntryOfDataEntryPropertyListDataEntryProperty = dataEntryPropertyListDataEntryProperty.getDataEntry();
                dataEntryPropertyListDataEntryProperty.setDataEntry(dataEntry);
                dataEntryPropertyListDataEntryProperty = em.merge(dataEntryPropertyListDataEntryProperty);
                if (oldDataEntryOfDataEntryPropertyListDataEntryProperty != null) {
                    oldDataEntryOfDataEntryPropertyListDataEntryProperty.getDataEntryPropertyList().remove(dataEntryPropertyListDataEntryProperty);
                    oldDataEntryOfDataEntryPropertyListDataEntryProperty = em.merge(oldDataEntryOfDataEntryPropertyListDataEntryProperty);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findDataEntry(dataEntry.getDataEntryPK()) != null) {
                throw new PreexistingEntityException("DataEntry " + dataEntry + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DataEntry dataEntry) throws IllegalOrphanException, NonexistentEntityException, Exception {
        dataEntry.getDataEntryPK().setDataEntryTypeId(dataEntry.getDataEntryType().getId());
        dataEntry.getDataEntryPK().setStepTestCaseId(dataEntry.getStep().getStepPK().getTestCaseId());
        dataEntry.getDataEntryPK().setStepId(dataEntry.getStep().getStepPK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DataEntry persistentDataEntry = em.find(DataEntry.class, dataEntry.getDataEntryPK());
            DataEntryType dataEntryTypeOld = persistentDataEntry.getDataEntryType();
            DataEntryType dataEntryTypeNew = dataEntry.getDataEntryType();
            Step stepOld = persistentDataEntry.getStep();
            Step stepNew = dataEntry.getStep();
            List<DataEntryProperty> dataEntryPropertyListOld = persistentDataEntry.getDataEntryPropertyList();
            List<DataEntryProperty> dataEntryPropertyListNew = dataEntry.getDataEntryPropertyList();
            List<String> illegalOrphanMessages = null;
            for (DataEntryProperty dataEntryPropertyListOldDataEntryProperty : dataEntryPropertyListOld) {
                if (!dataEntryPropertyListNew.contains(dataEntryPropertyListOldDataEntryProperty)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain DataEntryProperty " + dataEntryPropertyListOldDataEntryProperty + " since its dataEntry field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (dataEntryTypeNew != null) {
                dataEntryTypeNew = em.getReference(dataEntryTypeNew.getClass(), dataEntryTypeNew.getId());
                dataEntry.setDataEntryType(dataEntryTypeNew);
            }
            if (stepNew != null) {
                stepNew = em.getReference(stepNew.getClass(), stepNew.getStepPK());
                dataEntry.setStep(stepNew);
            }
            List<DataEntryProperty> attachedDataEntryPropertyListNew = new ArrayList<>();
            for (DataEntryProperty dataEntryPropertyListNewDataEntryPropertyToAttach : dataEntryPropertyListNew) {
                dataEntryPropertyListNewDataEntryPropertyToAttach = em.getReference(dataEntryPropertyListNewDataEntryPropertyToAttach.getClass(), dataEntryPropertyListNewDataEntryPropertyToAttach.getDataEntryPropertyPK());
                attachedDataEntryPropertyListNew.add(dataEntryPropertyListNewDataEntryPropertyToAttach);
            }
            dataEntryPropertyListNew = attachedDataEntryPropertyListNew;
            dataEntry.setDataEntryPropertyList(dataEntryPropertyListNew);
            dataEntry = em.merge(dataEntry);
            if (dataEntryTypeOld != null && !dataEntryTypeOld.equals(dataEntryTypeNew)) {
                dataEntryTypeOld.getDataEntryList().remove(dataEntry);
                dataEntryTypeOld = em.merge(dataEntryTypeOld);
            }
            if (dataEntryTypeNew != null && !dataEntryTypeNew.equals(dataEntryTypeOld)) {
                dataEntryTypeNew.getDataEntryList().add(dataEntry);
                dataEntryTypeNew = em.merge(dataEntryTypeNew);
            }
            if (stepOld != null && !stepOld.equals(stepNew)) {
                stepOld.getDataEntryList().remove(dataEntry);
                stepOld = em.merge(stepOld);
            }
            if (stepNew != null && !stepNew.equals(stepOld)) {
                stepNew.getDataEntryList().add(dataEntry);
                stepNew = em.merge(stepNew);
            }
            for (DataEntryProperty dataEntryPropertyListNewDataEntryProperty : dataEntryPropertyListNew) {
                if (!dataEntryPropertyListOld.contains(dataEntryPropertyListNewDataEntryProperty)) {
                    DataEntry oldDataEntryOfDataEntryPropertyListNewDataEntryProperty = dataEntryPropertyListNewDataEntryProperty.getDataEntry();
                    dataEntryPropertyListNewDataEntryProperty.setDataEntry(dataEntry);
                    dataEntryPropertyListNewDataEntryProperty = em.merge(dataEntryPropertyListNewDataEntryProperty);
                    if (oldDataEntryOfDataEntryPropertyListNewDataEntryProperty != null && !oldDataEntryOfDataEntryPropertyListNewDataEntryProperty.equals(dataEntry)) {
                        oldDataEntryOfDataEntryPropertyListNewDataEntryProperty.getDataEntryPropertyList().remove(dataEntryPropertyListNewDataEntryProperty);
                        oldDataEntryOfDataEntryPropertyListNewDataEntryProperty = em.merge(oldDataEntryOfDataEntryPropertyListNewDataEntryProperty);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                DataEntryPK id = dataEntry.getDataEntryPK();
                if (findDataEntry(id) == null) {
                    throw new NonexistentEntityException("The dataEntry with id " + id + " no longer exists.");
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

    public void destroy(DataEntryPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DataEntry dataEntry;
            try {
                dataEntry = em.getReference(DataEntry.class, id);
                dataEntry.getDataEntryPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The dataEntry with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<DataEntryProperty> dataEntryPropertyListOrphanCheck = dataEntry.getDataEntryPropertyList();
            for (DataEntryProperty dataEntryPropertyListOrphanCheckDataEntryProperty : dataEntryPropertyListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This DataEntry (" + dataEntry + ") cannot be destroyed since the DataEntryProperty " + dataEntryPropertyListOrphanCheckDataEntryProperty + " in its dataEntryPropertyList field has a non-nullable dataEntry field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            DataEntryType dataEntryType = dataEntry.getDataEntryType();
            if (dataEntryType != null) {
                dataEntryType.getDataEntryList().remove(dataEntry);
                dataEntryType = em.merge(dataEntryType);
            }
            Step step = dataEntry.getStep();
            if (step != null) {
                step.getDataEntryList().remove(dataEntry);
                step = em.merge(step);
            }
            em.remove(dataEntry);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DataEntry> findDataEntryEntities() {
        return findDataEntryEntities(true, -1, -1);
    }

    public List<DataEntry> findDataEntryEntities(int maxResults, int firstResult) {
        return findDataEntryEntities(false, maxResults, firstResult);
    }

    private List<DataEntry> findDataEntryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DataEntry.class));
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

    public DataEntry findDataEntry(DataEntryPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DataEntry.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getDataEntryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DataEntry> rt = cq.from(DataEntry.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
