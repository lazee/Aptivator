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

public class StyleSheetTest {

    @Test
    public void testStyleSheet() {

    }

    @Test
    public void testStyleSheetEquals() {

        StyleSheet ss = new StyleSheet();
        ss.setId("TheID");
        ss.setSrcFile(new File("foo"));
        ss.setTitle("The title");

        StyleSheet ss2 = new StyleSheet();
        ss2.setId("TheID");
        ss2.setSrcFile(new File("foo"));
        ss2.setTitle("The title");

        Assert.assertEquals(ss2, ss);
        
    }

}
