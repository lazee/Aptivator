This documentation describes how to release Aptivator for Mac

1. First release the standard Aptivator package with maven

2. Copy the generated release zip package into mac_os/resources/aptivator.zip

3. Go into the mac_os folder

4. Edit the version property in build.properties

5. Run 'ant release'.

6. Inside the dist folder you should now find the distribution files that can be uploaded to the download server(s)