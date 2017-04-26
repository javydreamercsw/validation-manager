package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Baseline;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.controller.BaselineJpaController;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class BaselineServer extends Baseline
        implements EntityServer<Baseline> {

    public BaselineServer() {
    }

    public BaselineServer(Date creationDate, String baselineName) {
        super(creationDate, baselineName);
    }

    @Override
    public int write2DB() throws Exception {
        BaselineJpaController c = new BaselineJpaController(DataBaseManager
                .getEntityManagerFactory());
        if (getId() == null) {
            Baseline b = new Baseline();
            update(b, this);
            c.create(b);
            setId(b.getId());
        } else {
            Baseline b = getEntity();
            update(b, this);
            c.edit(b);
            setId(b.getId());
        }
        update();
        return getId();
    }

    @Override
    public Baseline getEntity() {
        return new BaselineJpaController(DataBaseManager
                .getEntityManagerFactory()).findBaseline(getId());
    }

    @Override
    public void update(Baseline target, Baseline source) {
        target.setBaselineName(source.getBaselineName());
        target.setCreationDate(source.getCreationDate());
        target.setId(source.getId());
        target.setRequirementList(source.getRequirementList());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public static BaselineServer createBaseline(String name,
            List<Requirement> requirements) {
        BaselineServer b = new BaselineServer(new Date(), name);
        try {
            b.write2DB();
            //Add requirements
            if (b.getRequirementList() == null) {
                b.setRequirementList(new ArrayList<>());
            }
            b.getRequirementList().addAll(requirements);
            b.write2DB();
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return b;
    }
}
