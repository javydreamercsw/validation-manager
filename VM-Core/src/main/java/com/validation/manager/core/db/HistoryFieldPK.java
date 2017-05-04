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
public class HistoryFieldPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "History_Field_IDGEN")
    @TableGenerator(name = "History_Field_IDGEN", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "history_field",
            initialValue = 1,
            allocationSize = 1)
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "field_type_id")
    private int fieldTypeId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "history_id")
    private int historyId;

    public HistoryFieldPK() {
    }

    public HistoryFieldPK(int fieldTypeId, int historyId) {
        this.fieldTypeId = fieldTypeId;
        this.historyId = historyId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFieldTypeId() {
        return fieldTypeId;
    }

    public void setFieldTypeId(int fieldTypeId) {
        this.fieldTypeId = fieldTypeId;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) fieldTypeId;
        hash += (int) historyId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof HistoryFieldPK)) {
            return false;
        }
        HistoryFieldPK other = (HistoryFieldPK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.fieldTypeId != other.fieldTypeId) {
            return false;
        }
        return this.historyId == other.historyId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.HistoryFieldPK[ id=" + id
                + ", fieldTypeId=" + fieldTypeId + ", historyId="
                + historyId + " ]";
    }
}
