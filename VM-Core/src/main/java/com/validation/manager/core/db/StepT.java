/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "step_t")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "StepT.findAll", query = "SELECT s FROM StepT s"),
    @NamedQuery(name = "StepT.findByRecordId", query = "SELECT s FROM StepT s WHERE s.recordId = :recordId"),
    @NamedQuery(name = "StepT.findById", query = "SELECT s FROM StepT s WHERE s.id = :id"),
    @NamedQuery(name = "StepT.findByTestCaseId", query = "SELECT s FROM StepT s WHERE s.testCaseId = :testCaseId"),
    @NamedQuery(name = "StepT.findByTestCaseTestId", query = "SELECT s FROM StepT s WHERE s.testCaseTestId = :testCaseTestId"),
    @NamedQuery(name = "StepT.findByStepSequence", query = "SELECT s FROM StepT s WHERE s.stepSequence = :stepSequence"),
    @NamedQuery(name = "StepT.findByText", query = "SELECT s FROM StepT s WHERE s.text = :text")})
public class StepT implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "record_id")
    private Integer recordId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "test_case_id")
    private int testCaseId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "test_case_test_id")
    private int testCaseTestId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "step_sequence")
    private int stepSequence;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "text")
    private String text;
    @Lob
    @Size(max = 65535)
    @Column(name = "notes")
    private String notes;

    public StepT() {
    }

    public StepT(Integer recordId) {
        this.recordId = recordId;
    }

    public StepT(Integer recordId, int id, int testCaseId, int testCaseTestId, int stepSequence, String text) {
        this.recordId = recordId;
        this.id = id;
        this.testCaseId = testCaseId;
        this.testCaseTestId = testCaseTestId;
        this.stepSequence = stepSequence;
        this.text = text;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(int testCaseId) {
        this.testCaseId = testCaseId;
    }

    public int getTestCaseTestId() {
        return testCaseTestId;
    }

    public void setTestCaseTestId(int testCaseTestId) {
        this.testCaseTestId = testCaseTestId;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (recordId != null ? recordId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StepT)) {
            return false;
        }
        StepT other = (StepT) object;
        if ((this.recordId == null && other.recordId != null) || (this.recordId != null && !this.recordId.equals(other.recordId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.StepT[ recordId=" + recordId + " ]";
    }
    
}
