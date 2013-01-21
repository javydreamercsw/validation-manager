/*
 * To change this template, choose Tools | Templates
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
    @NamedQuery(name = "TestCaseT.findById", query = "SELECT t FROM TestCaseT t WHERE t.id = :id"),
    @NamedQuery(name = "TestCaseT.findByTestId", query = "SELECT t FROM TestCaseT t WHERE t.testId = :testId"),
    @NamedQuery(name = "TestCaseT.findByVersion", query = "SELECT t FROM TestCaseT t WHERE t.version = :version"),
    @NamedQuery(name = "TestCaseT.findByCreationDate", query = "SELECT t FROM TestCaseT t WHERE t.creationDate = :creationDate"),
    @NamedQuery(name = "TestCaseT.findByModificationDate", query = "SELECT t FROM TestCaseT t WHERE t.modificationDate = :modificationDate"),
    @NamedQuery(name = "TestCaseT.findByActive", query = "SELECT t FROM TestCaseT t WHERE t.active = :active"),
    @NamedQuery(name = "TestCaseT.findByIsOpen", query = "SELECT t FROM TestCaseT t WHERE t.isOpen = :isOpen"),
    @NamedQuery(name = "TestCaseT.findByAuthorId", query = "SELECT t FROM TestCaseT t WHERE t.authorId = :authorId"),
    @NamedQuery(name = "TestCaseT.findByUpdaterId", query = "SELECT t FROM TestCaseT t WHERE t.updaterId = :updaterId")})
public class TestCaseT implements Serializable {
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
    @Column(name = "test_id")
    private int testId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    private short version;
    @Lob
    @Size(max = 65535)
    @Column(name = "summary")
    private String summary;
    @Lob
    @Size(max = 65535)
    @Column(name = "expected_results")
    private String expectedResults;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "modification_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationDate;
    @Column(name = "active")
    private Boolean active;
    @Column(name = "is_open")
    private Boolean isOpen;
    @Basic(optional = false)
    @NotNull
    @Column(name = "author_id")
    private int authorId;
    @Column(name = "updater_id")
    private Integer updaterId;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getExpectedResults() {
        return expectedResults;
    }

    public void setExpectedResults(String expectedResults) {
        this.expectedResults = expectedResults;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public Integer getUpdaterId() {
        return updaterId;
    }

    public void setUpdaterId(Integer updaterId) {
        this.updaterId = updaterId;
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
