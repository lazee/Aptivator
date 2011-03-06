package net.jakobnielsen.aptivator.doxia.wrapper;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * Modified Wrapper for an input file.
 *
 * Original author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 */
public final class InputFileWrapper extends AbstractFileWrapper {

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
