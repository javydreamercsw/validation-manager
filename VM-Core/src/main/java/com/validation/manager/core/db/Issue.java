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

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Table(name = "issue")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Issue.findAll", query = "SELECT i FROM Issue i")
    , @NamedQuery(name = "Issue.findById",
            query = "SELECT i FROM Issue i WHERE i.issuePK.id = :id")
    , @NamedQuery(name = "Issue.findByIssueTypeId",
            query = "SELECT i FROM Issue i WHERE i.issuePK.issueTypeId = :issueTypeId")
    , @NamedQuery(name = "Issue.findByTitle",
            query = "SELECT i FROM Issue i WHERE i.title = :title")
    , @NamedQuery(name = "Issue.findByCreationTime",
            query = "SELECT i FROM Issue i WHERE i.creationTime = :creationTime")})
public class Issue implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected IssuePK issuePK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "title")
    private String title;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2_147_483_647)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creationTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;
    @JoinColumn(name = "issue_resolution_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private IssueResolution issueResolutionId;
    @JoinColumn(name = "issue_type_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private IssueType issueType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "issue")
    private List<ExecutionStepHasIssue> executionStepHasIssueList;

    public Issue() {
    }

    public Issue(IssuePK issuePK) {
        this.issuePK = issuePK;
    }

    public Issue(IssuePK issuePK, String title, String description,
            Date creationTime) {
        this.issuePK = issuePK;
        this.title = title;
        this.description = description;
        this.creationTime = creationTime;
    }

    public Issue(int issueTypeId) {
        this.issuePK = new IssuePK(issueTypeId);
    }

    public IssuePK getIssuePK() {
        return issuePK;
    }

    public void setIssuePK(IssuePK issuePK) {
        this.issuePK = issuePK;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public IssueResolution getIssueResolutionId() {
        return issueResolutionId;
    }

    public void setIssueResolutionId(IssueResolution issueResolutionId) {
        this.issueResolutionId = issueResolutionId;
    }

    public IssueType getIssueType() {
        return issueType;
    }

    public void setIssueType(IssueType issueType) {
        this.issueType = issueType;
    }

    @XmlTransient
    @JsonIgnore
    public List<ExecutionStepHasIssue> getExecutionStepHasIssueList() {
        return executionStepHasIssueList;
    }

    public void setExecutionStepHasIssueList(List<ExecutionStepHasIssue> executionStepHasIssueList) {
        this.executionStepHasIssueList = executionStepHasIssueList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (issuePK != null ? issuePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof Issue)) {
            return false;
        }
        Issue other = (Issue) object;
        return !((this.issuePK == null && other.issuePK != null)
                || (this.issuePK != null && !this.issuePK.equals(other.issuePK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Issue[ issuePK=" + issuePK + " ]";
    }
}
