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
package net.jakobnielsen.aptivator.desktop;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Wrapper class around the Java Desktop API introduced in Java 6
 */
public class DesktopUtil {

    private DesktopUtil() {
        // Intentional
    }

    private static boolean isDesktop() {
        return Desktop.isDesktopSupported();
    }

    private static Desktop getDesktop() {
        if (isDesktop()) {
            return Desktop.getDesktop();
        }
        return null;
    }

    public static boolean openUrl(String url) {
        try {
            return openUrl(new URL(url));
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static boolean openUrl(URL url) {
        if (isDesktop() && getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                getDesktop().browse(url.toURI());
                return true;
            } catch (IOException e) {
                return false;
            } catch (URISyntaxException e) {
                return false;
            }
        }
        return false;
    }

    public static boolean openFile(File file) {
        if (isDesktop() && getDesktop().isSupported(Desktop.Action.OPEN)) {
            try {
                getDesktop().open(file);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public static boolean printFile(File file) {
        if (isDesktop() && getDesktop().isSupported(Desktop.Action.PRINT)) {
            try {
                getDesktop().print(file);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public static boolean openMail(String mailto) {
        try {
            return openMail(new URI(mailto));
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public static boolean openMail(URI mailto) {
        if (isDesktop() && getDesktop().isSupported(Desktop.Action.MAIL)) {
            try {
                getDesktop().mail(mailto);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }
}
