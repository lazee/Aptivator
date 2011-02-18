package net.jakobnielsen.aptivator.bookmark.entities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Group extends Node {

    private Group parent;

    private List<Node> children;

    public Group(Group parent, String name) {
        this.parent = parent;
        setName(name);
    }
    
    public Group(Group parent, List<Node> children) {
        this.parent = parent;
        this.children = children;
    }

    public boolean hasParent() {
        return parent == null;
    }
    
    public List<Node> getChildren() {
        return children;
    }

    public int getChildCount() {
        if (children != null) {
            return children.size();
        }
        return 0;
    }

    public Node getChildAt(int index) {
        if (children != null && getChildCount() >= index + 1) {
            return children.get(index);
        }
        return null;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public int getIndex(Node node) {
        if (children != null) {
            return -1;
        }
        return children.indexOf(node);

    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean isBookmarked(File f) {
        if (children == null || f == null) {
            return false;
        }
        for (Node n : children) {
            if (n instanceof Group) {
                Group g = (Group) n;
                if (g.isBookmarked(f)) {
                    return true;
                }
            } else if (n instanceof Bookmark) {
                Bookmark b = (Bookmark) n;
                if (b.getPath() != null && f.equals(b.getPath())) {
                    return true;
                }
            }
        }
        return false;
    }

     public synchronized boolean removeBookmark(File f) {
        if (children == null || f == null) {
            return false;
        }
        for (Node n : children) {
            if (n instanceof Group) {
                Group g = (Group) n;
                if (g.removeBookmark(f)) {
                    return true;
                }
            } else if (n instanceof Bookmark) {
                Bookmark b = (Bookmark) n;
                if (b.getPath() != null && f.equals(b.getPath())) {
                    children.remove(n);
                    return true;
                }
            }
        }
        return false;
    }

    public synchronized boolean addNode(Node node) {
        if (children == null) {
            children = new ArrayList<Node>();
        }
        return children.add(node);
    }
}
