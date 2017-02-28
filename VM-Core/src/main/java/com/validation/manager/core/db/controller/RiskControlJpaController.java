/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.RiskControl;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.RiskControlType;
import com.validation.manager.core.db.RiskItem;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.RiskControlHasTestCase;
import com.validation.manager.core.db.RiskControlHasRequirement;
import com.validation.manager.core.db.RiskControlPK;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
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
        if (riskControl.getRiskItemList1() == null) {
            riskControl.setRiskItemList1(new ArrayList<RiskItem>());
        }
        if (riskControl.getRiskControlHasTestCaseList() == null) {
            riskControl.setRiskControlHasTestCaseList(new ArrayList<RiskControlHasTestCase>());
        }
        if (riskControl.getRiskControlHasRequirementList() == null) {
            riskControl.setRiskControlHasRequirementList(new ArrayList<RiskControlHasRequirement>());
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
            List<RiskItem> attachedRiskItemList1 = new ArrayList<RiskItem>();
            for (RiskItem riskItemList1RiskItemToAttach : riskControl.getRiskItemList1()) {
                riskItemList1RiskItemToAttach = em.getReference(riskItemList1RiskItemToAttach.getClass(), riskItemList1RiskItemToAttach.getRiskItemPK());
                attachedRiskItemList1.add(riskItemList1RiskItemToAttach);
            }
            riskControl.setRiskItemList1(attachedRiskItemList1);
            List<RiskControlHasTestCase> attachedRiskControlHasTestCaseList = new ArrayList<RiskControlHasTestCase>();
            for (RiskControlHasTestCase riskControlHasTestCaseListRiskControlHasTestCaseToAttach : riskControl.getRiskControlHasTestCaseList()) {
                riskControlHasTestCaseListRiskControlHasTestCaseToAttach = em.getReference(riskControlHasTestCaseListRiskControlHasTestCaseToAttach.getClass(), riskControlHasTestCaseListRiskControlHasTestCaseToAttach.getRiskControlHasTestCasePK());
                attachedRiskControlHasTestCaseList.add(riskControlHasTestCaseListRiskControlHasTestCaseToAttach);
            }
            riskControl.setRiskControlHasTestCaseList(attachedRiskControlHasTestCaseList);
            List<RiskControlHasRequirement> attachedRiskControlHasRequirementList = new ArrayList<RiskControlHasRequirement>();
            for (RiskControlHasRequirement riskControlHasRequirementListRiskControlHasRequirementToAttach : riskControl.getRiskControlHasRequirementList()) {
                riskControlHasRequirementListRiskControlHasRequirementToAttach = em.getReference(riskControlHasRequirementListRiskControlHasRequirementToAttach.getClass(), riskControlHasRequirementListRiskControlHasRequirementToAttach.getRiskControlHasRequirementPK());
                attachedRiskControlHasRequirementList.add(riskControlHasRequirementListRiskControlHasRequirementToAttach);
            }
            riskControl.setRiskControlHasRequirementList(attachedRiskControlHasRequirementList);
            em.persist(riskControl);
            if (riskControlType != null) {
                riskControlType.getRiskControlList().add(riskControl);
                riskControlType = em.merge(riskControlType);
            }
            for (RiskItem riskItemListRiskItem : riskControl.getRiskItemList()) {
                riskItemListRiskItem.getRiskControlList().add(riskControl);
                riskItemListRiskItem = em.merge(riskItemListRiskItem);
            }
            for (RiskItem riskItemList1RiskItem : riskControl.getRiskItemList1()) {
                riskItemList1RiskItem.getRiskControlList().add(riskControl);
                riskItemList1RiskItem = em.merge(riskItemList1RiskItem);
            }
            for (RiskControlHasTestCase riskControlHasTestCaseListRiskControlHasTestCase : riskControl.getRiskControlHasTestCaseList()) {
                RiskControl oldRiskControlOfRiskControlHasTestCaseListRiskControlHasTestCase = riskControlHasTestCaseListRiskControlHasTestCase.getRiskControl();
                riskControlHasTestCaseListRiskControlHasTestCase.setRiskControl(riskControl);
                riskControlHasTestCaseListRiskControlHasTestCase = em.merge(riskControlHasTestCaseListRiskControlHasTestCase);
                if (oldRiskControlOfRiskControlHasTestCaseListRiskControlHasTestCase != null) {
                    oldRiskControlOfRiskControlHasTestCaseListRiskControlHasTestCase.getRiskControlHasTestCaseList().remove(riskControlHasTestCaseListRiskControlHasTestCase);
                    oldRiskControlOfRiskControlHasTestCaseListRiskControlHasTestCase = em.merge(oldRiskControlOfRiskControlHasTestCaseListRiskControlHasTestCase);
                }
            }
            for (RiskControlHasRequirement riskControlHasRequirementListRiskControlHasRequirement : riskControl.getRiskControlHasRequirementList()) {
                RiskControl oldRiskControlOfRiskControlHasRequirementListRiskControlHasRequirement = riskControlHasRequirementListRiskControlHasRequirement.getRiskControl();
                riskControlHasRequirementListRiskControlHasRequirement.setRiskControl(riskControl);
                riskControlHasRequirementListRiskControlHasRequirement = em.merge(riskControlHasRequirementListRiskControlHasRequirement);
                if (oldRiskControlOfRiskControlHasRequirementListRiskControlHasRequirement != null) {
                    oldRiskControlOfRiskControlHasRequirementListRiskControlHasRequirement.getRiskControlHasRequirementList().remove(riskControlHasRequirementListRiskControlHasRequirement);
                    oldRiskControlOfRiskControlHasRequirementListRiskControlHasRequirement = em.merge(oldRiskControlOfRiskControlHasRequirementListRiskControlHasRequirement);
                }
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

    public void edit(RiskControl riskControl) throws IllegalOrphanException, NonexistentEntityException, Exception {
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
            List<RiskItem> riskItemList1Old = persistentRiskControl.getRiskItemList1();
            List<RiskItem> riskItemList1New = riskControl.getRiskItemList1();
            List<RiskControlHasTestCase> riskControlHasTestCaseListOld = persistentRiskControl.getRiskControlHasTestCaseList();
            List<RiskControlHasTestCase> riskControlHasTestCaseListNew = riskControl.getRiskControlHasTestCaseList();
            List<RiskControlHasRequirement> riskControlHasRequirementListOld = persistentRiskControl.getRiskControlHasRequirementList();
            List<RiskControlHasRequirement> riskControlHasRequirementListNew = riskControl.getRiskControlHasRequirementList();
            List<String> illegalOrphanMessages = null;
            for (RiskControlHasTestCase riskControlHasTestCaseListOldRiskControlHasTestCase : riskControlHasTestCaseListOld) {
                if (!riskControlHasTestCaseListNew.contains(riskControlHasTestCaseListOldRiskControlHasTestCase)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RiskControlHasTestCase " + riskControlHasTestCaseListOldRiskControlHasTestCase + " since its riskControl field is not nullable.");
                }
            }
            for (RiskControlHasRequirement riskControlHasRequirementListOldRiskControlHasRequirement : riskControlHasRequirementListOld) {
                if (!riskControlHasRequirementListNew.contains(riskControlHasRequirementListOldRiskControlHasRequirement)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RiskControlHasRequirement " + riskControlHasRequirementListOldRiskControlHasRequirement + " since its riskControl field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
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
            List<RiskItem> attachedRiskItemList1New = new ArrayList<RiskItem>();
            for (RiskItem riskItemList1NewRiskItemToAttach : riskItemList1New) {
                riskItemList1NewRiskItemToAttach = em.getReference(riskItemList1NewRiskItemToAttach.getClass(), riskItemList1NewRiskItemToAttach.getRiskItemPK());
                attachedRiskItemList1New.add(riskItemList1NewRiskItemToAttach);
            }
            riskItemList1New = attachedRiskItemList1New;
            riskControl.setRiskItemList1(riskItemList1New);
            List<RiskControlHasTestCase> attachedRiskControlHasTestCaseListNew = new ArrayList<RiskControlHasTestCase>();
            for (RiskControlHasTestCase riskControlHasTestCaseListNewRiskControlHasTestCaseToAttach : riskControlHasTestCaseListNew) {
                riskControlHasTestCaseListNewRiskControlHasTestCaseToAttach = em.getReference(riskControlHasTestCaseListNewRiskControlHasTestCaseToAttach.getClass(), riskControlHasTestCaseListNewRiskControlHasTestCaseToAttach.getRiskControlHasTestCasePK());
                attachedRiskControlHasTestCaseListNew.add(riskControlHasTestCaseListNewRiskControlHasTestCaseToAttach);
            }
            riskControlHasTestCaseListNew = attachedRiskControlHasTestCaseListNew;
            riskControl.setRiskControlHasTestCaseList(riskControlHasTestCaseListNew);
            List<RiskControlHasRequirement> attachedRiskControlHasRequirementListNew = new ArrayList<RiskControlHasRequirement>();
            for (RiskControlHasRequirement riskControlHasRequirementListNewRiskControlHasRequirementToAttach : riskControlHasRequirementListNew) {
                riskControlHasRequirementListNewRiskControlHasRequirementToAttach = em.getReference(riskControlHasRequirementListNewRiskControlHasRequirementToAttach.getClass(), riskControlHasRequirementListNewRiskControlHasRequirementToAttach.getRiskControlHasRequirementPK());
                attachedRiskControlHasRequirementListNew.add(riskControlHasRequirementListNewRiskControlHasRequirementToAttach);
            }
            riskControlHasRequirementListNew = attachedRiskControlHasRequirementListNew;
            riskControl.setRiskControlHasRequirementList(riskControlHasRequirementListNew);
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
            for (RiskControlHasTestCase riskControlHasTestCaseListNewRiskControlHasTestCase : riskControlHasTestCaseListNew) {
                if (!riskControlHasTestCaseListOld.contains(riskControlHasTestCaseListNewRiskControlHasTestCase)) {
                    RiskControl oldRiskControlOfRiskControlHasTestCaseListNewRiskControlHasTestCase = riskControlHasTestCaseListNewRiskControlHasTestCase.getRiskControl();
                    riskControlHasTestCaseListNewRiskControlHasTestCase.setRiskControl(riskControl);
                    riskControlHasTestCaseListNewRiskControlHasTestCase = em.merge(riskControlHasTestCaseListNewRiskControlHasTestCase);
                    if (oldRiskControlOfRiskControlHasTestCaseListNewRiskControlHasTestCase != null && !oldRiskControlOfRiskControlHasTestCaseListNewRiskControlHasTestCase.equals(riskControl)) {
                        oldRiskControlOfRiskControlHasTestCaseListNewRiskControlHasTestCase.getRiskControlHasTestCaseList().remove(riskControlHasTestCaseListNewRiskControlHasTestCase);
                        oldRiskControlOfRiskControlHasTestCaseListNewRiskControlHasTestCase = em.merge(oldRiskControlOfRiskControlHasTestCaseListNewRiskControlHasTestCase);
                    }
                }
            }
            for (RiskControlHasRequirement riskControlHasRequirementListNewRiskControlHasRequirement : riskControlHasRequirementListNew) {
                if (!riskControlHasRequirementListOld.contains(riskControlHasRequirementListNewRiskControlHasRequirement)) {
                    RiskControl oldRiskControlOfRiskControlHasRequirementListNewRiskControlHasRequirement = riskControlHasRequirementListNewRiskControlHasRequirement.getRiskControl();
                    riskControlHasRequirementListNewRiskControlHasRequirement.setRiskControl(riskControl);
                    riskControlHasRequirementListNewRiskControlHasRequirement = em.merge(riskControlHasRequirementListNewRiskControlHasRequirement);
                    if (oldRiskControlOfRiskControlHasRequirementListNewRiskControlHasRequirement != null && !oldRiskControlOfRiskControlHasRequirementListNewRiskControlHasRequirement.equals(riskControl)) {
                        oldRiskControlOfRiskControlHasRequirementListNewRiskControlHasRequirement.getRiskControlHasRequirementList().remove(riskControlHasRequirementListNewRiskControlHasRequirement);
                        oldRiskControlOfRiskControlHasRequirementListNewRiskControlHasRequirement = em.merge(oldRiskControlOfRiskControlHasRequirementListNewRiskControlHasRequirement);
                    }
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

    public void destroy(RiskControlPK id) throws IllegalOrphanException, NonexistentEntityException {
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
            List<String> illegalOrphanMessages = null;
            List<RiskControlHasTestCase> riskControlHasTestCaseListOrphanCheck = riskControl.getRiskControlHasTestCaseList();
            for (RiskControlHasTestCase riskControlHasTestCaseListOrphanCheckRiskControlHasTestCase : riskControlHasTestCaseListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This RiskControl (" + riskControl + ") cannot be destroyed since the RiskControlHasTestCase " + riskControlHasTestCaseListOrphanCheckRiskControlHasTestCase + " in its riskControlHasTestCaseList field has a non-nullable riskControl field.");
            }
            List<RiskControlHasRequirement> riskControlHasRequirementListOrphanCheck = riskControl.getRiskControlHasRequirementList();
            for (RiskControlHasRequirement riskControlHasRequirementListOrphanCheckRiskControlHasRequirement : riskControlHasRequirementListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This RiskControl (" + riskControl + ") cannot be destroyed since the RiskControlHasRequirement " + riskControlHasRequirementListOrphanCheckRiskControlHasRequirement + " in its riskControlHasRequirementList field has a non-nullable riskControl field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
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
