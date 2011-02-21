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

package net.jakobnielsen.aptivator.settings;

import net.jakobnielsen.aptivator.settings.entities.RecentFiles;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class RecentFilesTest {

    @Test
    public void testAddFile() throws Exception {
        RecentFiles rf = new RecentFiles(3);
        rf.addFile(new File("/foo1"));
        rf.addFile(new File("/foo2"));
        rf.addFile(new File("/foo3"));
        rf.addFile(new File("/foo4"));

        List<File> lst = rf.getRecentFiles();
        Assert.assertEquals(3, lst.size());
        Assert.assertEquals("/foo4", lst.get(0).getAbsolutePath());
        Assert.assertEquals("/foo3", lst.get(1).getAbsolutePath());
        Assert.assertEquals("/foo2", lst.get(2).getAbsolutePath());

        rf.addFile(new File("/foo2"));
        lst = rf.getRecentFiles();
        Assert.assertEquals("/foo2", lst.get(0).getAbsolutePath());
        Assert.assertEquals("/foo4", lst.get(1).getAbsolutePath());

    }
   
}
