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
import javax.persistence.Lob;
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
@Table(name = "investigation")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Investigation.findAll",
            query = "SELECT i FROM Investigation i")
    , @NamedQuery(name = "Investigation.findById",
            query = "SELECT i FROM Investigation i WHERE i.id = :id")})
public class Investigation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "InvestigationGen")
    @TableGenerator(name = "InvestigationGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "investigation",
            allocationSize = 1,
            initialValue = 1000)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "investigation")
    private List<UserHasInvestigation> userHasInvestigationList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "investigation")
    private List<ExceptionHasInvestigation> exceptionHasInvestigationList;

    public Investigation() {
    }

    public Investigation(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    @JsonIgnore
    public List<UserHasInvestigation> getUserHasInvestigationList() {
        return userHasInvestigationList;
    }

    public void setUserHasInvestigationList(List<UserHasInvestigation> userHasInvestigationList) {
        this.userHasInvestigationList = userHasInvestigationList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof Investigation)) {
            return false;
        }
        Investigation other = (Investigation) object;
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Investigation[ id=" + id + " ]";
    }

    @XmlTransient
    @JsonIgnore
    public List<ExceptionHasInvestigation> getExceptionHasInvestigationList() {
        return exceptionHasInvestigationList;
    }

    public void setExceptionHasInvestigationList(List<ExceptionHasInvestigation> exceptionHasInvestigationList) {
        this.exceptionHasInvestigationList = exceptionHasInvestigationList;
    }
}
