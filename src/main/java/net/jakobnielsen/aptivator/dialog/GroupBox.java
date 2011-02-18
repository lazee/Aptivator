package net.jakobnielsen.aptivator.dialog;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class GroupBox extends JDialog implements ActionListener, PropertyChangeListener {

    private String typedText = null;

    private JTextField textField;

    private JOptionPane optionPane;

    private String btnString1 = "Enter";

    private String btnString2 = "Cancel";

    /**
     * Returns null if the typed string was invalid; otherwise, returns the string as the user entered it.
     */
    public String getValidatedText() {
        return typedText;
    }


    public void actionPerformed(ActionEvent e) {
        optionPane.setValue(btnString1);
    }

    public void propertyChange(PropertyChangeEvent evt) {

    }


}