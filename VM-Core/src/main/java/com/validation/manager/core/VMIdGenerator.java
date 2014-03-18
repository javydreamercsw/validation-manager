package com.validation.manager.core;

import static com.validation.manager.core.DataBaseManager.namedQuery;
import static com.validation.manager.core.DataBaseManager.namedQuery;
import com.validation.manager.core.db.VmId;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.persistence.PrePersist;

/**
 * Work around issue with sequencing on H2 databases on Linux
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class VMIdGenerator {

    private static final Logger LOG = getLogger(VMIdGenerator.class.getSimpleName());

    /**
     * Just before persisting an entity.
     *
     * @param o object to be persisted
     * @throws Exception if something goes wrong
     */
    @PrePersist
    public void persistEntity(Object o) throws Exception {
        LOG.log(Level.FINE, "Pre-persist: {0}", o);
        if (o instanceof VmId) {
            VmId vmId = (VmId) o;
            if (vmId.getId() == null || vmId.getId() <= 0) {
                LOG.log(Level.FINE, "Detected: {0}, fixing id...", vmId);
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                int id = namedQuery("VmId.findAll").size() + 1;
                while (vmId.getId() <= 0) {
                    parameters.put("id", id);
                    if (namedQuery("VmId.findById", parameters).isEmpty()) {
                        vmId.setId(id);
                        LOG.log(Level.FINE, "Assigned id: {0} to {1}", new Object[]{id, vmId.getTableName()});
                    } else {
                        id++;
                    }
                }
            }
        }
    }
}
