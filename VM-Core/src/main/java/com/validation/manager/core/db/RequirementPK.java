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
public class RequirementPK implements Serializable {

    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ReqGen")
    @TableGenerator(name = "ReqGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "requirement",
            allocationSize = 1,
            initialValue = 1000)
    @NotNull
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "versionable_version_id")
    private int versionableVersionId;

    public RequirementPK() {
    }

    public RequirementPK(int id, int versionableVersionId) {
        this.id = id;
        this.versionableVersionId = versionableVersionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersionableVersionId() {
        return versionableVersionId;
    }

    public void setVersionableVersionId(int versionableVersionId) {
        this.versionableVersionId = versionableVersionId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) versionableVersionId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RequirementPK)) {
            return false;
        }
        RequirementPK other = (RequirementPK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.versionableVersionId != other.versionableVersionId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RequirementPK[ id=" + id + ", versionableVersionId=" + versionableVersionId + " ]";
    }

}
