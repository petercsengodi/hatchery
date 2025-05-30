package hu.csega.games.adapters.opengl;

import java.awt.Dimension;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

import hu.csega.games.adapters.opengl.gl2.OpenGLProfileGL2GLUAdapter;
import hu.csega.games.adapters.opengl.gl2.OpenGLProfileGL2TriangleAdapter;
import hu.csega.games.adapters.opengl.gl3.OpenGLProfileGL3Adapter;
import hu.csega.games.adapters.opengl.models.OpenGLModelStoreImpl;
import hu.csega.games.engine.env.Environment;
import hu.csega.games.engine.g3d.GameModelStore;
import hu.csega.games.engine.impl.GameEngine;
import hu.csega.games.engine.intf.GameAdapter;
import hu.csega.games.engine.intf.GameCanvas;
import hu.csega.games.engine.intf.GameWindow;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

public class OpenGLGameAdapter implements GameAdapter {

	private static final boolean FORCE_GL2_GLU = false;

	private String shaderRoot;
	private String textureRoot;
	private String meshRoot;
	private String animationRoot;
	private boolean lightingEnabled;

	public OpenGLGameAdapter(String shaderRoot, String textureRoot, String meshRoot, String animationRoot,
			boolean lightingEnabled) {
		this.shaderRoot = shaderRoot;
		this.textureRoot = textureRoot;
		this.meshRoot = meshRoot;
		this.animationRoot = animationRoot;
		this.lightingEnabled = lightingEnabled;
	}

	@Override
	public GameWindow createWindow(GameEngine engine, Environment env) {
		OpenGLFrame openGLFrame = new OpenGLFrame(engine, env);
		env.registerForDisposing(openGLFrame);
		return openGLFrame;
	}

	@Override
	public GameCanvas createCanvas(final GameEngine engine) {
		OpenGLProfileAdapter openGLProfileAdapter;
		GLProfile glProfile;

		if(FORCE_GL2_GLU) {
			
			try {
				logger.info("Trying to acquire GL2 profile...");
				glProfile = GLProfile.get(GLProfile.GL2);
				openGLProfileAdapter = new OpenGLProfileGL2GLUAdapter();
				logger.info("GL2 profile acquired, adapter: " + openGLProfileAdapter.getClass().getSimpleName());
			} catch(Exception ex2) {
				logger.error("Couldn't get GL2 for GLU! (" + ex2.getMessage() + ')');
				throw new RuntimeException("Couldn't get GLProfile!");
			}

		} else {

			try {
				logger.info("Trying to acquire GL3 profile...");
				glProfile = GLProfile.get(GLProfile.GL3);
				openGLProfileAdapter = new OpenGLProfileGL3Adapter();
				// openGLProfileAdapter = new OpenGLProfileGL3Adapter2();
				logger.info("GL3 profile acquired, adapter: " + openGLProfileAdapter.getClass().getSimpleName());
			} catch(Exception ex1) {
				logger.warning("Couldn't get GL3! (" + ex1.getMessage() + ')');

				try {
					logger.info("Trying to acquire GL2 profile...");
					glProfile = GLProfile.get(GLProfile.GL2);
					// openGLProfileAdapter = new OpenGLProfileGL2Adapter();
					openGLProfileAdapter = new OpenGLProfileGL2TriangleAdapter(lightingEnabled);
					logger.info("GL2 profile acquired, adapter: " + openGLProfileAdapter.getClass().getSimpleName());
				} catch(Exception ex2) {
					logger.error("Couldn't get GL2 either! (" + ex2.getMessage() + ')');
					throw new RuntimeException("Couldn't get GLProfile!");
				}
			}

		}

		OpenGLModelStoreImpl store = (OpenGLModelStoreImpl)engine.getStore();
		store.setAdapter(openGLProfileAdapter);
		store.setShaderRoot(shaderRoot);
		store.setTextureRoot(textureRoot);
		store.setMeshRoot(meshRoot);
		store.setAnimationRoot(animationRoot);

		GLCapabilities glCapabilities = new GLCapabilities(glProfile);
		OpenGLGraphics graphics = new OpenGLGraphics();
		GLEventListener eventListener = new OpenGLEventListener(engine, graphics);

		final GLCanvas glCanvas = new GLCanvas(glCapabilities);
		glCanvas.setPreferredSize(new Dimension(640, 480));
		glCanvas.addGLEventListener(eventListener);

		return new OpenGLCanvas(glCanvas);
	}

	@Override
	public GameModelStore createStore(GameEngine engine) {
		return new OpenGLModelStoreImpl();
	}

	private static final Logger logger = LoggerFactory.createLogger(OpenGLGameAdapter.class);
}
