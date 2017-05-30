package com.validation.manager.core.db.fmea;

import com.validation.manager.test.AbstractVMTestCase;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class FMEATest extends AbstractVMTestCase {

    private static final Logger LOG =
 getLogger(FMEATest.class.getName());

    /**
     * Test of toString method, of class FMEA.
     */
    @Test
    public void testFMEA() {
        //TODO: Fix
//        System.out.println("Test FMEA");
//        System.out.println("Create FMEA");
//        FMEAServer fmea = new FMEAServer("Test FMEA");
//        System.out.println("Create Project");
//        ProjectServer product = new ProjectServer("Test product", "Project Notes");
//        try {
//            product.write2DB();
//        } catch (IllegalOrphanException ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        } catch (NonexistentEntityException ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        }
//        try {
//            assertTrue(new FMEAJpaController(DataBaseManager.getEntityManagerFactory()).findFMEA(fmea.write2DB()) != null);
//        } catch (IllegalOrphanException ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        } catch (NonexistentEntityException ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        }
//        System.out.println("Done!");
//        System.out.println("Create Risk Categories");
//        List<RiskCategory> riskCategories = new ArrayList<RiskCategory>();
//        RiskCategoryServer rcs;
//        try {
//            rcs = new RiskCategoryServer("Severity", 1, 5);
//            rcs.write2DB();
//            riskCategories.add(new RiskCategoryJpaController(DataBaseManager.getEntityManagerFactory()).findRiskCategory(rcs.getId()));
//            rcs = new RiskCategoryServer("Occurence", 1, 5);
//            rcs.write2DB();
//            riskCategories.add(new RiskCategoryJpaController(DataBaseManager.getEntityManagerFactory()).findRiskCategory(rcs.getId()));
//            rcs = new RiskCategoryServer("Detectability", 1, 5);
//            rcs.write2DB();
//            riskCategories.add(new RiskCategoryJpaController(DataBaseManager.getEntityManagerFactory()).findRiskCategory(rcs.getId()));
//        } catch (IllegalOrphanException ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        } catch (NonexistentEntityException ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        }
//        System.out.println("Done!");
//        System.out.println("Adding Risk Categories to FMEA...");
//        for (Iterator<RiskCategory> it = riskCategories.iterator(); it.hasNext();) {
//            RiskCategory rc = it.next();
//            if (fmea.getRiskCategoryList() == null) {
//                fmea.setRiskCategoryList(new ArrayList<RiskCategory>());
//            }
//            fmea.getRiskCategoryList().add(rc);
//        }
//        try {
//            fmea.write2DB();
//        } catch (IllegalOrphanException ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        } catch (NonexistentEntityException ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        }
//        System.out.println("Done!");
//        System.out.println("Create Hazards");
//        List<Hazard> hazards = new ArrayList<Hazard>();
//        for (int i = 0; i < 5; i++) {
//            HazardServer hs = new HazardServer("Hazard " + i, "Hazard Description " + i);
//            try {
//                hs.write2DB();
//                hazards.add(new HazardJpaController(DataBaseManager.getEntityManagerFactory()).findHazard(hs.getId()));
//            } catch (NonexistentEntityException ex) {
//                LOG.log(Level.SEVERE, null, ex);
//                fail();
//            } catch (Exception ex) {
//                LOG.log(Level.SEVERE, null, ex);
//                fail();
//            }
//        }
//        System.out.println("Done!");
//        System.out.println("Create Causes");
//        List<Cause> causes = new ArrayList<Cause>();
//        for (int i = 0; i < 5; i++) {
//            CauseServer cs = new CauseServer("Cause " + i, "Cause Description " + i);
//            try {
//                cs.write2DB();
//                causes.add(new CauseJpaController(DataBaseManager.getEntityManagerFactory()).findCause(cs.getId()));
//            } catch (NonexistentEntityException ex) {
//                LOG.log(Level.SEVERE, null, ex);
//                fail();
//            } catch (Exception ex) {
//                LOG.log(Level.SEVERE, null, ex);
//                fail();
//            }
//        }
//        System.out.println("Done!");
//        System.out.println("Create Risk Control");
//        List<RiskControlType> controlTypes = new ArrayList<RiskControlType>();
//        for (int i = 0; i < 5; i++) {
//            RiskControlTypeServer rcts = new RiskControlTypeServer("RC Type " + i, "RC Type Description" + i);
//            try {
//                rcts.write2DB();
//            } catch (IllegalOrphanException ex) {
//                LOG.log(Level.SEVERE, null, ex);
//                fail();
//            } catch (NonexistentEntityException ex) {
//                LOG.log(Level.SEVERE, null, ex);
//                fail();
//            } catch (Exception ex) {
//                LOG.log(Level.SEVERE, null, ex);
//                fail();
//            }
//            controlTypes.add(new RiskControlTypeJpaController(DataBaseManager.getEntityManagerFactory()).findRiskControlType(rcts.getId()));
//        }
//        System.out.println("Done!");
//        System.out.println("Create Requirement Spec");
//        RequirementSpec rss = null;
//        try {
//            rss = TestHelper.createRequirementSpec("Test", "Test",
//                    product, 1);
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        }
//        System.out.println("Create Requirement Spec Node");
//        RequirementSpecNode rsns = null;
//        try {
//            rsns = TestHelper.createRequirementSpecNode(
//                    rss, "Test", "Test", "Test");
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        }
//        System.out.println("Create Requirements");
//        List<Requirement> requirements = new ArrayList<Requirement>();
//        for (int i = 0; i < 5; i++) {
//            try {
//                Requirement reqs =
//                        TestHelper.createRequirement("SRS-SW-00" + i,
//                        "Description " + i,
//                        rsns.getRequirementSpecNodePK(),
//                        "Notes " + i, 2, 1);
//                requirements.add(new RequirementJpaController(
//                        DataBaseManager.getEntityManagerFactory())
//                        .findRequirement(reqs.getRequirementPK()));
//            } catch (Exception ex) {
//                LOG.log(Level.SEVERE, null, ex);
//                fail();
//            }
//        }
//        System.out.println("Done!");
//        System.out.println("Create Risk Control");
//        List<RiskControl> controls = new ArrayList<RiskControl>();
//        for (int i = 0; i < 5; i++) {
//            RiskControlServer cs = new RiskControlServer(controlTypes.get(i).getId());
//            try {
//                System.out.println("Adding Requirements (" + (i + 1) + ")");
//                cs.setRequirementList(new ArrayList<Requirement>());
//                for (int j = 0; j < i; j++) {
//                    cs.getRequirementList().add(requirements.get(j));
//                }
//                cs.write2DB();
//                controls.add(new RiskControlJpaController(DataBaseManager.getEntityManagerFactory()).findRiskControl(cs.getRiskControlPK()));
//                System.out.println("Done!");
//            } catch (NonexistentEntityException ex) {
//                LOG.log(Level.SEVERE, null, ex);
//                fail();
//            } catch (Exception ex) {
//                LOG.log(Level.SEVERE, null, ex);
//                fail();
//            }
//        }
//        System.out.println("Done!");
//        System.out.println("Create Failure Modes");
//        List<FailureMode> fms = new ArrayList<FailureMode>();
//        for (int i = 0; i < 5; i++) {
//            FailureModeServer fm = new FailureModeServer("Failure Mode " + i, "Failure Mode Desc " + i);
//            try {
//                fm.write2DB();
//            } catch (NonexistentEntityException ex) {
//                LOG.log(Level.SEVERE, null, ex);
//                fail();
//            } catch (Exception ex) {
//                LOG.log(Level.SEVERE, null, ex);
//                fail();
//            }
//            fms.add(new FailureModeJpaController(DataBaseManager.getEntityManagerFactory()).findFailureMode(fm.getId()));
//        }
//        System.out.println("Done!");
//        System.out.println("Create Test");
//        TestServer ts = new TestServer("Test", "Test purpose", "Test scope");
//        try {
//            ts.write2DB();
//        } catch (IllegalOrphanException ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        } catch (NonexistentEntityException ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        }
//        System.out.println("Create Test Cases");
//        VMUserServer user = null;
//        try {
//            user = new VMUserServer("user", "pass", "Name",
//                    "Last Name", "test@test.com");
//            user.write2DB();
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        }
//        for (int i = 0; i < 5; i++) {
//            TestCaseServer tc = new TestCaseServer(i, Short.valueOf("1"), new Date());
//            try {
//                tc.setTest(new TestJpaController(DataBaseManager.getEntityManagerFactory()).findTest(ts.getId()));
//                tc.setExpectedResults("Expected results " + i);
//                tc.setSummary("Summary " + i);
////                tc.setVmUser(new VmUserJpaController(DataBaseManager.getEntityManagerFactory()).findVmUser(user.getId()));
//                tc.write2DB();
//            } catch (NonexistentEntityException ex) {
//                LOG.log(Level.SEVERE, null, ex);
//                fail();
//            } catch (Exception ex) {
//                LOG.log(Level.SEVERE, null, ex);
//                fail();
//            }
//            ts.getTestCaseList().add(new TestCaseJpaController(DataBaseManager.getEntityManagerFactory()).findTestCase(tc.getTestCasePK()));
//        }
//        try {
//            ts.write2DB();
//        } catch (IllegalOrphanException ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        } catch (NonexistentEntityException ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        }
//        System.out.println("Done!");
//        System.out.println("Create RiskItems");
//        List<RiskItem> riskItems = new ArrayList<RiskItem>();
//        for (int i = 0; i < 5; i++) {
//            try {
//                RiskItemServer ri = 
//                        new RiskItemServer(fmea.getId(), (i + 1), 1);
//                ri.write2DB();
//                System.out.println(ri);
//                System.out.println("Adding hazards (" + (i + 1) + ")");
//                ri.setHazardList(new ArrayList<Hazard>());
//                for (int j = 0; j < (i + 1); j++) {
//                    Hazard hazard = hazards.get(j);
//                    System.out.println(hazard);
//                    ri.getHazardList().add(hazard);
//                }
//                System.out.println("Done!");
//                System.out.println("Adding failure modes (" + (i + 1) + ")");
//                ri.setFailureModeList(new ArrayList<FailureMode>());
//                for (int j = 0; j < (i + 1); j++) {
//                    FailureMode fm = fms.get(j);
//                    System.out.println(fm);
//                    ri.getFailureModeList().add(fm);
//                }
//                System.out.println("Done!");
//                System.out.println("Adding causes (" + (i + 1) + ")");
//                ri.setCauseList(new ArrayList<Cause>());
//                for (int j = 0; j < (i + 1); j++) {
//                    Cause cause = causes.get(j);
//                    System.out.println(cause);
//                    ri.getCauseList().add(cause);
//                }
//                System.out.println("Done!");
//                System.out.println("Adding Risk Controls (" + (i + 1) + ")");
//                ri.setRiskControlList(new ArrayList<RiskControl>());
//                for (int j = 0; j < (i + 1); j++) {
//                    RiskControl control = controls.get(j);
//                    System.out.println(control);
//                    ri.getRiskControlList().add(control);
//                }
//                System.out.println("Done!");
//                System.out.println("Adding Risk Item values (" + (i + 1) + ")");
//                for (int j = 0; j < (i + 1); j++) {
//                    RiskControl control = controls.get(j);
//                    System.out.println(control);
//                    ri.getRiskControlList().add(controls.get(j));
//                }
//                System.out.println("Done!");
//                System.out.println("Adding Residual Risk Controls (" 
//                        + (i + 1) + ")");
//                ri.setRiskControlList1(new ArrayList<RiskControl>());
//                for (int j = 0; j < (i + 1); j++) {
//                    RiskControl control = controls.get(j);
//                    System.out.println(control);
//                    ri.getRiskControlList1().add(control);
//                }
//                ri.write2DB();
//                System.out.println("Done!");
//                riskItems.add(new RiskItemJpaController(DataBaseManager.getEntityManagerFactory()).findRiskItem(ri.getRiskItemPK()));
//            } catch (IllegalOrphanException ex) {
//                LOG.log(Level.SEVERE, null, ex);
//                fail();
//            } catch (NonexistentEntityException ex) {
//                LOG.log(Level.SEVERE, null, ex);
//                fail();
//            } catch (Exception ex) {
//                LOG.log(Level.SEVERE, null, ex);
//                fail();
//            }
//        }
//        System.out.println("Done!");
//        System.out.println("Add RiskItems to FMEA");
//        fmea.setRiskItemList(riskItems);
//        try {
//            fmea.write2DB();
//        } catch (IllegalOrphanException ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        } catch (NonexistentEntityException ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            fail();
//        }
//        System.out.println("Done!");
    }
}
