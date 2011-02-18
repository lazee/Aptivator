package net.jakobnielsen.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.File;

/**
 * Simple helper task for building a list of jar files used for the mac building process.
 */
public class DirToStringTask extends Task {

    private String dir;

    private String propertyName;

    private int indents = 4;

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setIndents(int indents) {
        this.indents = indents;
    }

    @Override
    public void execute() throws BuildException {
        if (dir == null) {
            throw new BuildException("dir parameter is required");
        }

        if (propertyName == null) {
            throw new BuildException("A property name is required");
        }

        File directory = new File(dir);

        if (!directory.exists()) {
            throw new BuildException("The given directory does not exist.");
        }

        if (!directory.isDirectory()) {
            throw new BuildException("The given path is not a directory.");
        }

        if (!directory.canRead()) {
            throw new BuildException("The given directory is not readable.");
        }

        StringBuffer sb = new StringBuffer();
        for (File f : directory.listFiles()) {
            if (f.isFile() && f.getName().endsWith(".jar")) {
                for (int i = 0; i < indents; i++) {
                    sb.append(" ");
                }
                sb.append("<string>$JAVAROOT/");
                sb.append(f.getName());
                sb.append("</string>\n");
            }
        }

       getProject().setProperty(propertyName, sb.toString());

    }
}
