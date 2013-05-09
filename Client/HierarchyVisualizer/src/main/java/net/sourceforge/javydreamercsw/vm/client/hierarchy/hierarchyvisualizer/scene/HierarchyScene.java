package net.sourceforge.javydreamercsw.vm.client.hierarchy.hierarchyvisualizer.scene;

import com.validation.manager.core.db.Requirement;
import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.vm.client.hierarchy.hierarchyvisualizer.scene.node.RequirementHierarchyNode;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorFactory.DirectionalAnchorKind;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class HierarchyScene extends GraphScene<Object, Object> {

    private LayerWidget mainLayer, connectionLayer, interactionLayer;
    private Map<Object, Widget> widgets = new HashMap<Object, Widget>();
    private static final Logger LOG =
            Logger.getLogger(HierarchyScene.class.getSimpleName());

    public HierarchyScene() {
        mainLayer = new LayerWidget(this);
        connectionLayer = new LayerWidget(this);
        interactionLayer = new LayerWidget(this);
        addChild(mainLayer);
        addChild(connectionLayer);
        addChild(interactionLayer);
        getActions().addAction(ActionFactory.createZoomAction());
        getActions().addAction(ActionFactory.createPanAction());
        validate();
    }

    @Override
    protected Widget attachNodeWidget(Object n) {
        AbstractHierarchyNode widget = createWidget(n);
        if (widget != null) {
            mainLayer.addChild(widget);
            validate();
            for (Widget child : widget.getChildren()) {
//               TODO: addChildWidget(widget, child);
            }
            addWidgetActions(mainLayer, connectionLayer);
        } else {
            LOG.log(Level.WARNING, "Null widget returned!");
        }
        return widget;
    }

    private void addChildWidget(Widget parent, Widget child) {
        mainLayer.addChild(child);
        LOG.log(Level.INFO,
                "Parent has now {0} children.", parent.getChildren().size());
        //Now link them
        ConnectionWidget conw = new ConnectionWidget(this);
        setSource(conw, parent, ANCHOR_TYPE.RECTANGULAR);
        setTarget(conw, child, ANCHOR_TYPE.CENTER);
        conw.setSourceAnchorShape(AnchorShape.NONE);
        conw.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        connectionLayer.addChild(conw);
        validate();
    }

    public void addWidgetActions(LayerWidget mainLayer, LayerWidget connectionLayer) {
        List<Widget> children = mainLayer.getChildren();
        for (Iterator<Widget> it = children.iterator(); it.hasNext();) {
            Widget widget = it.next();
            // the order is important to not consume events
            widget.getActions().addAction(ActionFactory.createSelectAction(
                    new SelProvider()));
            widget.getActions().addAction(ActionFactory.createMoveAction());
            widget.getActions().addAction(ActionFactory.createResizeAction());
        }
        validate();
    }

    private static class SelProvider implements SelectProvider {

        @Override
        public boolean isAimingAllowed(Widget widget, Point localLocation,
                boolean invertSelection) {
            LOG.log(Level.INFO,
                    "sel.isAimingAllowed {0}", localLocation);
            return false;
        }

        @Override
        public boolean isSelectionAllowed(Widget widget, Point localLocation,
                boolean invertSelection) {
            LOG.log(Level.INFO,
                    "sel.isSelectionAllowed {0}", localLocation);
            return true;
        }

        @Override
        public void select(Widget widget, Point localLocation,
                boolean invertSelection) {

            LOG.log(Level.INFO,
                    "sel.select {0}", localLocation);
            widget.getScene().setFocusedWidget(widget);
        }
    }

    private void setSource(ConnectionWidget conw, Widget source, ANCHOR_TYPE type) {
        switch (type) {
            case CENTER:
                conw.setSourceAnchor(AnchorFactory.createCenterAnchor(source));
                break;
            case CIRCULAR:
                conw.setSourceAnchor(AnchorFactory.createCircularAnchor(source, 5));
                break;
            case DIRECTIONAL:
                conw.setSourceAnchor(AnchorFactory.createDirectionalAnchor(source,
                        DirectionalAnchorKind.HORIZONTAL));
                break;
            case RECTANGULAR:
                conw.setSourceAnchor(AnchorFactory.createRectangularAnchor(source));
                break;
            default:
                LOG.log(Level.WARNING,
                        "Unexpected anchor: {0}", type.name());
        }
    }

    private void setTarget(ConnectionWidget conw, Widget target, ANCHOR_TYPE type) {
        switch (type) {
            case CENTER:
                conw.setTargetAnchor(AnchorFactory.createCenterAnchor(target));
                break;
            case CIRCULAR:
                conw.setTargetAnchor(AnchorFactory.createCircularAnchor(target, 5));
                break;
            case DIRECTIONAL:
                conw.setTargetAnchor(AnchorFactory.createDirectionalAnchor(target,
                        DirectionalAnchorKind.HORIZONTAL));
                break;
            case RECTANGULAR:
                conw.setTargetAnchor(AnchorFactory.createRectangularAnchor(target));
                break;
            default:
                LOG.log(Level.WARNING,
                        "Unexpected anchor: {0}", type.name());
        }
    }

    @Override
    protected Widget attachEdgeWidget(Object e) {
        return null;
    }

    @Override
    protected void attachEdgeSourceAnchor(Object edge, Object oldSource, Object newSource) {
    }

    @Override
    protected void attachEdgeTargetAnchor(Object edge, Object oldTarget, Object newTarget) {
    }

    public void addRequirement(Requirement req) {
        widgets.put(req.getRequirementPK(), attachNodeWidget(req));
    }

    private AbstractHierarchyNode createWidget(Object o) {
        if (o instanceof Requirement) {
            Requirement requirement = (Requirement) o;
            return new RequirementHierarchyNode(requirement, this);
        } else {
            return null;
        }
    }
}
