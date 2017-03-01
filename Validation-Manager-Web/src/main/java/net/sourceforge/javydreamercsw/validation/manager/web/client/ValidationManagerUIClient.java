package net.sourceforge.javydreamercsw.validation.manager.web.client;

import com.vaadin.client.UIDL;
import com.vaadin.client.ui.dd.VAcceptCriterion;
import com.vaadin.client.ui.dd.VDragEvent;
import com.vaadin.shared.ui.dd.AcceptCriterion;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;

@AcceptCriterion(ValidationManagerUI.class)
public class ValidationManagerUIClient extends VAcceptCriterion {

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {
        // TODO: provide implementation
        return true;
    }
}
