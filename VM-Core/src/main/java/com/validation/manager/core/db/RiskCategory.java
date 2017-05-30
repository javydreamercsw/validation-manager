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
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "risk_category")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RiskCategory.findAll",
            query = "SELECT r FROM RiskCategory r")
    , @NamedQuery(name = "RiskCategory.findById",
            query = "SELECT r FROM RiskCategory r WHERE r.id = :id")
    , @NamedQuery(name = "RiskCategory.findByName",
            query = "SELECT r FROM RiskCategory r WHERE r.name = :name")
    , @NamedQuery(name = "RiskCategory.findByMinimum",
            query = "SELECT r FROM RiskCategory r WHERE r.minimum = :minimum")
    , @NamedQuery(name = "RiskCategory.findByMaximum",
            query = "SELECT r FROM RiskCategory r WHERE r.maximum = :maximum")})
public class RiskCategory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "RCGen")
    @TableGenerator(name = "RCGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "risk_category",
            allocationSize = 1,
            initialValue = 1000)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Column(name = "minimum")
    private int minimum;
    @Basic(optional = false)
    @NotNull
    @Column(name = "maximum")
    private int maximum;
    @ManyToMany(mappedBy = "riskCategoryList")
    private List<Fmea> fmeaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "riskCategory")
    private List<RiskItemHasRiskCategory> riskItemHasRiskCategoryList;

    public RiskCategory() {
    }

    public RiskCategory(String name, int minimum, int maximum) {
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    @XmlTransient
    @JsonIgnore
    public List<Fmea> getFmeaList() {
        return fmeaList;
    }

    public void setFmeaList(List<Fmea> fmeaList) {
        this.fmeaList = fmeaList;
    }

    @XmlTransient
    @JsonIgnore
    public List<RiskItemHasRiskCategory> getRiskItemHasRiskCategoryList() {
        return riskItemHasRiskCategoryList;
    }

    public void setRiskItemHasRiskCategoryList(List<RiskItemHasRiskCategory> riskItemHasRiskCategoryList) {
        this.riskItemHasRiskCategoryList = riskItemHasRiskCategoryList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof RiskCategory)) {
            return false;
        }
        RiskCategory other = (RiskCategory) object;
        return !((this.id == null && other.id != null)
                || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RiskCategory[ id=" + id + " ]";
    }
}
