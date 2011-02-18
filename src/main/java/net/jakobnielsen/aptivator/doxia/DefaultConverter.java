/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.jakobnielsen.aptivator.doxia;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.jakobnielsen.aptivator.doxia.wrapper.Wrapper;
import org.codehaus.plexus.util.xml.XmlUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.maven.doxia.logging.Log;
import org.apache.maven.doxia.logging.SystemStreamLog;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkFactory;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.XmlStreamReader;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import java.io.FileReader;

import net.jakobnielsen.aptivator.doxia.wrapper.InputFileWrapper;

/**
 * Default implementation of <code>Converter</code>
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id: DefaultConverter.java 712860 2008-11-10 22:54:37Z hboutemy $
 */
public class DefaultConverter {

    private static final String APT_PARSER = "apt";

    /**
     * Supported input format, i.e. supported Doxia parser
     */
    public static final String[] SUPPORTED_FROM_FORMAT = {APT_PARSER};

    public static final String XHTML_SINK = "xhtml";

    public static final String ITEXT_SINK = "itext";

    /**
     * Supported output format, i.e. supported Doxia Sink
     */
    public static final String[] SUPPORTED_TO_FORMAT = {XHTML_SINK, ITEXT_SINK};

   

    /**
     * Doxia logger
     */
    private Log log;

    ///**
    // * {@inheritDoc}
    // */
    //public void enableLogging(Log log) {
    //    this.log = log;
    //}

    /**
     * Returns a logger for this sink. If no logger has been configured, a new SystemStreamLog is returned.
     *
     * @return Log
     */
    protected Log getLog() {
        if (log == null) {
            log = new SystemStreamLog();
        }

        return log;
    }

//    /**
//     * {@inheritDoc}
//     */
//    public String[] getInputFormats() {
//        return SUPPORTED_FROM_FORMAT;
//    }

//    /**
//     * {@inheritDoc}
//     */
//    public String[] getOutputFormats() {
//        return SUPPORTED_TO_FORMAT;
//    }

    /**
     * {@inheritDoc}
     */
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
                parser.enableLogging(log);
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
            sink.enableLogging(log);

            if (getLog().isDebugEnabled()) {
                getLog().debug("Sink used: " + sink.getClass().getName());
            }

