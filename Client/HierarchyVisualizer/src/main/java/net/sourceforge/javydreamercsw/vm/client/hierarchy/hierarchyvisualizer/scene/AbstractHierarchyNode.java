package net.sourceforge.javydreamercsw.vm.client.hierarchy.hierarchyvisualizer.scene;

import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.net.URL;
import javax.swing.ImageIcon;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.general.IconNodeWidget;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Utilities;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractHierarchyNode extends IconNodeWidget {

    protected final Object object;

    /**
     * Get id for the object.
     *
     * @return id
     */
    public abstract Object getID();

    /**
     * Get the image that represents the current state of the object.
     *
     * @return image that represents the current state of the object
     */
    public abstract Image getCurrentImage();

    /**
     * Get the current label that represents the current state of the object.
     *
     * @return current label that represents the current state of the object
     */
    public abstract String getCurrentLabel();

    /**
     * Update the widget
     */
    public void update() {
        Image currentImage = getCurrentImage();
        if (currentImage != null) {
            setImage(currentImage);
        }
        String currentLabel = getCurrentLabel();
        if (currentLabel != null) {
            setLabel(currentLabel);
        }
        //Update children
        updateChildren();
    }

    protected abstract void updateChildren();

    public AbstractHierarchyNode(Object object, Scene scene) {
        super(scene);
        this.object = object;
        init();
    }

    private void init() {
        setImage(getCurrentImage());
        setLabel(getCurrentLabel());
        if (getPreferredLocation() == null) {
            setPreferredLocation(new Point(10, 20));
        }
    }

    public AbstractHierarchyNode(Object object, Scene scene, TextOrientation orientation) {
        super(scene, orientation);
        this.object = object;
        init();
    }

    protected ImageIcon createImage(String module_id, String path, String description)
            throws Exception {
        File icon = InstalledFileLocator.getDefault().locate(path,
                "com.validation.manager.client.hierarchy", false);
        URL imageURL = Utilities.toURI(icon).toURL();
        if (imageURL == null) {
            throw new Exception("Resource not found: " + path);
        } else {
            return new ImageIcon(imageURL, description);
        }
    }
}
