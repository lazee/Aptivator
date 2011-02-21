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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.codehaus.plexus.util.StringUtils;

/**
 * Wrapper for an output stream.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id: OutputStreamWrapper.java 712866 2008-11-10 23:12:36Z hboutemy $
 */
public class ByteArrayOutputStreamWrapper extends AbstractWrapper implements Wrapper {

    /** serialVersionUID */
    static final long serialVersionUID = 3329037527245430610L;

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
