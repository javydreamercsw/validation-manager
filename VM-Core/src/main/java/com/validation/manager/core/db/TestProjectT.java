/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "test_project_t")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TestProjectT.findAll", query = "SELECT t FROM TestProjectT t"),
    @NamedQuery(name = "TestProjectT.findByRecordId", query = "SELECT t FROM TestProjectT t WHERE t.recordId = :recordId"),
    @NamedQuery(name = "TestProjectT.findById", query = "SELECT t FROM TestProjectT t WHERE t.id = :id"),
    @NamedQuery(name = "TestProjectT.findByName", query = "SELECT t FROM TestProjectT t WHERE t.name = :name"),
    @NamedQuery(name = "TestProjectT.findByActive", query = "SELECT t FROM TestProjectT t WHERE t.active = :active")})
public class TestProjectT implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "record_id")
    private Integer recordId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Column(name = "active")
    private boolean active;
    @Lob
    @Size(max = 65535)
    @Column(name = "notes")
    private String notes;

    public TestProjectT() {
    }

    public TestProjectT(Integer recordId) {
        this.recordId = recordId;
    }

    public TestProjectT(Integer recordId, int id, String name, boolean active) {
        this.recordId = recordId;
        this.id = id;
        this.name = name;
        this.active = active;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (recordId != null ? recordId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TestProjectT)) {
            return false;
        }
        TestProjectT other = (TestProjectT) object;
        if ((this.recordId == null && other.recordId != null) || (this.recordId != null && !this.recordId.equals(other.recordId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.TestProjectT[ recordId=" + recordId + " ]";
    }
    
}
