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

import com.validation.manager.core.db.RiskControl;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.RiskControlType;
import com.validation.manager.core.db.RiskControlHasTestCase;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.RiskControlHasRequirement;
import com.validation.manager.core.db.RiskControlHasResidualRiskItem;
import com.validation.manager.core.db.RiskControlPK;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
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
        if (riskControl.getRiskControlHasTestCaseList() == null) {
            riskControl.setRiskControlHasTestCaseList(new ArrayList<>());
        }
        if (riskControl.getRiskControlHasRequirementList() == null) {
            riskControl.setRiskControlHasRequirementList(new ArrayList<>());
        }
        if (riskControl.getRiskControlHasResidualRiskItemList() == null) {
            riskControl.setRiskControlHasResidualRiskItemList(new ArrayList<>());
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
            List<RiskControlHasTestCase> attachedRiskControlHasTestCaseList = new ArrayList<>();
            for (RiskControlHasTestCase riskControlHasTestCaseListRiskControlHasTestCaseToAttach : riskControl.getRiskControlHasTestCaseList()) {
                riskControlHasTestCaseListRiskControlHasTestCaseToAttach = em.getReference(riskControlHasTestCaseListRiskControlHasTestCaseToAttach.getClass(), riskControlHasTestCaseListRiskControlHasTestCaseToAttach.getRiskControlHasTestCasePK());
                attachedRiskControlHasTestCaseList.add(riskControlHasTestCaseListRiskControlHasTestCaseToAttach);
            }
            riskControl.setRiskControlHasTestCaseList(attachedRiskControlHasTestCaseList);
            List<RiskControlHasRequirement> attachedRiskControlHasRequirementList = new ArrayList<>();
            for (RiskControlHasRequirement riskControlHasRequirementListRiskControlHasRequirementToAttach : riskControl.getRiskControlHasRequirementList()) {
                riskControlHasRequirementListRiskControlHasRequirementToAttach = em.getReference(riskControlHasRequirementListRiskControlHasRequirementToAttach.getClass(), riskControlHasRequirementListRiskControlHasRequirementToAttach.getRiskControlHasRequirementPK());
                attachedRiskControlHasRequirementList.add(riskControlHasRequirementListRiskControlHasRequirementToAttach);
            }
            riskControl.setRiskControlHasRequirementList(attachedRiskControlHasRequirementList);
            List<RiskControlHasResidualRiskItem> attachedRiskControlHasResidualRiskItemList = new ArrayList<>();
            for (RiskControlHasResidualRiskItem riskControlHasResidualRiskItemListRiskControlHasResidualRiskItemToAttach : riskControl.getRiskControlHasResidualRiskItemList()) {
                riskControlHasResidualRiskItemListRiskControlHasResidualRiskItemToAttach = em.getReference(riskControlHasResidualRiskItemListRiskControlHasResidualRiskItemToAttach.getClass(), riskControlHasResidualRiskItemListRiskControlHasResidualRiskItemToAttach.getRiskControlHasResidualRiskItemPK());
                attachedRiskControlHasResidualRiskItemList.add(riskControlHasResidualRiskItemListRiskControlHasResidualRiskItemToAttach);
            }
            riskControl.setRiskControlHasResidualRiskItemList(attachedRiskControlHasResidualRiskItemList);
            em.persist(riskControl);
            if (riskControlType != null) {
                riskControlType.getRiskControlList().add(riskControl);
                riskControlType = em.merge(riskControlType);
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
            for (RiskControlHasResidualRiskItem riskControlHasResidualRiskItemListRiskControlHasResidualRiskItem : riskControl.getRiskControlHasResidualRiskItemList()) {
                RiskControl oldRiskControlOfRiskControlHasResidualRiskItemListRiskControlHasResidualRiskItem = riskControlHasResidualRiskItemListRiskControlHasResidualRiskItem.getRiskControl();
                riskControlHasResidualRiskItemListRiskControlHasResidualRiskItem.setRiskControl(riskControl);
                riskControlHasResidualRiskItemListRiskControlHasResidualRiskItem = em.merge(riskControlHasResidualRiskItemListRiskControlHasResidualRiskItem);
                if (oldRiskControlOfRiskControlHasResidualRiskItemListRiskControlHasResidualRiskItem != null) {
                    oldRiskControlOfRiskControlHasResidualRiskItemListRiskControlHasResidualRiskItem.getRiskControlHasResidualRiskItemList().remove(riskControlHasResidualRiskItemListRiskControlHasResidualRiskItem);
                    oldRiskControlOfRiskControlHasResidualRiskItemListRiskControlHasResidualRiskItem = em.merge(oldRiskControlOfRiskControlHasResidualRiskItemListRiskControlHasResidualRiskItem);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findRiskControl(riskControl.getRiskControlPK()) != null) {
                throw new PreexistingEntityException("RiskControl " + riskControl + " already exists.", ex);
            }
            throw ex;
        }
        finally {
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
            List<RiskControlHasTestCase> riskControlHasTestCaseListOld = persistentRiskControl.getRiskControlHasTestCaseList();
            List<RiskControlHasTestCase> riskControlHasTestCaseListNew = riskControl.getRiskControlHasTestCaseList();
            List<RiskControlHasRequirement> riskControlHasRequirementListOld = persistentRiskControl.getRiskControlHasRequirementList();
            List<RiskControlHasRequirement> riskControlHasRequirementListNew = riskControl.getRiskControlHasRequirementList();
            List<RiskControlHasResidualRiskItem> riskControlHasResidualRiskItemListOld = persistentRiskControl.getRiskControlHasResidualRiskItemList();
            List<RiskControlHasResidualRiskItem> riskControlHasResidualRiskItemListNew = riskControl.getRiskControlHasResidualRiskItemList();
            List<String> illegalOrphanMessages = null;
            for (RiskControlHasTestCase riskControlHasTestCaseListOldRiskControlHasTestCase : riskControlHasTestCaseListOld) {
                if (!riskControlHasTestCaseListNew.contains(riskControlHasTestCaseListOldRiskControlHasTestCase)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain RiskControlHasTestCase " + riskControlHasTestCaseListOldRiskControlHasTestCase + " since its riskControl field is not nullable.");
                }
            }
            for (RiskControlHasRequirement riskControlHasRequirementListOldRiskControlHasRequirement : riskControlHasRequirementListOld) {
                if (!riskControlHasRequirementListNew.contains(riskControlHasRequirementListOldRiskControlHasRequirement)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain RiskControlHasRequirement " + riskControlHasRequirementListOldRiskControlHasRequirement + " since its riskControl field is not nullable.");
                }
            }
            for (RiskControlHasResidualRiskItem riskControlHasResidualRiskItemListOldRiskControlHasResidualRiskItem : riskControlHasResidualRiskItemListOld) {
                if (!riskControlHasResidualRiskItemListNew.contains(riskControlHasResidualRiskItemListOldRiskControlHasResidualRiskItem)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain RiskControlHasResidualRiskItem " + riskControlHasResidualRiskItemListOldRiskControlHasResidualRiskItem + " since its riskControl field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (riskControlTypeNew != null) {
                riskControlTypeNew = em.getReference(riskControlTypeNew.getClass(), riskControlTypeNew.getId());
                riskControl.setRiskControlType(riskControlTypeNew);
            }
            List<RiskControlHasTestCase> attachedRiskControlHasTestCaseListNew = new ArrayList<>();
            for (RiskControlHasTestCase riskControlHasTestCaseListNewRiskControlHasTestCaseToAttach : riskControlHasTestCaseListNew) {
                riskControlHasTestCaseListNewRiskControlHasTestCaseToAttach = em.getReference(riskControlHasTestCaseListNewRiskControlHasTestCaseToAttach.getClass(), riskControlHasTestCaseListNewRiskControlHasTestCaseToAttach.getRiskControlHasTestCasePK());
                attachedRiskControlHasTestCaseListNew.add(riskControlHasTestCaseListNewRiskControlHasTestCaseToAttach);
            }
            riskControlHasTestCaseListNew = attachedRiskControlHasTestCaseListNew;
            riskControl.setRiskControlHasTestCaseList(riskControlHasTestCaseListNew);
            List<RiskControlHasRequirement> attachedRiskControlHasRequirementListNew = new ArrayList<>();
            for (RiskControlHasRequirement riskControlHasRequirementListNewRiskControlHasRequirementToAttach : riskControlHasRequirementListNew) {
                riskControlHasRequirementListNewRiskControlHasRequirementToAttach = em.getReference(riskControlHasRequirementListNewRiskControlHasRequirementToAttach.getClass(), riskControlHasRequirementListNewRiskControlHasRequirementToAttach.getRiskControlHasRequirementPK());
                attachedRiskControlHasRequirementListNew.add(riskControlHasRequirementListNewRiskControlHasRequirementToAttach);
            }
            riskControlHasRequirementListNew = attachedRiskControlHasRequirementListNew;
            riskControl.setRiskControlHasRequirementList(riskControlHasRequirementListNew);
            List<RiskControlHasResidualRiskItem> attachedRiskControlHasResidualRiskItemListNew = new ArrayList<>();
            for (RiskControlHasResidualRiskItem riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItemToAttach : riskControlHasResidualRiskItemListNew) {
                riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItemToAttach = em.getReference(riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItemToAttach.getClass(), riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItemToAttach.getRiskControlHasResidualRiskItemPK());
                attachedRiskControlHasResidualRiskItemListNew.add(riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItemToAttach);
            }
            riskControlHasResidualRiskItemListNew = attachedRiskControlHasResidualRiskItemListNew;
            riskControl.setRiskControlHasResidualRiskItemList(riskControlHasResidualRiskItemListNew);
            riskControl = em.merge(riskControl);
            if (riskControlTypeOld != null && !riskControlTypeOld.equals(riskControlTypeNew)) {
                riskControlTypeOld.getRiskControlList().remove(riskControl);
                riskControlTypeOld = em.merge(riskControlTypeOld);
            }
            if (riskControlTypeNew != null && !riskControlTypeNew.equals(riskControlTypeOld)) {
                riskControlTypeNew.getRiskControlList().add(riskControl);
                riskControlTypeNew = em.merge(riskControlTypeNew);
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
            for (RiskControlHasResidualRiskItem riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem : riskControlHasResidualRiskItemListNew) {
                if (!riskControlHasResidualRiskItemListOld.contains(riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem)) {
                    RiskControl oldRiskControlOfRiskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem = riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem.getRiskControl();
                    riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem.setRiskControl(riskControl);
                    riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem = em.merge(riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem);
                    if (oldRiskControlOfRiskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem != null && !oldRiskControlOfRiskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem.equals(riskControl)) {
                        oldRiskControlOfRiskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem.getRiskControlHasResidualRiskItemList().remove(riskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem);
                        oldRiskControlOfRiskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem = em.merge(oldRiskControlOfRiskControlHasResidualRiskItemListNewRiskControlHasResidualRiskItem);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RiskControlPK id = riskControl.getRiskControlPK();
                if (findRiskControl(id) == null) {
                    throw new NonexistentEntityException("The riskControl with id " + id + " no longer exists.");
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

    public void destroy(RiskControlPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskControl riskControl;
            try {
                riskControl = em.getReference(RiskControl.class, id);
                riskControl.getRiskControlPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The riskControl with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RiskControlHasTestCase> riskControlHasTestCaseListOrphanCheck = riskControl.getRiskControlHasTestCaseList();
            for (RiskControlHasTestCase riskControlHasTestCaseListOrphanCheckRiskControlHasTestCase : riskControlHasTestCaseListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This RiskControl (" + riskControl + ") cannot be destroyed since the RiskControlHasTestCase " + riskControlHasTestCaseListOrphanCheckRiskControlHasTestCase + " in its riskControlHasTestCaseList field has a non-nullable riskControl field.");
            }
            List<RiskControlHasRequirement> riskControlHasRequirementListOrphanCheck = riskControl.getRiskControlHasRequirementList();
            for (RiskControlHasRequirement riskControlHasRequirementListOrphanCheckRiskControlHasRequirement : riskControlHasRequirementListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This RiskControl (" + riskControl + ") cannot be destroyed since the RiskControlHasRequirement " + riskControlHasRequirementListOrphanCheckRiskControlHasRequirement + " in its riskControlHasRequirementList field has a non-nullable riskControl field.");
            }
            List<RiskControlHasResidualRiskItem> riskControlHasResidualRiskItemListOrphanCheck = riskControl.getRiskControlHasResidualRiskItemList();
            for (RiskControlHasResidualRiskItem riskControlHasResidualRiskItemListOrphanCheckRiskControlHasResidualRiskItem : riskControlHasResidualRiskItemListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This RiskControl (" + riskControl + ") cannot be destroyed since the RiskControlHasResidualRiskItem " + riskControlHasResidualRiskItemListOrphanCheckRiskControlHasResidualRiskItem + " in its riskControlHasResidualRiskItemList field has a non-nullable riskControl field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            RiskControlType riskControlType = riskControl.getRiskControlType();
            if (riskControlType != null) {
                riskControlType.getRiskControlList().remove(riskControl);
                riskControlType = em.merge(riskControlType);
            }
            em.remove(riskControl);
            em.getTransaction().commit();
        }
        finally {
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
        }
        finally {
            em.close();
        }
    }

    public RiskControl findRiskControl(RiskControlPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RiskControl.class, id);
        }
        finally {
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
        }
        finally {
            em.close();
        }
    }

}
