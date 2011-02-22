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
package net.jakobnielsen.aptivator.cli;

import net.jakobnielsen.aptivator.Aptivator;
import org.apache.log4j.PropertyConfigurator;
import org.xhtmlrenderer.util.GeneralUtil;

import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;

/**
 * Aptivator client.
 *
 * @author <a href="mailto:jakobnielsen@gmail.com">Jakob Vad Nielsen</a>
 */
public class AptivatorCli implements Runnable {

    static {
        if (GeneralUtil.isMacOSX()) {
            try {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Aptivator");
                System.setProperty("apple.awt.antialiasing", "true");
                System.setProperty("apple.awt.textantialiasing", "true");
                System.setProperty("apple.awt.graphics.UseQuartz", "true");
                System.setProperty("apple.awt.rendering", "speed");
            } catch (Exception ex) {
                // Ignoring at the moment
            }
        }
    }

    private final String[] args;

    public AptivatorCli(String[] args) {
        this.args = args;
    }

    public void run() {
        Aptivator aptivator = new Aptivator();
        aptivator.setArgs(args);
        aptivator.run();
    }


    public static void main(String[] args) {
        PropertyConfigurator.configure(AptivatorCli.class.getResource("/META-INF/log4j.properties"));
        try {
            SwingUtilities.invokeAndWait(new AptivatorCli(args));
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}