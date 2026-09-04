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
package net.sourceforge.javydreamercsw.validation.manager.web.component;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TranslationConverter implements Converter<String, String> {

    private static final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);

    @Override
    public Result<String> convertToModel(String value, ValueContext context) {
        return Result.ok(value);
    }

    @Override
    public String convertToPresentation(String value, ValueContext context) {
        return TRANSLATOR.translate(value);
    }
}
