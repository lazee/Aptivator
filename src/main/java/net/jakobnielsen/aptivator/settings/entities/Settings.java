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

import java.awt.Dimension;
import java.io.File;

/**
 *
 * @author jakob
 */
public class Settings {

    private int refreshInterval = 60; // in seconds

    private Dimension appSize = new Dimension(600, 500);

    private final RecentFiles recentFiles = new RecentFiles(10);

    private Stylesheets stylesheets = new Stylesheets();

    public Dimension getAppSize() {
        return appSize;
    }

    public void setAppSize(Dimension appSize) {
        this.appSize = appSize;
    }

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public RecentFiles getRecentFiles() {
        return recentFiles;
    }

    public void addRecentFile(File f) {
        recentFiles.addFile(f);
    }

    public void clearRecentFiles() {
        recentFiles.clear();
    }
    
    public void setStylesheets(Stylesheets stylesheets) {
        this.stylesheets = stylesheets;
    }

    public Stylesheets getStylesheets() {
        return stylesheets;
    }

}
