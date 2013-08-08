/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "step_has_requirement")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "StepHasRequirement.findAll", query = "SELECT s FROM StepHasRequirement s"),
    @NamedQuery(name = "StepHasRequirement.findByStepId", query = "SELECT s FROM StepHasRequirement s WHERE s.stepHasRequirementPK.stepId = :stepId"),
    @NamedQuery(name = "StepHasRequirement.findByStepTestCaseId", query = "SELECT s FROM StepHasRequirement s WHERE s.stepHasRequirementPK.stepTestCaseId = :stepTestCaseId"),
    @NamedQuery(name = "StepHasRequirement.findByStepTestCaseTestId", query = "SELECT s FROM StepHasRequirement s WHERE s.stepHasRequirementPK.stepTestCaseTestId = :stepTestCaseTestId"),
    @NamedQuery(name = "StepHasRequirement.findByRequirementId", query = "SELECT s FROM StepHasRequirement s WHERE s.stepHasRequirementPK.requirementId = :requirementId"),
    @NamedQuery(name = "StepHasRequirement.findByRequirementVersion", query = "SELECT s FROM StepHasRequirement s WHERE s.stepHasRequirementPK.requirementVersion = :requirementVersion")})
public class StepHasRequirement implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected StepHasRequirementPK stepHasRequirementPK;
    @JoinColumns({
        @JoinColumn(name = "step_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "step_test_case_id", referencedColumnName = "test_case_id", insertable = false, updatable = false),
        @JoinColumn(name = "step_test_case_test_id", referencedColumnName = "test_case_test_id", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private Step step;
    @JoinColumns({
        @JoinColumn(name = "requirement_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "requirement_version", referencedColumnName = "version", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private Requirement requirement;

    public StepHasRequirement() {
    }

    public StepHasRequirement(StepHasRequirementPK stepHasRequirementPK) {
        this.stepHasRequirementPK = stepHasRequirementPK;
    }

    public StepHasRequirement(int stepId, int stepTestCaseId, int stepTestCaseTestId, int requirementId, int requirementVersion) {
        this.stepHasRequirementPK = new StepHasRequirementPK(stepId, stepTestCaseId, stepTestCaseTestId, requirementId, requirementVersion);
    }

    public StepHasRequirementPK getStepHasRequirementPK() {
        return stepHasRequirementPK;
    }

    public void setStepHasRequirementPK(StepHasRequirementPK stepHasRequirementPK) {
        this.stepHasRequirementPK = stepHasRequirementPK;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (stepHasRequirementPK != null ? stepHasRequirementPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StepHasRequirement)) {
            return false;
        }
        StepHasRequirement other = (StepHasRequirement) object;
        if ((this.stepHasRequirementPK == null && other.stepHasRequirementPK != null) || (this.stepHasRequirementPK != null && !this.stepHasRequirementPK.equals(other.stepHasRequirementPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.StepHasRequirement[ stepHasRequirementPK=" + stepHasRequirementPK + " ]";
    }
    
}
