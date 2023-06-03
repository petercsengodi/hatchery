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

	public static void main(String[] args) {
		LoggerFactory.setDefaultLevel(LOGGING_LEVEL);
		logger = LoggerFactory.createLogger(SuperstitionGameStarter.class);
		logger.info("Starting game.");

		ResourceAdapter resourceAdapter = new FileResourceAdapter("Hatchery");
		UnitStore.registerInstance(ResourceAdapter.class, resourceAdapter);

		String shaderRoot = resourceAdapter.shaderRoot();
		String textureRoot = resourceAdapter.textureRoot();

		Connector connector = new SuperstitionOpenGLConnector(shaderRoot, textureRoot);
		ApplicationStarter starter = new ApplicationStarter(connector);
		starter.start(args);
	}
}
