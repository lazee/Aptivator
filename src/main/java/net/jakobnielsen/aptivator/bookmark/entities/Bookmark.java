package net.jakobnielsen.aptivator.bookmark.entities;

import java.io.File;

public class Bookmark extends Node {

    private Group parent;

    private File path;

    public Bookmark(Group parent) {
        this.parent = parent;
    }

    public Bookmark(Group parent, String name, File path) {
        this.parent = parent;
        this.path = path;
        setName(name);
    }

    public Group getParent() {
        return parent;
    }

    public void setParent(Group parent) {
        this.parent = parent;
    }

    public File getPath() {
        return path;
    }

    public void setPath(File path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return getName();
    }
}
