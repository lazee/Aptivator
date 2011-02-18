package net.jakobnielsen.aptivator.bookmark;

import net.jakobnielsen.aptivator.bookmark.entities.Group;
import net.jakobnielsen.aptivator.bookmark.entities.Node;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

public class BookmarkTreeModel implements TreeModel {

    private List<TreeModelListener> treeModelListeners;

    private Group parent;

    Logger log = Logger.getLogger(BookmarkTreeModel.class);

    public BookmarkTreeModel(Group parent) {
        log.debug("Creating new instance of bookmark tree model. Adding parent : " + parent.toString());
        treeModelListeners = new ArrayList();
        this.parent = parent;
    }

    public Object getRoot() {
        log.debug("getRoot. Returning : " + parent.toString());
        return parent;
    }

    public Object getChild(Object parent, int index) {
        log.debug("getChild()");
        if (parent == null) {
            return null;
        } else if (parent instanceof Group) {
            Group groupNode = (Group) parent;
            if (groupNode.getChildCount() > 0) {
                return groupNode.getChildAt(index);
            }
        }
        return null;
    }

    public int getChildCount(Object parent) {
        log.debug("getChildeCount: " + parent.toString());
        if (parent == null) {
            log.debug("Returning null (1)");
            return 0;
        } else if (parent instanceof Group) {
            Group groupNode = (Group) parent;
            log.debug("Returning " + groupNode.getChildCount());
            return groupNode.getChildCount();
        }
        log.debug("Returning null (2)");
        return 0;
    }

    public boolean isLeaf(Object node) {
        log.debug("isLeaf() : " + node.toString());
        if (node instanceof Group) {
            if (((Group) node).getChildCount() > 0) {
                log.debug("Returning false");
                return false;
            }
        }
        log.debug("Returning true");
        return true;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    public int getIndexOfChild(Object parent, Object child) {
        if (parent == null || child == null) {
            return 0;
        } else if (parent instanceof Group) {
            Group groupNode = (Group) parent;
            return groupNode.getIndex((Node) child);
        }
        return 0;
    }

    public void addTreeModelListener(TreeModelListener l) {
        if (!treeModelListeners.contains(l)) {
            treeModelListeners.add(l);
        }
    }

    public void removeTreeModelListener(TreeModelListener l) {
        if (treeModelListeners.contains(l)) {
            treeModelListeners.remove(l);
        }
    }
}
