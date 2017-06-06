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
@Table(name = "notification")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Notification.findAll",
            query = "SELECT n FROM Notification n")
    , @NamedQuery(name = "Notification.findById",
            query = "SELECT n FROM Notification n WHERE n.notificationPK.id = :id")
    , @NamedQuery(name = "Notification.findByNotificationTypeId",
            query = "SELECT n FROM Notification n WHERE n.notificationPK.notificationTypeId = :notificationTypeId")
    , @NamedQuery(name = "Notification.findByCreationDate",
            query = "SELECT n FROM Notification n WHERE n.creationDate = :creationDate")
    , @NamedQuery(name = "Notification.findByAcknowledgeDate",
            query = "SELECT n FROM Notification n WHERE n.acknowledgeDate = :acknowledgeDate")})
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected NotificationPK notificationPK;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2_147_483_647)
    @Column(name = "content")
    private String content;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name = "acknowledge_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date acknowledgeDate;
    @JoinColumn(name = "notification_type_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private NotificationType notificationType;
    @JoinColumn(name = "target_user", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private VmUser targetUser;
    @JoinColumn(name = "author", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private VmUser author;
    @Basic(optional = false)
    @NotNull
    @Column(name = "archieved")
    private boolean archieved;

    public Notification() {
    }

    public Notification(NotificationPK notificationPK) {
        this.notificationPK = notificationPK;
    }

    public Notification(NotificationPK notificationPK, String content, Date creationDate) {
        this.notificationPK = notificationPK;
        this.content = content;
        this.creationDate = creationDate;
    }

    public Notification(int id, int notificationTypeId) {
        this.notificationPK = new NotificationPK(id, notificationTypeId);
    }

    public NotificationPK getNotificationPK() {
        return notificationPK;
    }

    public void setNotificationPK(NotificationPK notificationPK) {
        this.notificationPK = notificationPK;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getAcknowledgeDate() {
        return acknowledgeDate;
    }

    public void setAcknowledgeDate(Date acknowledgeDate) {
        this.acknowledgeDate = acknowledgeDate;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public VmUser getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(VmUser targetUser) {
        this.targetUser = targetUser;
    }

    public VmUser getAuthor() {
        return author;
    }

    public void setAuthor(VmUser author) {
        this.author = author;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (notificationPK != null ? notificationPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Notification)) {
            return false;
        }
        Notification other = (Notification) object;
        if ((this.notificationPK == null && other.notificationPK != null) || (this.notificationPK != null && !this.notificationPK.equals(other.notificationPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Notification[ notificationPK=" + notificationPK + " ]";
    }

    public boolean getArchieved() {
        return archieved;
    }

    public void setArchieved(boolean archieved) {
        this.archieved = archieved;
    }

}
