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
package net.jakobnielsen.aptivator;

import com.lowagie.text.DocumentException;
import net.jakobnielsen.aptivator.desktop.DesktopUtil;
import net.jakobnielsen.aptivator.dialog.ErrorBox;
import net.jakobnielsen.aptivator.dialog.InfoBox;
import net.jakobnielsen.aptivator.doxia.ConverterException;
import net.jakobnielsen.aptivator.doxia.DefaultConverter;
import net.jakobnielsen.aptivator.doxia.UnsupportedFormatException;
import net.jakobnielsen.aptivator.doxia.wrapper.ByteArrayOutputStreamWrapper;
import net.jakobnielsen.aptivator.doxia.wrapper.InputFileWrapper;
import net.jakobnielsen.aptivator.renderer.AptivatorScrollPane;
import net.jakobnielsen.aptivator.renderer.Styler;
import net.jakobnielsen.aptivator.settings.entities.StyleSheet;
import net.jakobnielsen.aptivator.settings.entities.Stylesheets;
import org.codehaus.plexus.PlexusContainer;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.swing.BasicPanel;
import org.xhtmlrenderer.swing.FSMouseListener;
import org.xhtmlrenderer.swing.LinkListener;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author jakob
 */
public class AptivatorDocument {

    private XHTMLPanel xHTMLPanel;

    private Styler styler;

    private File file;

    private StyleSheet activeStyleSheet;

    private StyleSheet defaultStylesheet;

    DefaultComboBoxModel stylesheetsModel = new DefaultComboBoxModel();

    private JComboBox stylesheetCombo;

    private PlexusContainer plexus;

    public AptivatorDocument(PlexusContainer plexus) {
        this.plexus = plexus;
        this.styler = new Styler();
        defaultStylesheet = new StyleSheet();
        defaultStylesheet.setTitle("Default stylesheet");
    }

    protected void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setStylesheets(Stylesheets stylesheets) {
        stylesheetsModel.removeAllElements();
        stylesheetsModel.addElement(defaultStylesheet);
        for (StyleSheet s : stylesheets.getStylesheets()) {
            stylesheetsModel.addElement(s);
        }
        if (activeStyleSheet != null) {
            stylesheetCombo.setSelectedItem(activeStyleSheet);
        }
    }

    public void createXHtmlPanel() {
        xHTMLPanel = new XHTMLPanel();
        xHTMLPanel.setFocusable(true);
        xHTMLPanel.getSharedContext().getTextRenderer().setSmoothingThreshold(8);
        List l = xHTMLPanel.getMouseTrackingListeners();

        for (Object aL : l) {
            FSMouseListener listener = (FSMouseListener) aL;
            if (listener instanceof LinkListener) {
                xHTMLPanel.removeMouseTrackingListener(listener);
            }
        }

        xHTMLPanel.addMouseTrackingListener(new LinkListener() {
            @Override
            public void linkClicked(BasicPanel panel, String uri) {
                if (uri.startsWith("http://")) {
                    if (!DesktopUtil.openUrl(uri)) {
                        ErrorBox.show("Could not open url in external browser");
                    }
                } else if (uri.startsWith("mailto:")) {
                    if (!DesktopUtil.openMail(uri)) {
                        ErrorBox.show("Could not open default mail program");
                    }
                } else {
                    InfoBox.show("Internal links not supported yet: " + uri);
                }
            }
        });

    }

    private XHTMLPanel getXHTMLPanel() {
        return xHTMLPanel;
    }

