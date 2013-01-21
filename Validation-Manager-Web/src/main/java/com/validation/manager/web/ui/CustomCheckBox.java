package com.validation.manager.web.ui;

import com.vaadin.ui.CheckBox;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class CustomCheckBox extends CheckBox{
    private String id;

    public CustomCheckBox(String id, String caption, boolean initialState) {
        super(caption, initialState);
        this.id = id;
    }
}
