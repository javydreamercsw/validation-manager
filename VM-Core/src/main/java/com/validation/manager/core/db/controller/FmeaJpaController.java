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
import com.validation.manager.core.db.Fmea;
import com.validation.manager.core.db.FmeaPK;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.RiskItem;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.RiskCategory;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class FmeaJpaController implements Serializable {

    public FmeaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Fmea fmea) throws PreexistingEntityException, Exception {
        if (fmea.getFmeaPK() == null) {
            fmea.setFmeaPK(new FmeaPK());
        }
        if (fmea.getRiskItemList() == null) {
            fmea.setRiskItemList(new ArrayList<>());
        }
        if (fmea.getFmeaList() == null) {
            fmea.setFmeaList(new ArrayList<>());
        }
        if (fmea.getRiskCategoryList() == null) {
            fmea.setRiskCategoryList(new ArrayList<>());
        }
        fmea.getFmeaPK().setProjectId(fmea.getProject().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Fmea parent = fmea.getParent();
            if (parent != null) {
                parent = em.getReference(parent.getClass(), parent.getFmeaPK());
                fmea.setParent(parent);
            }
            Project project = fmea.getProject();
            if (project != null) {
                project = em.getReference(project.getClass(), project.getId());
                fmea.setProject(project);
            }
            List<RiskItem> attachedRiskItemList = new ArrayList<>();
            for (RiskItem riskItemListRiskItemToAttach : fmea.getRiskItemList()) {
                riskItemListRiskItemToAttach = em.getReference(riskItemListRiskItemToAttach.getClass(), riskItemListRiskItemToAttach.getRiskItemPK());
                attachedRiskItemList.add(riskItemListRiskItemToAttach);
            }
            fmea.setRiskItemList(attachedRiskItemList);
            List<Fmea> attachedFmeaList = new ArrayList<>();
            for (Fmea fmeaListFmeaToAttach : fmea.getFmeaList()) {
                fmeaListFmeaToAttach = em.getReference(fmeaListFmeaToAttach.getClass(), fmeaListFmeaToAttach.getFmeaPK());
                attachedFmeaList.add(fmeaListFmeaToAttach);
            }
            fmea.setFmeaList(attachedFmeaList);
            List<RiskCategory> attachedRiskCategoryList = new ArrayList<>();
            for (RiskCategory riskCategoryListRiskCategoryToAttach : fmea.getRiskCategoryList()) {
                riskCategoryListRiskCategoryToAttach = em.getReference(riskCategoryListRiskCategoryToAttach.getClass(), riskCategoryListRiskCategoryToAttach.getId());
                attachedRiskCategoryList.add(riskCategoryListRiskCategoryToAttach);
            }
            fmea.setRiskCategoryList(attachedRiskCategoryList);
            em.persist(fmea);
            if (parent != null) {
                parent.getFmeaList().add(fmea);
                parent = em.merge(parent);
            }
            if (project != null) {
                project.getFmeaList().add(fmea);
                project = em.merge(project);
            }
            for (RiskItem riskItemListRiskItem : fmea.getRiskItemList()) {
                Fmea oldFmeaOfRiskItemListRiskItem = riskItemListRiskItem.getFmea();
                riskItemListRiskItem.setFmea(fmea);
                riskItemListRiskItem = em.merge(riskItemListRiskItem);
                if (oldFmeaOfRiskItemListRiskItem != null) {
                    oldFmeaOfRiskItemListRiskItem.getRiskItemList().remove(riskItemListRiskItem);
                    oldFmeaOfRiskItemListRiskItem = em.merge(oldFmeaOfRiskItemListRiskItem);
                }
            }
            for (Fmea fmeaListFmea : fmea.getFmeaList()) {
                Fmea oldParentOfFmeaListFmea = fmeaListFmea.getParent();
                fmeaListFmea.setParent(fmea);
                fmeaListFmea = em.merge(fmeaListFmea);
                if (oldParentOfFmeaListFmea != null) {
                    oldParentOfFmeaListFmea.getFmeaList().remove(fmeaListFmea);
                    oldParentOfFmeaListFmea = em.merge(oldParentOfFmeaListFmea);
                }
            }
            for (RiskCategory riskCategoryListRiskCategory : fmea.getRiskCategoryList()) {
                riskCategoryListRiskCategory.getFmeaList().add(fmea);
                riskCategoryListRiskCategory = em.merge(riskCategoryListRiskCategory);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findFmea(fmea.getFmeaPK()) != null) {
                throw new PreexistingEntityException("Fmea " + fmea + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Fmea fmea) throws IllegalOrphanException, NonexistentEntityException, Exception {
        fmea.getFmeaPK().setProjectId(fmea.getProject().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Fmea persistentFmea = em.find(Fmea.class, fmea.getFmeaPK());
            Fmea parentOld = persistentFmea.getParent();
            Fmea parentNew = fmea.getParent();
            Project projectOld = persistentFmea.getProject();
            Project projectNew = fmea.getProject();
            List<RiskItem> riskItemListOld = persistentFmea.getRiskItemList();
            List<RiskItem> riskItemListNew = fmea.getRiskItemList();
            List<Fmea> fmeaListOld = persistentFmea.getFmeaList();
            List<Fmea> fmeaListNew = fmea.getFmeaList();
            List<RiskCategory> riskCategoryListOld = persistentFmea.getRiskCategoryList();
            List<RiskCategory> riskCategoryListNew = fmea.getRiskCategoryList();
            List<String> illegalOrphanMessages = null;
            for (RiskItem riskItemListOldRiskItem : riskItemListOld) {
                if (!riskItemListNew.contains(riskItemListOldRiskItem)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain RiskItem " + riskItemListOldRiskItem + " since its fmea field is not nullable.");
                }
            }
            for (Fmea fmeaListOldFmea : fmeaListOld) {
                if (!fmeaListNew.contains(fmeaListOldFmea)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Fmea " + fmeaListOldFmea + " since its parent field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (parentNew != null) {
                parentNew = em.getReference(parentNew.getClass(), parentNew.getFmeaPK());
                fmea.setParent(parentNew);
            }
            if (projectNew != null) {
                projectNew = em.getReference(projectNew.getClass(), projectNew.getId());
                fmea.setProject(projectNew);
            }
            List<RiskItem> attachedRiskItemListNew = new ArrayList<>();
            for (RiskItem riskItemListNewRiskItemToAttach : riskItemListNew) {
                riskItemListNewRiskItemToAttach = em.getReference(riskItemListNewRiskItemToAttach.getClass(), riskItemListNewRiskItemToAttach.getRiskItemPK());
                attachedRiskItemListNew.add(riskItemListNewRiskItemToAttach);
            }
            riskItemListNew = attachedRiskItemListNew;
            fmea.setRiskItemList(riskItemListNew);
            List<Fmea> attachedFmeaListNew = new ArrayList<>();
            for (Fmea fmeaListNewFmeaToAttach : fmeaListNew) {
                fmeaListNewFmeaToAttach = em.getReference(fmeaListNewFmeaToAttach.getClass(), fmeaListNewFmeaToAttach.getFmeaPK());
                attachedFmeaListNew.add(fmeaListNewFmeaToAttach);
            }
            fmeaListNew = attachedFmeaListNew;
            fmea.setFmeaList(fmeaListNew);
            List<RiskCategory> attachedRiskCategoryListNew = new ArrayList<>();
            for (RiskCategory riskCategoryListNewRiskCategoryToAttach : riskCategoryListNew) {
                riskCategoryListNewRiskCategoryToAttach = em.getReference(riskCategoryListNewRiskCategoryToAttach.getClass(), riskCategoryListNewRiskCategoryToAttach.getId());
                attachedRiskCategoryListNew.add(riskCategoryListNewRiskCategoryToAttach);
            }
            riskCategoryListNew = attachedRiskCategoryListNew;
            fmea.setRiskCategoryList(riskCategoryListNew);
            fmea = em.merge(fmea);
            if (parentOld != null && !parentOld.equals(parentNew)) {
                parentOld.getFmeaList().remove(fmea);
                parentOld = em.merge(parentOld);
            }
            if (parentNew != null && !parentNew.equals(parentOld)) {
                parentNew.getFmeaList().add(fmea);
                parentNew = em.merge(parentNew);
            }
            if (projectOld != null && !projectOld.equals(projectNew)) {
                projectOld.getFmeaList().remove(fmea);
                projectOld = em.merge(projectOld);
            }
            if (projectNew != null && !projectNew.equals(projectOld)) {
                projectNew.getFmeaList().add(fmea);
                projectNew = em.merge(projectNew);
            }
            for (RiskItem riskItemListNewRiskItem : riskItemListNew) {
                if (!riskItemListOld.contains(riskItemListNewRiskItem)) {
                    Fmea oldFmeaOfRiskItemListNewRiskItem = riskItemListNewRiskItem.getFmea();
                    riskItemListNewRiskItem.setFmea(fmea);
                    riskItemListNewRiskItem = em.merge(riskItemListNewRiskItem);
                    if (oldFmeaOfRiskItemListNewRiskItem != null && !oldFmeaOfRiskItemListNewRiskItem.equals(fmea)) {
                        oldFmeaOfRiskItemListNewRiskItem.getRiskItemList().remove(riskItemListNewRiskItem);
                        oldFmeaOfRiskItemListNewRiskItem = em.merge(oldFmeaOfRiskItemListNewRiskItem);
                    }
                }
            }
            for (Fmea fmeaListNewFmea : fmeaListNew) {
                if (!fmeaListOld.contains(fmeaListNewFmea)) {
                    Fmea oldParentOfFmeaListNewFmea = fmeaListNewFmea.getParent();
                    fmeaListNewFmea.setParent(fmea);
                    fmeaListNewFmea = em.merge(fmeaListNewFmea);
                    if (oldParentOfFmeaListNewFmea != null && !oldParentOfFmeaListNewFmea.equals(fmea)) {
                        oldParentOfFmeaListNewFmea.getFmeaList().remove(fmeaListNewFmea);
                        oldParentOfFmeaListNewFmea = em.merge(oldParentOfFmeaListNewFmea);
                    }
                }
            }
            for (RiskCategory riskCategoryListOldRiskCategory : riskCategoryListOld) {
                if (!riskCategoryListNew.contains(riskCategoryListOldRiskCategory)) {
                    riskCategoryListOldRiskCategory.getFmeaList().remove(fmea);
                    riskCategoryListOldRiskCategory = em.merge(riskCategoryListOldRiskCategory);
                }
            }
            for (RiskCategory riskCategoryListNewRiskCategory : riskCategoryListNew) {
                if (!riskCategoryListOld.contains(riskCategoryListNewRiskCategory)) {
                    riskCategoryListNewRiskCategory.getFmeaList().add(fmea);
                    riskCategoryListNewRiskCategory = em.merge(riskCategoryListNewRiskCategory);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                FmeaPK id = fmea.getFmeaPK();
                if (findFmea(id) == null) {
                    throw new NonexistentEntityException("The fmea with id " + id + " no longer exists.");
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

    public void destroy(FmeaPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Fmea fmea;
            try {
                fmea = em.getReference(Fmea.class, id);
                fmea.getFmeaPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The fmea with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RiskItem> riskItemListOrphanCheck = fmea.getRiskItemList();
            for (RiskItem riskItemListOrphanCheckRiskItem : riskItemListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Fmea (" + fmea + ") cannot be destroyed since the RiskItem " + riskItemListOrphanCheckRiskItem + " in its riskItemList field has a non-nullable fmea field.");
            }
            List<Fmea> fmeaListOrphanCheck = fmea.getFmeaList();
            for (Fmea fmeaListOrphanCheckFmea : fmeaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Fmea (" + fmea + ") cannot be destroyed since the Fmea " + fmeaListOrphanCheckFmea + " in its fmeaList field has a non-nullable parent field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Fmea parent = fmea.getParent();
            if (parent != null) {
                parent.getFmeaList().remove(fmea);
                parent = em.merge(parent);
            }
            Project project = fmea.getProject();
            if (project != null) {
                project.getFmeaList().remove(fmea);
                project = em.merge(project);
            }
            List<RiskCategory> riskCategoryList = fmea.getRiskCategoryList();
            for (RiskCategory riskCategoryListRiskCategory : riskCategoryList) {
                riskCategoryListRiskCategory.getFmeaList().remove(fmea);
                riskCategoryListRiskCategory = em.merge(riskCategoryListRiskCategory);
            }
            em.remove(fmea);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Fmea> findFmeaEntities() {
        return findFmeaEntities(true, -1, -1);
    }

    public List<Fmea> findFmeaEntities(int maxResults, int firstResult) {
        return findFmeaEntities(false, maxResults, firstResult);
    }

    private List<Fmea> findFmeaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Fmea.class));
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

    public Fmea findFmea(FmeaPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Fmea.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getFmeaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Fmea> rt = cq.from(Fmea.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
