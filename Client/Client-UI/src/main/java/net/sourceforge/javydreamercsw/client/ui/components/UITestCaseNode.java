/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.client.ui.components;

import com.validation.manager.core.db.TestCase;
import java.beans.IntrospectionException;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UITestCaseNode extends BeanNode {

    public UITestCaseNode(TestCase tc, IStepChildFactory stepChildFactory)
            throws IntrospectionException {
        super(tc, Children.create(stepChildFactory, true),
                Lookups.singleton(tc));
        setDisplayName(tc.getName());
    }
}
