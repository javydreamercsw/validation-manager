package net.sourceforge.javydreamercsw.client.ui.components.project.viewer.scene.node;

import com.validation.manager.core.db.Step;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.sourceforge.javydreamercsw.client.ui.components.project.viewer.scene.AbstractHierarchyNode;
import org.netbeans.api.visual.widget.Scene;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class StepHierarchyNode extends AbstractHierarchyNode {

    private static final Logger LOG
            = Logger.getLogger(StepHierarchyNode.class.getSimpleName());

    public StepHierarchyNode(Object object, Scene scene) {
        super(object, scene);
    }

    public StepHierarchyNode(Object object, Scene scene, TextOrientation orientation) {
        super(object, scene, orientation);
    }

    @Override
    public Object getID() {
        return ((Step) object).getStepPK();
    }

    @Override
    public Image getCurrentImage() {
        BufferedImage image = null;
        try {
            //TODO: Select image based on execution result.
            //Step step=((Step) object);
            image = ImageIO.read(getClass().getResource(
                    "/net/sourceforge/javydreamercsw/vm/client/hierarchy/visualizer/step_pass.png"));
//            image = ImageIO.read(getClass().getResource(
//                    "/net/sourceforge/javydreamercsw/vm/client/hierarchy/visualizer/step_fail.png"));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return image;
    }

    @Override
    public String getCurrentLabel() {
        Step step = ((Step) object);
        return "Test Case: " + step.getTestCase().getName() + ", step " + step.getStepSequence();
    }

    @Override
    protected List<AbstractHierarchyNode> getNodeChildren() {
        return new ArrayList<>();
    }

}
