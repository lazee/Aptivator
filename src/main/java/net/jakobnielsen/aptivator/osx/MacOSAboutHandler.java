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

package net.jakobnielsen.aptivator.osx;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import net.jakobnielsen.aptivator.SwingTools;
import net.jakobnielsen.aptivator.dialog.AboutBox;

import javax.swing.JFrame;

/**
 * MacOS helper
 *
 * @author <a href="mailto:jakobnielsen@gmail.com">Jakob Vad Nielsen</a>
 */
public class MacOSAboutHandler extends Application {

    public MacOSAboutHandler() {
        addApplicationListener(new AboutBoxHandler());
    }

    class AboutBoxHandler extends ApplicationAdapter {

        public void handleAbout(ApplicationEvent event) {
            AboutBox aboutBox = new AboutBox(new JFrame());
            aboutBox.pack();
            SwingTools.locateOnScreen(aboutBox);
            aboutBox.setVisible(true);
        }
    }
}