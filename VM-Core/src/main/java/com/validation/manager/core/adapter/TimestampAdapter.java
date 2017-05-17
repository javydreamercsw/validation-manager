package com.validation.manager.core.adapter;

import java.sql.Timestamp;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
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
