package hu.csega.editors;

import hu.csega.editors.common.resources.FileResourceAdapter;
import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.editors.transformations.layer1.presentation.opengl.TransformationTesterConnector;
import hu.csega.games.common.ApplicationStarter;
import hu.csega.games.common.Connector;
import hu.csega.games.library.TextureLibrary;
import hu.csega.games.units.UnitStore;
import hu.csega.toolshed.logging.Level;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

public class TransformationTesterStarter {

	private static final Level LOGGING_LEVEL = Level.TRACE;
	private static Logger logger;

	public static TextureLibrary TEXTURES;

	public static void main(String[] args) {
		LoggerFactory.setDefaultLevel(LOGGING_LEVEL);
		logger = LoggerFactory.createLogger(TransformationTesterStarter.class);
		logger.info("Starting translation tester.");

		ResourceAdapter resourceAdapter = new FileResourceAdapter("Hatchery");
		UnitStore.registerInstance(ResourceAdapter.class, resourceAdapter);

		TEXTURES = new TextureLibrary(resourceAdapter.textureFolder());

		String shaderRoot = resourceAdapter.shaderRoot();
		String textureRoot = resourceAdapter.textureRoot();
		String meshRoot = resourceAdapter.meshRoot();
		String animationRoot = resourceAdapter.animationRoot();

		Connector connector = new TransformationTesterConnector(shaderRoot, textureRoot, meshRoot, animationRoot);
		ApplicationStarter starter = new ApplicationStarter(connector);
		starter.start(args);
	}

}