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

import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.validation.manager.core.VMException;
import com.validation.manager.core.tool.MD5;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class UserPasswordConverter implements Converter<String, String> {

    @Override
    public Result<String> convertToModel(String value, ValueContext context) {
        return Result.ok(value == null ? "" : value);
    }

    @Override
    public String convertToPresentation(String value, ValueContext context) {
        try {
            return value == null ? "" : MD5.encrypt(value);
        } catch (VMException ex) {
            Exceptions.printStackTrace(ex);
        }
        return "";
    }
}
