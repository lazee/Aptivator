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
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import net.jakobnielsen.aptivator.SwingTools;
import net.jakobnielsen.aptivator.settings.entities.Settings;
import net.jakobnielsen.aptivator.settings.entities.StyleSheet;
import net.jakobnielsen.aptivator.settings.entities.Stylesheets;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

/**
 * Settings dialog
 *
 * @author <a href="mailto:jakobnielsen@gmail.com">Jakob Vad Nielsen</a>
 */
public class SettingsDialog extends JDialog {

    private boolean save = false;

    private final JTextField f1;

    private JList stylesheetList;

    private DefaultListModel listModel;

    private Settings settings;

    private ResourceBundle rb;

    public SettingsDialog(Frame owner, final Settings settings, ResourceBundle rb) {
        super(owner, rb.getString("menu.settings"), true);
        this.rb = rb;
        setMinimumSize(new Dimension(300, 300));
        this.settings = settings;
        getContentPane().setPreferredSize(new Dimension(500, 350));
        f1 = new JTextField("" + settings.getRefreshInterval(), 4);
        getContentPane().add(createPreferences());
        JPanel bottomPanel = new JPanel();
        /*-- Ok button --*/
        JButton btOK = new JButton(rb.getString("text.ok"));
        ActionListener lst = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                save = true;

                setVisible(false);
            }
        };
        btOK.addActionListener(lst);
        bottomPanel.add(btOK, BorderLayout.EAST);
        /*-- cancel button --*/
        JButton btCancel = new JButton(rb.getString("text.cancel"));
        lst = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        };
        btCancel.addActionListener(lst);
        bottomPanel.add(btCancel, BorderLayout.WEST);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        pack();
        //setResizable(false);

        listModel.clear();
        for (StyleSheet s : settings.getStylesheets().getStylesheets()) {
            listModel.addElement(s);
        }
    }

    public Settings getSettings() {
        settings.setStylesheets(Stylesheets.fromListModel(listModel));
        return settings;
    }

    public boolean isSave() {
        return save;
    }

    private JComponent createPreferences() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty("jgoodies.noContentBorder", Boolean.TRUE);
        tabbedPane.add(rb.getString("menu.general"), buildGeneralPanel());
        tabbedPane.add(rb.getString("menu.stylesheets"), buildStyleSheetPanel());
        return tabbedPane;
    }

    private JComponent buildGeneralPanel() {

        FormLayout layout = new FormLayout(
                "pref, 4dlu, pref", // columns
                "pref, 2dlu, pref, 2dlu, pref"); // rows

        layout.setRowGroups(new int[][]{{1, 3, 5}});

        JPanel panel = new JPanel(layout);
        panel.setBorder(Borders.DIALOG_BORDER);

        CellConstraints cc = new CellConstraints();
        panel.add(new JLabel(rb.getString("form.field.interval")), cc.xy(1, 1));

        panel.add(f1, cc.xy(3, 1));

        return panel;
    }

    private JComponent buildStyleSheetPanel() {
        FormLayout formLayout = new FormLayout(
                // Columns
                "fill:default:grow, left:4dlu:noGrow, fill:50dlu:noGrow",
                // Rows
                "center:d:noGrow,top:3dlu:noGrow,center:d:grow");

        // Label
        final JLabel mainLabel = new JLabel();
        mainLabel.setText(rb.getString("form.field.stylesheets"));
        CellConstraints cc = new CellConstraints();

        // List
        listModel = new DefaultListModel();
        stylesheetList = new JList(listModel);
        stylesheetList.setSelectionMode(0);

        StyleSheetRenderer renderer = new StyleSheetRenderer();
        stylesheetList.setCellRenderer(renderer);

        JScrollPane stylesheetScroll = new JScrollPane(stylesheetList);

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FormLayout(
                // Columns
                "fill:default:grow",
                // Rows
                "center:default:noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow"));

        // Buttons
        JButton addButton = new JButton();
        addButton.setText(rb.getString("text.add"));
        buttonPanel.add(addButton, cc.xy(1, 1));
        addButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        StylesheetDialog sd = new StylesheetDialog(new JFrame(), null, rb);
                        sd.pack();
                        SwingTools.locateOnScreen(sd);
                        sd.setVisible(true);

                        if (rb.getString("text.ok").equals(sd.getAction())) {
                            listModel.addElement(sd.getStyleSheet());
                        }

                    }
                }
        );

        JButton editButton = new JButton();
        editButton.setText(rb.getString("text.edit"));
        buttonPanel.add(editButton, cc.xy(1, 3));
        editButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        if (stylesheetList.getSelectedIndex() > -1) {
                            int idx = stylesheetList.getSelectedIndex();
                            StyleSheet ss = (StyleSheet) listModel.getElementAt(idx);
                            StylesheetDialog sd = new StylesheetDialog(new JFrame(), ss, rb);
                            sd.pack();
                            SwingTools.locateOnScreen(sd);
                            sd.setVisible(true);

                            if (rb.getString("text.ok").equals(sd.getAction())) {
                                listModel.remove(idx);
                                listModel.add(idx, sd.getStyleSheet());
                            }
                        }

                    }
                }
        );

        JButton removeButton = new JButton();
        removeButton.setText(rb.getString("text.remove"));
        buttonPanel.add(removeButton, cc.xy(1, 5));
        removeButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        if (stylesheetList.getSelectedIndex() > -1) {
                            listModel.remove(stylesheetList.getSelectedIndex());
                        }
                    }
                }
        );

        // Main code
        PanelBuilder builder = new PanelBuilder(formLayout);
        builder.setDefaultDialogBorder();

        builder.add(mainLabel, cc.xy(1, 1));
        builder.add(stylesheetScroll, cc.xy(1, 3, CellConstraints.DEFAULT, CellConstraints.FILL));
        builder.add(buttonPanel, cc.xy(3, 3, CellConstraints.DEFAULT, CellConstraints.FILL));

        return builder.getPanel();
    }

    class StyleSheetRenderer extends JLabel implements ListCellRenderer {

        public StyleSheetRenderer() {
            setOpaque(true);
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(Color.WHITE);
                setForeground(list.getForeground());
            }

            StyleSheet s = (StyleSheet) listModel.getElementAt(index);

            setText(s.getTitle());
            setToolTipText(s.getSrcFile().getAbsolutePath());
            return this;
        }
    }

}
