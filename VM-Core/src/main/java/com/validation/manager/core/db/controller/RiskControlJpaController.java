/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.RiskControlType;
import com.validation.manager.core.db.RiskItem;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RiskControl;
import com.validation.manager.core.db.RiskControlPK;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RiskControlJpaController implements Serializable {

    public RiskControlJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RiskControl riskControl) throws PreexistingEntityException, Exception {
        if (riskControl.getRiskControlPK() == null) {
            riskControl.setRiskControlPK(new RiskControlPK());
        }
        if (riskControl.getRiskItemList() == null) {
            riskControl.setRiskItemList(new ArrayList<RiskItem>());
        }
        if (riskControl.getTestCaseList() == null) {
            riskControl.setTestCaseList(new ArrayList<TestCase>());
        }
        if (riskControl.getRequirementList() == null) {
            riskControl.setRequirementList(new ArrayList<Requirement>());
        }
        if (riskControl.getRiskItemList1() == null) {
            riskControl.setRiskItemList1(new ArrayList<RiskItem>());
        }
        riskControl.getRiskControlPK().setRiskControlTypeId(riskControl.getRiskControlType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskControlType riskControlType = riskControl.getRiskControlType();
            if (riskControlType != null) {
                riskControlType = em.getReference(riskControlType.getClass(), riskControlType.getId());
                riskControl.setRiskControlType(riskControlType);
            }
            List<RiskItem> attachedRiskItemList = new ArrayList<RiskItem>();
            for (RiskItem riskItemListRiskItemToAttach : riskControl.getRiskItemList()) {
                riskItemListRiskItemToAttach = em.getReference(riskItemListRiskItemToAttach.getClass(), riskItemListRiskItemToAttach.getRiskItemPK());
                attachedRiskItemList.add(riskItemListRiskItemToAttach);
            }
            riskControl.setRiskItemList(attachedRiskItemList);
            List<TestCase> attachedTestCaseList = new ArrayList<TestCase>();
            for (TestCase testCaseListTestCaseToAttach : riskControl.getTestCaseList()) {
                testCaseListTestCaseToAttach = em.getReference(testCaseListTestCaseToAttach.getClass(), testCaseListTestCaseToAttach.getTestCasePK());
                attachedTestCaseList.add(testCaseListTestCaseToAttach);
            }
            riskControl.setTestCaseList(attachedTestCaseList);
            List<Requirement> attachedRequirementList = new ArrayList<Requirement>();
            for (Requirement requirementListRequirementToAttach : riskControl.getRequirementList()) {
                requirementListRequirementToAttach = em.getReference(requirementListRequirementToAttach.getClass(), requirementListRequirementToAttach.getId());
                attachedRequirementList.add(requirementListRequirementToAttach);
            }
            riskControl.setRequirementList(attachedRequirementList);
            List<RiskItem> attachedRiskItemList1 = new ArrayList<RiskItem>();
            for (RiskItem riskItemList1RiskItemToAttach : riskControl.getRiskItemList1()) {
                riskItemList1RiskItemToAttach = em.getReference(riskItemList1RiskItemToAttach.getClass(), riskItemList1RiskItemToAttach.getRiskItemPK());
                attachedRiskItemList1.add(riskItemList1RiskItemToAttach);
            }
            riskControl.setRiskItemList1(attachedRiskItemList1);
            em.persist(riskControl);
            if (riskControlType != null) {
                riskControlType.getRiskControlList().add(riskControl);
                riskControlType = em.merge(riskControlType);
            }
            for (RiskItem riskItemListRiskItem : riskControl.getRiskItemList()) {
                riskItemListRiskItem.getRiskControlList().add(riskControl);
                riskItemListRiskItem = em.merge(riskItemListRiskItem);
            }
            for (TestCase testCaseListTestCase : riskControl.getTestCaseList()) {
                testCaseListTestCase.getRiskControlList().add(riskControl);
                testCaseListTestCase = em.merge(testCaseListTestCase);
            }
            for (Requirement requirementListRequirement : riskControl.getRequirementList()) {
                requirementListRequirement.getRiskControlList().add(riskControl);
                requirementListRequirement = em.merge(requirementListRequirement);
            }
            for (RiskItem riskItemList1RiskItem : riskControl.getRiskItemList1()) {
                riskItemList1RiskItem.getRiskControlList().add(riskControl);
                riskItemList1RiskItem = em.merge(riskItemList1RiskItem);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRiskControl(riskControl.getRiskControlPK()) != null) {
                throw new PreexistingEntityException("RiskControl " + riskControl + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RiskControl riskControl) throws NonexistentEntityException, Exception {
        riskControl.getRiskControlPK().setRiskControlTypeId(riskControl.getRiskControlType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskControl persistentRiskControl = em.find(RiskControl.class, riskControl.getRiskControlPK());
            RiskControlType riskControlTypeOld = persistentRiskControl.getRiskControlType();
            RiskControlType riskControlTypeNew = riskControl.getRiskControlType();
            List<RiskItem> riskItemListOld = persistentRiskControl.getRiskItemList();
            List<RiskItem> riskItemListNew = riskControl.getRiskItemList();
            List<TestCase> testCaseListOld = persistentRiskControl.getTestCaseList();
            List<TestCase> testCaseListNew = riskControl.getTestCaseList();
            List<Requirement> requirementListOld = persistentRiskControl.getRequirementList();
            List<Requirement> requirementListNew = riskControl.getRequirementList();
            List<RiskItem> riskItemList1Old = persistentRiskControl.getRiskItemList1();
            List<RiskItem> riskItemList1New = riskControl.getRiskItemList1();
            if (riskControlTypeNew != null) {
                riskControlTypeNew = em.getReference(riskControlTypeNew.getClass(), riskControlTypeNew.getId());
                riskControl.setRiskControlType(riskControlTypeNew);
            }
            List<RiskItem> attachedRiskItemListNew = new ArrayList<RiskItem>();
            for (RiskItem riskItemListNewRiskItemToAttach : riskItemListNew) {
                riskItemListNewRiskItemToAttach = em.getReference(riskItemListNewRiskItemToAttach.getClass(), riskItemListNewRiskItemToAttach.getRiskItemPK());
                attachedRiskItemListNew.add(riskItemListNewRiskItemToAttach);
            }
            riskItemListNew = attachedRiskItemListNew;
            riskControl.setRiskItemList(riskItemListNew);
            List<TestCase> attachedTestCaseListNew = new ArrayList<TestCase>();
            for (TestCase testCaseListNewTestCaseToAttach : testCaseListNew) {
                testCaseListNewTestCaseToAttach = em.getReference(testCaseListNewTestCaseToAttach.getClass(), testCaseListNewTestCaseToAttach.getTestCasePK());
                attachedTestCaseListNew.add(testCaseListNewTestCaseToAttach);
            }
            testCaseListNew = attachedTestCaseListNew;
            riskControl.setTestCaseList(testCaseListNew);
            List<Requirement> attachedRequirementListNew = new ArrayList<Requirement>();
            for (Requirement requirementListNewRequirementToAttach : requirementListNew) {
                requirementListNewRequirementToAttach = em.getReference(requirementListNewRequirementToAttach.getClass(), requirementListNewRequirementToAttach.getId());
                attachedRequirementListNew.add(requirementListNewRequirementToAttach);
            }
            requirementListNew = attachedRequirementListNew;
            riskControl.setRequirementList(requirementListNew);
            List<RiskItem> attachedRiskItemList1New = new ArrayList<RiskItem>();
            for (RiskItem riskItemList1NewRiskItemToAttach : riskItemList1New) {
                riskItemList1NewRiskItemToAttach = em.getReference(riskItemList1NewRiskItemToAttach.getClass(), riskItemList1NewRiskItemToAttach.getRiskItemPK());
                attachedRiskItemList1New.add(riskItemList1NewRiskItemToAttach);
            }
            riskItemList1New = attachedRiskItemList1New;
            riskControl.setRiskItemList1(riskItemList1New);
            riskControl = em.merge(riskControl);
            if (riskControlTypeOld != null && !riskControlTypeOld.equals(riskControlTypeNew)) {
                riskControlTypeOld.getRiskControlList().remove(riskControl);
                riskControlTypeOld = em.merge(riskControlTypeOld);
            }
            if (riskControlTypeNew != null && !riskControlTypeNew.equals(riskControlTypeOld)) {
                riskControlTypeNew.getRiskControlList().add(riskControl);
                riskControlTypeNew = em.merge(riskControlTypeNew);
            }
            for (RiskItem riskItemListOldRiskItem : riskItemListOld) {
                if (!riskItemListNew.contains(riskItemListOldRiskItem)) {
                    riskItemListOldRiskItem.getRiskControlList().remove(riskControl);
                    riskItemListOldRiskItem = em.merge(riskItemListOldRiskItem);
                }
            }
            for (RiskItem riskItemListNewRiskItem : riskItemListNew) {
                if (!riskItemListOld.contains(riskItemListNewRiskItem)) {
                    riskItemListNewRiskItem.getRiskControlList().add(riskControl);
                    riskItemListNewRiskItem = em.merge(riskItemListNewRiskItem);
                }
            }
            for (TestCase testCaseListOldTestCase : testCaseListOld) {
                if (!testCaseListNew.contains(testCaseListOldTestCase)) {
                    testCaseListOldTestCase.getRiskControlList().remove(riskControl);
                    testCaseListOldTestCase = em.merge(testCaseListOldTestCase);
                }
            }
            for (TestCase testCaseListNewTestCase : testCaseListNew) {
                if (!testCaseListOld.contains(testCaseListNewTestCase)) {
                    testCaseListNewTestCase.getRiskControlList().add(riskControl);
                    testCaseListNewTestCase = em.merge(testCaseListNewTestCase);
                }
            }
            for (Requirement requirementListOldRequirement : requirementListOld) {
                if (!requirementListNew.contains(requirementListOldRequirement)) {
                    requirementListOldRequirement.getRiskControlList().remove(riskControl);
                    requirementListOldRequirement = em.merge(requirementListOldRequirement);
                }
            }
            for (Requirement requirementListNewRequirement : requirementListNew) {
                if (!requirementListOld.contains(requirementListNewRequirement)) {
                    requirementListNewRequirement.getRiskControlList().add(riskControl);
                    requirementListNewRequirement = em.merge(requirementListNewRequirement);
                }
            }
            for (RiskItem riskItemList1OldRiskItem : riskItemList1Old) {
                if (!riskItemList1New.contains(riskItemList1OldRiskItem)) {
                    riskItemList1OldRiskItem.getRiskControlList().remove(riskControl);
                    riskItemList1OldRiskItem = em.merge(riskItemList1OldRiskItem);
                }
            }
            for (RiskItem riskItemList1NewRiskItem : riskItemList1New) {
                if (!riskItemList1Old.contains(riskItemList1NewRiskItem)) {
                    riskItemList1NewRiskItem.getRiskControlList().add(riskControl);
                    riskItemList1NewRiskItem = em.merge(riskItemList1NewRiskItem);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RiskControlPK id = riskControl.getRiskControlPK();
                if (findRiskControl(id) == null) {
                    throw new NonexistentEntityException("The riskControl with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(RiskControlPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskControl riskControl;
            try {
                riskControl = em.getReference(RiskControl.class, id);
                riskControl.getRiskControlPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The riskControl with id " + id + " no longer exists.", enfe);
            }
            RiskControlType riskControlType = riskControl.getRiskControlType();
            if (riskControlType != null) {
                riskControlType.getRiskControlList().remove(riskControl);
                riskControlType = em.merge(riskControlType);
            }
            List<RiskItem> riskItemList = riskControl.getRiskItemList();
            for (RiskItem riskItemListRiskItem : riskItemList) {
                riskItemListRiskItem.getRiskControlList().remove(riskControl);
                riskItemListRiskItem = em.merge(riskItemListRiskItem);
            }
            List<TestCase> testCaseList = riskControl.getTestCaseList();
            for (TestCase testCaseListTestCase : testCaseList) {
                testCaseListTestCase.getRiskControlList().remove(riskControl);
                testCaseListTestCase = em.merge(testCaseListTestCase);
            }
            List<Requirement> requirementList = riskControl.getRequirementList();
            for (Requirement requirementListRequirement : requirementList) {
                requirementListRequirement.getRiskControlList().remove(riskControl);
                requirementListRequirement = em.merge(requirementListRequirement);
            }
            List<RiskItem> riskItemList1 = riskControl.getRiskItemList1();
            for (RiskItem riskItemList1RiskItem : riskItemList1) {
                riskItemList1RiskItem.getRiskControlList().remove(riskControl);
                riskItemList1RiskItem = em.merge(riskItemList1RiskItem);
            }
            em.remove(riskControl);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RiskControl> findRiskControlEntities() {
        return findRiskControlEntities(true, -1, -1);
    }

    public List<RiskControl> findRiskControlEntities(int maxResults, int firstResult) {
        return findRiskControlEntities(false, maxResults, firstResult);
    }

    private List<RiskControl> findRiskControlEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RiskControl.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public RiskControl findRiskControl(RiskControlPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RiskControl.class, id);
        } finally {
            em.close();
        }
    }

    public int getRiskControlCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RiskControl> rt = cq.from(RiskControl.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
