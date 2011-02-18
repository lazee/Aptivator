/*
 * Copyright 2008 Jakob Vad Nielsen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.jakobnielsen.aptivator.bookmark;

import net.jakobnielsen.aptivator.ActionCommands;
import net.jakobnielsen.aptivator.bookmark.entities.Bookmark;
import net.jakobnielsen.aptivator.bookmark.entities.Group;
import org.apache.log4j.Logger;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class BookmarkTree extends JTree {

    ActionListener al;

    JPopupMenu popup;

    Logger log = Logger.getLogger(BookmarkTree.class);


    public BookmarkTree(Group parent) {
        super(new BookmarkTreeModel(parent));
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        //Icon bookIcon = null;
        //renderer.setLeafIcon(bookIcon);
        //renderer.setClosedIcon(bookIcon);
        //renderer.setOpenIcon(bookIcon);
        setCellRenderer(renderer);
        setShowsRootHandles(true);
        setEditable(true);
        setCellRenderer(new DefaultTreeCellRenderer());
        setCellEditor(new LeafCellEditor(this, (DefaultTreeCellRenderer) getCellRenderer()));
        getCellEditor().addCellEditorListener(new BookmarkCellEditorListener());

        
        createGroupPopup();
        addMouseListener(new PopupListener());


        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && getSelectionCount() == 1) {
                    Object o = getLastSelectedPathComponent();
                    if (o instanceof Group) {

                    } else if (o instanceof Bookmark) {
                        al.actionPerformed(new ActionEvent(getLastSelectedPathComponent(), ActionEvent.ACTION_PERFORMED,
                                ActionCommands.OPEN_BOOKMARK));
                    }
                }
                e.consume();
            }

        });

        addKeyListener(new KeyAdapter() {

            public void keyReleased(KeyEvent e) {
                Object o = getLastSelectedPathComponent();
                if (o instanceof Group) {

                } else {
                    al.actionPerformed(new ActionEvent(getLastSelectedPathComponent(), ActionEvent.ACTION_PERFORMED,
                            ActionCommands.OPEN_BOOKMARK));
                    e.consume();
                }
            }
        }

        );


    }

    @Override
    public String getToolTipText(MouseEvent event) {
        // Get item index
//        int index = locationToIndex(event.getPoint());
//
//        if (getModel() != null && index > -1) {
//            // Get item
//            Object item = getModel().getElementAt(index);
//
//            // Return the tool tip text
//            return "" + item;ha
//        }
        return "";
    }

    public void addActionListener(ActionListener al) {
        this.al = al;
    }

    public void createGroupPopup() {
        popup = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Add group");

        ActionListener lst = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object o = getLastSelectedPathComponent();
                if (o instanceof Group) {
                    al.actionPerformed(new ActionEvent(o, ActionEvent.ACTION_PERFORMED, ActionCommands.ADD_GROUP));
                } else if (o instanceof Bookmark) {
                    al.actionPerformed(new ActionEvent(o, ActionEvent.ACTION_PERFORMED, ActionCommands.ADD_BOOKMARK));
                }
            }
        };
        menuItem.addActionListener(lst);
        popup.add(menuItem);
    }

    class PopupListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (getLastSelectedPathComponent() != null) {
                if (getLastSelectedPathComponent() instanceof Group) {
                    if (e.isPopupTrigger()) {
                        popup.show(e.getComponent(), e.getX(), e.getY());
                    }
                } else {
                    log.trace("Selected item is not a group");
                }
            } else {
                log.trace("Nothing selected in tree");
            }
        }
    }

    class BookmarkCellEditorListener implements CellEditorListener {

        public void editingStopped(ChangeEvent e) {

        }

        public void editingCanceled(ChangeEvent e) {
        }
    }

    class LeafCellEditor extends DefaultTreeCellEditor {

        public LeafCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
            super(tree, renderer);
        }

        public LeafCellEditor(JTree tree, DefaultTreeCellRenderer renderer,
                TreeCellEditor editor) {
            super(tree, renderer, editor);
        }

        public boolean isCellEditable(EventObject event) {
            Object node = tree.getLastSelectedPathComponent();
            if (node instanceof Bookmark) {
                return false;
            }
            boolean returnValue = super.isCellEditable(event);
            if (returnValue) {
                if ((node != null) && (node instanceof Group)) {
                    Group g = (Group) node;
                    returnValue = !g.hasParent();
                }
            }
            return returnValue;
        }
    }
}
