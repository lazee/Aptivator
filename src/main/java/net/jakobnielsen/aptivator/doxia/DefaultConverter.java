package net.jakobnielsen.aptivator.doxia;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import net.jakobnielsen.aptivator.doxia.wrapper.InputFileWrapper;
import net.jakobnielsen.aptivator.doxia.wrapper.Wrapper;
import org.apache.log4j.Logger;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkFactory;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.XmlStreamReader;
import org.codehaus.plexus.util.xml.XmlUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;

/**
 * Modified implementation of the default <code>Converter</code>
 *
 * Original author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 */
public class DefaultConverter {

    private static Logger log = Logger.getLogger(DefaultConverter.class);

    private static final String APT_PARSER = "apt";

    /** Supported input format, i.e. supported Doxia parser */
    public static final String[] SUPPORTED_FROM_FORMAT = {APT_PARSER};

    public static final String XHTML_SINK = "xhtml";

    public static final String ITEXT_SINK = "itext";

    /** Supported output format, i.e. supported Doxia Sink */
    public static final String[] SUPPORTED_TO_FORMAT = {XHTML_SINK, ITEXT_SINK};

    /**
     * Returns a logger for this sink. If no logger has been configured, a new SystemStreamLog is returned.
     *
     * @return Log
     */
    protected Logger getLog() {
        return log;
    }

    /** {@inheritDoc} */
    public void convert(PlexusContainer plexus, InputFileWrapper input, Wrapper output, String outFormat)
            throws UnsupportedFormatException, ConverterException {
        if (input == null) {
            throw new IllegalArgumentException("input is required");
        }
        if (output == null) {
            throw new IllegalArgumentException("output is required");
        }

        try {
            Parser parser;
            try {
                parser = ConverterUtil.getParser(plexus, input.getFormat(), SUPPORTED_FROM_FORMAT);
            } catch (ComponentLookupException e) {
                throw new ConverterException("ComponentLookupException: " + e.getMessage(), e);
            }

            if (getLog().isDebugEnabled()) {
                getLog().debug("Parser used: " + parser.getClass().getName());
            }

            SinkFactory sinkFactory;
            try {
                sinkFactory = ConverterUtil.getSinkFactory(plexus, output.getFormat(), SUPPORTED_TO_FORMAT);
            } catch (ComponentLookupException e) {
                throw new ConverterException("ComponentLookupException: " + e.getMessage(), e);
            }

            Sink sink;
            try {
                sink = sinkFactory.createSink(output.getOutputStream(), output.getEncoding());
            } catch (IOException e) {
                throw new ConverterException("IOException: " + e.getMessage(), e);
            }
            if (getLog().isDebugEnabled()) {
                getLog().debug("Sink used: " + sink.getClass().getName());
            }

            parse(parser, input.getFormat(), new FileReader(input.getFile()), sink);
        } catch (FileNotFoundException ex) {
            log.error(ex);
        }
    }

    /**
     * @param parser      not null
     * @param inputFormat not null
     * @param reader      not null
     * @param sink        not null
     * @throws ConverterException if any
     */
    private void parse(Parser parser, String inputFormat, Reader reader, Sink sink)
            throws ConverterException {
        try {
            parser.parse(reader, sink);
        } catch (ParseException e) {
            throw new ConverterException(
                    "ParseException: " + e.getMessage() + ". Line:" + e.getLineNumber() + " Column: " +
                            e.getColumnNumber(), e);
        } finally {
            IOUtil.close(reader);
            sink.flush();
            sink.close();
        }
    }


    /**
     * @param f not null file
     * @return the detected encoding for f or <code>null</code> if not able to detect it.
     * @throws IllegalArgumentException      if f is not a file.
     * @throws UnsupportedOperationException if could not detect the file encoding.
     * @see {@link XmlStreamReader#getEncoding()} for xml files
     * @see {@link CharsetDetector#detect()} for text files
     */
    public static String autoDetectEncoding(File f) {
        if (!f.isFile()) {
            throw new IllegalArgumentException(
                    "The file '" + f.getAbsolutePath() + "' is not a file, could not detect encoding.");
        }

        Reader reader = null;
        InputStream is = null;
        try {
            if (XmlUtil.isXml(f)) {
                reader = ReaderFactory.newXmlReader(f);
                return ((XmlStreamReader) reader).getEncoding();
            }

            is = new BufferedInputStream(new FileInputStream(f));
            CharsetDetector detector = new CharsetDetector();
            detector.setText(is);
            CharsetMatch match = detector.detect();

            return match.getName().toUpperCase(Locale.ENGLISH);
        } catch (IOException e) {
            // nop
        } finally {
            IOUtil.close(reader);
            IOUtil.close(is);
        }

        StringBuffer msg = new StringBuffer();
        msg.append("Could not detect the encoding for file: ");
        msg.append(f.getAbsolutePath());
        msg.append("\n Specify explicitly the encoding.");
        throw new UnsupportedOperationException(msg.toString());
    }


}
