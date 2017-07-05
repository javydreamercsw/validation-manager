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
package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.DataEntryProperty;
import com.validation.manager.core.db.controller.DataEntryPropertyJpaController;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class DataEntryPropertyServer extends DataEntryProperty
        implements EntityServer<DataEntryProperty> {

    private static final Logger LOG
            = Logger.getLogger(DataEntryPropertyServer.class.getSimpleName());

    public static DataEntryProperty createProperty(String name, Serializable val) {
        DataEntryProperty dep = new DataEntryProperty();
        dep.setPropertyName(name);
        dep.setPropertyValue(convertToString(val).get());
        return dep;
    }

    public static Optional<String> convertToString(final Serializable object) {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            return Optional.of(Base64.getEncoder().encodeToString(baos.toByteArray()));
        }
        catch (final IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return Optional.empty();
        }
    }

    public static <T extends Serializable> Object
            convertFrom(final String objectAsString) {
        final byte[] data = Base64.getDecoder().decode(objectAsString);
        try (final ObjectInputStream ois
                = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return ois.readObject();
        }
        catch (final IOException | ClassNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return Optional.empty();
        }
    }

    DataEntryPropertyServer(DataEntryProperty dep) {
        update(DataEntryPropertyServer.this, dep);
    }

    @Override
    public int write2DB() throws Exception {
        DataEntryPropertyJpaController c
                = new DataEntryPropertyJpaController(DataBaseManager
                        .getEntityManagerFactory());
        if (getDataEntryPropertyPK() == null) {
            DataEntryProperty dep = new DataEntryProperty();
            update(dep, this);
            c.create(dep);
            setDataEntryPropertyPK(dep.getDataEntryPropertyPK());
        } else {
            DataEntryProperty dep = getEntity();
            update(dep, this);
            c.edit(dep);
        }
        update();
        return getDataEntryPropertyPK().getId();
    }

    @Override
    public DataEntryProperty getEntity() {
        return new DataEntryPropertyJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findDataEntryProperty(getDataEntryPropertyPK());
    }

    @Override
    public void update(DataEntryProperty target, DataEntryProperty source) {
        target.setDataEntry(source.getDataEntry());
        target.setDataEntryPropertyPK(source.getDataEntryPropertyPK());
        target.setPropertyName(source.getPropertyName());
        target.setPropertyValue(source.getPropertyValue());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
