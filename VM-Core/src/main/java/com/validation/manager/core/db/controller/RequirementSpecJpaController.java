/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.SpecLevel;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.RequirementSpecPK;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementSpecJpaController implements Serializable {

    public RequirementSpecJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RequirementSpec requirementSpec) throws PreexistingEntityException, Exception {
        if (requirementSpec.getRequirementSpecPK() == null) {
            requirementSpec.setRequirementSpecPK(new RequirementSpecPK());
        }
        if (requirementSpec.getRequirementSpecNodeList() == null) {
            requirementSpec.setRequirementSpecNodeList(new ArrayList<RequirementSpecNode>());
        }
        requirementSpec.getRequirementSpecPK().setProjectId(requirementSpec.getProject().getId());
        requirementSpec.getRequirementSpecPK().setSpecLevelId(requirementSpec.getSpecLevel().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SpecLevel specLevel = requirementSpec.getSpecLevel();
            if (specLevel != null) {
                specLevel = em.getReference(specLevel.getClass(), specLevel.getId());
                requirementSpec.setSpecLevel(specLevel);
            }
            Project project = requirementSpec.getProject();
            if (project != null) {
                project = em.getReference(project.getClass(), project.getId());
                requirementSpec.setProject(project);
            }
            List<RequirementSpecNode> attachedRequirementSpecNodeList = new ArrayList<RequirementSpecNode>();
            for (RequirementSpecNode requirementSpecNodeListRequirementSpecNodeToAttach : requirementSpec.getRequirementSpecNodeList()) {
                requirementSpecNodeListRequirementSpecNodeToAttach = em.getReference(requirementSpecNodeListRequirementSpecNodeToAttach.getClass(), requirementSpecNodeListRequirementSpecNodeToAttach.getRequirementSpecNodePK());
                attachedRequirementSpecNodeList.add(requirementSpecNodeListRequirementSpecNodeToAttach);
            }
            requirementSpec.setRequirementSpecNodeList(attachedRequirementSpecNodeList);
            em.persist(requirementSpec);
            if (specLevel != null) {
                specLevel.getRequirementSpecList().add(requirementSpec);
                specLevel = em.merge(specLevel);
            }
            if (project != null) {
                project.getRequirementSpecList().add(requirementSpec);
                project = em.merge(project);
            }
            for (RequirementSpecNode requirementSpecNodeListRequirementSpecNode : requirementSpec.getRequirementSpecNodeList()) {
                RequirementSpec oldRequirementSpecOfRequirementSpecNodeListRequirementSpecNode = requirementSpecNodeListRequirementSpecNode.getRequirementSpec();
                requirementSpecNodeListRequirementSpecNode.setRequirementSpec(requirementSpec);
                requirementSpecNodeListRequirementSpecNode = em.merge(requirementSpecNodeListRequirementSpecNode);
                if (oldRequirementSpecOfRequirementSpecNodeListRequirementSpecNode != null) {
                    oldRequirementSpecOfRequirementSpecNodeListRequirementSpecNode.getRequirementSpecNodeList().remove(requirementSpecNodeListRequirementSpecNode);
                    oldRequirementSpecOfRequirementSpecNodeListRequirementSpecNode = em.merge(oldRequirementSpecOfRequirementSpecNodeListRequirementSpecNode);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRequirementSpec(requirementSpec.getRequirementSpecPK()) != null) {
                throw new PreexistingEntityException("RequirementSpec " + requirementSpec + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RequirementSpec requirementSpec) throws IllegalOrphanException, NonexistentEntityException, Exception {
        requirementSpec.getRequirementSpecPK().setProjectId(requirementSpec.getProject().getId());
        requirementSpec.getRequirementSpecPK().setSpecLevelId(requirementSpec.getSpecLevel().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RequirementSpec persistentRequirementSpec = em.find(RequirementSpec.class, requirementSpec.getRequirementSpecPK());
            SpecLevel specLevelOld = persistentRequirementSpec.getSpecLevel();
            SpecLevel specLevelNew = requirementSpec.getSpecLevel();
            Project projectOld = persistentRequirementSpec.getProject();
            Project projectNew = requirementSpec.getProject();
            List<RequirementSpecNode> requirementSpecNodeListOld = persistentRequirementSpec.getRequirementSpecNodeList();
            List<RequirementSpecNode> requirementSpecNodeListNew = requirementSpec.getRequirementSpecNodeList();
            List<String> illegalOrphanMessages = null;
            for (RequirementSpecNode requirementSpecNodeListOldRequirementSpecNode : requirementSpecNodeListOld) {
                if (!requirementSpecNodeListNew.contains(requirementSpecNodeListOldRequirementSpecNode)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RequirementSpecNode " + requirementSpecNodeListOldRequirementSpecNode + " since its requirementSpec field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (specLevelNew != null) {
                specLevelNew = em.getReference(specLevelNew.getClass(), specLevelNew.getId());
                requirementSpec.setSpecLevel(specLevelNew);
            }
            if (projectNew != null) {
                projectNew = em.getReference(projectNew.getClass(), projectNew.getId());
                requirementSpec.setProject(projectNew);
            }
            List<RequirementSpecNode> attachedRequirementSpecNodeListNew = new ArrayList<RequirementSpecNode>();
            for (RequirementSpecNode requirementSpecNodeListNewRequirementSpecNodeToAttach : requirementSpecNodeListNew) {
                requirementSpecNodeListNewRequirementSpecNodeToAttach = em.getReference(requirementSpecNodeListNewRequirementSpecNodeToAttach.getClass(), requirementSpecNodeListNewRequirementSpecNodeToAttach.getRequirementSpecNodePK());
                attachedRequirementSpecNodeListNew.add(requirementSpecNodeListNewRequirementSpecNodeToAttach);
            }
            requirementSpecNodeListNew = attachedRequirementSpecNodeListNew;
            requirementSpec.setRequirementSpecNodeList(requirementSpecNodeListNew);
            requirementSpec = em.merge(requirementSpec);
            if (specLevelOld != null && !specLevelOld.equals(specLevelNew)) {
                specLevelOld.getRequirementSpecList().remove(requirementSpec);
                specLevelOld = em.merge(specLevelOld);
            }
            if (specLevelNew != null && !specLevelNew.equals(specLevelOld)) {
                specLevelNew.getRequirementSpecList().add(requirementSpec);
                specLevelNew = em.merge(specLevelNew);
            }
            if (projectOld != null && !projectOld.equals(projectNew)) {
                projectOld.getRequirementSpecList().remove(requirementSpec);
                projectOld = em.merge(projectOld);
            }
            if (projectNew != null && !projectNew.equals(projectOld)) {
                projectNew.getRequirementSpecList().add(requirementSpec);
                projectNew = em.merge(projectNew);
            }
            for (RequirementSpecNode requirementSpecNodeListNewRequirementSpecNode : requirementSpecNodeListNew) {
                if (!requirementSpecNodeListOld.contains(requirementSpecNodeListNewRequirementSpecNode)) {
                    RequirementSpec oldRequirementSpecOfRequirementSpecNodeListNewRequirementSpecNode = requirementSpecNodeListNewRequirementSpecNode.getRequirementSpec();
                    requirementSpecNodeListNewRequirementSpecNode.setRequirementSpec(requirementSpec);
                    requirementSpecNodeListNewRequirementSpecNode = em.merge(requirementSpecNodeListNewRequirementSpecNode);
                    if (oldRequirementSpecOfRequirementSpecNodeListNewRequirementSpecNode != null && !oldRequirementSpecOfRequirementSpecNodeListNewRequirementSpecNode.equals(requirementSpec)) {
                        oldRequirementSpecOfRequirementSpecNodeListNewRequirementSpecNode.getRequirementSpecNodeList().remove(requirementSpecNodeListNewRequirementSpecNode);
                        oldRequirementSpecOfRequirementSpecNodeListNewRequirementSpecNode = em.merge(oldRequirementSpecOfRequirementSpecNodeListNewRequirementSpecNode);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RequirementSpecPK id = requirementSpec.getRequirementSpecPK();
                if (findRequirementSpec(id) == null) {
                    throw new NonexistentEntityException("The requirementSpec with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(RequirementSpecPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RequirementSpec requirementSpec;
            try {
                requirementSpec = em.getReference(RequirementSpec.class, id);
                requirementSpec.getRequirementSpecPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The requirementSpec with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RequirementSpecNode> requirementSpecNodeListOrphanCheck = requirementSpec.getRequirementSpecNodeList();
            for (RequirementSpecNode requirementSpecNodeListOrphanCheckRequirementSpecNode : requirementSpecNodeListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This RequirementSpec (" + requirementSpec + ") cannot be destroyed since the RequirementSpecNode " + requirementSpecNodeListOrphanCheckRequirementSpecNode + " in its requirementSpecNodeList field has a non-nullable requirementSpec field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            SpecLevel specLevel = requirementSpec.getSpecLevel();
            if (specLevel != null) {
                specLevel.getRequirementSpecList().remove(requirementSpec);
                specLevel = em.merge(specLevel);
            }
            Project project = requirementSpec.getProject();
            if (project != null) {
                project.getRequirementSpecList().remove(requirementSpec);
                project = em.merge(project);
            }
            em.remove(requirementSpec);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RequirementSpec> findRequirementSpecEntities() {
        return findRequirementSpecEntities(true, -1, -1);
    }

    public List<RequirementSpec> findRequirementSpecEntities(int maxResults, int firstResult) {
        return findRequirementSpecEntities(false, maxResults, firstResult);
    }

    private List<RequirementSpec> findRequirementSpecEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RequirementSpec.class));
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

    public RequirementSpec findRequirementSpec(RequirementSpecPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RequirementSpec.class, id);
        } finally {
            em.close();
        }
    }

    public int getRequirementSpecCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RequirementSpec> rt = cq.from(RequirementSpec.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}