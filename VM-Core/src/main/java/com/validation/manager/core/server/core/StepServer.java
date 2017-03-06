package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.StepPK;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.controller.StepJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class StepServer extends Step implements EntityServer<Step> {

    public StepServer(TestCase tc, int stepSequence, String text) {
        super(new StepPK(tc.getId()), stepSequence, text.getBytes());
        setTestCase(tc);
        if (getTestCase() == null) {
            throw new RuntimeException("Provided TestCase that doesn't exist in the database yet!");
        }
    }

    public StepServer(StepPK stepPK) {
        super(stepPK);
        update(StepServer.this,
                new StepJpaController(DataBaseManager
                        .getEntityManagerFactory())
                        .findStep(stepPK));
    }

    public StepServer(Step step) {
        super(step.getStepPK());
        update(StepServer.this, step);
    }

    @Override
    public int write2DB() throws NonexistentEntityException, Exception {
        StepJpaController controller = new StepJpaController(
                getEntityManagerFactory());
        if (getStepPK().getId() > 0) {
            Step temp = controller.findStep(getStepPK());
            update(temp, this);
            controller.edit(temp);
            update(this, temp);
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
        target.setExecutionStepList(source.getExecutionStepList());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public void addRequirement(Requirement req) throws Exception {
        if (!getRequirementList().contains(req)
                && !getEntity().getRequirementList().contains(req)) {
            RequirementServer rs = new RequirementServer(req);
            int initial = rs.getStepList().size();
            if (!rs.getStepList().contains(getEntity())) {
                rs.getStepList().add(getEntity());
                rs.write2DB();
                rs.update();
            }
            assert initial < rs.getStepList().size();
            getRequirementList().add(req);
        }
    }

    public void removeRequirement(Requirement req) throws Exception {
        if (getRequirementList().contains(req)
                && getEntity().getRequirementList().contains(req)) {
//            String query = "delete from step_has_requirement "
//                    + "where step_id=" + getStepPK().getId()
//                    + " and step_test_case_id=" + getStepPK().getTestCaseId()
//                    + " and requirement_id=" + req.getId()
//                    + " and major_version=" + req.getMajorVersion()
//                    + " and mid_version=" + req.getMidVersion()
//                    + " and minor_version=" + req.getMinorVersion();
//            DataBaseManager.nativeUpdateQuery(query);
            RequirementServer rs = new RequirementServer(req);
            rs.getStepList().remove(getEntity());
            rs.write2DB();
            getRequirementList().remove(req);
        }
    }
}
