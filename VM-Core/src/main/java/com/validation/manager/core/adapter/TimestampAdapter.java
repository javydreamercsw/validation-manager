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
package com.validation.manager.core.adapter;

import java.sql.Timestamp;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TimestampAdapter extends XmlAdapter<java.util.Date, java.sql.Timestamp> {

    @Override
    public Timestamp unmarshal(Date v) throws Exception {
        if (v == null) {
            return null;
        } else {
            return new Timestamp(v.getTime());
        }
    }

    @Override
    public Date marshal(Timestamp v) throws Exception {
        if (v == null) {
            return null;
        } else {
            return new Date(v.getTime());
        }
    }
}
