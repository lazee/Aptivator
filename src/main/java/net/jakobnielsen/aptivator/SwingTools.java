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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Internal swing helper class.
 *
 * @author <a href="mailto:jakobnielsen@gmail.com">Jakob Vad Nielsen</a>
 */
public final class SwingTools {

    private SwingTools() {
        // Intentional
    }
    
    /**
     * Center frame on screen.
     *
     * @param frame The Frame to be centered.
     */
    public static void locateOnScreen(Frame frame) {
        Dimension paneSize = frame.getSize();
        Dimension screenSize = frame.getToolkit().getScreenSize();
        frame.setLocation(
                (screenSize.width - paneSize.width) / 2,
                (screenSize.height - paneSize.height) / 2);
    }

    /**
     * Center dialog box on screen.
     *
     * @param frame The JDialog to be centered.
     */
    public static void locateOnScreen(JDialog frame) {
        Dimension paneSize = frame.getSize();
        Dimension screenSize = frame.getToolkit().getScreenSize();
        frame.setLocation(
                (screenSize.width - paneSize.width) / 2,
                (screenSize.height - paneSize.height) / 2);
    }

    public static JMenuItem createMenuItem(ActionListener listener, String text, String id) {
        return new JMenuItem(new MenuAction(listener, text, id));
    }
}
