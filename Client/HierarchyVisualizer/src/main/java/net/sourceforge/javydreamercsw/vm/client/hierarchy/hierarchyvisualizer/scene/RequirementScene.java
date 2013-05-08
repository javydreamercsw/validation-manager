package net.sourceforge.javydreamercsw.vm.client.hierarchy.hierarchyvisualizer.scene;

import com.validation.manager.core.db.Requirement;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementScene extends GraphScene<RequirementNode, Requirement> {

    private LayerWidget mainLayer;

    public RequirementScene() {
        mainLayer = new LayerWidget(this);
        addChild(mainLayer);
        getActions().addAction(ActionFactory.createZoomAction());
        getActions().addAction(ActionFactory.createPanAction());
    }

    @Override
    protected Widget attachNodeWidget(RequirementNode n) {
        return null;
    }

    @Override
    protected Widget attachEdgeWidget(Requirement e) {
        return null;
    }

    @Override
    protected void attachEdgeSourceAnchor(Requirement e, RequirementNode n, RequirementNode n1) {
    }

    @Override
    protected void attachEdgeTargetAnchor(Requirement e, RequirementNode n, RequirementNode n1) {
    }
}
