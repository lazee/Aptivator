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

public class Books {

    private List<Book> books;

    public Books() {
        books = new ArrayList<Book>();
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public void addBook(Book book) {
        if (book != null && !isBook(book.getDescriptorFile())) {
            books.add(book);
        }
    }

    public boolean isBook(File descriptor) {
        for (Book b : books) {
            if (b.getDescriptorFile().equals(descriptor)) {
                return true;
            }
        }
        return false;
    }

    public Book getBook(File descriptor) {
        for (Book b : books) {
            if (b.getDescriptorFile().equals(descriptor)) {
                return b;
            }
        }
        return null;
    }

    public void removeBook(Book book) {
        if (books.contains(book)) {
            books.remove(book);
        }
    }

    public List<File> getDescriptorFiles() {
        List<File> files = new ArrayList<File>();
        for (Book b : books) {
            files.add(b.getDescriptorFile());
        }
        return files;
    }

    @Override
    public String toString() {
        // FIXME We should add a tree renderer for books and remove this hack
        return "Books";
    }
}
