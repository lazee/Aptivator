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

package net.jakobnielsen.aptivator;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ListModel for logging
 *
 * @author <a href="mailto:jakobnielsen@gmail.com">Jakob Vad Nielsen</a>
 */
public class LogListModel extends AbstractListModel {

    private static final int MAX_SIZE = 200;
    
    private final List<String> delegate;

    public LogListModel() {
        super();
        this.delegate = new ArrayList<String>();
    }

    @Override
    public int getSize() {
        return delegate.size();
    }

    @Override
    public Object getElementAt(int index) {
        return delegate.get(index);
    }

    public void clear() {
        int oldSize = delegate.size();
        delegate.clear();
        fireIntervalRemoved(this, 0, oldSize);
    }

    public void addElement(String element) {
        int index = delegate.size();
        delegate.add(element);
        fireIntervalAdded(this, index, index);
        if (delegate.size() > MAX_SIZE) {
            delegate.remove(0);
            fireIntervalRemoved(this, 0, 0);
        }
    }
}
