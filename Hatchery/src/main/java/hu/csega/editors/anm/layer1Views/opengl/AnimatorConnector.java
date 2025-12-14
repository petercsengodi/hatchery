package hu.csega.editors.anm.layer1Views.opengl;

import hu.csega.editors.AnimatorStarter;
import hu.csega.editors.anm.AnimatorUIComponents;
import hu.csega.editors.anm.common.CommonInvalidatable;
import hu.csega.editors.anm.components.ComponentOpenGLSetExtractor;
import hu.csega.editors.anm.components.ComponentRefreshViews;
import hu.csega.editors.anm.layer1Views.opengl.renderers.AnimatorAnimationRenderer;
import hu.csega.editors.anm.layer1Views.opengl.renderers.AnimatorMeshRenderer;
import hu.csega.games.adapters.opengl.OpenGLCanvas;
import hu.csega.games.adapters.opengl.OpenGLGameAdapter;
import hu.csega.games.common.Connector;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.env.Environment;
import hu.csega.games.engine.impl.GameEngine;
import hu.csega.games.engine.intf.*;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.units.UnitStore;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AnimatorConnector implements Connector, GameWindow {

	private final List<GameWindowListener> listeners = new ArrayList<>();
	private ComponentRefreshViews refreshViews;
	private GameEngineFacade facade;

	private final String shaderRoot;
	private final String textureRoot;
	private final String meshRoot;
	private final String animationRoot;

	public AnimatorConnector(String shaderRoot, String textureRoot, String meshRoot, String animationRoot) {
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

		startGameEngine(env);

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

	private GameEngine startGameEngine(Environment env) {

		// Open GL View

		GameAdapter adapter = new OpenGLGameAdapter(shaderRoot, textureRoot, meshRoot, animationRoot, false);
		GameEngine engine = GameEngine.create(AnimatorStarter.GAME_DESCRIPTOR, adapter);
		GameEngineFacade facade = engine.getFacade();
		UnitStore.registerInstance(GameEngineFacade.class, facade);

		AnimatorInitStep animatorInitStep = new AnimatorInitStep();

		ComponentOpenGLSetExtractor openGLExtractor = UnitStore.instance(ComponentOpenGLSetExtractor.class);
		AnimatorAnimationRenderer animationRenderer = new AnimatorAnimationRenderer(openGLExtractor);


		AnimatorMeshRenderer meshRenderer = new AnimatorMeshRenderer();

		AnimatorRenderStep animatorRenderStep = new AnimatorRenderStep();
		animatorRenderStep.registerRenderer(AnimationPersistent.class, animationRenderer);
		animatorRenderStep.registerRenderer(FreeTriangleMeshModel.class, meshRenderer);

		engine.step(GameEngineStep.INIT, animatorInitStep);
		engine.step(GameEngineStep.RENDER, animatorRenderStep);


		// Swing View(s)

		AnimatorUIComponents components = UnitStore.instance(AnimatorUIComponents.class);
		GameWindow gameWindow = adapter.createWindow(engine, env);
		logger.info("Window/Frame instance created: " + gameWindow.getClass().getName());
		components.buildUI(gameWindow, facade, textureRoot);


		// Adds canvas to content pane, but the model doesn't exist at this point.
		engine.startIn(components.gameWindow, components.panel3D);

		refreshViews = UnitStore.instance(ComponentRefreshViews.class);

		final GameCanvas canvas = engine.getCanvas();
		AnimatorMouseController mouseController = new AnimatorMouseController(canvas, facade);
		animatorRenderStep.setMouseController(mouseController);

		Component component = ((OpenGLCanvas) canvas).getRealCanvas();
		component.addMouseListener(mouseController);
		component.addMouseMotionListener(mouseController);
		component.addMouseWheelListener(mouseController);

		openGLExtractor.addDependent(canvas::repaint);

		return engine;
	}

	private String className() {
		return getClass().getSimpleName();
	}

	@Override
	public void repaintEverything() {
		if(refreshViews != null) {
			refreshViews.refreshAll();
		}
	}

	private static final Logger logger = LoggerFactory.createLogger(AnimatorConnector.class);
}
