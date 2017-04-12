package net.sourceforge.javydreamercsw.validation.manager.web;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Window;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class VMWindow extends Window {

    protected final ValidationManagerUI menu;
    private final ThemeResource small = new ThemeResource("VMSmall.png");

    public VMWindow() {
        super();
        menu = null;
        setIcon(small);
    }

    public VMWindow(String caption) {
        super(caption);
        menu = null;
        setIcon(small);
    }

    public VMWindow(ValidationManagerUI menu, String caption) {
        super(caption);
        this.menu = menu;
        setIcon(small);
    }

    public VMWindow(ValidationManagerUI menu) {
        super();
        this.menu = menu;
        setIcon(small);
    }
}
