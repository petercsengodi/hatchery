package hu.csega.editors.ftm.layer1.presentation.opengl;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;

import hu.csega.editors.ftm.layer1.presentation.swing.dialogs.FreeTriangleMeshDialogs;
import hu.csega.editors.ftm.layer1.presentation.swing.menu.FreeTriangleMeshMenu;
import hu.csega.editors.ftm.layer1.presentation.swing.view.FreeTriangleMeshTexture;
import hu.csega.editors.ftm.layer1.presentation.swing.view.FreeTriangleMeshTreeMapping;
import hu.csega.editors.ftm.layer1.presentation.swing.view.FreeTriangleMeshWireframe;
import hu.csega.editors.ftm.layer1.presentation.swing.view.FreeTriangleMeshXYSideView;
import hu.csega.editors.ftm.layer1.presentation.swing.view.FreeTriangleMeshXZSideView;
import hu.csega.editors.ftm.layer1.presentation.swing.view.FreeTriangleMeshZYSideView;
import hu.csega.games.adapters.opengl.OpenGLCanvas;
import hu.csega.games.adapters.opengl.OpenGLGameAdapter;
import hu.csega.games.common.Connector;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.env.Environment;
import hu.csega.games.engine.impl.GameEngine;
import hu.csega.games.engine.intf.GameAdapter;
import hu.csega.games.engine.intf.GameCanvas;
import hu.csega.games.engine.intf.GameDescriptor;
import hu.csega.games.engine.intf.GameEngineStep;
import hu.csega.games.engine.intf.GameWindow;
import hu.csega.games.engine.intf.GameWindowListener;
import hu.csega.games.units.UnitStore;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

public class FreeTriangleMeshConnector implements Connector, GameWindow {

	private List<GameWindowListener> listeners = new ArrayList<>();
	private FreeTriangleMeshDialogs dialogs;
	private GameWindow gameWindow;
	private JTree tree;

	private final String shaderRoot;
	private final String textureRoot;
	private final String meshRoot;
	private final String animationRoot;

	public FreeTriangleMeshConnector(String shaderRoot, String textureRoot, String meshRoot, String animationRoot) {
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

		GameDescriptor descriptor = new GameDescriptor();
		descriptor.setId("ftm");
		descriptor.setTitle("Free Triangle Mesh Tool");
		descriptor.setVersion("v00.00.0002");
		descriptor.setDescription("A tool for creating vertices and triangles based upon them.");
		descriptor.setMouseCentered(false);

		GameAdapter adapter = new OpenGLGameAdapter(shaderRoot, textureRoot, meshRoot, animationRoot, false);
		GameEngine engine = GameEngine.create(descriptor, adapter);
		GameEngineFacade facade = engine.getFacade();

		UnitStore.registerInstance(GameEngineFacade.class, facade);

		FreeTriangleMeshRenderStep renderer = new FreeTriangleMeshRenderStep();

		engine.step(GameEngineStep.INIT, new FreeTriangleMeshInitStep());
		engine.step(GameEngineStep.MODIFY, new FreeTriangleMeshModifyStep());
		engine.step(GameEngineStep.RENDER, renderer);

		engine.getControl().registerKeyListener(new FreeTriangleMeshKeyListener());

		gameWindow = adapter.createWindow(engine, env);
		gameWindow.setFullScreen(true);

		JFrame frame = (JFrame) gameWindow;
		KeyListener keyListener = (KeyListener) gameWindow;
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new GridLayout(2, 2));
		contentPane.add(new FreeTriangleMeshXZSideView(facade));

		FreeTriangleMeshMenu.createMenuForJFrame(frame, facade);

		engine.startIn(gameWindow, contentPane);

		contentPane.add(new FreeTriangleMeshXYSideView(facade));

		GameCanvas gameCanvas = engine.getCanvas();
		FreeTriangleMeshWireframe wireframeCanvas = new FreeTriangleMeshWireframe(facade, gameCanvas);

		JTabbedPane bottomRightTab = new JTabbedPane();
		bottomRightTab.addKeyListener(keyListener);
		bottomRightTab.addTab("ZY Side View", new FreeTriangleMeshZYSideView(facade));
		bottomRightTab.addTab("Wireframe", wireframeCanvas);
		bottomRightTab.addTab("Texture Window", new FreeTriangleMeshTexture(facade, textureRoot));

		FreeTriangleMeshTreeMapping treeModel = new FreeTriangleMeshTreeMapping(facade);
		tree = new JTree(treeModel);
		JScrollPane scrollPaneForTree = new JScrollPane(tree);
		bottomRightTab.addTab("Tree View", scrollPaneForTree);

		contentPane.add(bottomRightTab);

		FreeTriangleMeshMouseController mouseController = new FreeTriangleMeshMouseController(facade, gameCanvas, wireframeCanvas);

		Component component = ((OpenGLCanvas) gameCanvas).getRealCanvas();
		component.addMouseListener(mouseController);
		component.addMouseMotionListener(mouseController);
		component.addMouseWheelListener(mouseController);

		dialogs = new FreeTriangleMeshDialogs(frame);
		UnitStore.registerInstance(FreeTriangleMeshDialogs.class, dialogs);

		return engine;
	}

	private String className() {
		return getClass().getSimpleName();
	}

	@Override
	public void repaintEverything() {
		if(gameWindow != null)
			gameWindow.repaintEverything();
		if(tree != null) {
			tree.invalidate();
			tree.repaint();
		}
	}

	private static final Logger logger = LoggerFactory.createLogger(FreeTriangleMeshConnector.class);
}
