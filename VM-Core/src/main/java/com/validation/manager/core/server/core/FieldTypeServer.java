package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.FieldType;
import com.validation.manager.core.db.controller.FieldTypeJpaController;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class FieldTypeServer extends FieldType
        implements EntityServer<FieldType> {

    public static FieldType findType(String simpleName) {
        parameters.clear();
        parameters.put("typeName", simpleName);
        List r = DataBaseManager.namedQuery("FieldType.findByTypeName", parameters);
        return r.isEmpty() ? null : (FieldType) r.get(0);
    }

    @Override
    public int write2DB() throws Exception {
        FieldTypeJpaController c = new FieldTypeJpaController(DataBaseManager
                .getEntityManagerFactory());
        if (getId() == null) {
            FieldType ft = new FieldType();
            update(ft, this);
            c.create(ft);
            update(this, ft);
        } else {
            FieldType ft = getEntity();
            update(ft, this);
            c.edit(ft);
            update(this, ft);
        }
        return getId();
    }

    @Override
    public FieldType getEntity() {
        return new FieldTypeJpaController(DataBaseManager
                .getEntityManagerFactory()).findFieldType(getId());
    }

    @Override
    public void update(FieldType target, FieldType source) {
        target.setHistoryFieldList(source.getHistoryFieldList());
        target.setId(source.getId());
        target.setTypeName(source.getTypeName());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
