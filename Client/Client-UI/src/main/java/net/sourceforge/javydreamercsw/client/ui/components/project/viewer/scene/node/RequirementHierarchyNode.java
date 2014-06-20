package net.sourceforge.javydreamercsw.client.ui.components.project.viewer.scene.node;

import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.server.core.RequirementServer;
import com.validation.manager.core.tool.ImageProvider;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.client.ui.components.project.viewer.scene.AbstractHierarchyNode;
import org.netbeans.api.visual.widget.Scene;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementHierarchyNode extends AbstractHierarchyNode {

    private BufferedImage image = null;

    private static final Logger LOG
            = Logger.getLogger(RequirementHierarchyNode.class.getSimpleName());

    public RequirementHierarchyNode(Object object, Scene scene) {
        super(object, scene);
    }

    public RequirementHierarchyNode(Object object, Scene scene, 
            TextOrientation orientation) {
        super(object, scene, orientation);
    }

    @Override
    public Object getID() {
        return ((Requirement) object).getId();
    }

    @Override
    public Image getCurrentImage() {
        if (image == null) {
            try {
                int coverage
                        = new RequirementServer(((Requirement) object)).getTestCoverage();
                ImageProvider provider = null;
                for (ImageProvider p : Lookup.getDefault().lookupAll(ImageProvider.class)) {
                    if (p.supported(((Requirement) object))) {
                        provider = p;
                        break;
                    }
                }
                if (provider != null) {
                    image = provider.getIcon(((Requirement) object), coverage);
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        return image;
    }

    @Override
    public String getCurrentLabel() {
        return ((Requirement) object).getUniqueId();
    }

    @Override
    protected List<AbstractHierarchyNode> getNodeChildren() {
        ArrayList<AbstractHierarchyNode> children
                = new ArrayList<>();
        //Clear and recreate
        Requirement req = (Requirement) object;
        LOG.log(Level.FINE, "Updating children for {0}", req.getUniqueId());
        for (Requirement r : req.getRequirementList1()) {
            children.add(new RequirementHierarchyNode(r, getScene()));
        }
        for (Step s : req.getStepList()) {
            children.add(new StepHierarchyNode(s, getScene()));
        }
        return children;
    }
}
