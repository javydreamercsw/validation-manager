/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "test_case_t")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TestCaseT.findAll", query = "SELECT t FROM TestCaseT t"),
    @NamedQuery(name = "TestCaseT.findByRecordId", query = "SELECT t FROM TestCaseT t WHERE t.recordId = :recordId"),
    @NamedQuery(name = "TestCaseT.findByActive", query = "SELECT t FROM TestCaseT t WHERE t.active = :active"),
    @NamedQuery(name = "TestCaseT.findByAuthorId", query = "SELECT t FROM TestCaseT t WHERE t.authorId = :authorId"),
    @NamedQuery(name = "TestCaseT.findByCreationDate", query = "SELECT t FROM TestCaseT t WHERE t.creationDate = :creationDate"),
    @NamedQuery(name = "TestCaseT.findById", query = "SELECT t FROM TestCaseT t WHERE t.id = :id"),
    @NamedQuery(name = "TestCaseT.findByIsOpen", query = "SELECT t FROM TestCaseT t WHERE t.isOpen = :isOpen"),
    @NamedQuery(name = "TestCaseT.findByModificationDate", query = "SELECT t FROM TestCaseT t WHERE t.modificationDate = :modificationDate"),
    @NamedQuery(name = "TestCaseT.findByTestId", query = "SELECT t FROM TestCaseT t WHERE t.testId = :testId"),
    @NamedQuery(name = "TestCaseT.findByUpdaterId", query = "SELECT t FROM TestCaseT t WHERE t.updaterId = :updaterId"),
    @NamedQuery(name = "TestCaseT.findByVersion", query = "SELECT t FROM TestCaseT t WHERE t.version = :version")})
public class TestCaseT implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "record_id")
    private Integer recordId;
    @Column(name = "active")
    private Boolean active;
    @Column(name = "author_id")
    private Integer authorId;
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "expected_results")
    private String expectedResults;
    @Column(name = "id")
    private Integer id;
    @Column(name = "is_open")
    private Boolean isOpen;
    @Column(name = "modification_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationDate;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "summary")
    private String summary;
    @Column(name = "test_id")
    private Integer testId;
    @Column(name = "updater_id")
    private Integer updaterId;
    @Column(name = "version")
    private Short version;

    public TestCaseT() {
    }

    public TestCaseT(Integer recordId) {
        this.recordId = recordId;
    }

    public TestCaseT(Integer recordId, int id, int testId, short version, Date creationDate, Date modificationDate, int authorId) {
        this.recordId = recordId;
        this.id = id;
        this.testId = testId;
        this.version = version;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.authorId = authorId;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getExpectedResults() {
        return expectedResults;
    }

    public void setExpectedResults(String expectedResults) {
        this.expectedResults = expectedResults;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public Integer getUpdaterId() {
        return updaterId;
    }

    public void setUpdaterId(Integer updaterId) {
        this.updaterId = updaterId;
    }

    public Short getVersion() {
        return version;
    }

    public void setVersion(Short version) {
        this.version = version;
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
        if (!(object instanceof TestCaseT)) {
            return false;
        }
        TestCaseT other = (TestCaseT) object;
        if ((this.recordId == null && other.recordId != null) || (this.recordId != null && !this.recordId.equals(other.recordId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.TestCaseT[ recordId=" + recordId + " ]";
    }

}
