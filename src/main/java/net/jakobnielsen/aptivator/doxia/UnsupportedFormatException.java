package net.jakobnielsen.aptivator.doxia;

import net.jakobnielsen.aptivator.AptivatorException;
import org.codehaus.plexus.util.StringUtils;

/**
 * Unsupported format exception.
 */
public class UnsupportedFormatException extends AptivatorException {

    public UnsupportedFormatException(String format, String[] supportedFormat) {
        super("Unsupported format '" + format + "'. The allowed format are: "
                + StringUtils.join(supportedFormat, ", "));
    }

}
