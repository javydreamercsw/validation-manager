package net.sourceforge.javydreamercsw.vm.client.hierarchy.hierarchyvisualizer.scene.node;

import com.validation.manager.core.db.Requirement;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import net.sourceforge.javydreamercsw.vm.client.hierarchy.hierarchyvisualizer.scene.AbstractHierarchyNode;
import org.netbeans.api.visual.widget.Scene;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementHierarchyNode extends AbstractHierarchyNode {

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
    protected void updateChildren() {
        //Clear and recreate
        getChildren().clear();
        Requirement req = (Requirement) object;
        for (Requirement r : req.getRequirementList()) {
            getChildren().add(new RequirementHierarchyNode(r, getScene()));
        }
    }
}
