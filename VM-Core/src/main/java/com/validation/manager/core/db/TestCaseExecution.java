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
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "test_case_execution")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TestCaseExecution.findAll",
            query = "SELECT t FROM TestCaseExecution t")
    , @NamedQuery(name = "TestCaseExecution.findById",
            query = "SELECT t FROM TestCaseExecution t WHERE t.id = :id")})
public class TestCaseExecution implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "testCaseExecution")
    private List<ExecutionStep> executionStepList;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "TestCaseExecGen")
    @TableGenerator(name = "TestCaseExecGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "test_case_execution",
            allocationSize = 1,
            initialValue = 1000)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @NotNull
    @Lob
    @Size(max = 2147483647)
    @Column(name = "scope")
    private String scope;
    @NotNull
    @Column(name = "name")
    private String name;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "conclusion")
    private String conclusion;

    public TestCaseExecution() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
        if (!(object instanceof TestCaseExecution)) {
            return false;
        }
        TestCaseExecution other = (TestCaseExecution) object;
        return !((this.id == null && other.id != null)
                || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.TestCaseExecution[ id=" + id + " ]";
    }

    @XmlTransient
    @JsonIgnore
    public List<ExecutionStep> getExecutionStepList() {
        return executionStepList;
    }

    public void setExecutionStepList(List<ExecutionStep> executionStepList) {
        this.executionStepList = executionStepList;
    }

    /**
     * @return the scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * @param scope the scope to set
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the conclusion
     */
    public String getConclusion() {
        return conclusion;
    }

    /**
     * @param conclusion the conclusion to set
     */
    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }
}
