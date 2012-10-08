package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
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

/**
 *
 * @author Javier A. Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public class VMIdServer extends VmId implements EntityServer{

    private static final long serialVersionUID = 1L;

    public VMIdServer(Integer id) throws VMException {
        VmIdJpaController controller = new VmIdJpaController(
                DataBaseManager.getEntityManagerFactory());
        VmId xincoId = controller.findVmId(id);
        if (xincoId != null) {
            fill(xincoId);
        } else {
            throw new VMException("VMId with id: " + id + " not found!");
        }
    }

    public VMIdServer(String tablename, int lastId) {
        super(tablename, lastId);
        setId(0);
    }

    private void fill(VmId vmId) {
        setId(vmId.getId());
        setLastId(vmId.getLastId());
        setTableName(vmId.getTableName());
    }

    //write to db
    @Override
    public int write2DB() throws VMException {
        try {
            VmIdJpaController controller = new VmIdJpaController(
                    DataBaseManager.getEntityManagerFactory());
            VmId xincoId;
            if (getId() > 0) {
                xincoId = controller.findVmId(getId());
                xincoId.setId(getId());
                xincoId.setLastId(getLastId());
                xincoId.setTableName(getTableName());
                controller.edit(xincoId);
            } else {
                xincoId = new VmId();
                xincoId.setId(getId());
                xincoId.setLastId(getLastId());
                xincoId.setTableName(getTableName());
                controller.create(xincoId);
            }
            return getId();
        } catch (Exception ex) {
            Logger.getLogger(VMIdServer.class.getSimpleName()).log(Level.SEVERE, null, ex);
            throw new VMException(ex);
        }
    }

    public static int deleteFromDB(VmId id) throws VMException {
        VmIdJpaController controller = new VmIdJpaController(
                DataBaseManager.getEntityManagerFactory());
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
        List<Object> result = DataBaseManager.namedQuery("VmId.findByTableName", parameters);
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
        List<Object> result = DataBaseManager.namedQuery("VMId.findAll");
        if (!result.isEmpty()) {
            for (Object o : result) {
                ids.add(new VMIdServer(((VmId) o).getId()));
            }
        } else {
            throw new VMException("No ids found!");
        }
        return ids;
    }
}
