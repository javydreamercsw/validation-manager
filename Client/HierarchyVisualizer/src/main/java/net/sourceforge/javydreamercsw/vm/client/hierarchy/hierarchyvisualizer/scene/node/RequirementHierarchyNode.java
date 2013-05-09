package net.sourceforge.javydreamercsw.vm.client.hierarchy.hierarchyvisualizer.scene.node;

import com.validation.manager.core.db.Requirement;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.sourceforge.javydreamercsw.vm.client.hierarchy.hierarchyvisualizer.scene.AbstractHierarchyNode;
import net.sourceforge.javydreamercsw.vm.client.hierarchy.hierarchyvisualizer.scene.HierarchyScene;
import org.netbeans.api.visual.widget.Scene;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementHierarchyNode extends AbstractHierarchyNode {

    private static final Logger LOG =
            Logger.getLogger(RequirementHierarchyNode.class.getSimpleName());

    public RequirementHierarchyNode(Object object, Scene scene) {
        super(object, scene);
    }

    public RequirementHierarchyNode(Object object, Scene scene, TextOrientation orientation) {
        super(object, scene, orientation);
    }

    @Override
    public Object getID() {
        return ((Requirement) object).getRequirementPK();
    }

    @Override
    public Image getCurrentImage() {
        try {
            BufferedImage image = ImageIO.read(getClass().getResource(
                    "/net/sourceforge/javydreamercsw/vm/client/hierarchy/visualizer/Requirement_Good.png"));
            return image;
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public String getCurrentLabel() {
        return ((Requirement) object).getUniqueId();
    }

    @Override
    protected List<AbstractHierarchyNode> getNodeChildren() {
        ArrayList<AbstractHierarchyNode> children =
                new ArrayList<AbstractHierarchyNode>();
        //Clear and recreate
        Requirement req = (Requirement) object;
        LOG.log(Level.INFO, "Updating children for {0}", req.getUniqueId());
        for (Requirement r : req.getRequirementList()) {
            children.add(new RequirementHierarchyNode(r, getScene()));
        }
        return children;
    }
}
