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
package net.jakobnielsen.aptivator.dialog;

import net.jakobnielsen.aptivator.filter.PdfFileFilter;

import javax.swing.JFileChooser;

/**
 * Export chooser dialog.
 *
 * @author <a href="mailto:jakobnielsen@gmail.com">Jakob Vad Nielsen</a>
 */
public class AptivatorExportChooser extends JFileChooser {

    public AptivatorExportChooser() {
        super();
        super.addChoosableFileFilter(new PdfFileFilter());
        super.setFileSelectionMode(JFileChooser.FILES_ONLY);
    }
}
