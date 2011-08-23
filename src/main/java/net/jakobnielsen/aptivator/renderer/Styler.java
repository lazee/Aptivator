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
package net.jakobnielsen.aptivator.renderer;

import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Add css to xhtml document.
 *
 * @author <a href="mailto:jakobnielsen@gmail.com">Jakob Vad Nielsen</a>
 */
public class Styler {

 private static Logger log = Logger.getLogger(Styler.class);

    public ByteArrayInputStream style(String xhtml) {
        return new ByteArrayInputStream(createOutputString(xhtml, getCss("maven-theme.css")).getBytes());
    }

    public String styleToString(String xhtml) {
        return createOutputString(xhtml, getCss("maven-theme.css"));
    }

    public ByteArrayInputStream styleFromFile(String xhtml, File css) {
        return new ByteArrayInputStream(createOutputString(xhtml, getCssFromFile(css)).getBytes());
    }

    public String styleToStringFromFile(String xhtml, File css) {
        return createOutputString(xhtml, getCssFromFile(css));
    }

    private String createOutputString(String xhtml, String themeCss) {
        String s = xhtml;
        String baseCss = getCss("maven-base.css");
        // This is really a hack, but this is the easiest way we can insert additional css in the document.
        s = s.replaceAll("<head>", "<head><style language='text/css'>" + baseCss + "</style>" +
                "<style language='text/css'>" + themeCss + "</style>");
        return s;
    }

    private String getCssFromFile(File css) {
        if (css == null || !css.exists() || !css.canRead()) {
            return "";
        }
        InputStream is = null;
        try {
            is = new FileInputStream(css);
            return getCssFromStream(is);
        } catch (FileNotFoundException e) {
            return "";
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                log.error(ex);
            }
        }
    }

    private String getCss(String cssFileName) {
        InputStream is = null;
        try {
            is = Styler.class.getResourceAsStream("/META-INF/" + cssFileName);
            return getCssFromStream(is);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                log.error(ex);
            }
        }
    }

    private String getCssFromStream(InputStream is) {
        try {
            if (is != null) {
                StringBuffer out = new StringBuffer();
                byte[] b = new byte[1000];
                int n = is.read(b);
                while (n != -1) {
                    out.append(new String(b, 0, n));
                    n = is.read(b);
                }
                return out.toString();
            }
            return "";
        } catch (IOException ex) {
            log.error(ex);
        }
        return "";
    }
}