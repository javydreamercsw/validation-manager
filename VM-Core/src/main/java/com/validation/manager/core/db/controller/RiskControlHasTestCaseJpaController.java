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
import com.validation.manager.core.db.RiskControl;
import com.validation.manager.core.db.RiskControlHasTestCase;
import com.validation.manager.core.db.RiskControlHasTestCasePK;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class RiskControlHasTestCaseJpaController implements Serializable {

    public RiskControlHasTestCaseJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RiskControlHasTestCase riskControlHasTestCase) throws PreexistingEntityException, Exception {
        if (riskControlHasTestCase.getRiskControlHasTestCasePK() == null) {
            riskControlHasTestCase.setRiskControlHasTestCasePK(new RiskControlHasTestCasePK());
        }
        riskControlHasTestCase.getRiskControlHasTestCasePK().setRiskControlId(riskControlHasTestCase.getRiskControl().getRiskControlPK().getId());
        riskControlHasTestCase.getRiskControlHasTestCasePK().setRiskControlRiskControlTypeId(riskControlHasTestCase.getRiskControl().getRiskControlPK().getRiskControlTypeId());
        riskControlHasTestCase.getRiskControlHasTestCasePK().setTestCaseId(riskControlHasTestCase.getTestCase().getTestCasePK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskControl riskControl = riskControlHasTestCase.getRiskControl();
            if (riskControl != null) {
                riskControl = em.getReference(riskControl.getClass(), riskControl.getRiskControlPK());
                riskControlHasTestCase.setRiskControl(riskControl);
            }
            TestCase testCase = riskControlHasTestCase.getTestCase();
            if (testCase != null) {
                testCase = em.getReference(testCase.getClass(), testCase.getTestCasePK());
                riskControlHasTestCase.setTestCase(testCase);
            }
            em.persist(riskControlHasTestCase);
            if (riskControl != null) {
                riskControl.getRiskControlHasTestCaseList().add(riskControlHasTestCase);
                riskControl = em.merge(riskControl);
            }
            if (testCase != null) {
                testCase.getRiskControlHasTestCaseList().add(riskControlHasTestCase);
                testCase = em.merge(testCase);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findRiskControlHasTestCase(riskControlHasTestCase.getRiskControlHasTestCasePK()) != null) {
                throw new PreexistingEntityException("RiskControlHasTestCase " + riskControlHasTestCase + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RiskControlHasTestCase riskControlHasTestCase) throws NonexistentEntityException, Exception {
        riskControlHasTestCase.getRiskControlHasTestCasePK().setRiskControlId(riskControlHasTestCase.getRiskControl().getRiskControlPK().getId());
        riskControlHasTestCase.getRiskControlHasTestCasePK().setRiskControlRiskControlTypeId(riskControlHasTestCase.getRiskControl().getRiskControlPK().getRiskControlTypeId());
        riskControlHasTestCase.getRiskControlHasTestCasePK().setTestCaseId(riskControlHasTestCase.getTestCase().getTestCasePK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskControlHasTestCase persistentRiskControlHasTestCase = em.find(RiskControlHasTestCase.class, riskControlHasTestCase.getRiskControlHasTestCasePK());
            RiskControl riskControlOld = persistentRiskControlHasTestCase.getRiskControl();
            RiskControl riskControlNew = riskControlHasTestCase.getRiskControl();
            TestCase testCaseOld = persistentRiskControlHasTestCase.getTestCase();
            TestCase testCaseNew = riskControlHasTestCase.getTestCase();
            if (riskControlNew != null) {
                riskControlNew = em.getReference(riskControlNew.getClass(), riskControlNew.getRiskControlPK());
                riskControlHasTestCase.setRiskControl(riskControlNew);
            }
            if (testCaseNew != null) {
                testCaseNew = em.getReference(testCaseNew.getClass(), testCaseNew.getTestCasePK());
                riskControlHasTestCase.setTestCase(testCaseNew);
            }
            riskControlHasTestCase = em.merge(riskControlHasTestCase);
            if (riskControlOld != null && !riskControlOld.equals(riskControlNew)) {
                riskControlOld.getRiskControlHasTestCaseList().remove(riskControlHasTestCase);
                riskControlOld = em.merge(riskControlOld);
            }
            if (riskControlNew != null && !riskControlNew.equals(riskControlOld)) {
                riskControlNew.getRiskControlHasTestCaseList().add(riskControlHasTestCase);
                riskControlNew = em.merge(riskControlNew);
            }
            if (testCaseOld != null && !testCaseOld.equals(testCaseNew)) {
                testCaseOld.getRiskControlHasTestCaseList().remove(riskControlHasTestCase);
                testCaseOld = em.merge(testCaseOld);
            }
            if (testCaseNew != null && !testCaseNew.equals(testCaseOld)) {
                testCaseNew.getRiskControlHasTestCaseList().add(riskControlHasTestCase);
                testCaseNew = em.merge(testCaseNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RiskControlHasTestCasePK id = riskControlHasTestCase.getRiskControlHasTestCasePK();
                if (findRiskControlHasTestCase(id) == null) {
                    throw new NonexistentEntityException("The riskControlHasTestCase with id " + id + " no longer exists.");
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

    public void destroy(RiskControlHasTestCasePK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskControlHasTestCase riskControlHasTestCase;
            try {
                riskControlHasTestCase = em.getReference(RiskControlHasTestCase.class, id);
                riskControlHasTestCase.getRiskControlHasTestCasePK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The riskControlHasTestCase with id " + id + " no longer exists.", enfe);
            }
            RiskControl riskControl = riskControlHasTestCase.getRiskControl();
            if (riskControl != null) {
                riskControl.getRiskControlHasTestCaseList().remove(riskControlHasTestCase);
                riskControl = em.merge(riskControl);
            }
            TestCase testCase = riskControlHasTestCase.getTestCase();
            if (testCase != null) {
                testCase.getRiskControlHasTestCaseList().remove(riskControlHasTestCase);
                testCase = em.merge(testCase);
            }
            em.remove(riskControlHasTestCase);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RiskControlHasTestCase> findRiskControlHasTestCaseEntities() {
        return findRiskControlHasTestCaseEntities(true, -1, -1);
    }

    public List<RiskControlHasTestCase> findRiskControlHasTestCaseEntities(int maxResults, int firstResult) {
        return findRiskControlHasTestCaseEntities(false, maxResults, firstResult);
    }

    private List<RiskControlHasTestCase> findRiskControlHasTestCaseEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RiskControlHasTestCase.class));
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

    public RiskControlHasTestCase findRiskControlHasTestCase(RiskControlHasTestCasePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RiskControlHasTestCase.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getRiskControlHasTestCaseCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RiskControlHasTestCase> rt = cq.from(RiskControlHasTestCase.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
