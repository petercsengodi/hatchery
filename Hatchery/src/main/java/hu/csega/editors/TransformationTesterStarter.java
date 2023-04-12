package hu.csega.editors;

import hu.csega.editors.common.resources.FileResourceAdapter;
import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.editors.transformations.layer1.presentation.opengl.TransformationTesterConnector;
import hu.csega.games.common.ApplicationStarter;
import hu.csega.games.common.Connector;
import hu.csega.games.library.TextureLibrary;
import hu.csega.games.library.util.FileUtil;
import hu.csega.games.units.UnitStore;
import hu.csega.toolshed.logging.Level;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.io.File;

public class TransformationTesterStarter {

	private static final Level LOGGING_LEVEL = Level.TRACE;
	private static Logger logger;

	public static FileUtil FILES;
	public static TextureLibrary TEXTURES;

	public static void main(String[] args) {
		LoggerFactory.setDefaultLevel(LOGGING_LEVEL);
		logger = LoggerFactory.createLogger(TransformationTesterStarter.class);
		logger.info("Starting translation tester.");

		ResourceAdapter resourceAdapter = new FileResourceAdapter("Hatchery");
		UnitStore.registerInstance(ResourceAdapter.class, resourceAdapter);

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

		Connector connector = new TransformationTesterConnector(shaderRoot);
		ApplicationStarter starter = new ApplicationStarter(connector);
		starter.start(args);
	}

}