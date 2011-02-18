package net.jakobnielsen.aptivator;

import net.jakobnielsen.aptivator.doxia.book.BookTreeModel;
import net.jakobnielsen.aptivator.doxia.book.Books;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

public class BookTree extends JTree {


    public BookTree(Books books) {
        super(new BookTreeModel(books));
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        Icon bookIcon = null;
        renderer.setLeafIcon(bookIcon);
        renderer.setClosedIcon(bookIcon);
        renderer.setOpenIcon(bookIcon);
        setCellRenderer(renderer);
    }

}
