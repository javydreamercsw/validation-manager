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
import com.validation.manager.core.db.ProjectType;
import com.validation.manager.core.db.Template;
import com.validation.manager.core.db.TemplateNode;
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
public class TemplateJpaController implements Serializable {

    public TemplateJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Template template) {
        if (template.getTemplateNodeList() == null) {
            template.setTemplateNodeList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProjectType projectTypeId = template.getProjectTypeId();
            if (projectTypeId != null) {
                projectTypeId = em.getReference(projectTypeId.getClass(), projectTypeId.getId());
                template.setProjectTypeId(projectTypeId);
            }
            List<TemplateNode> attachedTemplateNodeList = new ArrayList<>();
            for (TemplateNode templateNodeListTemplateNodeToAttach : template.getTemplateNodeList()) {
                templateNodeListTemplateNodeToAttach = em.getReference(templateNodeListTemplateNodeToAttach.getClass(), templateNodeListTemplateNodeToAttach.getTemplateNodePK());
                attachedTemplateNodeList.add(templateNodeListTemplateNodeToAttach);
            }
            template.setTemplateNodeList(attachedTemplateNodeList);
            em.persist(template);
            if (projectTypeId != null) {
                projectTypeId.getTemplateList().add(template);
                projectTypeId = em.merge(projectTypeId);
            }
            for (TemplateNode templateNodeListTemplateNode : template.getTemplateNodeList()) {
                Template oldTemplateOfTemplateNodeListTemplateNode = templateNodeListTemplateNode.getTemplate();
                templateNodeListTemplateNode.setTemplate(template);
                templateNodeListTemplateNode = em.merge(templateNodeListTemplateNode);
                if (oldTemplateOfTemplateNodeListTemplateNode != null) {
                    oldTemplateOfTemplateNodeListTemplateNode.getTemplateNodeList().remove(templateNodeListTemplateNode);
                    oldTemplateOfTemplateNodeListTemplateNode = em.merge(oldTemplateOfTemplateNodeListTemplateNode);
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

    public void edit(Template template) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Template persistentTemplate = em.find(Template.class, template.getId());
            ProjectType projectTypeIdOld = persistentTemplate.getProjectTypeId();
            ProjectType projectTypeIdNew = template.getProjectTypeId();
            List<TemplateNode> templateNodeListOld = persistentTemplate.getTemplateNodeList();
            List<TemplateNode> templateNodeListNew = template.getTemplateNodeList();
            List<String> illegalOrphanMessages = null;
            for (TemplateNode templateNodeListOldTemplateNode : templateNodeListOld) {
                if (!templateNodeListNew.contains(templateNodeListOldTemplateNode)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain TemplateNode " + templateNodeListOldTemplateNode + " since its template field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (projectTypeIdNew != null) {
                projectTypeIdNew = em.getReference(projectTypeIdNew.getClass(), projectTypeIdNew.getId());
                template.setProjectTypeId(projectTypeIdNew);
            }
            List<TemplateNode> attachedTemplateNodeListNew = new ArrayList<>();
            for (TemplateNode templateNodeListNewTemplateNodeToAttach : templateNodeListNew) {
                templateNodeListNewTemplateNodeToAttach = em.getReference(templateNodeListNewTemplateNodeToAttach.getClass(), templateNodeListNewTemplateNodeToAttach.getTemplateNodePK());
                attachedTemplateNodeListNew.add(templateNodeListNewTemplateNodeToAttach);
            }
            templateNodeListNew = attachedTemplateNodeListNew;
            template.setTemplateNodeList(templateNodeListNew);
            template = em.merge(template);
            if (projectTypeIdOld != null && !projectTypeIdOld.equals(projectTypeIdNew)) {
                projectTypeIdOld.getTemplateList().remove(template);
                projectTypeIdOld = em.merge(projectTypeIdOld);
            }
            if (projectTypeIdNew != null && !projectTypeIdNew.equals(projectTypeIdOld)) {
                projectTypeIdNew.getTemplateList().add(template);
                projectTypeIdNew = em.merge(projectTypeIdNew);
            }
            for (TemplateNode templateNodeListNewTemplateNode : templateNodeListNew) {
                if (!templateNodeListOld.contains(templateNodeListNewTemplateNode)) {
                    Template oldTemplateOfTemplateNodeListNewTemplateNode = templateNodeListNewTemplateNode.getTemplate();
                    templateNodeListNewTemplateNode.setTemplate(template);
                    templateNodeListNewTemplateNode = em.merge(templateNodeListNewTemplateNode);
                    if (oldTemplateOfTemplateNodeListNewTemplateNode != null && !oldTemplateOfTemplateNodeListNewTemplateNode.equals(template)) {
                        oldTemplateOfTemplateNodeListNewTemplateNode.getTemplateNodeList().remove(templateNodeListNewTemplateNode);
                        oldTemplateOfTemplateNodeListNewTemplateNode = em.merge(oldTemplateOfTemplateNodeListNewTemplateNode);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = template.getId();
                if (findTemplate(id) == null) {
                    throw new NonexistentEntityException("The template with id " + id + " no longer exists.");
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
            Template template;
            try {
                template = em.getReference(Template.class, id);
                template.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The template with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<TemplateNode> templateNodeListOrphanCheck = template.getTemplateNodeList();
            for (TemplateNode templateNodeListOrphanCheckTemplateNode : templateNodeListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Template (" + template + ") cannot be destroyed since the TemplateNode " + templateNodeListOrphanCheckTemplateNode + " in its templateNodeList field has a non-nullable template field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            ProjectType projectTypeId = template.getProjectTypeId();
            if (projectTypeId != null) {
                projectTypeId.getTemplateList().remove(template);
                projectTypeId = em.merge(projectTypeId);
            }
            em.remove(template);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Template> findTemplateEntities() {
        return findTemplateEntities(true, -1, -1);
    }

    public List<Template> findTemplateEntities(int maxResults, int firstResult) {
        return findTemplateEntities(false, maxResults, firstResult);
    }

    private List<Template> findTemplateEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Template.class));
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

    public Template findTemplate(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Template.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getTemplateCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Template> rt = cq.from(Template.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
