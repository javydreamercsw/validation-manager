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
package com.validation.manager.core.db;

import com.validation.manager.core.history.Auditable;
import com.validation.manager.core.history.Versionable;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "step")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Step.findAll",
            query = "SELECT s FROM Step s")
    , @NamedQuery(name = "Step.findById",
            query = "SELECT s FROM Step s WHERE s.stepPK.id = :id")
    , @NamedQuery(name = "Step.findByTestCaseId",
            query = "SELECT s FROM Step s WHERE s.stepPK.testCaseId = :testCaseId")
    , @NamedQuery(name = "Step.findByStepSequence",
            query = "SELECT s FROM Step s WHERE s.stepSequence = :stepSequence")})
public class Step extends Versionable implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected StepPK stepPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "step_sequence")
    private int stepSequence;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Column(name = "text")
    @Auditable
    private byte[] text;
    @Lob
    @Column(name = "expected_result")
    @Auditable
    private byte[] expectedResult;
    @Lob
    @Size(max = 2_147_483_647)
    @Column(name = "notes")
    @Auditable
    private String notes;
    @ManyToMany(mappedBy = "stepList")
    private List<Requirement> requirementList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "step")
    private List<ExecutionStep> executionStepList;
    @JoinColumns({
        @JoinColumn(name = "test_case_id", referencedColumnName = "id",
                insertable = false, updatable = false)
        ,@JoinColumn(name = "test_case_type_id", referencedColumnName = "test_case_type_id",
                insertable = false, updatable = false)
    })
    @ManyToOne(optional = false)
    private TestCase testCase;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "step")
    private List<History> historyList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "step")
    private List<DataEntry> dataEntryList;

    public Step() {
    }

    public Step(StepPK stepPK) {
        this.stepPK = stepPK;
    }

    public Step(StepPK stepPK, int stepSequence, byte[] text) {
        this.stepPK = stepPK;
        this.stepSequence = stepSequence;
        this.text = text;
    }

    public Step(int testCaseId) {
        this.stepPK = new StepPK(testCaseId);
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

    public byte[] getText() {
        return text;
    }

    public void setText(byte[] text) {
        this.text = text;
    }

    public byte[] getExpectedResult() {
        return expectedResult;
    }

    @XmlTransient
    @JsonIgnore
    public List<Requirement> getRequirementList() {
        return requirementList;
    }

    public void setRequirementList(List<Requirement> requirementList) {
        this.requirementList = requirementList;
    }

    @XmlTransient
    @JsonIgnore
    public List<ExecutionStep> getExecutionStepList() {
        return executionStepList;
    }

    public void setExecutionStepList(List<ExecutionStep> executionStepList) {
        this.executionStepList = executionStepList;
    }

    public void setExpectedResult(byte[] expectedResult) {
        this.expectedResult = expectedResult;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
        if (!(object instanceof Step)) {
            return false;
        }
        Step other = (Step) object;
        return !((this.stepPK == null && other.stepPK != null)
                || (this.stepPK != null && !this.stepPK.equals(other.stepPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Step[ stepPK=" + stepPK + " ]";
    }

    @XmlTransient
    @JsonIgnore
    @Override
    public List<History> getHistoryList() {
        return historyList;
    }

    @Override
    public void setHistoryList(List<History> historyList) {
        this.historyList = historyList;
    }

    @XmlTransient
    @JsonIgnore
    public List<DataEntry> getDataEntryList() {
        return dataEntryList;
    }

    public void setDataEntryList(List<DataEntry> dataEntryList) {
        this.dataEntryList = dataEntryList;
    }
}
