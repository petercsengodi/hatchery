package hu.csega.superstition.engines.opengl;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import hu.csega.games.adapters.opengl.OpenGLGameAdapter;
import hu.csega.games.common.Connector;
import hu.csega.games.engine.env.Environment;
import hu.csega.games.engine.impl.GameEngine;
import hu.csega.games.engine.intf.GameAdapter;
import hu.csega.games.engine.intf.GameCanvas;
import hu.csega.games.engine.intf.GameDescriptor;
import hu.csega.games.engine.intf.GameEngineStep;
import hu.csega.games.engine.intf.GameWindow;
import hu.csega.games.engine.intf.GameWindowListener;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

public class SuperstitionOpenGLConnector implements Connector, GameWindow {

	private List<GameWindowListener> listeners = new ArrayList<>();

	private String shaderRoot;
	private String textureRoot;
	private String meshRoot;
	private String animationRoot;

	public SuperstitionOpenGLConnector(String shaderRoot, String textureRoot, String meshRoot, String animationRoot) {
		this.shaderRoot = shaderRoot;
		this.textureRoot = textureRoot;
		this.meshRoot = meshRoot;
		this.animationRoot = animationRoot;
	}

	@Override
	public void initialize() {
	}

	@Override
	public void run(Environment env) {
		logger.info(className() + " start run()");

		GameEngine ge = startGameEngine(env);
		logger.info("Received GameEngine instance: " + (ge != null));

		logger.info(className() + " end run()");
	}

	@Override
	public void register(GameWindowListener listener) {
		listeners.add(listener);
	}

	@Override
	public void add(GameCanvas canvas, Container container) {
	}

	@Override
	public void setFullScreen(boolean fullScreen) {
	}

	@Override
	public void showWindow() {
	}

	@Override
	public void closeWindow() {
		for(GameWindowListener listener: listeners) {
			listener.onFinishingWork();
		}
	}

	@Override
	public void closeApplication() {
		for(GameWindowListener listener: listeners) {
			listener.onFinishingWork();
		}
	}

	@Override
	public void dispose() {
		logger.info(className() + " start dispose()");

		logger.info(className() + " end dispose()");
	}

	@Override
	public void repaintEverything() {
	}

	private GameEngine startGameEngine(Environment env) {
		GameDescriptor descriptor = new GameDescriptor();
		descriptor.setId("superstition");
		descriptor.setTitle("Superstition – Witty Little Wizard");
		descriptor.setVersion("v00.00.0002");
		descriptor.setDescription("My first complete(?) Java/OpenGL game.");

		GameAdapter adapter = new OpenGLGameAdapter(shaderRoot, textureRoot, meshRoot, animationRoot, true);
		GameEngine engine = GameEngine.create(descriptor, adapter);

		engine.step(GameEngineStep.INIT, new SuperstitionInitStep());
		engine.step(GameEngineStep.MODIFY, new SuperstitionModifyStep());
		engine.step(GameEngineStep.RENDER, new SuperstitionRenderStep());

		engine.startInNewWindow(env);
		return engine;
	}

	private String className() {
		return getClass().getSimpleName();
	}

	private static final Logger logger = LoggerFactory.createLogger(SuperstitionOpenGLConnector.class);
}
