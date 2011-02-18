package net.jakobnielsen.aptivator.bookmark.entities;

import java.io.File;

public class Bookmarks {

    private Group root;

    public Bookmarks() {
        this.root = new Group(null, "Bookmarks");
    }

    public Bookmarks(Group root) {
        this.root = root;
    }

    public Group getRoot() {
        return root;
    }

    public void setRoot(Group root) {
        this.root = root;
    }

    public boolean isBookmarked(File f) {
        return root.isBookmarked(f);
    }

    public boolean removeBookmark(File f) {
        return root.removeBookmark(f);
    }

    public boolean addBookmark(Bookmark bookmark) {
        return root.addNode(bookmark);
    }

    public boolean addGroup(Group group) {
        return root.addNode(group);
    }
}
