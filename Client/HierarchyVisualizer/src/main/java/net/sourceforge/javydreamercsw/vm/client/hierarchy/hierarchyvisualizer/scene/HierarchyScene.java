package net.sourceforge.javydreamercsw.vm.client.hierarchy.hierarchyvisualizer.scene;

import com.validation.manager.core.db.Requirement;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
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
    private final int horizontalGap = 100;
    private final int verticalGap = 25;
    private ArrayList<LayerWidget> layersToClear = new ArrayList<LayerWidget>();

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

    /**
     * Organize the children.
     *
     * @param child
     */
    private void placeChild(Widget parent, Widget child, boolean invert) {
        Point point = new Point(getChildX(parent, invert),
                getChildY(parent, child, invert));
        LOG.log(Level.FINE, "Children location: {0}", point);
        child.setPreferredLocation(point);
    }

    private int getChildY(Widget parent, Widget child, boolean invert) {
        //Get parent location
        Rectangle bounds = parent.getBounds();
        return bounds == null ? 0 : (verticalGap
                + (child.getBounds() == null ? bounds.height
                : child.getBounds().height))
                * (mainLayer.getChildren().size() - 1);
    }

    private int getChildX(Widget parent, boolean invert) {
        //Get parent location
        Rectangle bounds = parent.getBounds();
        parent.getChildren().size();
        return invert ? 0 : bounds == null ? 0 : 
                bounds.x + bounds.width + horizontalGap;
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
