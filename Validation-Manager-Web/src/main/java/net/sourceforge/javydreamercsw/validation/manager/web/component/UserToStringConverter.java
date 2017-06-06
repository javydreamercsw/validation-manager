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

import com.vaadin.data.util.converter.Converter;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.server.core.VMUserServer;
import java.util.Locale;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class UserToStringConverter implements Converter<String, VmUser> {

    private VmUser user;

    @Override
    public Class<VmUser> getModelType() {
        return VmUser.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

    @Override
    public VmUser convertToModel(String value,
            Class<? extends VmUser> targetType, Locale locale) throws ConversionException {
        return user;
    }

    @Override
    public String convertToPresentation(VmUser value,
            Class<? extends String> targetType, Locale locale) throws ConversionException {
        user = value;
        return new VMUserServer(value).toString();
    }
}
