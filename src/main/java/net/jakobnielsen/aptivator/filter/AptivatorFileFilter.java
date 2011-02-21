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
package net.jakobnielsen.aptivator.filter;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class AptivatorFileFilter extends FileFilter {

    private String extension;

    public AptivatorFileFilter(String extension) {
        super();
        this.extension = extension;
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        String filename = file.getName();
        return filename.endsWith("." + extension);

    }

    @Override
    public String getDescription() {
        return "*." + extension;
    }
}
