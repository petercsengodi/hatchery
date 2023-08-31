package hu.csega.demo.noise;

import hu.csega.editors.AnimatorStarter;
import hu.csega.editors.common.resources.FileResourceAdapter;
import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.toolshed.logging.Level;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class GenerateNoiseDemo {

    private static final Level LOGGING_LEVEL = Level.INFO;
    private static Logger logger;

    public static final String DEMO_PROJECT = "noise";

    private static ResourceAdapter resourceAdapter;

    public static void main(String[] args) throws Exception {
        ////////////////////////////////////////////////////////////////////////////////////////////////
        // 1. Initialize logging:

        LoggerFactory.setDefaultLevel(LOGGING_LEVEL);
        logger = LoggerFactory.createLogger(AnimatorStarter.class);
        logger.info("Starting demo install...");


        ////////////////////////////////////////////////////////////////////////////////////////////////
        // 4. Checking current directory

        resourceAdapter = new FileResourceAdapter("Hatchery");
        File mainJS = new File(resourceAdapter.projectRoot() + "demo" + File.separator + DEMO_PROJECT + File.separator + "main.js");
        logger.info("main.js currently exists: " + mainJS.exists() + " Absolute path: " + mainJS.getAbsolutePath());

        if(mainJS.exists()) {
            logger.info("Deleting main.js ...");
            if(mainJS.delete()) {
                logger.info("Deleted main.js.");
            } else {
                logger.info("Could not delete main.js.");
            }
        }

        File head = new File(resourceAdapter.projectRoot() + "src" + File.separator + "main" + File.separator + "java" +
                File.separator + "hu" + File.separator + "csega" + File.separator + "demo" + File.separator + DEMO_PROJECT +
                File.separator + DEMO_PROJECT + ".head.js");
        File tail = new File(resourceAdapter.projectRoot() + "src" + File.separator + "main" + File.separator + "java" +
                File.separator + "hu" + File.separator + "csega" + File.separator + "demo" + File.separator + DEMO_PROJECT +
                File.separator + DEMO_PROJECT + ".tail.js");

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(mainJS))) {
            write(writer, head);

            write(writer, tail);
        }
    }

    private static void write(OutputStreamWriter writer, InputStream stream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while((line = reader.readLine()) != null) {
                writer.write(line);
                writer.write("\n");
            }
        }

        writer.flush();
    }

    private static void write(OutputStreamWriter writer, File file) throws IOException {
        write(writer, new FileInputStream(file));
    }

}
