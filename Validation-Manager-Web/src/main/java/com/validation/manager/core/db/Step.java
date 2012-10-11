/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "step")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Step.findAll", query = "SELECT s FROM Step s"),
    @NamedQuery(name = "Step.findById", query = "SELECT s FROM Step s WHERE s.stepPK.id = :id"),
    @NamedQuery(name = "Step.findByTestCaseId", query = "SELECT s FROM Step s WHERE s.stepPK.testCaseId = :testCaseId"),
    @NamedQuery(name = "Step.findByTestCaseTestId", query = "SELECT s FROM Step s WHERE s.stepPK.testCaseTestId = :testCaseTestId"),
    @NamedQuery(name = "Step.findByStepSequence", query = "SELECT s FROM Step s WHERE s.stepSequence = :stepSequence")})
public class Step implements Serializable {
//    @ManyToMany(mappedBy = "stepList")
//    private List<VmException> vmExceptionList;
//    @ManyToMany(mappedBy = "stepList")
//    private List<Requirement> requirementList;
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected StepPK stepPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "step_sequence", nullable = false)
    private int stepSequence;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "text", nullable = false, length = 65535)
    private String text;
    @Lob
    @Size(max = 65535)
    @Column(name = "notes", length = 65535)
    private String notes;
    @JoinTable(name = "step_has_exception", joinColumns = {
        @JoinColumn(name = "step_id", referencedColumnName = "id", nullable = false),
        @JoinColumn(name = "step_test_case_id", referencedColumnName = "test_case_id", nullable = false),
        @JoinColumn(name = "step_test_case_test_id", referencedColumnName = "test_case_test_id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "exception_id", referencedColumnName = "id", nullable = false),
        @JoinColumn(name = "exception_reporter_id", referencedColumnName = "reporter_id", nullable = false)})
    @ManyToMany
    private List<VmException> vmExceptionList;
    @JoinTable(name = "step_has_requirement", joinColumns = {
        @JoinColumn(name = "step_id", referencedColumnName = "id", nullable = false),
        @JoinColumn(name = "step_test_case_id", referencedColumnName = "test_case_id", nullable = false),
        @JoinColumn(name = "step_test_case_test_id", referencedColumnName = "test_case_test_id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "requirement_id", referencedColumnName = "id", nullable = false),
        @JoinColumn(name = "requirement_version", referencedColumnName = "version", nullable = false)})
    @ManyToMany
    private List<Requirement> requirementList;
    @JoinColumns({
        @JoinColumn(name = "test_case_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "test_case_test_id", referencedColumnName = "test_id", nullable = false, insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private TestCase testCase;

    public Step() {
    }

    public Step(StepPK stepPK) {
        this.stepPK = stepPK;
    }

    public Step(StepPK stepPK, int stepSequence, String text) {
        this.stepPK = stepPK;
        this.stepSequence = stepSequence;
        this.text = text;
    }

    public Step(int testCaseId, int testCaseTestId) {
        this.stepPK = new StepPK(testCaseId, testCaseTestId);
    }

    public StepPK getStepPK() {
        return stepPK;
    }

    public void setStepPK(StepPK stepPK) {
        this.stepPK = stepPK;
    }

    public int getStepSequence() {
        return stepSequence;
    }

    public void setStepSequence(int stepSequence) {
        this.stepSequence = stepSequence;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @XmlTransient
    public List<VmException> getVmExceptionList() {
        return vmExceptionList;
    }

    public void setVmExceptionList(List<VmException> vmExceptionList) {
        this.vmExceptionList = vmExceptionList;
    }

    @XmlTransient
    public List<Requirement> getRequirementList() {
        return requirementList;
    }

    public void setRequirementList(List<Requirement> requirementList) {
        this.requirementList = requirementList;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (stepPK != null ? stepPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Step)) {
            return false;
        }
        Step other = (Step) object;
        if ((this.stepPK == null && other.stepPK != null) || (this.stepPK != null && !this.stepPK.equals(other.stepPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Step[ stepPK=" + stepPK + " ]";
    }
}
