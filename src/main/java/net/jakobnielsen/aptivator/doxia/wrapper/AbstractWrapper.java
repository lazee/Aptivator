package net.jakobnielsen.aptivator.doxia.wrapper;

import org.codehaus.plexus.util.StringUtils;

import java.io.Serializable;

/**
 * Modified Abstract wrapper for Doxia converter.
 *
 * Original author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 */
abstract class AbstractWrapper implements Serializable {

    public static final String AUTO_FORMAT = "auto";

    private String format;

    private String[] supportedFormat;

    /**
     * @param format could be null.
     * @param supportedFormat not null.
     * @throws IllegalArgumentException if supportedFormat is null.
     */
    AbstractWrapper(String format, String[] supportedFormat) {
        this.format = (StringUtils.isNotEmpty(format) ? format : AUTO_FORMAT);
        if (supportedFormat == null) {
            throw new IllegalArgumentException("supportedFormat is required");
        }
        this.supportedFormat = supportedFormat.clone();
    }

    /**
     * @return the wanted format.
     */
    public String getFormat() {
        return this.format;
    }

    /**
     * @param format The wanted format.
     */
    void setFormat(String format) {
        this.format = format;
    }

    /**
     * @return the supportedFormat
     */
    public String[] getSupportedFormat() {
        return supportedFormat;
    }

    /**
     * @param supportedFormat the supportedFormat to set
     */
    void setSupportedFormat(String[] supportedFormat) {
        this.supportedFormat = supportedFormat.clone();
    }

    /** {@inheritDoc} */
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof AbstractWrapper)) {
            return false;
        }

        AbstractWrapper that = (AbstractWrapper) other;
        return (getFormat() == null ? that.getFormat() == null : getFormat().equals(that.getFormat()));
    }

    /** {@inheritDoc} */
    public int hashCode() {
        int result = 17;
        result = 37 * result + (format != null ? format.hashCode() : 0);
        return result;
    }

    /** {@inheritDoc} */
    public java.lang.String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("format = '");
        buf.append(getFormat());
        buf.append("'");
        return buf.toString();
    }
}
