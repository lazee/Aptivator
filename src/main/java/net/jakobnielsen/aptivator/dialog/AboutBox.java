/*
 * Copyright 2008 Jakob Vad Nielsen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.jakobnielsen.aptivator.dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AboutBox extends JDialog {

    public AboutBox(Frame owner) {
        super(owner, "Aptivator", true);

        KeyListener keyListener = new KeyListener() {

            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                setVisible(false);
            }

            public void keyReleased(KeyEvent e) {

            }
        };

        JLabel imageLabel = new JLabel();
        imageLabel.setIcon(new ImageIcon(AboutBox.class.getResource("/META-INF/icons/aptivator.png")));
        imageLabel.setPreferredSize(new Dimension(650, 180));
        imageLabel.setFocusable(false);
        imageLabel.addMouseListener(
                new MouseListener() {
                    public void mouseClicked(MouseEvent e) {
                        setVisible(false);
                    }

                    public void mousePressed(MouseEvent e) {
                    }

                    public void mouseReleased(MouseEvent e) {
                    }

                    public void mouseEntered(MouseEvent e) {
                    }

                    public void mouseExited(MouseEvent e) {
                    }
                }
        );
        imageLabel.addKeyListener(keyListener);

        String message = "Simple APT (Almost Plain Text) Viewer by Jakob Vad Nielsen\n";

        message += "\nAptivator homepage : http://aptivator.jakobnielsen.net\n";

        message += "\nJDK: " + System.getProperty("java.version");
        message += "\nVM: " + System.getProperty("java.vm.name");
        message += "\nVendor: " + System.getProperty("java.vendor");


        // Licenses
        JTextArea licensesTxt = new JTextArea(getLicenses());
        licensesTxt.setFont(new Font("Courier", Font.BOLD, 12));
        licensesTxt.setForeground(Color.BLACK);
        licensesTxt.setBackground(Color.WHITE);
        licensesTxt.setEditable(false);
        licensesTxt.addKeyListener(keyListener);
        JScrollPane licensesScroll = new JScrollPane(licensesTxt);
        licensesScroll.setPreferredSize(new Dimension(650, 300));
        licensesScroll.addKeyListener(keyListener);

        // License
        JTextArea licenceTxt = new JTextArea(getLicense());
        licenceTxt.setFont(new Font("Courier", Font.BOLD, 12));
        licenceTxt.setForeground(Color.BLACK);
        licenceTxt.setBackground(Color.WHITE);
        licenceTxt.setEditable(false);
        licenceTxt.addKeyListener(keyListener);
        JScrollPane licenseScroll = new JScrollPane(licenceTxt);
        licenseScroll.setPreferredSize(new Dimension(650, 300));
        licenseScroll.addKeyListener(keyListener);

        // System info
        JTextArea infoTxt = new JTextArea(message);
        infoTxt.setFont(new Font("Courier", Font.BOLD, 12));
        infoTxt.setForeground(Color.BLACK);
        infoTxt.setBackground(Color.WHITE);
        infoTxt.setEditable(false);
        infoTxt.addKeyListener(keyListener);
        JScrollPane infoScroll = new JScrollPane(infoTxt);
        infoScroll.setPreferredSize(new Dimension(650, 300));
        infoScroll.addKeyListener(keyListener);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("General", null, infoScroll, "");
        tabs.addTab("License", null, licenseScroll, "");
        tabs.addTab("Third-party licenses", null, licensesScroll, "");
        tabs.addKeyListener(keyListener);
        tabs.setTabPlacement(JTabbedPane.BOTTOM);

        getContentPane().add(imageLabel, BorderLayout.NORTH);
        getContentPane().add(tabs, BorderLayout.CENTER);
        getContentPane().addKeyListener(keyListener);

        pack();
        setResizable(false);
    }

    private String getLicenses() {
        return getFile("licenses.txt");
    }

    private String getLicense() {
        return getFile("license.txt");
    }

    private String getFile(String name) {
        InputStream is = null;
        try {
            is = AboutBox.class.getResourceAsStream("/META-INF/" + name);
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
            Logger.getLogger(AboutBox.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(AboutBox.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "";
    }
}