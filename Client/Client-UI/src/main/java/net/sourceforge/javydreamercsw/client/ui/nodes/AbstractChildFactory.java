package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.RiskControl;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.Test;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import java.beans.IntrospectionException;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 * This class provides a common way for creating nodes depending on object
 * passed as a key. Useful since many factories need to render children in
 * different places.
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractChildFactory extends ChildFactory<Object> {

    private boolean showChildren = true;

    @Override
    protected Node[] createNodesForKey(Object key) {
        return new Node[]{createNodeForKey(key)};
    }

    @Override
    protected Node createNodeForKey(Object key) {
        try {
            if (key instanceof Project) {
                Project project = (Project) key;
                return new ProjectNode(project,
                        isShowChildren() ? 
                                new SubProjectChildFactory(project) : null);
            } else if (key instanceof RequirementSpec) {
                RequirementSpec rs = (RequirementSpec) key;
                return new UIRequirementSpecNode(rs);
            } else if (key instanceof TestProject) {
                TestProject tp = (TestProject) key;
                return new UITestProjectNode(tp);
            } else if (key instanceof Requirement) {
                Requirement req = (Requirement) key;
                return new UIRequirementNode(req, 
                        new RequirementTestChildFactory(req));
            } else if (key instanceof RequirementSpecNode) {
                RequirementSpecNode rs = (RequirementSpecNode) key;
                return new UIRequirementSpecNodeNode(rs);
            } else if (key instanceof Step) {
                Step step = (Step) key;
                return new StepNode(step);
            } else if (key instanceof RiskControl) {
                RiskControl rs = (RiskControl) key;
                return new RiskControlNode(rs);
            } else if (key instanceof TestCase) {
                TestCase tc = (TestCase) key;
                return new TestCaseNode(tc);
            } else if (key instanceof Test) {
                Test t = (Test) key;
                return new TestNode(t);
            } else if (key instanceof TestPlan) {
                TestPlan plan = (TestPlan) key;
                return new TestPlanNode(plan);
            } else {
                return null;
            }
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    protected void refresh() {
        updateBean();
        refresh(true);
    }

    /**
     * This makes sure the bean is up to date.
     */
    protected abstract void updateBean();

    /**
     * @return the showChildren
     */
    public boolean isShowChildren() {
        return showChildren;
    }

    /**
     * @param showChildren the showChildren to set
     */
    public AbstractChildFactory setShowChildren(boolean showChildren) {
        this.showChildren = showChildren;
        return this;
    }
}
