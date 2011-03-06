package net.jakobnielsen.aptivator.doxia;

import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.SinkFactory;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * Modified Utility class to play with Doxia objects.
 *
 * Original author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 */
public final class ConverterUtil {

    private ConverterUtil() {
        // Intentional
    }
    
    /**
     * @param plexus not null
     * @param format not null
     * @param supportedFormats not null
     * @return an instance of <code>Parser</code> depending on the format.
     * @throws ComponentLookupException if could not find the Parser for the given format.
     * @throws UnsupportedFormatException if the found parser is not instantiated.
     * @throws IllegalArgumentException if any parameter is null
     */
    public static Parser getParser(PlexusContainer plexus, String format, String[] supportedFormats)
            throws ComponentLookupException, UnsupportedFormatException {
        if (plexus == null) {
            throw new IllegalArgumentException("plexus is required");
        }
        if (format == null) {
            throw new IllegalArgumentException("format is required");
        }
        if (supportedFormats == null) {
            throw new IllegalArgumentException("supportedFormats is required");
        }

        Parser parser = null;
        for (String supportedFormat : supportedFormats) {
            if (format.equalsIgnoreCase(supportedFormat)) {
                parser = (Parser) plexus.lookup(Parser.ROLE, format);
            }
        }

        if (parser == null) {
            throw new UnsupportedFormatException(format, supportedFormats);
        }

        return parser;
    }

    /**
     * @param plexus not null
     * @param format not null
     * @param supportedFormats not null
     * @return an instance of <code>SinkFactory</code> depending on the given format.
     * @throws ComponentLookupException if could not find the SinkFactory for the given format.
     * @throws UnsupportedFormatException if the found sink is not instantiated.
     * @throws IllegalArgumentException if any parameter is null
     */
    public static SinkFactory getSinkFactory(PlexusContainer plexus, String format, String[] supportedFormats)
            throws ComponentLookupException, UnsupportedFormatException {
        if (plexus == null) {
            throw new IllegalArgumentException("plexus is required");
        }
        if (format == null) {
            throw new IllegalArgumentException("format is required");
        }
        if (supportedFormats == null) {
            throw new IllegalArgumentException("supportedFormats is required");
        }

        SinkFactory factory = null;
        for (String supportedFormat : supportedFormats) {
            if (format.equalsIgnoreCase(supportedFormat)) {
                factory = (SinkFactory) plexus.lookup(SinkFactory.ROLE, format);
            }
        }

        if (factory == null) {
            throw new UnsupportedFormatException(format, supportedFormats);
        }

        return factory;
    }
}
