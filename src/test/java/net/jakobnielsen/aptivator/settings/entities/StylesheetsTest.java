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

package net.jakobnielsen.aptivator.settings.entities;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StylesheetsTest {

    @Test
    public void testStyleSheets() {

        StyleSheet s1 = cs("1", "t1", "f1");
        StyleSheet s2 = cs("2", "t2", "f2");
        StyleSheet s3 = cs("3", "t3", "f3");
        
        List<StyleSheet> lst = new ArrayList<StyleSheet>();
        lst.add(s1);
        lst.add(s2);

        Stylesheets ss = new Stylesheets(lst);

        Assert.assertEquals(2, ss.getStylesheets().size());

        ss.add(s3);

        Assert.assertEquals(3, ss.getStylesheets().size());

        ss.remove(s1);

        Assert.assertEquals(2, ss.getStylesheets().size());

    }

    private StyleSheet cs(String id, String title, String file) {
        StyleSheet s = new StyleSheet();
        s.setId(id);
        s.setTitle(title);
        s.setSrcFile(new File(file));
        return s;
    }
    

}
