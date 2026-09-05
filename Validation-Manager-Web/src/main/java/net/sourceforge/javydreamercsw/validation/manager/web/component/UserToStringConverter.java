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

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.server.core.VMUserServer;
import java.util.StringTokenizer;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class UserToStringConverter implements Converter<String, VmUser> {

    @Override
    public Result<VmUser> convertToModel(String value, ValueContext context) {
        StringTokenizer st = new StringTokenizer(value, " ");
        String name = st.nextToken();
        String last = st.hasMoreTokens() ? st.nextToken() : "";
        for (VMUserServer user : VMUserServer.getVMUsers()) {
            if (user.getFirstName().equals(name) && user.getLastName().equals(last)) {
                return Result.ok(user);
            }
        }
        return Result.error("Unknown user: " + value);
    }

    @Override
    public String convertToPresentation(VmUser value, ValueContext context) {
        return new VMUserServer(value).toString();
    }
}
