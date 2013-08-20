package net.sourceforge.javydreamercsw.client.ui.components.requirement.edit;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestPlanHasTest;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.TestProjectJpaController;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class CoveringStepFactory extends ChildFactory<Step>
        implements Lookup.Provider {

    /**
     * The lookup for Lookup.Provider
     */
    private final Lookup lookup;
    /**
     * The InstanceContent that keeps this entity's abilities
     */
    private final InstanceContent instanceContent;
    private final ArrayList<Step> steps = new ArrayList<Step>();
    private static final Logger LOG
            = Logger.getLogger(CoveringStepFactory.class.getSimpleName());

    public CoveringStepFactory() {
        // Create an InstanceContent to hold abilities...
        instanceContent = new InstanceContent();
        // Create an AbstractLookup to expose InstanceContent contents...
        lookup = new AbstractLookup(instanceContent);
        // Add a "Reloadable" ability to this entity
        instanceContent.add(new Reloadable() {
            @Override
            public void reload() throws Exception {
                TestProjectJpaController controller
                        = new TestProjectJpaController(DataBaseManager.getEntityManagerFactory());
                Requirement req = Utilities.actionsGlobalContext().lookup(Requirement.class);
                for (TestProject tp : controller.findTestProjectEntities()) {
                    for (TestPlan plan : tp.getTestPlanList()) {
                        for (TestPlanHasTest tpht : plan.getTestPlanHasTestList()) {
                            for (TestCase tc : tpht.getTest().getTestCaseList()) {
                                for (Step s : tc.getStepList()) {
                                    if (s.getRequirementList().contains(req)) {
                                        steps.add(s);
                                    }
                                }
                            }
                        }
                    }
                }
                LOG.log(Level.INFO, "Test Cases found: {0}", steps.size());
                Collections.sort(steps, new Comparator<Step>() {
                    @Override
                    public int compare(Step o1, Step o2) {
                        return o1.getTestCase().getName()
                                .compareTo(o2.getTestCase().getName());
                    }
                });
            }
        });
    }

    public void refresh() {
        refresh(true);
    }

    @Override
    protected boolean createKeys(List<Step> toPopulate) {
        // The query node is reloadable, isn't it? Then just
        // get this ability from the lookup ...
        Reloadable r = getLookup().lookup(Reloadable.class);
        // ... and  use the ability
        if (r != null) {
            try {
                r.reload();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        toPopulate.addAll(steps);
        return true;
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    protected Node createNodeForKey(Step key) {
        Node result = null;
        try {
            result = new UIStepNode(key);
        } catch (IntrospectionException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