            parse(parser, input.getFormat(), new FileReader(input.getFile()), sink);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DefaultConverter.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    public void convert(PlexusContainer plexus, InputFileWrapper input, File outputDir, String name, String outFormat)
            throws UnsupportedFormatException, ConverterException {
        if (input == null) {
            throw new IllegalArgumentException("input is required");
        }
        if (outputDir == null) {
            throw new IllegalArgumentException("output is required");
        }
        if (name == null) {
            throw new IllegalArgumentException("filename is required");
        }



        try {
            Parser parser;
            try {
                parser = ConverterUtil.getParser(plexus, input.getFormat(), SUPPORTED_FROM_FORMAT);
                parser.enableLogging(log);
            } catch (ComponentLookupException e) {
                throw new ConverterException("ComponentLookupException: " + e.getMessage(), e);
            }

            if (getLog().isDebugEnabled()) {
                getLog().debug("Parser used: " + parser.getClass().getName());
            }

            SinkFactory sinkFactory;
            try {
                sinkFactory = ConverterUtil.getSinkFactory(plexus, outFormat, SUPPORTED_TO_FORMAT);
            } catch (ComponentLookupException e) {
                throw new ConverterException("ComponentLookupException: " + e.getMessage(), e);
            }

            Sink sink;
            try {
                sink = sinkFactory.createSink(outputDir, name);
            } catch (IOException e) {
                throw new ConverterException("IOException: " + e.getMessage(), e);
            }
            sink.enableLogging(log);

            if (getLog().isDebugEnabled()) {
                getLog().debug("Sink used: " + sink.getClass().getName());
            }

            parse(parser, input.getFormat(), new FileReader(input.getFile()), sink);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DefaultConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    /**
//     * {@inheritDoc}
//     */
//    public void setFormatOutput(boolean formatOutput) {
//        boolean formatOutput1 = formatOutput;
//    }

    /**
     * @param parser not null
     * @param inputFormat not null
     * @param reader not null
     * @param sink   not null
     * @throws ConverterException if any
     */
    private void parse(Parser parser, String inputFormat, Reader reader, Sink sink)
            throws ConverterException {
        try {
            parser.parse(reader, sink);
        } catch (ParseException e) {
            throw new ConverterException("ParseException: " + e.getMessage(), e);
        } finally {
            IOUtil.close(reader);
            sink.flush();
            sink.close();
        }
    }

    
    /**
     * @param f not null file
     * @return the detected encoding for f or <code>null</code> if not able to detect it.
     * @throws IllegalArgumentException if f is not a file.
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

//    /**
//     * Auto detect Doxia format for the given file depending: <ul> <li>the file name for TextMarkup based Doxia
//     * files</li> <li>the file content for XMLMarkup based Doxia files</li> </ul>
//     *
//     * @param f        not null file
//     * @param encoding a not null encoding.
//     * @return the detected encoding from f.
//     * @throws IllegalArgumentException if f is not a file.
//     * @throws UnsupportedOperationException if could not detect the Doxia format.
//     */
//    private static String autoDetectFormat(File f, String encoding) {
//        if (!f.isFile()) {
//            throw new IllegalArgumentException(
//                    "The file '" + f.getAbsolutePath() + "' is not a file, could not detect format.");
//        }
//
//        for (int i = 0; i < SUPPORTED_FROM_FORMAT.length; i++) {
//            String supportedFromFormat = SUPPORTED_FROM_FORMAT[i];
//
//            // Handle Doxia text files
//            if (supportedFromFormat.equalsIgnoreCase(APT_PARSER) && isDoxiaFileName(f, supportedFromFormat)) {
//                return supportedFromFormat;
//            }
//
//            // Handle Doxia xml files
//            String firstTag = getFirstTag(f);
//            if (firstTag == null) {
//                continue;
//            }
//        }
//
//        StringBuffer msg = new StringBuffer();
//        msg.append("Could not detect the Doxia format for file: ");
//        msg.append(f.getAbsolutePath());
//        msg.append("\n Specify explicitly the Doxia format.");
//        throw new UnsupportedOperationException(msg.toString());
//    }

//    /**
//     * @param f      not null
//     * @param format could be null
//     * @return <code>true</code> if the file name computes the format.
//     */
//    private static boolean isDoxiaFileName(File f, String format) {
//        if (f == null) {
//            throw new IllegalArgumentException("f is required.");
//        }
//
//        Pattern pattern = Pattern.compile("(.*?)\\." + format.toLowerCase(Locale.ENGLISH) + "$");
//        Matcher matcher = pattern.matcher(f.getTitle().toLowerCase(Locale.ENGLISH));
//
//        return matcher.matches();
//    }

//    /**
//     * @param xmlFile not null and should be a file.
//     * @return the first tag name if found, <code>null</code> in other case.
//     */
//    private static String getFirstTag(File xmlFile) {
//        if (xmlFile == null) {
//            throw new IllegalArgumentException("xmlFile is required.");
//        }
//        if (!xmlFile.isFile()) {
//            throw new IllegalArgumentException("The file '" + xmlFile.getAbsolutePath() + "' is not a file.");
//        }
//
//        Reader reader = null;
//        try {
//            reader = ReaderFactory.newXmlReader(xmlFile);
//            XmlPullParser parser = new MXParser();
//            parser.setInput(reader);
//            int eventType = parser.getEventType();
//            while (eventType != XmlPullParser.END_DOCUMENT) {
//                if (eventType == XmlPullParser.START_TAG) {
//                    return parser.getTitle();
//                }
//                eventType = parser.nextToken();
//            }
//        } catch (FileNotFoundException e) {
//            return null;
//        } catch (XmlPullParserException e) {
//            return null;
//        } catch (IOException e) {
//            return null;
//        } finally {
//            IOUtil.close(reader);
//        }
//
//        return null;
//    }
}
