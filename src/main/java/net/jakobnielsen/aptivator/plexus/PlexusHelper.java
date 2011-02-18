package net.jakobnielsen.aptivator.plexus;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PlexusHelper {

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

    /**
     * Stop the Plexus container.
     */
    public static void stopPlexusContainer(PlexusContainer plexus) {
        if (plexus == null) {
            return;
        }

        plexus.dispose();
        plexus = null;
    }

}
