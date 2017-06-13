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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "history_field")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "HistoryField.findAll",
            query = "SELECT h FROM HistoryField h")
    , @NamedQuery(name = "HistoryField.findById",
            query = "SELECT h FROM HistoryField h WHERE h.historyFieldPK.id = :id")
    , @NamedQuery(name = "HistoryField.findByFieldTypeId",
            query = "SELECT h FROM HistoryField h WHERE h.historyFieldPK.fieldTypeId = :fieldTypeId")
    , @NamedQuery(name = "HistoryField.findByHistoryId",
            query = "SELECT h FROM HistoryField h WHERE h.historyFieldPK.historyId = :historyId")
    , @NamedQuery(name = "HistoryField.findByFieldName",
            query = "SELECT h FROM HistoryField h WHERE h.fieldName = :fieldName")})
public class HistoryField implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected HistoryFieldPK historyFieldPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "field_name")
    private String fieldName;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2_147_483_647)
    @Column(name = "field_value")
    private String fieldValue;
    @JoinColumn(name = "field_type_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private FieldType fieldType;
    @JoinColumn(name = "history_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private History history;

    public HistoryField() {
    }

    public HistoryField(HistoryFieldPK historyFieldPK) {
        this.historyFieldPK = historyFieldPK;
    }

    public HistoryField(HistoryFieldPK historyFieldPK, String fieldName,
            String fieldValue) {
        this.historyFieldPK = historyFieldPK;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public HistoryField(int fieldTypeId, int historyId) {
        this.historyFieldPK = new HistoryFieldPK(fieldTypeId, historyId);
    }

    public HistoryFieldPK getHistoryFieldPK() {
        return historyFieldPK;
    }

    public void setHistoryFieldPK(HistoryFieldPK historyFieldPK) {
        this.historyFieldPK = historyFieldPK;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (historyFieldPK != null ? historyFieldPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof HistoryField)) {
            return false;
        }
        HistoryField other = (HistoryField) object;
        return !((this.historyFieldPK == null && other.historyFieldPK != null)
                || (this.historyFieldPK != null
                && !this.historyFieldPK.equals(other.historyFieldPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.HistoryField[ historyFieldPK="
                + historyFieldPK + " ]";
    }
}
