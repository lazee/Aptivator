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
