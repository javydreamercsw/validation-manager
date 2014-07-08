package net.sourceforge.javydreamercsw.client.ui.components.requirement.viewer.scene;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.List;
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
    }

    /**
     * Get the immediate child of this node.
     *
     * @return
     */
    protected abstract List<AbstractHierarchyNode> getNodeChildren();

    public AbstractHierarchyNode(Object object, Scene scene) {
        super(scene);
        this.object = object;
        init();
    }

    private void init() {
        setImage(getCurrentImage());
        setLabel(getCurrentLabel());
        if (getPreferredLocation() == null) {
            setPreferredLocation(new Point(300, 20));
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

    protected static BufferedImage resizeImage(BufferedImage originalImage,
            int type, final int IMG_WIDTH, final int IMG_HEIGHT) {

        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        return resizedImage;
    }
}
