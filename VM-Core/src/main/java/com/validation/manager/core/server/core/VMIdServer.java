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

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.namedQuery;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.VmId;
import com.validation.manager.core.db.controller.VmIdJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultronjavier.ortiz.78@gmail.com
 */
public final class VMIdServer extends VmId implements EntityServer<VmId> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG
            = Logger.getLogger(VMIdServer.class.getSimpleName());

    public VMIdServer(Integer id) throws VMException {
        super.setId(id);
        update();
    }

    public VMIdServer(String tablename, int lastId) {
        super(tablename, lastId);
    }

    //write to db
    @Override
    public int write2DB() throws VMException {
        try {
            VmIdJpaController controller = new VmIdJpaController(
                    getEntityManagerFactory());
            VmId vmId;
            if (getId() == null) {
                vmId = new VmId();
                update(vmId, this);
                controller.create(vmId);
                setId(vmId.getId());
            } else {
                vmId = getEntity();
                update(vmId, this);
                controller.edit(vmId);
            }
            update();
            return getId();
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new VMException(ex);
        }
    }

    public static int deleteFromDB(VmId id) throws VMException {
        VmIdJpaController controller = new VmIdJpaController(
                getEntityManagerFactory());
        if (id != null) {
            try {
                controller.destroy(id.getId());
            }
            catch (NonexistentEntityException ex) {
                throw new VMException(ex);
            }
        }
        return 0;
    }

    public static VMIdServer getVMId(String table) throws VMException {
        PARAMETERS.clear();
        PARAMETERS.put("tableName", table);
        List<Object> result = namedQuery("VmId.findByTableName", PARAMETERS);
        if (!result.isEmpty()) {
            return new VMIdServer(((VmId) result.get(0)).getId());
        } else {
            LOG.log(Level.WARNING, "Unable to find VM id for: {0}", table);
            return null;
        }
    }

    public static int getNextId(String table) throws VMException {
        VMIdServer vmId = getVMId(table);
        vmId.setLastId(vmId.getLastId() + 1);
        vmId.write2DB();
        return vmId.getLastId();
    }

    public static List<VMIdServer> getIds() throws VMException {
        ArrayList<VMIdServer> ids = new ArrayList<>();
        List<Object> result = namedQuery("VMId.findAll");
        if (!result.isEmpty()) {
            for (Object o : result) {
                ids.add(new VMIdServer(((VmId) o).getId()));
            }
        } else {
            throw new VMException("No ids found!");
        }
        return ids;
    }

    @Override
    public VmId getEntity() {
        return new VmIdJpaController(
                getEntityManagerFactory()).findVmId(getId());
    }

    @Override
    public void update(VmId target, VmId source) {
        target.setId(source.getId());
        target.setLastId(source.getLastId());
        target.setTableName(source.getTableName());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
