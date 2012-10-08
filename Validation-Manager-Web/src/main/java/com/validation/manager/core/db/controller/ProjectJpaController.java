/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.Project;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.TestProject;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ProjectJpaController implements Serializable {

    public ProjectJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Project product) {
        if (product.getTestProjectList() == null) {
            product.setTestProjectList(new ArrayList<TestProject>());
        }
        if (product.getRequirementList() == null) {
            product.setRequirementList(new ArrayList<Requirement>());
        }
        if (product.getRequirementSpecList() == null) {
            product.setRequirementSpecList(new ArrayList<RequirementSpec>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<TestProject> attachedTestProjectList = new ArrayList<TestProject>();
            for (TestProject testProjectListTestProjectToAttach : product.getTestProjectList()) {
                testProjectListTestProjectToAttach = em.getReference(testProjectListTestProjectToAttach.getClass(), testProjectListTestProjectToAttach.getId());
                attachedTestProjectList.add(testProjectListTestProjectToAttach);
            }
            product.setTestProjectList(attachedTestProjectList);
            List<Requirement> attachedRequirementList = new ArrayList<Requirement>();
            for (Requirement requirementListRequirementToAttach : product.getRequirementList()) {
                requirementListRequirementToAttach = em.getReference(requirementListRequirementToAttach.getClass(), requirementListRequirementToAttach.getRequirementPK());
                attachedRequirementList.add(requirementListRequirementToAttach);
            }
            product.setRequirementList(attachedRequirementList);
            List<RequirementSpec> attachedRequirementSpecList = new ArrayList<RequirementSpec>();
            for (RequirementSpec requirementSpecListRequirementSpecToAttach : product.getRequirementSpecList()) {
                requirementSpecListRequirementSpecToAttach = em.getReference(requirementSpecListRequirementSpecToAttach.getClass(), requirementSpecListRequirementSpecToAttach.getRequirementSpecPK());
                attachedRequirementSpecList.add(requirementSpecListRequirementSpecToAttach);
            }
            product.setRequirementSpecList(attachedRequirementSpecList);
            em.persist(product);
            for (TestProject testProjectListTestProject : product.getTestProjectList()) {
                testProjectListTestProject.getProjectList().add(product);
                testProjectListTestProject = em.merge(testProjectListTestProject);
            }
            for (Requirement requirementListRequirement : product.getRequirementList()) {
                Project oldProjectIdOfRequirementListRequirement = requirementListRequirement.getProject();
                requirementListRequirement.setProject(product);
                requirementListRequirement = em.merge(requirementListRequirement);
                if (oldProjectIdOfRequirementListRequirement != null) {
                    oldProjectIdOfRequirementListRequirement.getRequirementList().remove(requirementListRequirement);
                    oldProjectIdOfRequirementListRequirement = em.merge(oldProjectIdOfRequirementListRequirement);
                }
            }
            for (RequirementSpec requirementSpecListRequirementSpec : product.getRequirementSpecList()) {
                Project oldProjectOfRequirementSpecListRequirementSpec = requirementSpecListRequirementSpec.getProject();
                requirementSpecListRequirementSpec.setProject(product);
                requirementSpecListRequirementSpec = em.merge(requirementSpecListRequirementSpec);
                if (oldProjectOfRequirementSpecListRequirementSpec != null) {
                    oldProjectOfRequirementSpecListRequirementSpec.getRequirementSpecList().remove(requirementSpecListRequirementSpec);
                    oldProjectOfRequirementSpecListRequirementSpec = em.merge(oldProjectOfRequirementSpecListRequirementSpec);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Project product) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Project persistentProject = em.find(Project.class, product.getId());
            List<TestProject> testProjectListOld = persistentProject.getTestProjectList();
            List<TestProject> testProjectListNew = product.getTestProjectList();
            List<Requirement> requirementListOld = persistentProject.getRequirementList();
            List<Requirement> requirementListNew = product.getRequirementList();
            List<RequirementSpec> requirementSpecListOld = persistentProject.getRequirementSpecList();
            List<RequirementSpec> requirementSpecListNew = product.getRequirementSpecList();
            List<String> illegalOrphanMessages = null;
            for (Requirement requirementListOldRequirement : requirementListOld) {
                if (!requirementListNew.contains(requirementListOldRequirement)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Requirement " + requirementListOldRequirement + " since its productId field is not nullable.");
                }
            }
            for (RequirementSpec requirementSpecListOldRequirementSpec : requirementSpecListOld) {
                if (!requirementSpecListNew.contains(requirementSpecListOldRequirementSpec)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RequirementSpec " + requirementSpecListOldRequirementSpec + " since its product field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<TestProject> attachedTestProjectListNew = new ArrayList<TestProject>();
            for (TestProject testProjectListNewTestProjectToAttach : testProjectListNew) {
                testProjectListNewTestProjectToAttach = em.getReference(testProjectListNewTestProjectToAttach.getClass(), testProjectListNewTestProjectToAttach.getId());
                attachedTestProjectListNew.add(testProjectListNewTestProjectToAttach);
            }
            testProjectListNew = attachedTestProjectListNew;
            product.setTestProjectList(testProjectListNew);
            List<Requirement> attachedRequirementListNew = new ArrayList<Requirement>();
            for (Requirement requirementListNewRequirementToAttach : requirementListNew) {
                requirementListNewRequirementToAttach = em.getReference(requirementListNewRequirementToAttach.getClass(), requirementListNewRequirementToAttach.getRequirementPK());
                attachedRequirementListNew.add(requirementListNewRequirementToAttach);
            }
            requirementListNew = attachedRequirementListNew;
            product.setRequirementList(requirementListNew);
            List<RequirementSpec> attachedRequirementSpecListNew = new ArrayList<RequirementSpec>();
            for (RequirementSpec requirementSpecListNewRequirementSpecToAttach : requirementSpecListNew) {
                requirementSpecListNewRequirementSpecToAttach = em.getReference(requirementSpecListNewRequirementSpecToAttach.getClass(), requirementSpecListNewRequirementSpecToAttach.getRequirementSpecPK());
                attachedRequirementSpecListNew.add(requirementSpecListNewRequirementSpecToAttach);
            }
            requirementSpecListNew = attachedRequirementSpecListNew;
            product.setRequirementSpecList(requirementSpecListNew);
            product = em.merge(product);
            for (TestProject testProjectListOldTestProject : testProjectListOld) {
                if (!testProjectListNew.contains(testProjectListOldTestProject)) {
                    testProjectListOldTestProject.getProjectList().remove(product);
                    testProjectListOldTestProject = em.merge(testProjectListOldTestProject);
                }
            }
            for (TestProject testProjectListNewTestProject : testProjectListNew) {
                if (!testProjectListOld.contains(testProjectListNewTestProject)) {
                    testProjectListNewTestProject.getProjectList().add(product);
                    testProjectListNewTestProject = em.merge(testProjectListNewTestProject);
                }
            }
            for (Requirement requirementListNewRequirement : requirementListNew) {
                if (!requirementListOld.contains(requirementListNewRequirement)) {
                    Project oldProjectIdOfRequirementListNewRequirement = requirementListNewRequirement.getProject();
                    requirementListNewRequirement.setProject(product);
                    requirementListNewRequirement = em.merge(requirementListNewRequirement);
                    if (oldProjectIdOfRequirementListNewRequirement != null && !oldProjectIdOfRequirementListNewRequirement.equals(product)) {
                        oldProjectIdOfRequirementListNewRequirement.getRequirementList().remove(requirementListNewRequirement);
                        oldProjectIdOfRequirementListNewRequirement = em.merge(oldProjectIdOfRequirementListNewRequirement);
                    }
                }
            }
            for (RequirementSpec requirementSpecListNewRequirementSpec : requirementSpecListNew) {
                if (!requirementSpecListOld.contains(requirementSpecListNewRequirementSpec)) {
                    Project oldProjectOfRequirementSpecListNewRequirementSpec = requirementSpecListNewRequirementSpec.getProject();
                    requirementSpecListNewRequirementSpec.setProject(product);
                    requirementSpecListNewRequirementSpec = em.merge(requirementSpecListNewRequirementSpec);
                    if (oldProjectOfRequirementSpecListNewRequirementSpec != null && !oldProjectOfRequirementSpecListNewRequirementSpec.equals(product)) {
                        oldProjectOfRequirementSpecListNewRequirementSpec.getRequirementSpecList().remove(requirementSpecListNewRequirementSpec);
                        oldProjectOfRequirementSpecListNewRequirementSpec = em.merge(oldProjectOfRequirementSpecListNewRequirementSpec);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = product.getId();
                if (findProject(id) == null) {
                    throw new NonexistentEntityException("The product with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
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
            Project product;
            try {
                product = em.getReference(Project.class, id);
                product.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The product with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Requirement> requirementListOrphanCheck = product.getRequirementList();
            for (Requirement requirementListOrphanCheckRequirement : requirementListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Project (" + product + ") cannot be destroyed since the Requirement " + requirementListOrphanCheckRequirement + " in its requirementList field has a non-nullable productId field.");
            }
            List<RequirementSpec> requirementSpecListOrphanCheck = product.getRequirementSpecList();
            for (RequirementSpec requirementSpecListOrphanCheckRequirementSpec : requirementSpecListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Project (" + product + ") cannot be destroyed since the RequirementSpec " + requirementSpecListOrphanCheckRequirementSpec + " in its requirementSpecList field has a non-nullable product field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<TestProject> testProjectList = product.getTestProjectList();
            for (TestProject testProjectListTestProject : testProjectList) {
                testProjectListTestProject.getProjectList().remove(product);
                testProjectListTestProject = em.merge(testProjectListTestProject);
            }
            em.remove(product);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Project> findProjectEntities() {
        return findProjectEntities(true, -1, -1);
    }

    public List<Project> findProjectEntities(int maxResults, int firstResult) {
        return findProjectEntities(false, maxResults, firstResult);
    }

    private List<Project> findProjectEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Project.class));
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

    public Project findProject(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Project.class, id);
        } finally {
            em.close();
        }
    }

    public int getProjectCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Project> rt = cq.from(Project.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
