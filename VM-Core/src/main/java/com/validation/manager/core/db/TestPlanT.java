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
@Table(name = "test_plan_t")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TestPlanT.findAll", query = "SELECT t FROM TestPlanT t"),
    @NamedQuery(name = "TestPlanT.findByRecordId", query = "SELECT t FROM TestPlanT t WHERE t.recordId = :recordId"),
    @NamedQuery(name = "TestPlanT.findById", query = "SELECT t FROM TestPlanT t WHERE t.id = :id"),
    @NamedQuery(name = "TestPlanT.findByTestProjectId", query = "SELECT t FROM TestPlanT t WHERE t.testProjectId = :testProjectId"),
    @NamedQuery(name = "TestPlanT.findByActive", query = "SELECT t FROM TestPlanT t WHERE t.active = :active"),
    @NamedQuery(name = "TestPlanT.findByIsOpen", query = "SELECT t FROM TestPlanT t WHERE t.isOpen = :isOpen"),
    @NamedQuery(name = "TestPlanT.findByRegressionTestPlanId", query = "SELECT t FROM TestPlanT t WHERE t.regressionTestPlanId = :regressionTestPlanId"),
    @NamedQuery(name = "TestPlanT.findByRegressionTestPlanTestProjectId", query = "SELECT t FROM TestPlanT t WHERE t.regressionTestPlanTestProjectId = :regressionTestPlanTestProjectId")})
public class TestPlanT implements Serializable {
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
    @Column(name = "test_project_id")
    private int testProjectId;
    @Lob
    @Size(max = 65535)
    @Column(name = "notes")
    private String notes;
    @Basic(optional = false)
    @NotNull
    @Column(name = "active")
    private boolean active;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_open")
    private boolean isOpen;
    @Column(name = "regression_test_plan_id")
    private Integer regressionTestPlanId;
    @Column(name = "regression_test_plan_test_project_id")
    private Integer regressionTestPlanTestProjectId;

    public TestPlanT() {
    }

    public TestPlanT(Integer recordId) {
        this.recordId = recordId;
    }

    public TestPlanT(Integer recordId, int id, int testProjectId, boolean active, boolean isOpen) {
        this.recordId = recordId;
        this.id = id;
        this.testProjectId = testProjectId;
        this.active = active;
        this.isOpen = isOpen;
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

    public int getTestProjectId() {
        return testProjectId;
    }

    public void setTestProjectId(int testProjectId) {
        this.testProjectId = testProjectId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public Integer getRegressionTestPlanId() {
        return regressionTestPlanId;
    }

    public void setRegressionTestPlanId(Integer regressionTestPlanId) {
        this.regressionTestPlanId = regressionTestPlanId;
    }

    public Integer getRegressionTestPlanTestProjectId() {
        return regressionTestPlanTestProjectId;
    }

    public void setRegressionTestPlanTestProjectId(Integer regressionTestPlanTestProjectId) {
        this.regressionTestPlanTestProjectId = regressionTestPlanTestProjectId;
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
        if (!(object instanceof TestPlanT)) {
            return false;
        }
        TestPlanT other = (TestPlanT) object;
        if ((this.recordId == null && other.recordId != null) || (this.recordId != null && !this.recordId.equals(other.recordId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.TestPlanT[ recordId=" + recordId + " ]";
    }
    
}
