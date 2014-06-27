package com.validation.manager.core.server.core;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.StepPK;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.controller.StepJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class StepServer extends Step implements EntityServer<Step> {

    public StepServer(TestCase tc, int stepSequence, String text) {
        super(new StepPK(tc.getTestCasePK().getId(),
                tc.getTestCasePK().getTestId()), stepSequence, text.getBytes());
        setTestCase(tc);
        if (getTestCase() == null) {
            throw new RuntimeException("Provided TestCase that doesn't exist in the database yet!");
        }
    }

    public StepServer(Step step) {
        super(step.getStepPK());
        update(this, step);
    }

    @Override
    public int write2DB() throws NonexistentEntityException, Exception {
        StepJpaController controller = new StepJpaController(
                getEntityManagerFactory());
        if (getStepPK().getId() > 0) {
            Step temp = controller.findStep(getStepPK());
            update(temp, this);
            controller.edit(temp);
        } else {
            Step temp = new Step(getStepPK(), getStepSequence(), getText());
            update(temp, this);
            controller.create(temp);
            update(this, temp);
            setStepPK(temp.getStepPK());
        }
        return getStepPK().getId();
    }

    @Override
    public Step getEntity() {
        return new StepJpaController(getEntityManagerFactory())
                .findStep(getStepPK());
    }

    @Override
    public void update(Step target, Step source) {
        target.setExpectedResult(source.getExpectedResult());
        target.setNotes(source.getNotes());
        target.setRequirementList(source.getRequirementList());
        target.setStepSequence(source.getStepSequence());
        target.setTestCase(source.getTestCase());
        target.setText(source.getText());
        target.setVmExceptionList(source.getVmExceptionList());
    }
    
    @Override
    public void update() {
        update(this, getEntity());
    }
}
