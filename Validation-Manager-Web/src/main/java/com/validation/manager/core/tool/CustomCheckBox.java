/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.tool;

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
