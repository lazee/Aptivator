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
import net.jakobnielsen.aptivator.doxia.UnsupportedFormatException;
import net.jakobnielsen.aptivator.plexus.PlexusHelper;
import net.jakobnielsen.aptivator.settings.SettingsDialog;
import net.jakobnielsen.aptivator.settings.dao.SettingsDao;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 *  Aptivator application
 *
 *  @author <a href="mailto:jakobnielsen@gmail.com">Jakob Vad Nielsen</a>
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

    private SettingsDao settingsDAO;

    /** Plexus container */
    private PlexusContainer plexus;

    /** Constructor method */
    public Aptivator() {
        logListModel = new LogListModel();
        WriterAppender writeappender = new WriterAppender(new SimpleLayout(), new LogWriter(logListModel));
        writeappender.setName("A1");
        writeappender.setImmediateFlush(true);
        Logger.getRootLogger().addAppender(writeappender);
        Logger.getRootLogger().setLevel(Level.INFO);
        settingsDAO = new SettingsDaoProperties();
    }

    /**
     * Set application arguments
     *
     * @param args Array of arguments given to application at startup
     */
    public void setArgs(String[] args) {
        this.args = args;
    }

    /** Start the Aptivator application */
    public void run() {
        try {
            plexus = PlexusHelper.startPlexusContainer();
        } catch (PlexusContainerException e) {
            e.printStackTrace();
        }
        loadSettings();
        configureUI();
        aptivatorDocument = new AptivatorDocument(plexus);
        aptivatorDocument.createXHtmlPanel();
        buildInterface();
        createFileChooser();
        if (args.length > 0) {
            File doc = new File(args[0]);
            if (doc.exists() && doc.canRead()) {
                loadDocument(doc);
            } else {
                // TODO View error box
            }
        }
    }

    /** Configure the user interface */
    private void configureUI() {
        UIManager.put("Application.useSystemFontSettings", Boolean.TRUE);
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Can't set look & feel:" + e);
        }
    }

    /** Build the user interface */
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
        jframe.setTitle("Aptivator");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jframe.setVisible(true);

        // Setting size of logPanel
        splitPane.setDividerLocation(0.8);
    }

    public void actionPerformed(ActionEvent e) {
        if ("Refresh".equals(e.getActionCommand())) {
            reloadDocument();
        } else if ("Export".equals(e.getActionCommand())) {
            exportDocument();
        } else if ("ViewBrowser".equals(e.getActionCommand())) {
            try {
                aptivatorDocument.viewInBrowser();
            } catch (ConverterException e1) {
                ErrorBox.show("Could not view document in external browser");
            }
        }
    }

    private void createRecentMenu() {
        recentMenu = new JMenu("Open Recent");
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
            JMenuItem item = new JMenuItem("Clear list");
            ActionListener lst = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    settings.clearRecentFiles();
                    storeSettings();
                    updateRecentMenu();
                }
            };
            item.addActionListener(lst);
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
        ml.setBackground(Color.DARK_GRAY);
        ml.setForeground(Color.WHITE);
        return ml;
    }

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        fileMenu = new JMenu("File");

        /* Open */
        JMenuItem item = new JMenuItem("Open File");
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
        ActionListener lst = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = getFileChooser();
                fileChooser.showOpenDialog(new JFrame());
                if (fileChooser.getSelectedFile() != null) {
                    loadDocument(fileChooser.getSelectedFile());
                }
            }
        };
        item.addActionListener(lst);
        fileMenu.add(item, 0);

        /* Open recent */
        createRecentMenu();
        fileMenu.add(recentMenu, 1);


        /* Settings */
        item = new JMenuItem("Settings");

        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
        lst = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                SettingsDialog settingsDialog = new SettingsDialog(new JFrame(), getSettings());
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

            }
        };
        item.addActionListener(lst);
        fileMenu.add(item, 2);

        /* Separator */
        fileMenu.addSeparator();

        /* Quit */
        item = new JMenuItem("Quit Aptivator");
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
        lst = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        };
        item.addActionListener(lst);
        fileMenu.add(item, 4);

        menuBar.add(fileMenu);

        /* Document */
        documentMenu = new JMenu("Document");
        documentMenu.setEnabled(false);

        /* Reload */
        item = new JMenuItem("Reload");
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

        lst = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reloadDocument();
            }
        };

        item.addActionListener(lst);
        documentMenu.add(item);

        item = new JMenuItem("Export to PDF");
        lst = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportDocument();
            }
        };

        item.addActionListener(lst);
        documentMenu.add(item);

        menuBar.add(documentMenu);

        /* Help */
        JMenu menu = new JMenu("Help");

        //if (System.getProperty("mrj.version") == null) {
        item = new JMenuItem("About Aptivator");
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
        lst = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                AboutBox aboutBox = new AboutBox(new JFrame());
                aboutBox.pack();
                SwingTools.locateOnScreen(aboutBox);
                aboutBox.setVisible(true);

            }
        };
        item.addActionListener(lst);
        menu.add(item);

        //} else {
        //     new MacOSAboutHandler();
        //}

        menuBar.add(menu);
        return menuBar;
    }

    /** Settings ** */
    private void loadSettings() {
        settings = settingsDAO.getSettings();
    }

    private void storeSettings() {
        settingsDAO.setSettings(settings);
    }

    private Settings getSettings() {
        return settings;
    }

    /** Create the file chooser dialog */
    private void createFileChooser() {
        fileChooser = new AptivatorFileChooser("apt");
    }

    /**
     * Get the file chooser variable
     *
     * @return File chooser component ready for use.
     */
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

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        Transferable t = support.getTransferable();
        try {
            List<File> data = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
            if (data != null && data.size() > 0) {
                loadDocument(data.get(0));
            }
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void reloadDocument() {
        if (aptivatorDocument != null) {
            try {
                aptivatorDocument.loadAptFile();
                log.info("Reloaded document successfully.");
            } catch (UnsupportedEncodingException ex) {
                log.error("Could not open the chosen file due to encoding problems");
            } catch (FileNotFoundException ex) {
                log.error("The chosen file does not exists");
            } catch (UnsupportedFormatException ex) {
                log.error("The chosen file is not a valid APT file");
            } catch (ConverterException ex) {
                log.error("Parser exception: " + ex.getMessage());
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
                    log.info("Loaded : " + f.getAbsolutePath());
                } catch (UnsupportedEncodingException ex) {
                    log.error("Could not open the chosen file due to encoding problems");
                } catch (FileNotFoundException ex) {
                    log.error("The chosen file does not exists");
                } catch (UnsupportedFormatException ex) {
                    log.error("The chosen file is not a valid APT file");
                } catch (ConverterException ex) {
                    log.error("Parser exception: " + ex.getMessage());
                }
            }
        }
        );
    }

    private void exportDocument() {

        JFileChooser fileChooser = new AptivatorExportChooser();
        fileChooser.setDialogTitle("Export to PDF");
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
                Object[] options = {"No", "Yes"};
                int n = JOptionPane.showOptionDialog(jframe,
                        file.getAbsolutePath(),
                        "Override file",
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
                    log.info("PDF successfully stored to " + file.getAbsolutePath());
                } else {
                    ErrorBox.show("Could not store file as PDF!");
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


}
