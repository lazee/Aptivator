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

import org.codehaus.plexus.util.StringUtils;

import java.io.FileOutputStream;

public class FileOutputStreamWrapper extends AbstractWrapper implements Wrapper {

    private FileOutputStream out;

    private String encoding;

    private FileOutputStreamWrapper(FileOutputStream out, String format, String encoding, String[] supportedFormat) {
        super(format, supportedFormat);

        if (getFormat().equalsIgnoreCase(AUTO_FORMAT)) {
            throw new IllegalArgumentException("output format is required");
        }

        this.out = out;
        this.encoding = encoding;
    }

    public FileOutputStream getOutputStream() {
        return this.out;
    }

    public String getEncoding() {
        return encoding;
    }

    public static FileOutputStreamWrapper valueOf(FileOutputStream out, String format, String encoding, String[] supportedFormat)
            throws IllegalArgumentException {
        if (out == null) {
            throw new IllegalArgumentException("output writer is required");
        }
        if (StringUtils.isEmpty(format)) {
            throw new IllegalArgumentException("output format is required");
        }

        return new FileOutputStreamWrapper(out, format, encoding, supportedFormat);
    }
}
