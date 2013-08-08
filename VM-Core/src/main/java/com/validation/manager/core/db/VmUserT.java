/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "vm_user_t")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VmUserT.findAll", query = "SELECT v FROM VmUserT v"),
    @NamedQuery(name = "VmUserT.findByRecordId", query = "SELECT v FROM VmUserT v WHERE v.recordId = :recordId"),
    @NamedQuery(name = "VmUserT.findById", query = "SELECT v FROM VmUserT v WHERE v.id = :id"),
    @NamedQuery(name = "VmUserT.findByUsername", query = "SELECT v FROM VmUserT v WHERE v.username = :username"),
    @NamedQuery(name = "VmUserT.findByPassword", query = "SELECT v FROM VmUserT v WHERE v.password = :password"),
    @NamedQuery(name = "VmUserT.findByEmail", query = "SELECT v FROM VmUserT v WHERE v.email = :email"),
    @NamedQuery(name = "VmUserT.findByFirst", query = "SELECT v FROM VmUserT v WHERE v.first = :first"),
    @NamedQuery(name = "VmUserT.findByLast", query = "SELECT v FROM VmUserT v WHERE v.last = :last"),
    @NamedQuery(name = "VmUserT.findByLocale", query = "SELECT v FROM VmUserT v WHERE v.locale = :locale"),
    @NamedQuery(name = "VmUserT.findByLastModifed", query = "SELECT v FROM VmUserT v WHERE v.lastModifed = :lastModifed"),
    @NamedQuery(name = "VmUserT.findByAttempts", query = "SELECT v FROM VmUserT v WHERE v.attempts = :attempts"),
    @NamedQuery(name = "VmUserT.findByUserStatusId", query = "SELECT v FROM VmUserT v WHERE v.userStatusId = :userStatusId")})
public class VmUserT implements Serializable {
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
    @Column(name = "username")
    private String username;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "password")
    private String password;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 100)
    @Column(name = "email")
    private String email;
    @Size(max = 45)
    @Column(name = "first")
    private String first;
    @Size(max = 45)
    @Column(name = "last")
    private String last;
    @Size(max = 10)
    @Column(name = "locale")
    private String locale;
    @Column(name = "last_modifed")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifed;
    @Column(name = "attempts")
    private Integer attempts;
    @Column(name = "user_status_id")
    private Integer userStatusId;

    public VmUserT() {
    }

    public VmUserT(Integer recordId) {
        this.recordId = recordId;
    }

    public VmUserT(Integer recordId, int id, String username, String password) {
        this.recordId = recordId;
        this.id = id;
        this.username = username;
        this.password = password;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Date getLastModifed() {
        return lastModifed;
    }

    public void setLastModifed(Date lastModifed) {
        this.lastModifed = lastModifed;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public Integer getUserStatusId() {
        return userStatusId;
    }

    public void setUserStatusId(Integer userStatusId) {
        this.userStatusId = userStatusId;
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
        if (!(object instanceof VmUserT)) {
            return false;
        }
        VmUserT other = (VmUserT) object;
        if ((this.recordId == null && other.recordId != null) || (this.recordId != null && !this.recordId.equals(other.recordId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.VmUserT[ recordId=" + recordId + " ]";
    }
    
}
