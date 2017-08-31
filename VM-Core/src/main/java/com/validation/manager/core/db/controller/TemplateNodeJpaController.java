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
import com.validation.manager.core.db.Template;
import com.validation.manager.core.db.TemplateNode;
import com.validation.manager.core.db.TemplateNodePK;
import com.validation.manager.core.db.TemplateNodeType;
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
public class TemplateNodeJpaController implements Serializable {

    public TemplateNodeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TemplateNode templateNode) throws PreexistingEntityException, Exception {
        if (templateNode.getTemplateNodePK() == null) {
            templateNode.setTemplateNodePK(new TemplateNodePK());
        }
        if (templateNode.getTemplateNodeList() == null) {
            templateNode.setTemplateNodeList(new ArrayList<>());
        }
        templateNode.getTemplateNodePK().setTemplateId(templateNode.getTemplate().getId());
        templateNode.getTemplateNodePK().setTemplateNodeTypeId(templateNode.getTemplateNodeType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Template template = templateNode.getTemplate();
            if (template != null) {
                template = em.getReference(template.getClass(), template.getId());
                templateNode.setTemplate(template);
            }
            TemplateNode templateNodeRel = templateNode.getTemplateNode();
            if (templateNodeRel != null) {
                templateNodeRel = em.getReference(templateNodeRel.getClass(), templateNodeRel.getTemplateNodePK());
                templateNode.setTemplateNode(templateNodeRel);
            }
            TemplateNodeType templateNodeType = templateNode.getTemplateNodeType();
            if (templateNodeType != null) {
                templateNodeType = em.getReference(templateNodeType.getClass(), templateNodeType.getId());
                templateNode.setTemplateNodeType(templateNodeType);
            }
            List<TemplateNode> attachedTemplateNodeList = new ArrayList<>();
            for (TemplateNode templateNodeListTemplateNodeToAttach : templateNode.getTemplateNodeList()) {
                templateNodeListTemplateNodeToAttach = em.getReference(templateNodeListTemplateNodeToAttach.getClass(), templateNodeListTemplateNodeToAttach.getTemplateNodePK());
                attachedTemplateNodeList.add(templateNodeListTemplateNodeToAttach);
            }
            templateNode.setTemplateNodeList(attachedTemplateNodeList);
            em.persist(templateNode);
            if (template != null) {
                template.getTemplateNodeList().add(templateNode);
                template = em.merge(template);
            }
            if (templateNodeRel != null) {
                templateNodeRel.getTemplateNodeList().add(templateNode);
                templateNodeRel = em.merge(templateNodeRel);
            }
            if (templateNodeType != null) {
                templateNodeType.getTemplateNodeList().add(templateNode);
                templateNodeType = em.merge(templateNodeType);
            }
            for (TemplateNode templateNodeListTemplateNode : templateNode.getTemplateNodeList()) {
                TemplateNode oldTemplateNodeOfTemplateNodeListTemplateNode = templateNodeListTemplateNode.getTemplateNode();
                templateNodeListTemplateNode.setTemplateNode(templateNode);
                templateNodeListTemplateNode = em.merge(templateNodeListTemplateNode);
                if (oldTemplateNodeOfTemplateNodeListTemplateNode != null) {
                    oldTemplateNodeOfTemplateNodeListTemplateNode.getTemplateNodeList().remove(templateNodeListTemplateNode);
                    oldTemplateNodeOfTemplateNodeListTemplateNode = em.merge(oldTemplateNodeOfTemplateNodeListTemplateNode);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findTemplateNode(templateNode.getTemplateNodePK()) != null) {
                throw new PreexistingEntityException("TemplateNode " + templateNode + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TemplateNode templateNode) throws NonexistentEntityException, Exception {
        templateNode.getTemplateNodePK().setTemplateId(templateNode.getTemplate().getId());
        templateNode.getTemplateNodePK().setTemplateNodeTypeId(templateNode.getTemplateNodeType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TemplateNode persistentTemplateNode = em.find(TemplateNode.class, templateNode.getTemplateNodePK());
            Template templateOld = persistentTemplateNode.getTemplate();
            Template templateNew = templateNode.getTemplate();
            TemplateNode templateNodeRelOld = persistentTemplateNode.getTemplateNode();
            TemplateNode templateNodeRelNew = templateNode.getTemplateNode();
            TemplateNodeType templateNodeTypeOld = persistentTemplateNode.getTemplateNodeType();
            TemplateNodeType templateNodeTypeNew = templateNode.getTemplateNodeType();
            List<TemplateNode> templateNodeListOld = persistentTemplateNode.getTemplateNodeList();
            List<TemplateNode> templateNodeListNew = templateNode.getTemplateNodeList();
            if (templateNew != null) {
                templateNew = em.getReference(templateNew.getClass(), templateNew.getId());
                templateNode.setTemplate(templateNew);
            }
            if (templateNodeRelNew != null) {
                templateNodeRelNew = em.getReference(templateNodeRelNew.getClass(), templateNodeRelNew.getTemplateNodePK());
                templateNode.setTemplateNode(templateNodeRelNew);
            }
            if (templateNodeTypeNew != null) {
                templateNodeTypeNew = em.getReference(templateNodeTypeNew.getClass(), templateNodeTypeNew.getId());
                templateNode.setTemplateNodeType(templateNodeTypeNew);
            }
            List<TemplateNode> attachedTemplateNodeListNew = new ArrayList<>();
            for (TemplateNode templateNodeListNewTemplateNodeToAttach : templateNodeListNew) {
                templateNodeListNewTemplateNodeToAttach = em.getReference(templateNodeListNewTemplateNodeToAttach.getClass(), templateNodeListNewTemplateNodeToAttach.getTemplateNodePK());
                attachedTemplateNodeListNew.add(templateNodeListNewTemplateNodeToAttach);
            }
            templateNodeListNew = attachedTemplateNodeListNew;
            templateNode.setTemplateNodeList(templateNodeListNew);
            templateNode = em.merge(templateNode);
            if (templateOld != null && !templateOld.equals(templateNew)) {
                templateOld.getTemplateNodeList().remove(templateNode);
                templateOld = em.merge(templateOld);
            }
            if (templateNew != null && !templateNew.equals(templateOld)) {
                templateNew.getTemplateNodeList().add(templateNode);
                templateNew = em.merge(templateNew);
            }
            if (templateNodeRelOld != null && !templateNodeRelOld.equals(templateNodeRelNew)) {
                templateNodeRelOld.getTemplateNodeList().remove(templateNode);
                templateNodeRelOld = em.merge(templateNodeRelOld);
            }
            if (templateNodeRelNew != null && !templateNodeRelNew.equals(templateNodeRelOld)) {
                templateNodeRelNew.getTemplateNodeList().add(templateNode);
                templateNodeRelNew = em.merge(templateNodeRelNew);
            }
            if (templateNodeTypeOld != null && !templateNodeTypeOld.equals(templateNodeTypeNew)) {
                templateNodeTypeOld.getTemplateNodeList().remove(templateNode);
                templateNodeTypeOld = em.merge(templateNodeTypeOld);
            }
            if (templateNodeTypeNew != null && !templateNodeTypeNew.equals(templateNodeTypeOld)) {
                templateNodeTypeNew.getTemplateNodeList().add(templateNode);
                templateNodeTypeNew = em.merge(templateNodeTypeNew);
            }
            for (TemplateNode templateNodeListOldTemplateNode : templateNodeListOld) {
                if (!templateNodeListNew.contains(templateNodeListOldTemplateNode)) {
                    templateNodeListOldTemplateNode.setTemplateNode(null);
                    templateNodeListOldTemplateNode = em.merge(templateNodeListOldTemplateNode);
                }
            }
            for (TemplateNode templateNodeListNewTemplateNode : templateNodeListNew) {
                if (!templateNodeListOld.contains(templateNodeListNewTemplateNode)) {
                    TemplateNode oldTemplateNodeOfTemplateNodeListNewTemplateNode = templateNodeListNewTemplateNode.getTemplateNode();
                    templateNodeListNewTemplateNode.setTemplateNode(templateNode);
                    templateNodeListNewTemplateNode = em.merge(templateNodeListNewTemplateNode);
                    if (oldTemplateNodeOfTemplateNodeListNewTemplateNode != null && !oldTemplateNodeOfTemplateNodeListNewTemplateNode.equals(templateNode)) {
                        oldTemplateNodeOfTemplateNodeListNewTemplateNode.getTemplateNodeList().remove(templateNodeListNewTemplateNode);
                        oldTemplateNodeOfTemplateNodeListNewTemplateNode = em.merge(oldTemplateNodeOfTemplateNodeListNewTemplateNode);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                TemplateNodePK id = templateNode.getTemplateNodePK();
                if (findTemplateNode(id) == null) {
                    throw new NonexistentEntityException("The templateNode with id " + id + " no longer exists.");
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

    public void destroy(TemplateNodePK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TemplateNode templateNode;
            try {
                templateNode = em.getReference(TemplateNode.class, id);
                templateNode.getTemplateNodePK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The templateNode with id " + id + " no longer exists.", enfe);
            }
            Template template = templateNode.getTemplate();
            if (template != null) {
                template.getTemplateNodeList().remove(templateNode);
                template = em.merge(template);
            }
            TemplateNode templateNodeRel = templateNode.getTemplateNode();
            if (templateNodeRel != null) {
                templateNodeRel.getTemplateNodeList().remove(templateNode);
                templateNodeRel = em.merge(templateNodeRel);
            }
            TemplateNodeType templateNodeType = templateNode.getTemplateNodeType();
            if (templateNodeType != null) {
                templateNodeType.getTemplateNodeList().remove(templateNode);
                templateNodeType = em.merge(templateNodeType);
            }
            List<TemplateNode> templateNodeList = templateNode.getTemplateNodeList();
            for (TemplateNode templateNodeListTemplateNode : templateNodeList) {
                templateNodeListTemplateNode.setTemplateNode(null);
                templateNodeListTemplateNode = em.merge(templateNodeListTemplateNode);
            }
            em.remove(templateNode);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TemplateNode> findTemplateNodeEntities() {
        return findTemplateNodeEntities(true, -1, -1);
    }

    public List<TemplateNode> findTemplateNodeEntities(int maxResults, int firstResult) {
        return findTemplateNodeEntities(false, maxResults, firstResult);
    }

    private List<TemplateNode> findTemplateNodeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TemplateNode.class));
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

    public TemplateNode findTemplateNode(TemplateNodePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TemplateNode.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getTemplateNodeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TemplateNode> rt = cq.from(TemplateNode.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
