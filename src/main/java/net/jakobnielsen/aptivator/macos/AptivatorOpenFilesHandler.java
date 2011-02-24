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

package net.jakobnielsen.aptivator.macos;

import com.apple.eawt.AppEvent;
import com.apple.eawt.OpenFilesHandler;
import net.jakobnielsen.aptivator.AptivatorActions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class AptivatorOpenFilesHandler implements OpenFilesHandler {

    private ActionListener actionListener;

    public AptivatorOpenFilesHandler(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @Override
    public void openFiles(AppEvent.OpenFilesEvent openFilesEvent) {
        List<File> fileList = openFilesEvent.getFiles();
        if (fileList != null && fileList.size() > 0) {
            actionListener.actionPerformed(
                    new ActionEvent(fileList.get(0), ActionEvent.ACTION_PERFORMED, AptivatorActions.OPEN_FILE));
        }
    }
}
