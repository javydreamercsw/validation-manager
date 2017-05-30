package com.validation.manager.core.server.fmea;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Fmea;
import com.validation.manager.core.db.controller.FmeaJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.logging.Level;
import static java.util.logging.Logger.getLogger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class FMEAServer extends Fmea implements EntityServer<Fmea> {

    public FMEAServer(String name) {
        super(name);
        setId(0);
    }

    @Override
    public int write2DB() throws IllegalOrphanException,
            NonexistentEntityException, Exception {
        if (getId() > 0) {
            Fmea fmea = new FmeaJpaController(
                    getEntityManagerFactory()).findFmea(getId());
            update(fmea, this);
            new FmeaJpaController(
                    getEntityManagerFactory()).edit(fmea);
        } else {
            Fmea fmea = new Fmea(getName());
            update(fmea, this);
            new FmeaJpaController(
                    getEntityManagerFactory()).create(fmea);
            setId(fmea.getId());
        }
        return getId();
    }

    public static boolean deleteFMEA(int id) {
        boolean result = false;
        try {
            new FmeaJpaController(
                    getEntityManagerFactory()).destroy(id);
            result = true;
        } catch (NonexistentEntityException | IllegalOrphanException ex) {
            getLogger(FMEAServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public Fmea getEntity() {
        return new FmeaJpaController(
                getEntityManagerFactory()).findFmea(getId());
    }

    @Override
    public void update(Fmea target, Fmea source) {
        target.setFmeaList(source.getFmeaList());
        target.setParent(source.getParent());
        target.setRiskItemList(source.getRiskItemList());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
