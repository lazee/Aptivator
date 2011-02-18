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
package net.jakobnielsen.aptivator.settings.entities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecentFiles {

    private final int maxSize;

    private List<String> recentFiles = new ArrayList<String>();

    public RecentFiles(int maxSize) {
        this.maxSize = maxSize;
    }

    public void addFile(File f) {
        if (f != null && recentFiles.contains(f.getAbsolutePath())) {
            recentFiles.remove(f.getAbsolutePath());
            recentFiles.add(0, f.getAbsolutePath());
        } else if (f != null) {
            if (recentFiles.size() >= maxSize) {
                recentFiles.remove(recentFiles.size() - 1);
            }
            recentFiles.add(0, f.getAbsolutePath());
        }
    }

    public List<File> getRecentFiles() {
        List<File> files = new ArrayList<File>();
        for (String s : recentFiles) {
            files.add(new File(s));
        }
        return files;
    }

    public void clear() {
        recentFiles.clear();
    }

    public void setRecentFiles(List<String> recentFiles) {
        this.recentFiles = recentFiles;
    }
}
