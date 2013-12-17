package net.sourceforge.javydreamercsw.client.ui.components.project.viewer.scene;

import com.validation.manager.core.db.Requirement;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.client.ui.components.project.viewer.scene.node.RequirementHierarchyNode;
import net.sourceforge.javydreamercsw.client.ui.components.project.viewer.scene.node.StepHierarchyNode;
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

    private final LayerWidget mainLayer, connectionLayer, interactionLayer;
    private final Map<Object, Widget> widgets = new HashMap<Object, Widget>();
    private static final Logger LOG
            = Logger.getLogger(HierarchyScene.class.getSimpleName());
    private final int horizontalGap = 100;
    private final int verticalGap = 25;
    private final ArrayList<LayerWidget> layersToClear = new ArrayList<LayerWidget>();

    public HierarchyScene() {
        mainLayer = new LayerWidget(this);
        layersToClear.add(mainLayer);
        connectionLayer = new LayerWidget(this);
        layersToClear.add(connectionLayer);
        interactionLayer = new LayerWidget(this);
        layersToClear.add(interactionLayer);
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
            for (AbstractHierarchyNode child : widget.getNodeChildren()) {
                if (!hasWidget(child.getID())) {
                    addChildWidget(widget, child, true);
                }
                validate();
            }
            addWidgetActions(mainLayer, connectionLayer);
        } else {
            LOG.log(Level.WARNING, "Null widget returned!");
        }
        return widget;
    }

    private void addChildWidget(Widget parent, Widget child) {
        addChildWidget(parent, child, false);
    }

    /**
     * Connect parent with child.
     *
     * @param parent
     * @param child
     * @param invert Normally arrow goes from parent to child, set to true to
     * invert direction.
     */
    private void addChildWidget(Widget parent, Widget child, boolean invert) {
        mainLayer.addChild(child);
        //Now link them
        ConnectionWidget conw = new ConnectionWidget(this);
        setSource(conw, invert ? child : parent, ANCHOR_TYPE.RECTANGULAR);
        setTarget(conw, invert ? parent : child, ANCHOR_TYPE.DIRECTIONAL);
        conw.setSourceAnchorShape(AnchorShape.NONE);
        conw.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        connectionLayer.addChild(conw);
        placeChild(parent, child, invert);
        validate();
    }

    public void addWidgetActions(LayerWidget mainLayer, LayerWidget connectionLayer) {
        List<Widget> children = mainLayer.getChildren();
        for (Widget widget : children) {
            // the order is important to not consume events
            widget.getActions().addAction(ActionFactory.createSelectAction(
                    new SelProvider()));
            widget.getActions().addAction(ActionFactory.createMoveAction());
            widget.getActions().addAction(ActionFactory.createResizeAction());
        }
        validate();
    }

    /**
     * Organize the children.
     *
     * @param child
     */
    private void placeChild(Widget parent, Widget child, boolean invert) {
        Point point = new Point(getChildX(parent, child),
                getChildY(parent, child));
        LOG.log(Level.INFO, "Children location: {0}", point);
        child.setPreferredLocation(point);
    }

    private int getChildY(Widget parent, Widget child) {
        //Get parent location
        Rectangle bounds = parent.getBounds();
        return bounds == null ? 0 : (verticalGap
                + (child.getBounds() == null ? bounds.height
                : child.getBounds().height))
                * (mainLayer.getChildren().size() - 1);
    }

    private int getChildX(Widget parent, Widget child) {
        //Get parent location
        int x;
        Rectangle bounds = parent.getBounds();
        if (bounds != null && child instanceof RequirementHierarchyNode) {
            x = 0;
        } else if (bounds != null && child instanceof StepHierarchyNode) {
            x = (bounds.x + bounds.width + horizontalGap) * 3;
        } else {
            x = 0;
        }
        return x;
    }

    public void clear() {
        for (LayerWidget lw : layersToClear) {
            lw.removeChildren();
        }
        widgets.clear();
        validate();
    }

    private static class SelProvider implements SelectProvider {

        @Override
        public boolean isAimingAllowed(Widget widget, Point localLocation,
                boolean invertSelection) {
            LOG.log(Level.FINE,
                    "sel.isAimingAllowed {0}", localLocation);
            return false;
        }

        @Override
        public boolean isSelectionAllowed(Widget widget, Point localLocation,
                boolean invertSelection) {
            LOG.log(Level.FINE,
                    "sel.isSelectionAllowed {0}", localLocation);
            return true;
        }

        @Override
        public void select(Widget widget, Point localLocation,
                boolean invertSelection) {

            LOG.log(Level.FINE,
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

    public boolean hasWidget(Object key) {
        return widgets.containsKey(key);
    }
}
