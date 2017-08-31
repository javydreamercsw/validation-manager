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
package com.validation.manager.core.db.fmea;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Cause;
import com.validation.manager.core.db.FailureMode;
import com.validation.manager.core.db.Hazard;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.RiskCategory;
import com.validation.manager.core.db.RiskControl;
import com.validation.manager.core.db.RiskControlHasRequirement;
import com.validation.manager.core.db.RiskControlHasRequirementPK;
import com.validation.manager.core.db.RiskControlType;
import com.validation.manager.core.db.RiskItem;
import com.validation.manager.core.db.controller.CauseJpaController;
import com.validation.manager.core.db.controller.FailureModeJpaController;
import com.validation.manager.core.db.controller.FmeaJpaController;
import com.validation.manager.core.db.controller.HazardJpaController;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.db.controller.RiskCategoryJpaController;
import com.validation.manager.core.db.controller.RiskControlHasRequirementJpaController;
import com.validation.manager.core.db.controller.RiskControlJpaController;
import com.validation.manager.core.db.controller.RiskControlTypeJpaController;
import com.validation.manager.core.db.controller.RiskItemJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.RiskControlServer;
import com.validation.manager.core.server.fmea.CauseServer;
import com.validation.manager.core.server.fmea.FMEAServer;
import com.validation.manager.core.server.fmea.FailureModeServer;
import com.validation.manager.core.server.fmea.HazardServer;
import com.validation.manager.core.server.fmea.RiskCategoryServer;
import com.validation.manager.core.server.fmea.RiskControlTypeServer;
import com.validation.manager.core.server.fmea.RiskItemServer;
import com.validation.manager.test.AbstractVMTestCase;
import com.validation.manager.test.TestHelper;
import static com.validation.manager.test.TestHelper.createProject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class FMEATest extends AbstractVMTestCase {

    private static final Logger LOG
            = getLogger(FMEATest.class.getName());

    /**
     * Test of toString method, of class FMEA.
     */
    @Test
    public void testFMEA() {
        System.out.println("Test FMEA");
        System.out.println("Create FMEA");
        Project p = createProject("New Project", "Notes");
        FMEAServer fmea = new FMEAServer("Test FMEA", p);
        System.out.println("Create Project");
        try {
            fmea.write2DB();
        }
        catch (VMException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        catch (NonexistentEntityException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        try {
            assertTrue(new FmeaJpaController(DataBaseManager
                    .getEntityManagerFactory()).findFmea(fmea.getFmeaPK()) != null);
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        System.out.println("Done!");
        System.out.println("Create Risk Categories");
        List<RiskCategory> riskCategories = new ArrayList<>();
        RiskCategoryServer rcs;
        try {
            rcs = new RiskCategoryServer("Severity", 1, 5);
            rcs.write2DB();
            riskCategories.add(new RiskCategoryJpaController(DataBaseManager
                    .getEntityManagerFactory()).findRiskCategory(rcs.getId()));
            rcs = new RiskCategoryServer("Occurence", 1, 5);
            rcs.write2DB();
            riskCategories.add(new RiskCategoryJpaController(DataBaseManager
                    .getEntityManagerFactory()).findRiskCategory(rcs.getId()));
            rcs = new RiskCategoryServer("Detectability", 1, 5);
            rcs.write2DB();
            riskCategories.add(new RiskCategoryJpaController(DataBaseManager
                    .getEntityManagerFactory()).findRiskCategory(rcs.getId()));
        }
        catch (IllegalOrphanException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        catch (NonexistentEntityException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        System.out.println("Done!");
        System.out.println("Create Hazards");
        List<Hazard> HAZARDS = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            HazardServer hs = new HazardServer("Hazard " + i,
                    "Hazard Description " + i);
            try {
                hs.write2DB();
                HAZARDS.add(new HazardJpaController(DataBaseManager
                        .getEntityManagerFactory()).findHazard(hs.getId()));
            }
            catch (NonexistentEntityException ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
        }
        System.out.println("Done!");
        System.out.println("Create Causes");
        List<Cause> CAUSES = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            CauseServer cs = new CauseServer("Cause " + i,
                    "Cause Description " + i);
            try {
                cs.write2DB();
                CAUSES.add(new CauseJpaController(DataBaseManager
                        .getEntityManagerFactory()).findCause(cs.getId()));
            }
            catch (VMException ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
        }
        System.out.println("Done!");
        System.out.println("Create Risk Control");
        List<RiskControlType> controlTypes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            RiskControlTypeServer rcts = new RiskControlTypeServer("RC Type "
                    + i, "RC Type Description" + i);
            try {
                rcts.write2DB();
            }
            catch (IllegalOrphanException ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            catch (NonexistentEntityException ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            controlTypes.add(new RiskControlTypeJpaController(DataBaseManager
                    .getEntityManagerFactory()).findRiskControlType(rcts.getId()));
        }
        System.out.println("Done!");
        System.out.println("Create Requirement Spec");
        RequirementSpec rss = null;
        try {
            rss = TestHelper.createRequirementSpec("Test", "Test", p, 1);
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        System.out.println("Create Requirement Spec Node");
        RequirementSpecNode rsns = null;
        try {
            rsns = TestHelper.createRequirementSpecNode(
                    rss, "Test", "Test", "Test");
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        System.out.println("Create Requirements");
        List<Requirement> requirements = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            try {
                Requirement reqs
                        = TestHelper.createRequirement("SRS-SW-00" + i,
                                "Description " + i,
                                rsns.getRequirementSpecNodePK(),
                                "Notes " + i, 2, 1);
                requirements.add(new RequirementJpaController(
                        DataBaseManager.getEntityManagerFactory())
                        .findRequirement(reqs.getId()));
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
        }
        System.out.println("Done!");
        System.out.println("Create Risk Control");
        List<RiskControl> controls = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            RiskControlServer rc = new RiskControlServer(controlTypes.get(i).getId());
            try {
                rc.write2DB();
                System.out.println("Adding Requirements (" + (i + 1) + ")");
                rc.setRiskControlHasRequirementList(new ArrayList<>());
                RiskControlHasRequirementJpaController c
                        = new RiskControlHasRequirementJpaController(DataBaseManager
                                .getEntityManagerFactory());
                for (int j = 0; j < i; j++) {
                    RiskControlHasRequirement rchr = new RiskControlHasRequirement();
                    Requirement req = requirements.get(j);
                    rchr.setRequirement(req);
                    rchr.setRiskControl(rc.getEntity());
                    rchr.setRiskControlHasRequirementPK(new RiskControlHasRequirementPK(
                            rc.getRiskControlPK().getId(),
                            rc.getRiskControlPK().getRiskControlTypeId(),
                            req.getId(),
                            req.getMajorVersion(), req.getMidVersion(),
                            req.getMinorVersion()));
                    c.create(rchr);
                    rc.getRiskControlHasRequirementList().add(rchr);
                }
                controls.add(new RiskControlJpaController(DataBaseManager
                        .getEntityManagerFactory())
                        .findRiskControl(rc.getRiskControlPK()));
                System.out.println("Done!");
            }
            catch (NonexistentEntityException ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
        }
        System.out.println("Done!");
        System.out.println("Create Failure Modes");
        List<FailureMode> FAILURE_MODES = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            FailureModeServer fm = new FailureModeServer("Failure Mode "
                    + i, "Failure Mode Desc " + i);
            try {
                fm.write2DB();
            }
            catch (NonexistentEntityException ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            FAILURE_MODES.add(new FailureModeJpaController(DataBaseManager
                    .getEntityManagerFactory()).findFailureMode(fm.getId()));
        }
        System.out.println("Done!");
        System.out.println("Create RiskItems");
        List<RiskItem> riskItems = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            try {
                RiskItemServer ri
                        = new RiskItemServer(fmea.getFmeaPK(),
                                "Desc " + (i + 1));
                ri.write2DB();
                ri.setRiskItemHasHazardList(new ArrayList<>());
                for (int j = 0; j < (i + 1); j++) {
                    Hazard hazard = HAZARDS.get(j);
                    ri.addHazard(hazard, FAILURE_MODES.subList(0, new Random()
                            .nextInt(FAILURE_MODES.size())),
                            CAUSES.subList(0, new Random()
                                    .nextInt(CAUSES.size())));
                    riskItems.add(new RiskItemJpaController(DataBaseManager
                            .getEntityManagerFactory())
                            .findRiskItem(ri.getRiskItemPK()));
                }
            }
            catch (IllegalOrphanException ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            catch (NonexistentEntityException ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
        }
        System.out.println("Done!");
        System.out.println("Add RiskItems to FMEA");
        fmea.setRiskItemList(riskItems);
        try {
            fmea.write2DB();
        }
        catch (IllegalOrphanException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        catch (NonexistentEntityException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        System.out.println("Done!");
    }
}
