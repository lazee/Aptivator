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
import net.jakobnielsen.aptivator.settings.dao.SettingsDaoProperties;
import net.jakobnielsen.aptivator.settings.entities.StyleSheet;
import net.jakobnielsen.aptivator.settings.entities.Stylesheets;
import org.apache.log4j.Logger;
import org.codehaus.plexus.PlexusContainer;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.swing.BasicPanel;
import org.xhtmlrenderer.swing.FSMouseListener;
import org.xhtmlrenderer.swing.LinkListener;

import javax.swing.*;
import java.awt.*;
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
import java.net.URISyntaxException;
import java.util.List;
import java.util.ResourceBundle;

/*
   TODO: To much code duplication going on here.
*/

/**
 * Aptivator document viewer
 *
 * @author <a href="mailto:jakobnielsen@gmail.com">Jakob Vad Nielsen</a>
 */
public class AptivatorDocument {

    private XHTMLPanel xHTMLPanel;

    private Styler styler;

    private File file;

    private StyleSheet activeStyleSheet;

    private StyleSheet defaultStylesheet;

    private DefaultComboBoxModel stylesheetsModel = new DefaultComboBoxModel();

    private JComboBox stylesheetCombo;

    private PlexusContainer plexus;

    private static Logger log = Logger.getLogger(AptivatorDocument.class);

    private ResourceBundle rb;

    private SettingsDaoProperties settingsDao;

    public AptivatorDocument(PlexusContainer plexus, ResourceBundle rb, SettingsDaoProperties settingsDao) {
        this.settingsDao = settingsDao;
        this.plexus = plexus;
        this.rb = rb;
        this.styler = new Styler();
        defaultStylesheet = new StyleSheet();
        defaultStylesheet.setTitle(this.rb.getString("menu.default.stylesheet"));
    }

    protected void setFile(File file) {
        this.file = file;
    }

    public boolean hasFile() {
        return file != null;
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
                        ErrorBox.show(rb.getString("error.url.external.browser"), MessagesProperties.ERROR);
                    }
                } else if (uri.startsWith("mailto:")) {
                    if (!DesktopUtil.openMail(uri)) {
                        ErrorBox.show(rb.getString("error.open.mail"), MessagesProperties.ERROR);
                    }
                } else {
                    InfoBox.show(rb.getString("error.internal.links") + ": " + uri, MessagesProperties.ERROR);
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
        JToolBar toolBar = new JToolBar("");
        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        JButton refreshButton =
                makeButton(AptivatorUtil.REFRESH_ICON, AptivatorActions.REFRESH, rb.getString("menu.reload"),
                        rb.getString("menu.reload"), true);
        refreshButton.addActionListener(listener);
        toolBar.add(refreshButton);

        JButton exportButton =
                makeButton(AptivatorUtil.EXPORT_ICON, AptivatorActions.EXPORT, rb.getString("menu.export.to.pdf"),
                        rb.getString("menu.export.to.pdf"), true);
        exportButton.addActionListener(listener);
        toolBar.add(exportButton);

        JButton browseButton =
                makeButton(AptivatorUtil.BROWSER_ICON, AptivatorActions.VIEW_BROWSER, rb.getString("menu.view.browser"),
                        rb.getString("menu.view.browser"),
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
                        } catch (UnsupportedEncodingException ex) {
                            log.error(ex.getMessage());
                        } catch (FileNotFoundException ex) {
                            log.error(ex.getMessage());
                        } catch (UnsupportedFormatException ex) {
                            log.error(ex.getMessage());
                        } catch (ConverterException ex) {
                            log.error(ex.getMessage());
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
            return null;
        }
    }

    public boolean loadAptFile()
            throws UnsupportedEncodingException, FileNotFoundException, UnsupportedFormatException, ConverterException {
        return loadAptFile(file);
    }

    /*
      TODO Filthy method. Need to do some cleaning here soon.
     */
    private boolean loadAptFile(File f)
            throws UnsupportedEncodingException, FileNotFoundException, UnsupportedFormatException, ConverterException {

        if (f == null) {
            ErrorBox.show(rb.getString("error.file.null"), rb.getString(MessagesProperties.ERROR));
            return false;
        } else if (!f.exists()) {
            ErrorBox.show(rb.getString("error.file.missing") + ": " + f.getAbsolutePath(), MessagesProperties.ERROR);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputFileWrapper inputFileWrapper = InputFileWrapper.valueOf(f.getAbsolutePath(), "apt",
                DefaultConverter.autoDetectEncoding(f), DefaultConverter.SUPPORTED_FROM_FORMAT);
        ByteArrayOutputStreamWrapper outputStreamWrapper = ByteArrayOutputStreamWrapper.valueOf(
                byteArrayOutputStream,
                "xhtml", "utf-8", new String[]{DefaultConverter.XHTML_SINK});
        DefaultConverter defaultConverter = new DefaultConverter();
        defaultConverter.convert(plexus, inputFileWrapper, outputStreamWrapper);
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
                    log.error(ex);
                }
            } else {
                log.error("BAIS error");
            }
        } else {
            log.error("Write error");
        }

        return true;
    }

    public void setError() {
        try {
            loadAptFile(new File(AptivatorDocument.class.getResource("/META-INF/error.apt").toURI()));
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        } catch (UnsupportedFormatException e) {
            log.error(e.getMessage());
        } catch (ConverterException e) {
            log.error(e.getMessage());
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
        }
    }

    public boolean storeAsPdf(File outputFile) {

        if (outputFile == null) {
            ErrorBox.show(rb.getString("error.file.null"), MessagesProperties.ERROR);
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
            log.error(e);
        } catch (DocumentException e) {
            log.error(e);
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
            ErrorBox.show(rb.getString("error.file.null"), MessagesProperties.ERROR);
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
            defaultConverter.convert(plexus, inputFileWrapper, outputStreamWrapper);
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
                    // TODO Maybe introduce unique name for a preview and clean up old preview files
                    File outputFile = new File(settingsDao.getSettingsDir(), "aptivator.html");
                    try {
                        output = new BufferedWriter(new FileWriter(outputFile));
                        output.write(out);
                    } catch (IOException e) {
                        // Ignoring for now
                    } finally {
                        try {
                            output.close();
                        } catch (IOException e) {
                            // Ignoring for now
                        }
                    }
                    DesktopUtil.openFile(outputFile);
                } else {
                    log.error("BAIS error");
                }
            } else {
                log.error("Write error");
            }
        } catch (UnsupportedFormatException ex) {
            log.error(ex);
        } catch (IllegalArgumentException ex) {
            log.error(ex);
        } catch (UnsupportedEncodingException ex) {
            log.error(ex);
        } catch (FileNotFoundException ex) {
            log.error(ex);
        }
        return true;
    }


}
