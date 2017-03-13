package com.validation.manager.core.db;

import com.validation.manager.core.server.core.RequirementStatusServer;
import com.validation.manager.core.server.core.Versionable;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "requirement_status")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RequirementStatus.findAll",
            query = "SELECT r FROM RequirementStatus r")
    , @NamedQuery(name = "RequirementStatus.findById",
            query = "SELECT r FROM RequirementStatus r WHERE r.id = :id")
    , @NamedQuery(name = "RequirementStatus.findByStatus",
            query = "SELECT r FROM RequirementStatus r WHERE r.status = :status")})
public class RequirementStatus extends Versionable
        implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "ReqStatusGen")
    @TableGenerator(name = "ReqStatusGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "requirement_status",
            allocationSize = 1,
            initialValue = 1000)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "status")
    private String status;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "requirementStatusId")
    private List<Requirement> requirementList;

    public RequirementStatus() {
    }

    public RequirementStatus(String status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @XmlTransient
    @JsonIgnore
    public List<Requirement> getRequirementList() {
        return requirementList;
    }

    public void setRequirementList(List<Requirement> requirementList) {
        this.requirementList = requirementList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RequirementStatus)) {
            return false;
        }
        RequirementStatus other = (RequirementStatus) object;
        return !((this.id == null && other.id != null)
                || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RequirementStatus[ id=" + id + " ]";
    }

    @Override
    public boolean isChangeVersionable() {
        RequirementStatusServer rs = new RequirementStatusServer(getId());
        return !getStatus().equals(rs.getEntity().getStatus());
    }
}
