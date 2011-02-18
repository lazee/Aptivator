package net.jakobnielsen.aptivator.settings.entities;

import javax.swing.ListModel;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Stylesheets {

    private Map<String, StyleSheet> stylesheets = new TreeMap<String, StyleSheet>();

    public Stylesheets() {
    }

    public Stylesheets(List<StyleSheet> stylesheets) {
        for (StyleSheet s : stylesheets) {
            this.stylesheets.put(s.getId(), s);
        }
    }

    public boolean add(StyleSheet styleSheet) {
        if (stylesheets.containsKey(styleSheet.getId())) {
            return false;
        }
        stylesheets.put(styleSheet.getId(), styleSheet);
        return true;
    }
    
    public boolean remove(StyleSheet styleSheet) {
        if (stylesheets.containsKey(styleSheet.getId())) {
            stylesheets.remove(styleSheet.getId());
            return true;
        }
        return false;
    }

    public Collection<StyleSheet> getStylesheets() {
        return stylesheets.values();
    }

    public boolean store(File f) {
        Writer output = null;
        try {
            output = new BufferedWriter(new FileWriter(f));
            StringBuffer sb = new StringBuffer();
            for (StyleSheet s : getStylesheets()) {
                sb.append(s.getId() + "\n");
                sb.append(s.getTitle() + "\n");
                sb.append(s.getSrcFile().getAbsolutePath() + "\n");
            }
            output.write(sb.toString());
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                output.close();
            } catch (IOException e) {
            }
        }
    }

    public boolean loadFromFile(File f) {
        try {
            FileInputStream fstream = new FileInputStream(f);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            int counter = 0;
            StyleSheet activeStyleSheet = null;
            while ((strLine = br.readLine()) != null) {
                if (counter == 3) {
                    counter = 0;
                }
                if (counter == 0) {
                    activeStyleSheet = new StyleSheet();
                    activeStyleSheet.setId(strLine);
                } else if (counter == 1) {
                    activeStyleSheet.setTitle(strLine);
                } else if (counter == 2) {
                    activeStyleSheet.setSrcFile(new File(strLine));
                    add(activeStyleSheet);
                }
                counter++;
            }
            in.close();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Stylesheets fromListModel(ListModel listModel) {
        Stylesheets sl = new Stylesheets();
        for (int i = 0; i < listModel.getSize(); i++) {
            StyleSheet s = (StyleSheet) listModel.getElementAt(i);
            sl.add(s);
        }
        return sl;
    }

}
