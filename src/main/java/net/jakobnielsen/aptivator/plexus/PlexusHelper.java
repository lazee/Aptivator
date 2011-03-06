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

package net.jakobnielsen.aptivator.plexus;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Plexus helper.
 *
 * @author <a href="mailto:jakobnielsen@gmail.com">Jakob Vad Nielsen</a>
 */
public final class PlexusHelper {

    private PlexusHelper() {
        // Intentional
    }
    
    /**
     * Start the Plexus container.
     *
     * @throws org.codehaus.plexus.PlexusContainerException if any
     */
    public static PlexusContainer startPlexusContainer()
            throws PlexusContainerException {

                Map context = new HashMap();
        context.put("basedir", new File("").getAbsolutePath());

        ContainerConfiguration containerConfiguration = new DefaultContainerConfiguration();
        containerConfiguration.setName("Doxia");
        containerConfiguration.setContext(context);

        return new DefaultPlexusContainer(containerConfiguration);
    }

}
