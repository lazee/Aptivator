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
package net.jakobnielsen.aptivator;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.net.URL;

public class AptivatorUtil {

    public static final String EXPORT_ICON = "001_53";

    public static final String REFRESH_ICON = "001_39";

    public static final String BROWSER_ICON = "001_40";

    private AptivatorUtil() {
        // Intentional
    }

    public static URL createImageUrl(String imageName) {
        String imgLocation = "/META-INF/icons/shadow/standart/png/24x24/" + imageName + ".png";
        return AptivatorUtil.class.getResource(imgLocation);
    }

    public static Icon createIcon(String name, String description) {
        return new ImageIcon(AptivatorUtil.createImageUrl(name), description);
    }



}
