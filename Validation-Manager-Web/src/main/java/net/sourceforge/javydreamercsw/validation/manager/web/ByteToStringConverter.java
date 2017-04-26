package net.sourceforge.javydreamercsw.validation.manager.web;

import com.vaadin.data.util.converter.Converter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ByteToStringConverter implements Converter<String, byte[]> {

    private static final Logger LOG
            = Logger.getLogger(ByteToStringConverter.class.getSimpleName());

    @Override
    public byte[] convertToModel(String value,
            Class<? extends byte[]> targetType, Locale locale)
            throws ConversionException {
        try {
            String result = value;
            if (value == null) {
                result = "null";
            }
            return result.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String convertToPresentation(byte[] value,
            Class<? extends String> targetType, Locale locale)
            throws ConversionException {
        return value == null ? "null" : new String(value, StandardCharsets.UTF_8);
    }

    @Override
    public Class<byte[]> getModelType() {
        return byte[].class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
