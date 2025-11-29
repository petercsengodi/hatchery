package hu.csega.editors;

import hu.csega.editors.anm.components.Component3DView;
import hu.csega.editors.anm.components.ComponentExtractJointList;
import hu.csega.editors.anm.components.ComponentExtractPartList;
import hu.csega.editors.anm.components.ComponentJointListView;
import hu.csega.editors.anm.components.ComponentOpenGLExtractor;
import hu.csega.editors.anm.components.ComponentOpenGLTransformer;
import hu.csega.editors.anm.components.ComponentPartListView;
import hu.csega.editors.anm.components.ComponentRefreshViews;
import hu.csega.editors.anm.components.ComponentWireFrameConverter;
import hu.csega.editors.anm.components.ComponentWireFrameRenderer;
import hu.csega.editors.anm.components.ComponentWireFrameTransformer;
import hu.csega.editors.anm.layer1Views.opengl.AnimatorConnector;
import hu.csega.editors.anm.layer1Views.swing.AnimatorUIComponents;
import hu.csega.editors.anm.layer1Views.swing.components.jointlist.AnimatorJointListView;
import hu.csega.editors.anm.layer1Views.swing.components.partlist.AnimatorPartListView;
import hu.csega.editors.anm.layer1Views.swing.wireframe.AnimatorWireFrameConverter;
import hu.csega.editors.anm.layer1Views.swing.wireframe.AnimatorWireFrameTransformer;
import hu.csega.editors.anm.layer1Views.swing.wireframe.AnimatorWireFrameView;
import hu.csega.editors.anm.layer1Views.view3d.Animator3DView;
import hu.csega.editors.anm.layer1Views.view3d.AnimatorOpenGLExtractor;
import hu.csega.editors.anm.layer1Views.view3d.AnimatorOpenGLTransformer;
import hu.csega.editors.anm.layer2Transformation.AnimatorExtractJointList;
import hu.csega.editors.anm.layer2Transformation.AnimatorExtractPartList;
import hu.csega.editors.anm.layer4Data.model.AnimatorModel;
import hu.csega.editors.anm.layer4Data.model.AnimatorRefreshViews;
import hu.csega.editors.common.resources.FileResourceAdapter;
import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.games.common.ApplicationStarter;
import hu.csega.games.common.Connector;
import hu.csega.games.library.MeshLibrary;
import hu.csega.games.library.TextureLibrary;
import hu.csega.games.library.pixel.v1.Pixel;
import hu.csega.games.library.pixel.v1.PixelLibrary;
import hu.csega.games.library.pixel.v1.PixelSheet;
import hu.csega.games.units.UnitStore;
import hu.csega.toolshed.logging.Level;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Responsible for running the Animator tool.
 * A single instance in the JVM, accessible by any components, and
 * links all the components together.
 * However, not static methods are used, as writing a testable code is prio 1!
 */
public class AnimatorStarter {

	private static final Level LOGGING_LEVEL = Level.INFO;

	public static PixelLibrary PIXELS;
	public static BufferedImage[] SPRITES;

	public static void main(String[] args) {

		////////////////////////////////////////////////////////////////////////////////////////////////
		// 1. Initialize logging:

		LoggerFactory.setDefaultLevel(LOGGING_LEVEL);
		Logger logger = LoggerFactory.createLogger(AnimatorStarter.class);
		logger.info("Starting tool.");


		////////////////////////////////////////////////////////////////////////////////////////////////
		// 4. Checking current directory

		ResourceAdapter resourceAdapter = new FileResourceAdapter("Hatchery");
		UnitStore.registerInstance(ResourceAdapter.class, resourceAdapter);


		////////////////////////////////////////////////////////////////////////////////////////////////
		// 9. Pictograms

		File pixelLibraryFile = new File(resourceAdapter.projectRoot() + File.separator + "tmp.p16");
		PIXELS = PixelLibrary.load(pixelLibraryFile);

		SPRITES = new BufferedImage[12];
		for(int i = 0; i < SPRITES.length; i++) {
			BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			SPRITES[i] = img;
			PixelSheet pixelSheet = PIXELS.get(i);

			for(int y = 0; y < 16; y++) {
				for(int x = 0; x < 16; x++) {
					Pixel[] column = pixelSheet.pixels[x];
					Pixel p = column[y];
					int argb = (p.alpha << 24) + (p.red << 16) + (p.green << 8) + (p.blue);
					img.setRGB(x, y, argb);
				}
			}
		}


		////////////////////////////////////////////////////////////////////////////////////////////////
		// 2. Register components and providers:

		// FIXME: Loads everything, it should not.
		UnitStore.registerInstance(TextureLibrary.class, new TextureLibrary(resourceAdapter.textureFolder()));
		UnitStore.registerInstance(MeshLibrary.class, new MeshLibrary(resourceAdapter.meshFolder()));

		String shaderRoot = resourceAdapter.shaderRoot();
		String textureRoot = resourceAdapter.textureRoot();
		String meshRoot = resourceAdapter.meshRoot();
		String animationRoot = resourceAdapter.animationRoot();

		Connector connector = new AnimatorConnector(shaderRoot, textureRoot, meshRoot, animationRoot);
		UnitStore.registerInstance(Connector.class, connector);

		UnitStore.registerDefaultImplementation(AnimatorModel.class, AnimatorModel.class);
		UnitStore.registerDefaultImplementation(AnimatorUIComponents.class, AnimatorUIComponents.class);
		UnitStore.registerDefaultImplementation(ComponentRefreshViews.class, AnimatorRefreshViews.class);
		UnitStore.registerDefaultImplementation(ComponentPartListView.class, AnimatorPartListView.class);
		UnitStore.registerDefaultImplementation(ComponentJointListView.class, AnimatorJointListView.class);
		UnitStore.registerDefaultImplementation(ComponentOpenGLTransformer.class, AnimatorOpenGLTransformer.class);
		UnitStore.registerDefaultImplementation(ComponentOpenGLExtractor.class, AnimatorOpenGLExtractor.class);
		UnitStore.registerDefaultImplementation(ComponentExtractPartList.class, AnimatorExtractPartList.class);
		UnitStore.registerDefaultImplementation(ComponentExtractJointList.class, AnimatorExtractJointList.class);
		UnitStore.registerDefaultImplementation(Component3DView.class, Animator3DView.class);
		UnitStore.registerDefaultImplementation(ComponentWireFrameConverter.class, AnimatorWireFrameConverter.class);
		UnitStore.registerDefaultImplementation(ComponentWireFrameTransformer.class, AnimatorWireFrameTransformer.class);
		UnitStore.registerDefaultImplementation(ComponentWireFrameRenderer.class, AnimatorWireFrameView.class);


		////////////////////////////////////////////////////////////////////////////////////////////////
		// 4. Starting application:

		ApplicationStarter starter = new ApplicationStarter(connector);
		starter.start(args);


	}
}