package net.sourceforge.javydreamercsw.validation.manager.web;

import com.vaadin.ui.Window;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class VMWindow extends Window {

    protected final ValidationManagerUI menu;

    public VMWindow() {
        super();
        menu = null;
        setIcon(ValidationManagerUI.SMALL_APP_ICON);
    }

    public VMWindow(String caption) {
        super(caption);
        menu = null;
        setIcon(ValidationManagerUI.SMALL_APP_ICON);
    }

    public VMWindow(ValidationManagerUI menu, String caption) {
        super(caption);
        this.menu = menu;
        setIcon(ValidationManagerUI.SMALL_APP_ICON);
    }

    public VMWindow(ValidationManagerUI menu) {
        super();
        this.menu = menu;
        setIcon(ValidationManagerUI.SMALL_APP_ICON);
    }
}
