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

import net.jakobnielsen.aptivator.dialog.AboutBox;
import net.jakobnielsen.aptivator.dialog.AptivatorExportChooser;
import net.jakobnielsen.aptivator.dialog.AptivatorFileChooser;
import net.jakobnielsen.aptivator.dialog.ErrorBox;
import net.jakobnielsen.aptivator.doxia.ConverterException;
import net.jakobnielsen.aptivator.doxia.DoxiaLogWrapperOutputStream;
import net.jakobnielsen.aptivator.doxia.UnsupportedFormatException;
import net.jakobnielsen.aptivator.i18n.CustomClassLoader;
import net.jakobnielsen.aptivator.macos.AptivatorMacAccessor;
import net.jakobnielsen.aptivator.plexus.PlexusHelper;
import net.jakobnielsen.aptivator.settings.SettingsDialog;
import net.jakobnielsen.aptivator.settings.dao.SettingsDaoProperties;
import net.jakobnielsen.aptivator.settings.entities.Settings;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static net.jakobnielsen.aptivator.AptivatorActions.ABOUT;
import static net.jakobnielsen.aptivator.AptivatorActions.CLEAR_LOG_LIST;
import static net.jakobnielsen.aptivator.AptivatorActions.CLEAR_RECENT_LIST;
import static net.jakobnielsen.aptivator.AptivatorActions.DO_OPEN_FILE;
import static net.jakobnielsen.aptivator.AptivatorActions.EXPORT;
import static net.jakobnielsen.aptivator.AptivatorActions.OPEN_FILE;
import static net.jakobnielsen.aptivator.AptivatorActions.QUIT;
import static net.jakobnielsen.aptivator.AptivatorActions.REFRESH;
import static net.jakobnielsen.aptivator.AptivatorActions.SETTINGS;
import static net.jakobnielsen.aptivator.AptivatorActions.VIEW_BROWSER;

/**
 * Aptivator application
 *
 * @author <a href="mailto:jakobnielsen@gmail.com">Jakob Vad Nielsen</a>
 */
public class Aptivator extends TransferHandler implements ComponentListener, ActionListener {

    private static Logger log = Logger.getLogger(Aptivator.class);

    private AptivatorFileChooser fileChooser;

    private String[] args;

    private Settings settings;

    private AptivatorDocument aptivatorDocument;

    private JFrame jframe;

    private JMenu fileMenu;

    private JMenu recentMenu;

    private JPanel contentPanel;

    private JMenu documentMenu;

    private boolean firstDocument = true;

    private File activeExportDir = new File("");

    private LogListModel logListModel;

    private SettingsDaoProperties settingsDAO;

    private ResourceBundle rb;

    private PlexusContainer plexus;

    private Timer refreshTimer;

    public Aptivator() {
        logListModel = new LogListModel();
        WriterAppender writeappender = new WriterAppender(new SimpleLayout(), new LogWriter(logListModel));
        writeappender.setName("A1");
        writeappender.setImmediateFlush(true);
        Logger.getRootLogger().addAppender(writeappender);
        Logger.getRootLogger().setLevel(Level.INFO);
        settingsDAO = new SettingsDaoProperties();
        rb = ResourceBundle.getBundle("/META-INF/i18n/messages", Locale.US, new CustomClassLoader());
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public void run() {
        try {
            plexus = PlexusHelper.startPlexusContainer();
        } catch (PlexusContainerException e) {
            log.fatal("Could not load Plexus Container");
        }
        loadSettings();
        configureUI();
        aptivatorDocument = new AptivatorDocument(plexus, rb, settingsDAO);
        aptivatorDocument.createXHtmlPanel();
        buildInterface();
        createFileChooser();

        // Open requested file if any
        if (args.length > 0) {
            File doc = new File(args[0]);
            if (doc.exists() && doc.canRead()) {
                loadDocument(doc);
            } else {
                ErrorBox.show(rb.getString("error.file.missing " + doc.getAbsolutePath()), rb.getString("error"));
            }
        }

        if (AptivatorUtil.isMacOSX()) {
            AptivatorMacAccessor.activateOpenFileHandler(this);
        }

        overrideStd();
    }

    /*
        Oh joy of dirty hacks.

        Doxia uses its own logging mechanism that prints directly to StdOut and StdErr (Shame on you). We need to
        catch this logging, so that it can be viewed inside the application.
     */
    private void overrideStd() {
        System.setErr(new PrintStream(new DoxiaLogWrapperOutputStream(logListModel, true), true));
        System.setOut(new PrintStream(new DoxiaLogWrapperOutputStream(logListModel, false), true));
    }

    private void configureUI() {
        UIManager.put("Application.useSystemFontSettings", Boolean.TRUE);
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            log.error(rb.getString("error.look.and.feel") + ": " + e.getMessage());
        }
    }

