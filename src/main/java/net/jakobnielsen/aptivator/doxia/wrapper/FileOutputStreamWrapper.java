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
