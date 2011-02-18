package net.jakobnielsen.aptivator.doxia.book;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.Vector;

public class BookTreeModel implements TreeModel {

    private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();

    private Books books;

    public BookTreeModel(Books books) {
        this.books = books;
    }

    public Object getRoot() {
        return books;
    }

    public Object getChild(Object parent, int index) {
        if (index < 0) {
            return null;
        } else if (parent instanceof Books) {
            return ((Books) parent).getBooks().get(index);
        } else if (parent instanceof Book) {
            return ((Book) parent).getChapters().get(index);
        } else if (parent instanceof Chapter) {
            return ((Chapter) parent).getSections().get(index);
        }
        return null;
    }

    public int getChildCount(Object parent) {
        if (parent instanceof Books) {
            return ((Books) parent).getBooks().size();
        } else if (parent instanceof Book) {
            return ((Book) parent).getChapters().size();
        } else if (parent instanceof Chapter) {
            return ((Chapter) parent).getSections().size();
        }
        return 0;
    }

    public boolean isLeaf(Object node) {
        if (node instanceof Books) {
            return ((Books) node).getBooks().size() == 0;
        } else if (node instanceof Book) {
            return ((Book) node).getChapters().size() == 0;
        } else if (node instanceof Chapter) {
            return ((Chapter) node).getSections().size() == 0;
        }
        return true;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        System.out.println("*** valueForPathChanged : " + path + " --> " + newValue);
    }

    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof Books) {
            Books books = (Books) parent;
            Book book = (Book) child;
            return books.getBooks().indexOf(book);
        } else if (parent instanceof Book) {
            Book books = (Book) parent;
            Chapter chapter = (Chapter) child;
            return books.getChapters().indexOf(chapter);
        } else if (parent instanceof Chapter) {
            Chapter chapter = (Chapter) parent;
            Section section = (Section) child;
            return chapter.getSections().indexOf(child);
        }
        return -1;
    }

    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.addElement(l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.removeElement(l);
    }
}
