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

import java.io.IOException;
import java.io.Writer;

/**
 * Writer for log messages
 *
 * @author <a href="mailto:jakobnielsen@gmail.com">Jakob Vad Nielsen</a>
 */
public class LogWriter extends Writer {

    private LogListModel model;

    public LogWriter(LogListModel model) {
        this.model = model;
    }

    @Override
    public void write(int c) throws IOException {
        model.addElement("" + c);
    }

    @Override
    public void write(char[] cbuf) throws IOException {
        model.addElement(new String(cbuf));
    }

    @Override
    public void write(String str) throws IOException {
        model.addElement(str);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        model.addElement(str);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        model.addElement(new String(cbuf));
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
    }
}
