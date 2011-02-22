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
package net.jakobnielsen.aptivator.settings.dao;

import net.jakobnielsen.aptivator.AptivatorUtil;
import net.jakobnielsen.aptivator.settings.entities.Settings;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Settings DAO implementation that stores the settings in the users home directory.
 *
 * @author <a href="mailto:jakobnielsen@gmail.com">Jakob Vad Nielsen</a>
 */
public class SettingsDaoProperties implements SettingsDao {

    private static final String propertiesDirName = ".aptivator";

    private static final String propertiesFileName = "settings.properties";

    private static final String stylesheetsFileName = "stylesheets.cfg";

    private static final String AUTO_REFRESH_INTERVAL = "auto.refresh.interval";

    private static final String APP_SIZE_WIDTH = "app.size.width";

    private static final String APP_SIZE_HEIGHT = "app.size.height";

    private static final String APP_VERSION = "app.version";

    private static final String RECENT_FILES = "recent.files";

    private static final String APP_PROPERTIES_FILE = "aptivator.properties";

    private File settingsDir;

    Logger log = Logger.getLogger(SettingsDaoProperties.class);

    public SettingsDaoProperties() {
        settingsDir = new File(System.getProperty("user.home") + File.separator + propertiesDirName);
        if (settingsDir.isFile()) {
            log.debug("Deleting " + settingsDir.getAbsolutePath() + ".");
            if (!settingsDir.delete()) {
                log.debug("Could not delete : " + settingsDir.getAbsolutePath());
            }
        }

        if (!settingsDir.exists()) {
            log.debug("Creating properties directory: " + settingsDir.getAbsolutePath());
            if (!settingsDir.mkdir()) {
                log.error("Could not create : " + settingsDir.getAbsolutePath());
            }
        }
    }

    public Settings getSettings() {
        log.trace("getSettings() executed");
        try {
            Settings s = convertPropertiesToSettings(getProperties());
            s.getStylesheets().loadFromFile(getStylesheetsFile());
            return s;
        } catch (IOException ex) {
            return new Settings();
        }
    }

    public void setSettings(Settings settings) {
        if (settings == null) {
            return;
        }
        storeProperties(convertSettingsToProperties(settings));
        settings.getStylesheets().store(getStylesheetsFile());
    }

    private Properties convertSettingsToProperties(Settings settings) {
        Properties properties = new Properties();
        properties.setProperty(AUTO_REFRESH_INTERVAL, "" + settings.getRefreshInterval());
        properties.setProperty(APP_SIZE_WIDTH, "" + settings.getAppSize().width);
        properties.setProperty(APP_SIZE_HEIGHT, "" + settings.getAppSize().height);
        properties.setProperty(RECENT_FILES, createListStr(settings.getRecentFiles().getRecentFiles()));
        return properties;
    }

    private Settings convertPropertiesToSettings(Properties properties) {
        Settings settings = new Settings();

        if (properties.containsKey(AUTO_REFRESH_INTERVAL)) {
            settings.setRefreshInterval(Integer.parseInt(properties.getProperty(AUTO_REFRESH_INTERVAL)));
        }
        if (properties.containsKey(APP_SIZE_WIDTH) && properties.containsKey(APP_SIZE_HEIGHT)) {
            Dimension d = new Dimension();
            d.setSize(Integer.parseInt(properties.getProperty(APP_SIZE_WIDTH).trim()),
                    Integer.parseInt(properties.getProperty(APP_SIZE_HEIGHT).trim()));
            settings.setAppSize(d);
        }
        if (properties.containsKey(RECENT_FILES)) {
            settings.getRecentFiles().setRecentFiles(splitList(properties.getProperty(RECENT_FILES)));
        }
        if (properties.getProperty(APP_VERSION) != null) {
            settings.setAppVersion(properties.getProperty(APP_VERSION));
        }
        return settings;
    }

    protected List<String> splitList(String str) {
        List<String> l = new ArrayList<String>();
        String s = str;
        if (s.startsWith("{")) {
            s = s.substring(1);
        }
        if (str.endsWith("}")) {
            s = s.substring(0, s.length() - 1);
        }
        StringTokenizer st = new StringTokenizer(s, "; ");
        while (st.hasMoreTokens()) {
            l.add(st.nextToken());
        }
        return l;
    }

    protected String createListStr(List<File> lst) {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        boolean first = true;
        for (File f : lst) {
            if (!first) {
                sb.append("; ");
            }
            sb.append(f.getAbsolutePath());

            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    private void storeProperties(Properties properties) {
        try {
            properties.store(new FileOutputStream(getSettingsFile()), "Aptivator Settings");
        } catch (IOException ex) {
            log.error("Got IOException: ", ex);
        }
    }

    private Properties getProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(getSettingsFile()));
        Properties staticProperties = AptivatorUtil.getMetaInfProperties(APP_PROPERTIES_FILE);
        if (staticProperties != null) {
            if (staticProperties.getProperty(APP_VERSION) != null &&
                    !"PROJECT_VERSION".equals(staticProperties.getProperty(APP_VERSION))) {
                properties.put(APP_VERSION, staticProperties.getProperty(APP_VERSION));
            } else {
                log.warn("No app version in properties file");
            }
        } else {
            log.warn("Could not load app properties file");
        }
        return properties;
    }

    private File getStylesheetsFile() {
        return new File(settingsDir, File.separator + stylesheetsFileName);
    }

    private File getSettingsFile() {
        return new File(settingsDir, File.separator + propertiesFileName);
    }
}
