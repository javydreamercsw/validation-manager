package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.namedQuery;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.VmId;
import com.validation.manager.core.db.controller.VmIdJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;

/**
 *
 * @author Javier A. Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public class VMIdServer extends VmId implements EntityServer<VmId> {

    private static final long serialVersionUID = 1L;

    public VMIdServer(Integer id) throws VMException {
        VmIdJpaController controller = new VmIdJpaController(
                getEntityManagerFactory());
        VmId vmId = controller.findVmId(id);
        if (vmId != null) {
            update(this, vmId);
        } else {
            throw new VMException("VMId with id: " + id + " not found!");
        }
    }

    public VMIdServer(String tablename, int lastId) {
        super(tablename, lastId);
        setId(0);
    }

    //write to db
    @Override
    public int write2DB() throws VMException {
        try {
            VmIdJpaController controller = new VmIdJpaController(
                    getEntityManagerFactory());
            VmId vmId;
            if (getId() > 0) {
                vmId = controller.findVmId(getId());
                vmId.setId(getId());
                vmId.setLastId(getLastId());
                vmId.setTableName(getTableName());
                controller.edit(vmId);
            } else {
                vmId = new VmId();
                vmId.setId(getId());
                vmId.setLastId(getLastId());
                vmId.setTableName(getTableName());
                controller.create(vmId);
            }
            return getId();
        } catch (Exception ex) {
            getLogger(VMIdServer.class.getSimpleName()).log(Level.SEVERE, null, ex);
            throw new VMException(ex);
        }
    }

    public static int deleteFromDB(VmId id) throws VMException {
        VmIdJpaController controller = new VmIdJpaController(
                getEntityManagerFactory());
        if (id != null) {
            try {
                controller.destroy(id.getId());
            } catch (NonexistentEntityException ex) {
                throw new VMException(ex);
            }
        }
        return 0;
    }

    public static VMIdServer getVMId(String table) throws VMException {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("tableName", table);
        List<Object> result = namedQuery("VmId.findByTableName", parameters);
        if (!result.isEmpty()) {
            return new VMIdServer(((VmId) result.get(0)).getId());
        } else {
            throw new VMException("Unable to find VM id for: " + table);
        }
    }

    public static int getNextId(String table) throws VMException {
        VMIdServer vmId = getVMId(table);
        vmId.setLastId(vmId.getLastId() + 1);
        vmId.write2DB();
        return vmId.getLastId();
    }

    public static List<VMIdServer> getIds() throws VMException {
        ArrayList<VMIdServer> ids = new ArrayList<VMIdServer>();
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

    public VmId getEntity() {
        return new VmIdJpaController(
                getEntityManagerFactory()).findVmId(getId());
    }

    public void update(VmId target, VmId source) {
        target.setId(source.getId());
        target.setLastId(source.getLastId());
        target.setTableName(source.getTableName());
    }
    
    public void update() {
        update(this, getEntity());
    }
}
