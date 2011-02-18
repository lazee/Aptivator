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
