package net.jakobnielsen.aptivator.osx;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import net.jakobnielsen.aptivator.SwingTools;
import net.jakobnielsen.aptivator.dialog.AboutBox;

import javax.swing.JFrame;

public class MacOSAboutHandler extends Application {

    public MacOSAboutHandler() {
        addApplicationListener(new AboutBoxHandler());
    }

    class AboutBoxHandler extends ApplicationAdapter {

        public void handleAbout(ApplicationEvent event) {
            AboutBox aboutBox = new AboutBox(new JFrame());
            aboutBox.pack();
            SwingTools.locateOnScreen(aboutBox);
            aboutBox.setVisible(true);
        }
    }
}