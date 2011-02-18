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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Book {

    private File descriptorFile;

    private String title;

    private String author;

    private List<Chapter> chapters;

    public Book() {
        chapters = new ArrayList<Chapter>();
    }

    public File getDescriptorFile() {
        return descriptorFile;
    }

    public void setDescriptorFile(File descriptorFile) {
        this.descriptorFile = descriptorFile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    public void addChapter(Chapter chapter) {
        chapters.add(chapter);
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Book book = (Book) o;

        if (!descriptorFile.equals(book.descriptorFile)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return descriptorFile.hashCode();
    }

    @Override
    public String toString() {
        return title;
    }
}
