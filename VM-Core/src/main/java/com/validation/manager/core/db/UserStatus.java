package com.validation.manager.core.db;

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
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "user_status")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserStatus.findAll",
            query = "SELECT u FROM UserStatus u")
    , @NamedQuery(name = "UserStatus.findById",
            query = "SELECT u FROM UserStatus u WHERE u.id = :id")
    , @NamedQuery(name = "UserStatus.findByStatus",
            query = "SELECT u FROM UserStatus u WHERE u.status = :status")
    , @NamedQuery(name = "UserStatus.findByDescription",
            query = "SELECT u FROM UserStatus u WHERE u.description = :description")})
public class UserStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "UserStatusGEN")
    @TableGenerator(name = "UserStatusGEN",
            table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "user_status",
            initialValue = 1000,
            allocationSize = 1)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "status")
    private String status;
    @Size(max = 255)
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userStatusId")
    private List<VmUser> vmUserList;

    public UserStatus() {
    }

    public UserStatus(String status) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    @JsonIgnore
    public List<VmUser> getVmUserList() {
        return vmUserList;
    }

    public void setVmUserList(List<VmUser> vmUserList) {
        this.vmUserList = vmUserList;
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
        if (!(object instanceof UserStatus)) {
            return false;
        }
        UserStatus other = (UserStatus) object;
        return !((this.id == null && other.id != null)
                || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.UserStatus[ id=" + id + " ]";
    }
}
