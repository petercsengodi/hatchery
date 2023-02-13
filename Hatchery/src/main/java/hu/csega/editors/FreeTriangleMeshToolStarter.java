package hu.csega.editors;

import hu.csega.editors.ftm.layer1.presentation.opengl.FreeTriangleMeshConnector;
import hu.csega.games.common.ApplicationStarter;
import hu.csega.games.common.Connector;
import hu.csega.games.library.TextureLibrary;
import hu.csega.games.library.util.FileUtil;
import hu.csega.toolshed.logging.Level;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.io.File;

/**
 * Responsible for running the Free Triangle Mesh tool.
 * A single instance in the JVM, accessible by any components, and
 * links all the components together.
 * However, not static methods are used, as writing a testable code is prio 1!
 */
public class FreeTriangleMeshToolStarter {

	public static final String DEFAULT_TEXTURE_FILE = "res/textures/ship2.jpg";

	private static final Level LOGGING_LEVEL = Level.TRACE;
	private static Logger logger;

	public static FileUtil FILES;
	public static TextureLibrary TEXTURES;

	public static void main(String[] args) {
		LoggerFactory.setDefaultLevel(LOGGING_LEVEL);
		logger = LoggerFactory.createLogger(FreeTriangleMeshToolStarter.class);
		logger.info("Starting tool.");

		FILES = new FileUtil("Hatchery");

		String projectPath = FILES.projectPath();
		logger.info("Project path: " + projectPath);
		String workspacePath = FILES.workspacePath();
		logger.info("Workspace path: " + workspacePath);
		String resourcesPath = workspacePath + "GameResources" + File.separator;
		logger.info("Resources path: " + resourcesPath);

		String shaderRoot = resourcesPath + "shaders";
		logger.info("Shader root (no separator at the end): " + shaderRoot);

		String texturesRoot = resourcesPath + "textures";
		logger.info("Textures root (no separator at the end): " + texturesRoot);
		TEXTURES = new TextureLibrary(texturesRoot);

		Connector connector = new FreeTriangleMeshConnector(shaderRoot);
		ApplicationStarter starter = new ApplicationStarter(connector);
		starter.start(args);
	}

}