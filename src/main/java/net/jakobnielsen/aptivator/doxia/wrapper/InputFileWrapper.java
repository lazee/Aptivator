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

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * Wrapper for an input file.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id: InputFileWrapper.java 697523 2008-09-21 14:29:09Z vsiveton $
 */
public class InputFileWrapper extends AbstractFileWrapper {

    /**
     * Private constructor. a
     *
     * @param absolutePath    not null
     * @param format          could be null
     * @param charsetName     could be null
     * @param supportedFormat not null
     * @throws IllegalArgumentException if the file doesn't exist.
     * @throws UnsupportedEncodingException if the encoding is unsupported.
     * @throws FileNotFoundException if the file for absolutePath is not found.
     */
    private InputFileWrapper(String absolutePath, String format, String charsetName, String[] supportedFormat)
            throws IllegalArgumentException, UnsupportedEncodingException, FileNotFoundException {
        super(absolutePath, format, charsetName, supportedFormat);

        if (!getFile().exists()) {
            throw new FileNotFoundException("The file '" + getFile().getAbsolutePath() + "' doesn't exist.");
        }
    }

    /**
     * @param absolutePath    for a file or a directory not null.
     * @param format          could be null
     * @param supportedFormat not null
     * @return a type safe input reader
     * @throws IllegalArgumentException if the file doesn't exist.
     * @throws UnsupportedEncodingException if the encoding is unsupported.
     * @throws FileNotFoundException if the file for absolutePath is not found.
     * @see #valueOf(String, String, String, String[]) using AUTO_FORMAT
     */
    public static InputFileWrapper valueOf(String absolutePath, String format, String[] supportedFormat)
            throws IllegalArgumentException, UnsupportedEncodingException, FileNotFoundException {
        return valueOf(absolutePath, format, AUTO_FORMAT, supportedFormat);
    }

    /**
     * @param absolutePath    for a wanted file or a wanted directory, not null.
     * @param format          could be null
     * @param charsetName     could be null
     * @param supportedFormat not null
     * @return a type safe input reader
     * @throws IllegalArgumentException if the file doesn't exist.
     * @throws UnsupportedEncodingException if the encoding is unsupported.
     * @throws FileNotFoundException if the file for absolutePath is not found.
     */
    public static InputFileWrapper valueOf(String absolutePath, String format, String charsetName,
            String[] supportedFormat)
            throws IllegalArgumentException, UnsupportedEncodingException, FileNotFoundException {
        return new InputFileWrapper(absolutePath, format, charsetName, supportedFormat);
    }
}
