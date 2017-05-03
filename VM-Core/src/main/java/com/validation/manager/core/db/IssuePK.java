package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class IssuePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "ISSUE_IDGEN")
    @TableGenerator(name = "ISSUE_IDGEN", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "issue",
            initialValue = 1,
            allocationSize = 1)
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "issue_type_id")
    private int issueTypeId;

    public IssuePK() {
    }

    public IssuePK(int issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeId(int issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) issueTypeId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof IssuePK)) {
            return false;
        }
        IssuePK other = (IssuePK) object;
        if (this.id != other.id) {
            return false;
        }
        return this.issueTypeId == other.issueTypeId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.IssuePK[ id=" + id
                + ", issueTypeId=" + issueTypeId + " ]";
    }

}
