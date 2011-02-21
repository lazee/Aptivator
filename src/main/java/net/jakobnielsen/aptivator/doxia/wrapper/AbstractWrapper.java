/*
 * Copyright (c) 2008-2011 Jakob Vad Nielsen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.jakobnielsen.aptivator.doxia.wrapper;

import java.io.Serializable;

import org.codehaus.plexus.util.StringUtils;

/**
 * Abstract wrapper for Doxia converter.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id: AbstractWrapper.java 697523 2008-09-21 14:29:09Z vsiveton $
 */
abstract class AbstractWrapper implements Serializable {

    public static final String AUTO_FORMAT = "auto";

    /** serialVersionUID */
    static final long serialVersionUID = -1150854786311626411L;

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
        this.supportedFormat = supportedFormat;
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
        this.supportedFormat = supportedFormat;
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
