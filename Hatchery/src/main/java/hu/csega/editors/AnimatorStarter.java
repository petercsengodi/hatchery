package hu.csega.editors;

import java.io.File;

import hu.csega.editors.anm.layer1.opengl.AnimatorConnector;
import hu.csega.editors.anm.components.Component3DView;
import hu.csega.editors.anm.components.ComponentExtractPartList;
import hu.csega.editors.anm.components.ComponentOpenGLExtractor;
import hu.csega.editors.anm.components.ComponentOpenGLTransformer;
import hu.csega.editors.anm.components.ComponentPartListView;
import hu.csega.editors.anm.components.ComponentRefreshViews;
import hu.csega.editors.anm.components.ComponentWireFrameConverter;
import hu.csega.editors.anm.components.ComponentWireFrameRenderer;
import hu.csega.editors.anm.components.ComponentWireFrameTransformer;
import hu.csega.editors.anm.components.stubs.StubWireFrameTransformer;
import hu.csega.editors.anm.layer1.swing.components.partlist.AnimatorPartListView;
import hu.csega.editors.anm.layer1.swing.wireframe.AnimatorWireFrameConverter;
import hu.csega.editors.anm.layer1.swing.wireframe.AnimatorWireFrameTransformer;
import hu.csega.editors.anm.layer1.swing.wireframe.AnimatorWireFrameView;
import hu.csega.editors.anm.layer1.view3d.Animator3DView;
import hu.csega.editors.anm.layer1.view3d.AnimatorOpenGLExtractor;
import hu.csega.editors.anm.layer1.view3d.AnimatorOpenGLTransformer;
import hu.csega.editors.anm.layer2.transformation.AnimatorExtractPartList;
import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.editors.anm.layer4.data.model.AnimatorRefreshViews;
import hu.csega.editors.anm.layer1.swing.AnimatorUIComponents;
import hu.csega.editors.common.resources.FileResourceAdapter;
import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.games.common.ApplicationStarter;
import hu.csega.games.common.Connector;
import hu.csega.games.library.MeshLibrary;
import hu.csega.games.library.TextureLibrary;
import hu.csega.games.library.util.FileUtil;
import hu.csega.games.units.UnitStore;
import hu.csega.toolshed.logging.Level;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

/**
 * Responsible for running the Animator tool.
 * A single instance in the JVM, accessible by any components, and
 * links all the components together.
 * However, not static methods are used, as writing a testable code is prio 1!
 */
public class AnimatorStarter {

	public static final String DEFAULT_TEXTURE_FILE = "res/textures/ship2.jpg";

	private static final Level LOGGING_LEVEL = Level.INFO;
	private static Logger logger;


	public static void main(String[] args) {

		////////////////////////////////////////////////////////////////////////////////////////////////
		// 1. Initialize logging:

		LoggerFactory.setDefaultLevel(LOGGING_LEVEL);
		logger = LoggerFactory.createLogger(AnimatorStarter.class);
		logger.info("Starting tool.");


		////////////////////////////////////////////////////////////////////////////////////////////////
		// 4. Checking current directory

		ResourceAdapter resourceAdapter = new FileResourceAdapter("Hatchery");
		UnitStore.registerInstance(ResourceAdapter.class, resourceAdapter);

		AnimatorUIComponents ui = UnitStore.instance(AnimatorUIComponents.class);
		FileUtil files = new FileUtil("Hatchery");

		ui.userDirectory = System.getProperty("user.dir");
		logger.info("User directory: " + ui.userDirectory);
		logger.info("Project path: " + files.projectPath());
		String workspacePath = files.workspacePath();
		logger.info("Workspace path: " + workspacePath);
		String resourcesPath = workspacePath + "GameResources" + File.separator;
		logger.info("Resources path: " + resourcesPath);
		ui.texturesDirectory = resourcesPath + "textures" + File.separator;
		logger.info("Textures directory: " + ui.texturesDirectory);
		ui.meshesDirectory = resourcesPath + "meshes" + File.separator;
		logger.info("Meshes directory: " + ui.meshesDirectory);
		ui.animationsDirectory = resourcesPath + "animations" + File.separator;
		logger.info("Animations directory: " + ui.animationsDirectory);

		String shaderRoot = resourcesPath + "shaders";
		logger.info("Shader root (no separator at the end): " + shaderRoot);

		String texturesRoot = resourcesPath + "textures";
		logger.info("Textures root (no separator at the end): " + texturesRoot);

		String meshesRoot = resourcesPath + "meshes";
		logger.info("Meshes root (no separator at the end): " + meshesRoot);


		////////////////////////////////////////////////////////////////////////////////////////////////
		// 2. Register components and providers:

		UnitStore.registerInstance(FileUtil.class, files);
		UnitStore.registerInstance(TextureLibrary.class, new TextureLibrary(texturesRoot));
		UnitStore.registerInstance(MeshLibrary.class, new MeshLibrary(meshesRoot));

		Connector connector = new AnimatorConnector(shaderRoot);
		UnitStore.registerInstance(Connector.class, connector);

		UnitStore.registerDefaultImplementation(AnimatorModel.class, AnimatorModel.class);
		UnitStore.registerDefaultImplementation(AnimatorUIComponents.class, AnimatorUIComponents.class);
		UnitStore.registerDefaultImplementation(ComponentRefreshViews.class, AnimatorRefreshViews.class);
		UnitStore.registerDefaultImplementation(ComponentPartListView.class, AnimatorPartListView.class);
		UnitStore.registerDefaultImplementation(ComponentOpenGLTransformer.class, AnimatorOpenGLTransformer.class);
		UnitStore.registerDefaultImplementation(ComponentOpenGLExtractor.class, AnimatorOpenGLExtractor.class);
		UnitStore.registerDefaultImplementation(ComponentExtractPartList.class, AnimatorExtractPartList.class);
		UnitStore.registerDefaultImplementation(Component3DView.class, Animator3DView.class);
		UnitStore.registerDefaultImplementation(ComponentWireFrameConverter.class, AnimatorWireFrameConverter.class);
		UnitStore.registerDefaultImplementation(ComponentWireFrameTransformer.class, AnimatorWireFrameTransformer.class);
		UnitStore.registerDefaultImplementation(ComponentWireFrameRenderer.class, AnimatorWireFrameView.class);

		////////////////////////////////////////////////////////////////////////////////////////////////
		// 3. Register test instances if needed:

		UnitStore.registerInstance(ComponentWireFrameTransformer.class, new StubWireFrameTransformer());
		// UnitStore.registerInstance(ComponentOpenGLTransformer.class, new StubOpenGLTransformer());
		// UnitStore.registerInstance(ComponentExtractPartList.class, new StubExtractPartList());
		// UnitStore.registerInstance(Component3DView.class, new Stub3DView());

		////////////////////////////////////////////////////////////////////////////////////////////////
		// 5. Starting application:

		ApplicationStarter starter = new ApplicationStarter(connector);
		starter.start(args);
	}

}