/*
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.validation.manager.core;

import com.vaadin.ui.Component;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface ContentProvider {

    static final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);

    /**
     * Return the component caption. This will be attempted to be translated.
     *
     * @return component caption
     */
    String getComponentCaption();

    /**
     * The Content of the provider
     *
     * @return content.
     */
    Component getContent();

    /**
     * This is the unique id of the provider
     *
     * @return unique id for the provider.
     */
    String getId();

    /**
     * Refresh the contents.
     */
    void update();
}
