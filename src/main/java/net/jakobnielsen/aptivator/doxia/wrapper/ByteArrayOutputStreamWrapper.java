package net.jakobnielsen.aptivator.doxia.wrapper;

import org.codehaus.plexus.util.StringUtils;

import java.io.ByteArrayOutputStream;

/**
 * Modified Wrapper for an output stream.
 *
 * Original author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 */
public final class ByteArrayOutputStreamWrapper extends AbstractWrapper implements Wrapper {

    private ByteArrayOutputStream out;

    private String encoding;

    /**
     * Private constructor.
     *
     * @param out not null
     * @param format not null
     * @param encoding not null
     * @param supportedFormat not null
     * @throws IllegalArgumentException if any.
     */
    private ByteArrayOutputStreamWrapper(ByteArrayOutputStream out, String format, String encoding, String[] supportedFormat) {
        super(format, supportedFormat);

        if (getFormat().equalsIgnoreCase(AUTO_FORMAT)) {
            throw new IllegalArgumentException("output format is required");
        }

        this.out = out;
        this.encoding = encoding;
    }

    /**
     * @return the output stream
     */
    public ByteArrayOutputStream getOutputStream() {
        return this.out;
    }

    /**
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @param out not null
     * @param format not null
     * @param encoding not null
     * @param supportedFormat not null
     * @return a type safe output stream wrapper
     * @throws IllegalArgumentException If some of the given parameters are illegal.
     */
    public static ByteArrayOutputStreamWrapper valueOf(ByteArrayOutputStream out, String format, String encoding, String[] supportedFormat)
            throws IllegalArgumentException {
        if (out == null) {
            throw new IllegalArgumentException("output writer is required");
        }
        if (StringUtils.isEmpty(format)) {
            throw new IllegalArgumentException("output format is required");
        }

        return new ByteArrayOutputStreamWrapper(out, format, encoding, supportedFormat);
    }
}
