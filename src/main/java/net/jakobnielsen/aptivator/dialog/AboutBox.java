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
package net.jakobnielsen.aptivator.dialog;

import net.jakobnielsen.aptivator.AptivatorUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ResourceBundle;

/**
 * About box
 *
 * @author <a href="mailto:jakobnielsen@gmail.com">Jakob Vad Nielsen</a>
 */
public class AboutBox extends JDialog {

    public AboutBox(Frame owner, ResourceBundle rb) {
        super(owner, rb.getString("app.title"), true);

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

        StringBuilder sb = new StringBuilder();
        sb.append(rb.getString("app.title.long")).append("\n\n");
        sb.append(rb.getString("app.homepage")).append("\n\n");
        sb.append("JDK: ").append(System.getProperty("java.version")).append("\n");
        sb.append("VM: ").append(System.getProperty("java.vm.name")).append("\n");
        sb.append("Vendor: ").append(System.getProperty("java.vendor")).append("\n");

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
        JTextArea infoTxt = new JTextArea(sb.toString());
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
        tabs.addTab(rb.getString("menu.general"), null, infoScroll, "");
        tabs.addTab(rb.getString("menu.license"), null, licenseScroll, "");
        tabs.addTab(rb.getString("menu.licenses"), null, licensesScroll, "");
        tabs.addKeyListener(keyListener);
        tabs.setTabPlacement(JTabbedPane.BOTTOM);

        getContentPane().add(imageLabel, BorderLayout.NORTH);
        getContentPane().add(tabs, BorderLayout.CENTER);
        getContentPane().addKeyListener(keyListener);

        pack();
        setResizable(false);
    }

    private String getLicenses() {
        return getNotNullFile("licenses.txt");
    }

    private String getLicense() {
        return getNotNullFile("license.txt");
    }

    private String getNotNullFile(String f) {
        String s = AptivatorUtil.getMetaInfFile(f);
        return (s == null ? "" : s);
    }

   
}