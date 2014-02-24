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
public class TestCasePK implements Serializable {

    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TestCaseGen")
    @TableGenerator(name = "TestCaseGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "test_case",
            allocationSize = 1,
            initialValue = 1000)
    @NotNull
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "test_id")
    private int testId;

    public TestCasePK() {
    }

    public TestCasePK(int testId) {
        this.testId = testId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) testId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TestCasePK)) {
            return false;
        }
        TestCasePK other = (TestCasePK) object;
        if (this.id != other.id) {
            return false;
        }
        return this.testId == other.testId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.TestCasePK[ id=" + id + ", testId=" + testId + " ]";
    }

}
