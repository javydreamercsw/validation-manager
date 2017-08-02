/*
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "activity")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Activity.findAll",
            query = "SELECT a FROM Activity a")
    , @NamedQuery(name = "Activity.findById",
            query = "SELECT a FROM Activity a WHERE a.activityPK.id = :id")
    , @NamedQuery(name = "Activity.findByActivityType",
            query = "SELECT a FROM Activity a WHERE a.activityPK.activityType = :activityType")
    , @NamedQuery(name = "Activity.findByActivityTime",
            query = "SELECT a FROM Activity a WHERE a.activityTime = :activityTime")})
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ActivityPK activityPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "activity_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date activityTime;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "description")
    private String description;
    @JoinColumn(name = "activity_type", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private ActivityType activityType;
    @JoinColumn(name = "source_user", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private VmUser sourceUser;

    public Activity() {
    }

    public Activity(ActivityPK activityPK) {
        this.activityPK = activityPK;
    }

    public Activity(ActivityPK activityPK, Date activityTime, String description) {
        this.activityPK = activityPK;
        this.activityTime = activityTime;
        this.description = description;
    }

    public Activity(int activityType) {
        this.activityPK = new ActivityPK(activityType);
    }

    public ActivityPK getActivityPK() {
        return activityPK;
    }

    public void setActivityPK(ActivityPK activityPK) {
        this.activityPK = activityPK;
    }

    public Date getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(Date activityTime) {
        this.activityTime = activityTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public VmUser getSourceUser() {
        return sourceUser;
    }

    public void setSourceUser(VmUser sourceUser) {
        this.sourceUser = sourceUser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (activityPK != null ? activityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Activity)) {
            return false;
        }
        Activity other = (Activity) object;
        return !((this.activityPK == null && other.activityPK != null)
                || (this.activityPK != null && !this.activityPK.equals(other.activityPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Activity[ activityPK=" + activityPK + " ]";
    }
}
