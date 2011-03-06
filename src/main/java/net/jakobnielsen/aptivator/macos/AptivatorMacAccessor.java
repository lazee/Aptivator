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

package net.jakobnielsen.aptivator.macos;

import com.apple.eawt.Application;

import java.awt.event.ActionListener;

public final class AptivatorMacAccessor {

    private AptivatorMacAccessor() {
        // Intentional
    }

    public static void activateOpenFileHandler(ActionListener actionListener) {
        Application app = Application.getApplication();
        app.setOpenFileHandler(new AptivatorOpenFilesHandler(actionListener));
    }

    public static void setMacSystemProperties() {
        System.setProperty("apple.laf.useScreenMenuBar", Boolean.TRUE.toString());
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Aptivator");
        System.setProperty("apple.awt.antialiasing", Boolean.TRUE.toString());
        System.setProperty("apple.awt.textantialiasing", Boolean.TRUE.toString());
        System.setProperty("apple.awt.graphics.UseQuartz", Boolean.TRUE.toString());
        System.setProperty("apple.awt.rendering", "speed");
    }
}