    private void buildInterface() {
        /* Content contentPanel */
        contentPanel = new JPanel(new BorderLayout());

        /* Log panel */
        JList logList = buildLogMonitorList();


        /* Split panel */
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.8);
        splitPane.setOneTouchExpandable(true);
        splitPane.setTopComponent(contentPanel);
        splitPane.setBottomComponent(new JScrollPane(logList));

        /* Main frame */
        jframe = new JFrame();
        jframe.setJMenuBar(buildMenuBar());
        jframe.addComponentListener(this);
        jframe.setContentPane(splitPane);

        jframe.setSize(getSettings().getAppSize());
        jframe.setMinimumSize(new Dimension(100, 100));
        jframe.setTransferHandler(this);
        SwingTools.locateOnScreen(jframe);
        jframe.setTitle(rb.getString("app.title"));
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jframe.setVisible(true);

        // Setting size of logPanel
        splitPane.setDividerLocation(0.8);
    }

    public void actionPerformed(ActionEvent e) {
        if (REFRESH.equals(e.getActionCommand())) {
            reloadDocument();
        } else if (EXPORT.equals(e.getActionCommand())) {
            exportDocument();
        } else if (VIEW_BROWSER.equals(e.getActionCommand())) {
            try {
                aptivatorDocument.viewInBrowser();
            } catch (ConverterException e1) {
                ErrorBox.show(rb.getString("error.external.browser"), rb.getString("error"));
            }
        } else if (SETTINGS.equals(e.getActionCommand())) {
            SettingsDialog settingsDialog = new SettingsDialog(new JFrame(), getSettings(), rb);
            settingsDialog.pack();
            SwingTools.locateOnScreen(settingsDialog);
            settingsDialog.setVisible(true);

            if (settingsDialog.isSave()) {
                settings = settingsDialog.getSettings();
                storeSettings();
                if (aptivatorDocument != null) {
                    aptivatorDocument.setStylesheets(settings.getStylesheets());
                }
            }
        } else if (CLEAR_RECENT_LIST.equals(e.getActionCommand())) {
            settings.clearRecentFiles();
            storeSettings();
            updateRecentMenu();
        } else if (CLEAR_LOG_LIST.equals(e.getActionCommand())) {
            logListModel.clear();
        } else if (OPEN_FILE.equals(e.getActionCommand())) {
            JFileChooser fileChooser = getFileChooser();
            fileChooser.showOpenDialog(new JFrame());
            if (fileChooser.getSelectedFile() != null) {
                loadDocument(fileChooser.getSelectedFile());
            }
        } else if (DO_OPEN_FILE.equals(e.getActionCommand())) {
            loadDocument((File) e.getSource());
        } else if (ABOUT.equals(e.getActionCommand())) {
            AboutBox aboutBox = new AboutBox(new JFrame(), rb, settings.getAppVersion());
            aboutBox.pack();
            SwingTools.locateOnScreen(aboutBox);
            aboutBox.setVisible(true);
        } else if (QUIT.equals(e.getActionCommand())) {
            System.exit(1);
        } else {
            log.error(rb.getString("error.unhandled.action") + ": " + e.getActionCommand());
        }
    }

    private void createRecentMenu() {
        recentMenu = new JMenu(rb.getString("menu.open.recent"));
        if (settings.getRecentFiles().getRecentFiles().size() > 0) {
            for (File f : settings.getRecentFiles().getRecentFiles()) {

                JMenuItem item = new JMenuItem(f.getAbsolutePath());
                ActionListener lst = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        loadDocument(new File(e.getActionCommand()));
                    }
                };

                item.addActionListener(lst);
                recentMenu.add(item);
            }
            recentMenu.addSeparator();
            JMenuItem item = SwingTools.createMenuItem(this, rb.getString("menu.clear.list"), CLEAR_RECENT_LIST);
            recentMenu.add(item);
        } else {
            recentMenu.setEnabled(false);
        }
    }

    private void updateRecentMenu() {
        createRecentMenu();
        fileMenu.remove(1);
        fileMenu.add(recentMenu, 1);
    }

    private JList buildLogMonitorList() {
        JList ml = new JList(logListModel);
        ml.setBackground(Color.WHITE);
        ml.setForeground(Color.DARK_GRAY);

        createPopupMenu(ml);

        return ml;
    }

    public void createPopupMenu(JList list) {
        JMenuItem menuItem;

        //Create the popup menu.
        JPopupMenu popup = new JPopupMenu();

        menuItem = SwingTools.createMenuItem(this, rb.getString("menu.clear.list"), CLEAR_LOG_LIST);
        popup.add(menuItem);

        //Add listener to the text area so the popup menu can come up.
        MouseListener popupListener = new PopupListener(popup);
        list.addMouseListener(popupListener);
        list.add(popup);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        fileMenu = new JMenu(rb.getString("menu.file"));

        /* Open */

        JMenuItem item = SwingTools.createMenuItem(this, rb.getString("menu.open.file"), OPEN_FILE);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
        fileMenu.add(item, 0);

        /* Open recent */
        createRecentMenu();
        fileMenu.add(recentMenu, 1);


        /* Settings */
        item = SwingTools.createMenuItem(this, rb.getString("menu.settings"), SETTINGS);

        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

        fileMenu.add(item, 2);

        /* Separator */
        fileMenu.addSeparator();

        /* Quit */
        item = SwingTools.createMenuItem(this, rb.getString("menu.quit"), QUIT);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
        fileMenu.add(item, 4);

        menuBar.add(fileMenu);

        /* Document */
        documentMenu = new JMenu(rb.getString("menu.document"));
        documentMenu.setEnabled(false);

        /* Reload */
        item = SwingTools.createMenuItem(this, rb.getString("menu.reload"), REFRESH);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
        documentMenu.add(item);

        /* Export to PDF */
        item = SwingTools.createMenuItem(this, rb.getString("menu.export.to.pdf"), EXPORT);
        documentMenu.add(item);
        menuBar.add(documentMenu);

        /* View in browser */
        item = SwingTools.createMenuItem(this, rb.getString("menu.view.browser"), VIEW_BROWSER);
        documentMenu.add(item);


        /* Help */
        JMenu menu = new JMenu(rb.getString("menu.help"));

        item = SwingTools.createMenuItem(this, rb.getString("menu.about"), ABOUT);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
        menu.add(item);
        menuBar.add(menu);
        return menuBar;
    }

    private void loadSettings() {
        settings = settingsDAO.getSettings();
        loadRefreshTimer();
    }

    private void loadRefreshTimer() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
        if (settings.getRefreshInterval() != -1) {
            refreshTimer = new Timer(settings.getRefreshInterval() * 1000, this);
            refreshTimer.setActionCommand(AptivatorActions.REFRESH);
            refreshTimer.start();
        }
    }

    private void storeSettings() {
        settingsDAO.setSettings(settings);
        loadRefreshTimer();
    }

    private Settings getSettings() {
        return settings;
    }

    private void createFileChooser() {
        fileChooser = new AptivatorFileChooser("apt");
    }

    private JFileChooser getFileChooser() {
        return fileChooser;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        if (!support.isDrop()) {
            return false;
        }
        return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    /*
     * Enables support for dragging an APT file into the application from any supported OS.
     */
    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        Transferable t = support.getTransferable();
        try {
            List data = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
            if (data != null && data.size() > 0) {
                loadDocument((File) data.get(0));
            }
        } catch (UnsupportedFlavorException e) {
            log.error(rb.getString("error.load.dragged") + ": " + e.getMessage());
        } catch (IOException e) {
            log.error(rb.getString("error.load.dragged") + ": " + e.getMessage());
        }
        return false;
    }

    private void reloadDocument() {
        if (aptivatorDocument != null && aptivatorDocument.hasFile()) {
            try {
                aptivatorDocument.loadAptFile();
                log.info(rb.getString("info.reload.ok"));
            } catch (UnsupportedEncodingException ex) {
                log.error(rb.getString("error.encoding.problems"));
            } catch (FileNotFoundException ex) {
                log.error(rb.getString("error.file.missing"));
            } catch (UnsupportedFormatException ex) {
                log.error(rb.getString("error.invalid.apt"));
            } catch (ConverterException ex) {
                log.error(ex.getMessage());
            }
        }
    }

    private void loadDocument(final File f) {

        if (firstDocument) {
            firstDocument = false;
            documentMenu.setEnabled(true);
            contentPanel.add(aptivatorDocument.buildContentPane(this), BorderLayout.CENTER);
            aptivatorDocument.setStylesheets(settings.getStylesheets());
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    aptivatorDocument.setFile(f);
                    aptivatorDocument.loadAptFile();
                    settings.addRecentFile(f);
                    storeSettings();
                    updateRecentMenu();
                    log.info(rb.getString("info.load.ok") + " : " + f.getAbsolutePath());
                } catch (UnsupportedEncodingException ex) {
                    log.error(rb.getString("error.encoding.problems"));
                    aptivatorDocument.setError();
                } catch (FileNotFoundException ex) {
                    log.error(rb.getString("error.file.missing"));
                    aptivatorDocument.setError();
                } catch (UnsupportedFormatException ex) {
                    log.error(rb.getString("error.invalid.apt"));
                    aptivatorDocument.setError();
                } catch (ConverterException ex) {
                    log.error(ex.getMessage());
                    aptivatorDocument.setError();
                }
            }
        }
        );
    }

    private void exportDocument() {

        JFileChooser fileChooser = new AptivatorExportChooser();
        fileChooser.setDialogTitle(rb.getString("menu.export.to.pdf"));
        fileChooser.setCurrentDirectory(activeExportDir);

        fileChooser.showSaveDialog(new JFrame());
        if (fileChooser.getSelectedFile() != null) {
            String f = fileChooser.getSelectedFile().getAbsolutePath();
            if (f.indexOf(".") == -1) {
                f += ".pdf";
            }
            File file = new File(f);

            boolean proceed = true;

            if (file.exists()) {
                proceed = false;
                Object[] options = {rb.getString("text.no"), rb.getString("text.yes")};
                int n = JOptionPane.showOptionDialog(jframe,
                        file.getAbsolutePath(),
                        rb.getString("question.override.file"),
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[1]);
                if (n == 1) {
                    proceed = true;
                }
            }

            if (proceed) {
                activeExportDir = fileChooser.getCurrentDirectory();
                if (aptivatorDocument.storeAsPdf(file)) {
                    log.info(rb.getString("info.pdf.stored.in") + file.getAbsolutePath());
                } else {
                    ErrorBox.show(rb.getString("error.storing.pdf"), rb.getString("error"));
                }
            }
        }
    }

    public void componentResized(ComponentEvent e) {
        settings.setAppSize(jframe.getSize());
        storeSettings();
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }

    class PopupListener extends MouseAdapter {

        JPopupMenu popup;

        PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(),
                        e.getX(), e.getY());
            }
        }
    }


}
