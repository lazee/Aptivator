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

package net.jakobnielsen.aptivator.doxia;

import net.jakobnielsen.aptivator.LogListModel;

import java.io.IOException;
import java.io.OutputStream;

public class DoxiaLogWrapperOutputStream extends OutputStream {

    private LogListModel logListModel;

    private StringBuilder stringBuilder;

    private boolean isError;

    public DoxiaLogWrapperOutputStream(LogListModel logListModel, boolean isError) {
        this.stringBuilder = new StringBuilder();
        this.logListModel = logListModel;
        this.isError = isError;
    }

    @Override
    public void write(int b) throws IOException {
        if (b == 10) {
            // Newline
            if (stringBuilder.length() > 0) {
                if (isError) {
                    logListModel.addElement("STDERR: " + stringBuilder.toString());
                } else {
                    logListModel.addElement("STDOUT: " + stringBuilder.toString());
                }

                stringBuilder = new StringBuilder();
            }
        } else {
            stringBuilder.append((char) b);
        }
    }
}
