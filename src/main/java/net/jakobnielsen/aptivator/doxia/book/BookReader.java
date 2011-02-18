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
package net.jakobnielsen.aptivator.doxia.book;

import org.apache.maven.doxia.book.BookDoxiaException;
import org.apache.maven.doxia.book.context.BookContext;
import org.apache.maven.doxia.book.model.BookModel;
import org.apache.maven.doxia.book.services.indexer.BookIndexer;
import org.apache.maven.doxia.book.services.io.BookIo;
import org.apache.maven.doxia.book.services.validation.BookValidator;
import org.apache.maven.doxia.book.services.validation.DefaultBookValidator;
import org.apache.maven.doxia.book.services.validation.ValidationResult;
import org.apache.maven.doxia.index.IndexEntry;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BookReader {

    public static Books loadBooks(PlexusContainer plexus, List<File> files) {
        Books books = new Books();
        for (File f : files) {
            try {
                System.out.println("Loading book " + f.getAbsolutePath());
                books.addBook(BookReader.read(plexus, f));
            } catch (BookReaderException e) {
                System.out.println("Got exception while loading book: " + f.getAbsolutePath() + ": " + e.getMessage());
                // XXX Log book loading error
            }
        }
        return books;
    }

    public static Book read(PlexusContainer plexus, File f) throws BookReaderException {

        BookIndexer bookIndexer = null;
        try {
            bookIndexer = (BookIndexer) plexus.lookup(BookIndexer.ROLE);
        } catch (ComponentLookupException e) {
            throw new BookReaderException("Could not get book indexer from plexus", e);
        }
        BookIo bookIo = null;
        try {
            bookIo = (BookIo) plexus.lookup(BookIo.ROLE);
        } catch (ComponentLookupException e) {
            throw new BookReaderException("Could not get book IO from plexus", e);
        }

        BookModel bookModel = null;
        try {
            bookModel = bookIo.readBook(f);

            BookValidator validator = new DefaultBookValidator();
            ValidationResult result = validator.validateBook(bookModel);
            if (result.isAllOk()) {
                System.out.println("ALL IS OK!");
            } else {
                for (Object so : result.getErrors()) {
                    String s = (String) so;
                    System.out.println("ERROR: " + s);
                }
            }
        } catch (BookDoxiaException e) {
            throw new BookReaderException("Could not read book", e);
        }
        BookContext bookContext = new BookContext();
            bookContext.setBook(bookModel);
            List<File> files = BookReader.getFiles(f.getParentFile());
            bookIo.loadFiles(bookContext, files);
        try {
            bookIndexer.indexBook(bookModel, bookContext);
        } catch (BookDoxiaException e) {
            throw new BookReaderException("Could not index book: " + e.getMessage());
        }

        Book book = new Book();

            book.setAuthor(bookModel.getAuthor());
            book.setTitle(bookModel.getTitle());
            book.setDescriptorFile(f);

            // Chapters
            for (Object chapterObj : bookContext.getIndex().getChildEntries()) {
                IndexEntry chapterEntry = (IndexEntry) chapterObj;

                Chapter chapter = new Chapter();
                chapter.setId(chapterEntry.getId());
                chapter.setTitle(chapterEntry.getTitle());

                // Sectio‡ns
                for (Object sectionObj : chapterEntry.getChildEntries()) {
                    IndexEntry sectionEntry = (IndexEntry) sectionObj;

                    Section section = new Section();
                    section.setId(sectionEntry.getId());
                    section.setTitle(sectionEntry.getTitle());
                    section.setFile(new File(
                            f.getParentFile().getAbsolutePath() + File.separator + chapter.getId() + File.separator +
                                    section.getId() + ".apt"));

                    chapter.addSection(section);
                }
                book.addChapter(chapter);
            }

            return book;

    }

    private static List<File> getFiles(File f) {
        List<File> files = new ArrayList<File>();
        for (File file : f.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".apt")) {
                //System.out.println("Adding file : " + file.getAbsolutePath());
                files.add(file);
            } else if (file.isDirectory() && !file.getName().startsWith(".")) {
                List<File> subfiles = BookReader.getFiles(file);
                if (subfiles != null && subfiles.size() > 0) {
                    files.addAll(subfiles);
                }
            }
        }
        return files;
    }

}
