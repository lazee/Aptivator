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

package net.jakobnielsen.aptivator.settings;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import net.jakobnielsen.aptivator.dialog.AptivatorFileChooser;
import net.jakobnielsen.aptivator.dialog.ErrorBox;
import net.jakobnielsen.aptivator.settings.entities.StyleSheet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;

/**
 * Stylesheet dialog
 *
 * @author <a href="mailto:jakobnielsen@gmail.com">Jakob Vad Nielsen</a>
 */
public class StylesheetDialog extends JDialog {

    private JTextField titleField = new JTextField();

    private JTextField pathField = new JTextField();

    private JButton selectButton = new JButton("\u2026");

    private JButton okButton;

    private JButton cancelButton;

    private String action = null;

    private StyleSheet styleSheet;

    private ResourceBundle rb;

    public StylesheetDialog(Frame owner, StyleSheet styleSheet, ResourceBundle rb) {
        super(owner, rb.getString("text.stylesheet"), true);

        this.rb = rb;
        this.styleSheet = styleSheet;

        okButton = new JButton(rb.getString("text.ok"));
        cancelButton = new JButton(rb.getString("text.cancel"));
        
        JPanel mainPanel = buildMainPanel();

        selectButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser fileChooser = new AptivatorFileChooser("css");
                        fileChooser.showOpenDialog(new JFrame());
                        if (fileChooser.getSelectedFile() != null) {
                            pathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                        }
                    }
                }
        );

        cancelButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                        //dispose();
                        action = "FALSE";
                    }
                }
        );

        okButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (isValidTitle() && isValidFilePath()) {
                            // FIXME Validation
                            storeStyleSheetInObject();
                            setVisible(false);
                            //dispose();
                            action = "OK";
                        }
                    }
                }
        );

        if (styleSheet != null) {
            if (styleSheet.getTitle() != null) {
                titleField.setText(styleSheet.getTitle());
            }
            if (styleSheet.getSrcFile() != null) {
                pathField.setText(styleSheet.getSrcFile().getAbsolutePath());
            }
        }

        getContentPane().add(mainPanel);

        pack();
        setResizable(false);
    }

    private boolean isValidTitle() {
        if (titleField != null && !"".equals(titleField.getText().trim())) {
            return true;
        }
        ErrorBox.show(rb.getString("error.stylesheet.title"), rb.getString("error"));
        return false;
    }

    private boolean isValidFilePath() {
        if (pathField != null && !"".equals(pathField.getText().trim())) {
            File f = new File(pathField.getText());
            if (f.exists()) {
                return true;
            }
            ErrorBox.show(rb.getString("error.file.missing"),  rb.getString("error"));
            return false;
        }
        ErrorBox.show(rb.getString("error.stylesheet.path"), rb.getString("error"));
        return false;
    }

    private void storeStyleSheetInObject() {
        if (styleSheet == null) {
            styleSheet = new StyleSheet();
        }

        styleSheet.setTitle(titleField.getText().trim());
        styleSheet.setSrcFile(new File(pathField.getText()));
    }

    public String getAction() {
        return action;
    }

    public StyleSheet getStyleSheet() {
        return styleSheet;
    }

    private JPanel buildPanel() {
        FormLayout layout = new FormLayout(
                "pref, 3dlu, 35dlu, 2dlu, 35dlu, 2dlu, 35dlu, 2dlu, 35dlu",
                "2*(p, 2dlu), p");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();
        builder.add(new JLabel(rb.getString("text.title") + ":"), cc.xy(1, 1));
        builder.add(titleField, cc.xyw(3, 1, 7));
        builder.add(new JLabel(rb.getString("text.location") + ":"), cc.xy(1, 3));
        builder.add(pathField, cc.xyw(3, 3, 5));
        builder.add(selectButton, cc.xy(9, 3));
        return builder.getPanel();
    }

    private JPanel buildMainPanel() {
        FormLayout layout = new FormLayout(
                "d:grow, 50dlu, 50dlu",
                "p, 4dlu, p");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();
        builder.add(buildPanel(), cc.xyw(1, 1, 3));
        builder.add(okButton, cc.xy(2, 3));
        builder.add(cancelButton, cc.xy(3, 3));
        return builder.getPanel();
    }

}
