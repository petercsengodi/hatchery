package hu.csega.superstition;

import hu.csega.editors.common.resources.FileResourceAdapter;
import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.games.common.ApplicationStarter;
import hu.csega.games.common.Connector;
import hu.csega.games.units.UnitStore;
import hu.csega.superstition.engines.opengl.SuperstitionOpenGLConnector;
import hu.csega.toolshed.logging.Level;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

// A Fizz-buzz way of running the game

/**
 * Responsible for running the whole game.
 * A single instance in the JVM, accessible by any components, and
 * links all the components together.
 * However, not static methods are used, as writing a testable code is prio 1!
 */
public class SuperstitionGameStarter {

	private static final Level LOGGING_LEVEL = Level.TRACE;
	private static Logger logger;

	private static long randomSeed = System.currentTimeMillis();
	public static Random RANDOM;

	public static void main(String[] args) {
		LoggerFactory.setDefaultLevel(LOGGING_LEVEL);
		logger = LoggerFactory.createLogger(SuperstitionGameStarter.class);

		ResourceAdapter resourceAdapter = new FileResourceAdapter("Hatchery");
		UnitStore.registerInstance(ResourceAdapter.class, resourceAdapter);

		String seedingFile = resourceAdapter.projectRoot() + "temp/superstition.seeds.txt";
		try (FileWriter fw = new FileWriter(seedingFile, true)) { // append
			fw.write("\n" + (new Date()) + " – Random seed: " + randomSeed);
		} catch(Exception ex) {
			logger.warning("WARNING: Could not write seeding file.");
		}

		logger.info("Starting game with seed: " + randomSeed);
		RANDOM = new Random(randomSeed);

		String shaderRoot = resourceAdapter.shaderRoot();
		String textureRoot = resourceAdapter.textureRoot();
		String meshRoot = resourceAdapter.meshRoot();
		String animationRoot = resourceAdapter.animationRoot();

		Connector connector = new SuperstitionOpenGLConnector(shaderRoot, textureRoot, meshRoot, animationRoot);
		ApplicationStarter starter = new ApplicationStarter(connector);
		starter.start(args);
	}
}
