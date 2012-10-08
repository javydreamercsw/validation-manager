/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "test_case_execution")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TestCaseExecution.findAll", query = "SELECT t FROM TestCaseExecution t"),
    @NamedQuery(name = "TestCaseExecution.findById", query = "SELECT t FROM TestCaseExecution t WHERE t.id = :id")})
public class TestCaseExecution implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TestCaseExecGen")
    @TableGenerator(name = "TestCaseExecGen", table = "vm_id",
    pkColumnName = "table_name",
    valueColumnName = "last_id",
    pkColumnValue = "test_case_execution",
    allocationSize = 1,
    initialValue = 1000)
    @NotNull
    @Column(name = "id", nullable = false)
    private Integer id;

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

        if (!(object instanceof TestCaseExecution)) {
            return false;
        }
        TestCaseExecution other = (TestCaseExecution) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.TestCaseExecution[ id=" + id + " ]";
    }
}
