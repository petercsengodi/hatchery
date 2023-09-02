package hu.csega.demo.head;

import static hu.csega.demo.head.GenerateHeadDemo.DEMO_PROJECT;

import hu.csega.editors.AnimatorStarter;
import hu.csega.editors.common.resources.FileResourceAdapter;
import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.toolshed.logging.Level;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class InstallHeadDemo {

    private static final Level LOGGING_LEVEL = Level.INFO;
    private static Logger logger;

    public static void main(String[] args) throws Exception {
        ////////////////////////////////////////////////////////////////////////////////////////////////
        // 1. Initialize logging:

        LoggerFactory.setDefaultLevel(LOGGING_LEVEL);
        logger = LoggerFactory.createLogger(AnimatorStarter.class);
        logger.info("Starting demo install...");


        ////////////////////////////////////////////////////////////////////////////////////////////////
        // 4. Checking current directory

        ResourceAdapter resourceAdapter = new FileResourceAdapter("Hatchery");
        File demoFolder = new File(resourceAdapter.projectRoot() + "demo" + File.separator + DEMO_PROJECT);
        File webFolder = new File("/var/www/html/" + DEMO_PROJECT);

        logger.info("Deleting everything from: " + webFolder.getAbsolutePath());
        clean(webFolder);

        logger.info("Copying resources from: " + demoFolder.getAbsolutePath() + " To: " + webFolder.getAbsolutePath());
        recursiveCopy(demoFolder, webFolder);
    }

    private static void clean(File webFolder) {
        if(!webFolder.exists() || !webFolder.isDirectory()) {
            logger.error("Does not exist, or not a directory: " + webFolder.getAbsolutePath());
            throw new RuntimeException("Does not exist, or not a directory: " + webFolder.getAbsolutePath());
        }

        String[] list = webFolder.list();
        if(list == null)
            return;

        for(String name : list) {
            if(name.equals(".") || name.equals(".."))
                continue;

            File fileToDelete = new File(webFolder.getAbsolutePath() + File.separator + name);
            if(fileToDelete.isDirectory())
                clean(fileToDelete);

            if(!fileToDelete.delete())
                throw new RuntimeException("Could not delete: " + fileToDelete.getAbsolutePath());
        }
    }

    private static void recursiveCopy(File demoFolder, File webFolder) {
        if(webFolder.exists() && !webFolder.isDirectory()) {
            logger.error("Already exists, and not a directory: " + webFolder.getAbsolutePath());
            throw new RuntimeException("Already exists, and not a directory: " + webFolder.getAbsolutePath());
        }

        if(!webFolder.exists()) {
            if(!webFolder.mkdir())
                throw new RuntimeException("Could not create directory: " + webFolder.getAbsolutePath());
        }

        String[] list = demoFolder.list();
        if(list == null)
            return;

        for(String name : list) {
            if(name.equals(".") || name.equals(".."))
                continue;

            File fileToCopy = new File(demoFolder.getAbsolutePath() + File.separator + name);
            File targetFile = new File(webFolder.getAbsolutePath() + File.separator + name);
            if(fileToCopy.isDirectory())
                recursiveCopy(fileToCopy, targetFile);

            try {
                Files.copy(fileToCopy.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch(IOException ex) {
                throw new RuntimeException("Could not copy file: " + fileToCopy.getAbsolutePath(), ex);
            }
        }
    }

}
