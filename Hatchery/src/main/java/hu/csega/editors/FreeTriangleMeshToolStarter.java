package hu.csega.editors;

import hu.csega.editors.common.resources.FileResourceAdapter;
import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.editors.ftm.layer1.presentation.opengl.FreeTriangleMeshConnector;
import hu.csega.games.common.ApplicationStarter;
import hu.csega.games.common.Connector;
import hu.csega.games.library.TextureLibrary;
import hu.csega.games.library.pixel.v1.Pixel;
import hu.csega.games.library.pixel.v1.PixelLibrary;
import hu.csega.games.library.pixel.v1.PixelSheet;
import hu.csega.games.library.util.FileUtil;
import hu.csega.games.units.UnitStore;
import hu.csega.toolshed.logging.Level;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Responsible for running the Free Triangle Mesh tool.
 * A single instance in the JVM, accessible by any components, and
 * links all the components together.
 * However, not static methods are used, as writing a testable code is prio 1!
 */
public class FreeTriangleMeshToolStarter {

	public static final String DEFAULT_TEXTURE_FILE = "ship2.jpg";

	private static final Level LOGGING_LEVEL = Level.TRACE;
	private static Logger logger;

	public static TextureLibrary TEXTURES;
	public static PixelLibrary PIXELS;
	public static BufferedImage[] SPRITES;

	public static void main(String[] args) {
		LoggerFactory.setDefaultLevel(LOGGING_LEVEL);
		logger = LoggerFactory.createLogger(FreeTriangleMeshToolStarter.class);
		logger.info("Starting tool.");

		ResourceAdapter resourceAdapter = new FileResourceAdapter("Hatchery");
		UnitStore.registerInstance(ResourceAdapter.class, resourceAdapter);

		TEXTURES = new TextureLibrary(resourceAdapter.textureFolder());
		File pixelLibraryFile = new File(resourceAdapter.projectRoot() + File.separator + "tmp.p16");
		PIXELS = PixelLibrary.load(pixelLibraryFile);

		SPRITES = new BufferedImage[1];
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

		String shaderRoot = resourceAdapter.shaderRoot();
		String textureRoot = resourceAdapter.textureRoot();
		String meshRoot = resourceAdapter.meshRoot();
		String animationRoot = resourceAdapter.animationRoot();

		Connector connector = new FreeTriangleMeshConnector(shaderRoot, textureRoot, meshRoot, animationRoot);
		ApplicationStarter starter = new ApplicationStarter(connector);
		starter.start(args);
	}

}