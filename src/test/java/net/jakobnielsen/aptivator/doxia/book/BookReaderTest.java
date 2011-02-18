package net.jakobnielsen.aptivator.doxia.book;

import net.jakobnielsen.aptivator.plexus.PlexusHelper;
import org.codehaus.plexus.PlexusContainer;
import org.junit.Test;

public class BookReaderTest {

    @Test
    public void testRead() throws Exception {

        PlexusContainer plexus = PlexusHelper.startPlexusContainer();

        Book book = null;

        /*
        try {
            //book = BookReader.read(plexus, new File("/Users/jakob/idea/Aptivator/src/test/resources/testbook/test-book.xml"));
            book = BookReader.read(plexus, new File("/Users/jakob/idea/seamstress/seamstress-webapp/src/book/seamstress-book.xml"));
        } catch (BookReaderException e) {
            Assert.fail("Got exception: " + e.getMessage());
        }

        Assert.assertNotNull("Book was not expected to be null", book);
        Assert.assertEquals("Jakob", book.getAuthor());
        Assert.assertEquals("Test Book", book.getTitle());

        Assert.assertEquals(2, book.getChapters().size());

        Chapter chapter1 = (Chapter) book.getChapters().get(0);
        Assert.assertEquals("foo", chapter1.getId());
        Assert.assertEquals("Foo", chapter1.getTitle());

        Chapter chapter2 = (Chapter) book.getChapters().get(1);
        Assert.assertEquals("bar", chapter2.getId());
        Assert.assertEquals("Bar", chapter2.getTitle());

        Assert.assertNotNull(chapter1.getSections());
        
        Section section1 = (Section)chapter1.getSections().get(0);

        Assert.assertEquals( "foo1", section1.getId());
        Assert.assertEquals("/Users/jakob/idea/Aptivator/src/test/resources/testbook/foo/foo1.apt", section1.getFile().getAbsolutePath());
        Assert.assertEquals("Foo 1", section1.getTitle());
*/

    }


}
