package com.validation.manager.core.db;

import com.validation.manager.core.server.core.Versionable;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "requirement_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RequirementType.findAll", 
            query = "SELECT r FROM RequirementType r"),
    @NamedQuery(name = "RequirementType.findById", 
            query = "SELECT r FROM RequirementType r WHERE r.id = :id"),
    @NamedQuery(name = "RequirementType.findByDescription",
            query = "SELECT r FROM RequirementType r WHERE r.description = :description"),
    @NamedQuery(name = "RequirementType.findByName", 
            query = "SELECT r FROM RequirementType r WHERE r.name = :name")})
public class RequirementType extends Versionable implements Serializable {

    @Size(max = 255)
    @Column(name = "description")
    private String description;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "requirementTypeId")
    private List<Requirement> requirementList;

    public RequirementType() {
    }

    public RequirementType(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RequirementType)) {
            return false;
        }
        RequirementType other = (RequirementType) object;
        return (this.getId() != null || other.getId() == null) 
                && (this.getId() == null || this.getId().equals(other.getId()));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RequirementType[ id=" + getId() + " ]";
    }

}
