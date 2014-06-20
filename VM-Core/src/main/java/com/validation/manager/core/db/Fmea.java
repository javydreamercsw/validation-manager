package com.validation.manager.core.db;

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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@Table(name = "fmea")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Fmea.findAll", 
            query = "SELECT f FROM Fmea f"),
    @NamedQuery(name = "Fmea.findById", 
            query = "SELECT f FROM Fmea f WHERE f.id = :id"),
    @NamedQuery(name = "Fmea.findByDescription", 
            query = "SELECT f FROM Fmea f WHERE f.description = :description"),
    @NamedQuery(name = "Fmea.findByName", 
            query = "SELECT f FROM Fmea f WHERE f.name = :name")})
public class Fmea extends Versionable implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "FMEAGen")
    @TableGenerator(name = "FMEAGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "fmea",
            allocationSize = 1,
            initialValue = 1000)
    @Column(name = "id")
    private Integer id;
    @Size(max = 255)
    @Column(name = "description")
    private String description;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @JoinTable(name = "fmea_has_risk_category", joinColumns = {
        @JoinColumn(name = "FMEA_id", referencedColumnName = "id")},
            inverseJoinColumns = {
        @JoinColumn(name = "risk_category_id", referencedColumnName = "id")})
    @ManyToMany
    private List<RiskCategory> riskCategoryList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fmea")
    private List<RiskItem> riskItemList;
    @OneToMany(mappedBy = "parent")
    private List<Fmea> fmeaList;
    @JoinColumn(name = "parent", referencedColumnName = "id")
    @ManyToOne
    private Fmea parent;

    public Fmea() {
    }

    public Fmea(String name) {
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    @JsonIgnore
    public List<RiskCategory> getRiskCategoryList() {
        return riskCategoryList;
    }

    public void setRiskCategoryList(List<RiskCategory> riskCategoryList) {
        this.riskCategoryList = riskCategoryList;
    }

    @XmlTransient
    @JsonIgnore
    public List<RiskItem> getRiskItemList() {
        return riskItemList;
    }

    public void setRiskItemList(List<RiskItem> riskItemList) {
        this.riskItemList = riskItemList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Fmea> getFmeaList() {
        return fmeaList;
    }

    public void setFmeaList(List<Fmea> fmeaList) {
        this.fmeaList = fmeaList;
    }

    public Fmea getParent() {
        return parent;
    }

    public void setParent(Fmea parent) {
        this.parent = parent;
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
        if (!(object instanceof Fmea)) {
            return false;
        }
        Fmea other = (Fmea) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Fmea[ id=" + id + " ]";
    }

}