    public JComponent buildContentPane(ActionListener listener) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buildToolbar(listener), BorderLayout.PAGE_START);
        panel.add(buildMainPanel(), BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildToolbar(ActionListener listener) {
        JToolBar toolBar = new JToolBar("Still draggable");
        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        JButton refreshButton = makeButton(AptivatorUtil.REFRESH_ICON, "Refresh", "Refresh document", "Refresh", true);
        refreshButton.addActionListener(listener);
        toolBar.add(refreshButton);

        JButton exportButton = makeButton(AptivatorUtil.EXPORT_ICON, "Export", "Export to PDF", "Export to PDF", true);
        exportButton.addActionListener(listener);
        toolBar.add(exportButton);

        JButton browseButton =
                makeButton(AptivatorUtil.BROWSER_ICON, "ViewBrowser", "View in external browser", "View in browser",
                        true);
        browseButton.addActionListener(listener);
        toolBar.add(browseButton);


        toolBar.add(Box.createHorizontalGlue());

        stylesheetCombo = new JComboBox(stylesheetsModel);
        stylesheetCombo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (stylesheetCombo.getSelectedItem() != null) {
                    if (stylesheetCombo.getSelectedItem().equals(defaultStylesheet)) {
                        activeStyleSheet = null;
                    } else {
                        activeStyleSheet = (StyleSheet) stylesheetCombo.getSelectedItem();
                    }
                    if (file != null) {
                        try {
                            loadAptFile();
                        } catch (UnsupportedEncodingException e1) {
                            e1.printStackTrace();
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        } catch (UnsupportedFormatException e1) {
                            e1.printStackTrace();
                        } catch (ConverterException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }


        });


        toolBar.add(stylesheetCombo);

        return toolBar;
    }

    protected JButton makeButton(String imageName,
            String actionCommand,
            String toolTipText,
            String altText,
            boolean active) {
        JButton button = new JButton();
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        button.setIcon(AptivatorUtil.createIcon(imageName, altText));
        button.setEnabled(active);
        return button;
    }


    private JComponent buildMainPanel() {
        try {
            return new AptivatorScrollPane(getXHTMLPanel());
        } catch (Exception ex) {
            Logger.getLogger(Aptivator.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public boolean loadAptFile()
            throws UnsupportedEncodingException, FileNotFoundException, UnsupportedFormatException, ConverterException {


        if (file == null || !file.exists()) {
            ErrorBox.show("Given file is null");
            return false;
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InputFileWrapper inputFileWrapper = InputFileWrapper.valueOf(file.getAbsolutePath(), "apt",
                    DefaultConverter.autoDetectEncoding(file), DefaultConverter.SUPPORTED_FROM_FORMAT);
            ByteArrayOutputStreamWrapper outputStreamWrapper = ByteArrayOutputStreamWrapper.valueOf(
                    byteArrayOutputStream,
                    "xhtml", "utf-8", new String[]{DefaultConverter.XHTML_SINK});
            DefaultConverter defaultConverter = new DefaultConverter();
            defaultConverter.convert(plexus, inputFileWrapper, outputStreamWrapper, DefaultConverter.XHTML_SINK);
            if (outputStreamWrapper.getOutputStream() != null) {
                ByteArrayInputStream bais;
                if (activeStyleSheet != null) {
                    bais = styler.styleFromFile(outputStreamWrapper.getOutputStream().toString(),
                            activeStyleSheet.getSrcFile());
                } else {
                    bais = styler.style(outputStreamWrapper.getOutputStream().toString());
                }
                if (bais != null) {
                    try {
                        getXHTMLPanel().setDocument(bais, "");
                    } catch (Exception ex) {
                        Logger.getLogger(Aptivator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    Logger.getLogger(Aptivator.class.getName()).log(Level.SEVERE, "Bais error");
                }
            } else {
                Logger.getLogger(Aptivator.class.getName()).log(Level.SEVERE, "Writer error");
            }
        } catch (UnsupportedFormatException ex) {
            Logger.getLogger(Aptivator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Aptivator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Aptivator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Aptivator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public boolean storeAsPdf(File outputFile) {

        if (outputFile == null) {
            ErrorBox.show("Given output file is null");
            return false;
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(outputFile);
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocument(xHTMLPanel.getDocument(), xHTMLPanel.getSharedContext().getBaseURL());
            renderer.layout();
            renderer.createPDF(os);
            DesktopUtil.openFile(outputFile);
            return true;
        } catch (IOException e) {
            Logger.getLogger(Aptivator.class.getName()).log(Level.SEVERE, "IOException", e);
        } catch (DocumentException e) {
            Logger.getLogger(Aptivator.class.getName()).log(Level.SEVERE, "DocumentException", e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                // Ignoring
            }
        }
        return false;
    }

    public boolean viewInBrowser() throws ConverterException {
        if (file == null || !file.exists()) {
            ErrorBox.show("Given file is null");
            return false;
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InputFileWrapper inputFileWrapper = InputFileWrapper.valueOf(file.getAbsolutePath(), "apt",
                    DefaultConverter.autoDetectEncoding(file), DefaultConverter.SUPPORTED_FROM_FORMAT);
            ByteArrayOutputStreamWrapper outputStreamWrapper = ByteArrayOutputStreamWrapper.valueOf(
                    byteArrayOutputStream,
                    "xhtml", "utf-8", new String[]{DefaultConverter.XHTML_SINK});
            DefaultConverter defaultConverter = new DefaultConverter();
            defaultConverter.convert(plexus, inputFileWrapper, outputStreamWrapper, DefaultConverter.XHTML_SINK);
            if (outputStreamWrapper.getOutputStream() != null) {
                String out;
                if (activeStyleSheet != null) {
                    out = styler.styleToStringFromFile(outputStreamWrapper.getOutputStream().toString(),
                            activeStyleSheet.getSrcFile());
                } else {
                    out = styler.styleToString(outputStreamWrapper.getOutputStream().toString());
                }
                if (out != null) {
                    Writer output = null;
                    File outputFile = new File("/tmp/foo.html");
                    try {
                        output = new BufferedWriter(new FileWriter(outputFile));
                        output.write(out);
                    } catch (IOException e) {
                    } finally {
                        try {
                            output.close();
                        } catch (IOException e) {
                        }
                    }
                    DesktopUtil.openFile(outputFile);
                } else {
                    Logger.getLogger(Aptivator.class.getName()).log(Level.SEVERE, "Bais error");
                }
            } else {
                Logger.getLogger(Aptivator.class.getName()).log(Level.SEVERE, "Writer error");
            }
        } catch (UnsupportedFormatException ex) {
            Logger.getLogger(Aptivator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Aptivator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Aptivator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Aptivator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }


}
