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
import com.validation.manager.core.db.TemplateNode;
import com.validation.manager.core.db.TemplateNodeType;
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
public class TemplateNodeTypeJpaController implements Serializable {

    public TemplateNodeTypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TemplateNodeType templateNodeType) {
        if (templateNodeType.getTemplateNodeList() == null) {
            templateNodeType.setTemplateNodeList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<TemplateNode> attachedTemplateNodeList = new ArrayList<>();
            for (TemplateNode templateNodeListTemplateNodeToAttach : templateNodeType.getTemplateNodeList()) {
                templateNodeListTemplateNodeToAttach = em.getReference(templateNodeListTemplateNodeToAttach.getClass(), templateNodeListTemplateNodeToAttach.getTemplateNodePK());
                attachedTemplateNodeList.add(templateNodeListTemplateNodeToAttach);
            }
            templateNodeType.setTemplateNodeList(attachedTemplateNodeList);
            em.persist(templateNodeType);
            for (TemplateNode templateNodeListTemplateNode : templateNodeType.getTemplateNodeList()) {
                TemplateNodeType oldTemplateNodeTypeOfTemplateNodeListTemplateNode = templateNodeListTemplateNode.getTemplateNodeType();
                templateNodeListTemplateNode.setTemplateNodeType(templateNodeType);
                templateNodeListTemplateNode = em.merge(templateNodeListTemplateNode);
                if (oldTemplateNodeTypeOfTemplateNodeListTemplateNode != null) {
                    oldTemplateNodeTypeOfTemplateNodeListTemplateNode.getTemplateNodeList().remove(templateNodeListTemplateNode);
                    oldTemplateNodeTypeOfTemplateNodeListTemplateNode = em.merge(oldTemplateNodeTypeOfTemplateNodeListTemplateNode);
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

    public void edit(TemplateNodeType templateNodeType) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TemplateNodeType persistentTemplateNodeType = em.find(TemplateNodeType.class, templateNodeType.getId());
            List<TemplateNode> templateNodeListOld = persistentTemplateNodeType.getTemplateNodeList();
            List<TemplateNode> templateNodeListNew = templateNodeType.getTemplateNodeList();
            List<String> illegalOrphanMessages = null;
            for (TemplateNode templateNodeListOldTemplateNode : templateNodeListOld) {
                if (!templateNodeListNew.contains(templateNodeListOldTemplateNode)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain TemplateNode " + templateNodeListOldTemplateNode + " since its templateNodeType field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<TemplateNode> attachedTemplateNodeListNew = new ArrayList<>();
            for (TemplateNode templateNodeListNewTemplateNodeToAttach : templateNodeListNew) {
                templateNodeListNewTemplateNodeToAttach = em.getReference(templateNodeListNewTemplateNodeToAttach.getClass(), templateNodeListNewTemplateNodeToAttach.getTemplateNodePK());
                attachedTemplateNodeListNew.add(templateNodeListNewTemplateNodeToAttach);
            }
            templateNodeListNew = attachedTemplateNodeListNew;
            templateNodeType.setTemplateNodeList(templateNodeListNew);
            templateNodeType = em.merge(templateNodeType);
            for (TemplateNode templateNodeListNewTemplateNode : templateNodeListNew) {
                if (!templateNodeListOld.contains(templateNodeListNewTemplateNode)) {
                    TemplateNodeType oldTemplateNodeTypeOfTemplateNodeListNewTemplateNode = templateNodeListNewTemplateNode.getTemplateNodeType();
                    templateNodeListNewTemplateNode.setTemplateNodeType(templateNodeType);
                    templateNodeListNewTemplateNode = em.merge(templateNodeListNewTemplateNode);
                    if (oldTemplateNodeTypeOfTemplateNodeListNewTemplateNode != null && !oldTemplateNodeTypeOfTemplateNodeListNewTemplateNode.equals(templateNodeType)) {
                        oldTemplateNodeTypeOfTemplateNodeListNewTemplateNode.getTemplateNodeList().remove(templateNodeListNewTemplateNode);
                        oldTemplateNodeTypeOfTemplateNodeListNewTemplateNode = em.merge(oldTemplateNodeTypeOfTemplateNodeListNewTemplateNode);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = templateNodeType.getId();
                if (findTemplateNodeType(id) == null) {
                    throw new NonexistentEntityException("The templateNodeType with id " + id + " no longer exists.");
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
            TemplateNodeType templateNodeType;
            try {
                templateNodeType = em.getReference(TemplateNodeType.class, id);
                templateNodeType.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The templateNodeType with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<TemplateNode> templateNodeListOrphanCheck = templateNodeType.getTemplateNodeList();
            for (TemplateNode templateNodeListOrphanCheckTemplateNode : templateNodeListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This TemplateNodeType (" + templateNodeType + ") cannot be destroyed since the TemplateNode " + templateNodeListOrphanCheckTemplateNode + " in its templateNodeList field has a non-nullable templateNodeType field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(templateNodeType);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TemplateNodeType> findTemplateNodeTypeEntities() {
        return findTemplateNodeTypeEntities(true, -1, -1);
    }

    public List<TemplateNodeType> findTemplateNodeTypeEntities(int maxResults, int firstResult) {
        return findTemplateNodeTypeEntities(false, maxResults, firstResult);
    }

    private List<TemplateNodeType> findTemplateNodeTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TemplateNodeType.class));
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

    public TemplateNodeType findTemplateNodeType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TemplateNodeType.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getTemplateNodeTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TemplateNodeType> rt = cq.from(TemplateNodeType.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
