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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
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
@Table(name = "corrective_action")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CorrectiveAction.findAll",
            query = "SELECT c FROM CorrectiveAction c")
    , @NamedQuery(name = "CorrectiveAction.findById",
            query = "SELECT c FROM CorrectiveAction c WHERE c.id = :id")})
public class CorrectiveAction implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "correctiveAction")
    private List<ExceptionHasCorrectiveAction> exceptionHasCorrectiveActionList;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CAGen")
    @TableGenerator(name = "CAGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "corrective_action",
            allocationSize = 1,
            initialValue = 1000)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "details")
    private String details;
    @JoinTable(name = "user_has_corrective_action", joinColumns = {
        @JoinColumn(name = "corrective_action_id",
                referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "user_id", referencedColumnName = "id")})
    @ManyToMany
    private List<VmUser> vmUserList;

    public CorrectiveAction() {
    }

    public CorrectiveAction(String details) {
        this.details = details;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
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
        
        if (!(object instanceof CorrectiveAction)) {
            return false;
        }
        CorrectiveAction other = (CorrectiveAction) object;
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.CorrectiveAction[ id=" + id + " ]";
    }

    @XmlTransient
    @JsonIgnore
    public List<ExceptionHasCorrectiveAction> getExceptionHasCorrectiveActionList() {
        return exceptionHasCorrectiveActionList;
    }

    public void setExceptionHasCorrectiveActionList(List<ExceptionHasCorrectiveAction> exceptionHasCorrectiveActionList) {
        this.exceptionHasCorrectiveActionList = exceptionHasCorrectiveActionList;
    }
}
