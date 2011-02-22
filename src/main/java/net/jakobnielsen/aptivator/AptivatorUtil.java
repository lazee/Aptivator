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
package net.jakobnielsen.aptivator;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Utility methods
 *
 * @author <a href="mailto:jakobnielsen@gmail.com">Jakob Vad Nielsen</a>
 */
public class AptivatorUtil {

    private static Logger log = Logger.getLogger(AptivatorUtil.class);

    public static final String EXPORT_ICON = "001_53";

    public static final String REFRESH_ICON = "001_39";

    public static final String BROWSER_ICON = "001_40";

    private AptivatorUtil() {
        // Intentional
    }

    public static URL createImageUrl(String imageName) {
        String imgLocation = "/META-INF/icons/shadow/standart/png/24x24/" + imageName + ".png";
        return AptivatorUtil.class.getResource(imgLocation);
    }

    public static Icon createIcon(String name, String description) {
        return new ImageIcon(AptivatorUtil.createImageUrl(name), description);
    }

    public static String getMetaInfFile(String name) {
        InputStream is = null;
        try {
            is = AptivatorUtil.class.getResourceAsStream("/META-INF/" + name);
            if (is != null) {
                StringBuffer out = new StringBuffer();
                byte[] b = new byte[1000];
                for (int n; (n = is.read(b)) != -1;) {
                    out.append(new String(b, 0, n));
                }
                return out.toString();
            }
            return "";
        } catch (IOException ex) {
            log.error(ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                log.error(ex);
            }
        }
        return null;
    }

    public static Properties getMetaInfProperties(String name) {
        InputStream is = AptivatorUtil.class.getResourceAsStream("/META-INF/" + name);
        Properties p = new Properties();
        try {
            p.load(is);
        } catch (IOException e) {
            log.error(e);
            return null;
        }
        return p;
    }

    public static boolean isMacOSX() {
        String osName = System.getProperty("os.name");
        return osName.startsWith("Mac OS X");
    }


}
