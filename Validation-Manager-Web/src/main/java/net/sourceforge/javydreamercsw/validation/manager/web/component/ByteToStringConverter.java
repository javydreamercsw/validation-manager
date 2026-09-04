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
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ByteToStringConverter implements Converter<String, byte[]> {

    private static final Logger LOG
            = Logger.getLogger(ByteToStringConverter.class.getSimpleName());

    @Override
    public Result<byte[]> convertToModel(String value, ValueContext context) {
        try {
            String result = value;
            if (value == null) {
                result = "null";
            }
            return Result.ok(result.getBytes(StandardCharsets.UTF_8));
        } catch (RuntimeException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return Result.ok(new byte[0]);
    }

    @Override
    public String convertToPresentation(byte[] value, ValueContext context) {
        return value == null ? "null" : new String(value, StandardCharsets.UTF_8);
    }
}
