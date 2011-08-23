/*
 * Copyright (c) 2008-2011 Jakob Vad Nielsen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.jakobnielsen.aptivator.renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;

/**
 * <code>FSScrollPane</code> is a JScrollPane set up to support keyboard navigation of an XHTML/XML document rendered
 * with Flying Saucer.
 *
 * <p>In particular, it assigns key bindings to the view's {@link javax.swing.InputMap} for page-up, page-down,
 * line-up/down, page-start and page-end. The amount the document scrolls is based on the current viewport and the
 * current line height. If the view is resized, the scroll increment is automatically adjusted. Using FSScrollPane to
 * display an {@link org.xhtmlrenderer.simple.XHTMLPanel} should save you time as your users will have standard
 * keyboard
 * navigation out of the box.</p>
 *
 * <p>To use <code>FSScrollPane</code>, just instantiate it and add your XHTMLPanel on instantiation:</p>
 *
 * <pre>
 * XHTMLPanel panel = new XHTMLPanel();
 * FSScrollPane scroll = new FSScrollPane(view);
 * </pre>
 *
 * <p>The current input mappings to keys are:
 *
 * <dl>
 *
 * <dt>Scroll to Start<dt><dd>CONTROL-HOME or HOME</dd> <dt>Scroll Up 1 Page<dt><dd>PAGEUP</dd>
 *
 * <dt>Scroll Up 1 Line<dt><dd>UP-ARROW</dd> <dt>Scroll to Bottom<dt><dd>CONTROL-END or END</dd>
 *
 * <dt>Scroll Down 1 Page<dt></dt><dd>PAGEDOWN</dd> <dt>Scroll Down 1 Line<dt><dd>DOWN-ARROW</dd>
 *
 * </dl>
 *
 * <p>This class declares six constant strings you can use if you want to override one of these default settings on the
 * <code>InputMap</code>; these Strings will be trigger the relevant <code>Action</code> associated with the scrolling.
 * To change the key binding for "Scroll to Top" to <code>Alt-Home</code>, do this:</p>
 *
 * <pre>
 * panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
 * put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, KeyEvent.ALT_MASK), FSScrollPane.PAGE_START);
 * </pre>
 *
 * @author Patrick Wright
 */
public class AptivatorScrollPane extends JScrollPane {

    private static final long serialVersionUID = 2L;

    /**
     * Constant used for mapping a key binding to "scroll down 1 page"
     */
    public static final String PAGE_DOWN = "page-down";

    /**
     * Constant used for mapping a key binding to "scroll up 1 page"
     */
    public static final String PAGE_UP = "page-up";

    /**
     * Constant used for mapping a key binding to "scroll down 1 line"
     */
    public static final String LINE_DOWN = "down";

    /**
     * Constant used for mapping a key binding to "scroll up 1 line"
     */
    public static final String LINE_UP = "up";

    /**
     * Constant used for mapping a key binding to "scroll to end of document"
     */
    public static final String PAGE_END = "page-end";

    /**
     * Constant used for mapping a key binding to "scroll to top of document"
     */
    public static final String PAGE_START = "page-start";

    /* Instantiates a new FSScrollPane around the given Panel; see class documentation. */
    public AptivatorScrollPane(JPanel aview) {
        super(aview, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
        getVerticalScrollBar().setUnitIncrement(15);
    }

    /* @Override */
    public void setViewportView(Component view) {
        setPreferredSize(new Dimension((int) view.getSize().getWidth(), (int) view.getSize().getHeight()));
        if (view instanceof JComponent) {
            setDefaultInputMap((JComponent) view);
            setDefaultActionMap((JComponent) view);
        }
        addResizeListener(view);
        super.setViewportView(view);
    }

    /* Assigns the default keyboard bindings on the view for document navigation. */
    private void setDefaultInputMap(JComponent view) {
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
                put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), PAGE_DOWN);
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
                put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), PAGE_UP);
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
                put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), LINE_DOWN);
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
                put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), LINE_UP);
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
                put(KeyStroke.getKeyStroke(KeyEvent.VK_END, KeyEvent.CTRL_MASK), PAGE_END);
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
                put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), PAGE_END);
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
                put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, KeyEvent.CTRL_MASK), PAGE_START);
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
                put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), PAGE_START);

    }

    /* Assigns the default Actions for document navigation on the view. */
    private void setDefaultActionMap(JComponent view) {
        view.getActionMap().put(PAGE_DOWN,
                new AbstractAction() {
                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent evt) {
                        JScrollBar sb = getVerticalScrollBar();
                        sb.getModel().setValue(sb.getModel().getValue() + sb.getBlockIncrement(1));
                    }
                });
        view.getActionMap().put(PAGE_END,
                new AbstractAction() {
                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent evt) {
                        JScrollBar sb = getVerticalScrollBar();
                        sb.getModel().setValue(sb.getModel().getMaximum());
                    }
                });
        view.getActionMap().put(PAGE_UP,
                new AbstractAction() {
                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent evt) {
                        JScrollBar sb = getVerticalScrollBar();
                        sb.getModel().setValue(sb.getModel().getValue() - sb.getBlockIncrement(-1));
                    }
                });
        view.getActionMap().put(PAGE_START,
                new AbstractAction() {
                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent evt) {
                        JScrollBar sb = getVerticalScrollBar();
                        sb.getModel().setValue(0);
                    }
                });
        view.getActionMap().put(LINE_DOWN,
                new AbstractAction() {
                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent evt) {
                        JScrollBar sb = getVerticalScrollBar();
                        sb.getModel().setValue(sb.getModel().getValue() + sb.getUnitIncrement(1));
                    }
                });
        view.getActionMap().put(LINE_UP,
                new AbstractAction() {
                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent evt) {
                        JScrollBar sb = getVerticalScrollBar();
                        sb.getModel().setValue(sb.getModel().getValue() - sb.getUnitIncrement(-1));
                    }
                });
    }

    /* Adds a component listener on the view for resize events, to adjust the scroll increment. */
    private void addResizeListener(Component view) {
        view.addComponentListener(new ComponentAdapter() {
            /** Invoked when the component's size changes. Reset scrollable increment, because
             * page-down/up is relative to current view size.
             */
            public void componentResized(ComponentEvent e) {
                JScrollBar bar = getVerticalScrollBar();

                // NOTE: use the scroll pane size--the XHTMLPanel size is a virtual size of the entire
                // page

                // want to page down leaving the current line at the bottom be the first at the top
                // this will only work once unit increment is set correctly; multiplier is a workaround (PWW 28-01-05)
                int incr = (int) (getSize().getHeight() - (bar.getUnitIncrement(1) * 3));
                getVerticalScrollBar().setBlockIncrement(incr);
            }
        });
    }
}
