package hu.csega.editors.anm.layer1.opengl;

import hu.csega.editors.anm.components.Component3DView;
import hu.csega.editors.anm.components.ComponentRefreshViews;
import hu.csega.editors.anm.layer1.swing.AnimatorUIComponents;
import hu.csega.editors.anm.layer1.swing.components.jointlist.AnimatorJointListModel;
import hu.csega.editors.anm.layer1.swing.components.partlist.AnimatorPartListModel;
import hu.csega.editors.anm.layer1.swing.controllers.AnimatorPartEditorPanel;
import hu.csega.editors.anm.layer1.swing.controllers.AnimatorSceneLerpPanel;
import hu.csega.editors.anm.layer1.swing.controllers.AnimatorSceneSelectorPanel;
import hu.csega.editors.anm.layer1.swing.menu.AnimatorMenu;
import hu.csega.editors.anm.layer1.swing.views.AnimatorAnimationView;
import hu.csega.editors.anm.layer1.swing.views.AnimatorMeshTextureView;
import hu.csega.editors.anm.layer1.swing.views.AnimatorMeshXYSideView;
import hu.csega.editors.anm.layer1.swing.views.AnimatorMeshXZSideView;
import hu.csega.editors.anm.layer1.swing.views.AnimatorMeshZYSideView;
import hu.csega.editors.anm.layer1.swing.views.AnimatorViewCanvas;
import hu.csega.editors.anm.layer1.swing.wireframe.AnimatorWireFrameView;
import hu.csega.editors.anm.ui.layout.root.AnimatorRootLayoutManager;
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
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.units.UnitStore;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class AnimatorConnector implements Connector, GameWindow {

	private final List<GameWindowListener> listeners = new ArrayList<>();
	private ComponentRefreshViews refreshViews;

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

		GameDescriptor descriptor = new GameDescriptor();
		descriptor.setId("anm");
		descriptor.setTitle("Animator Tool");
		descriptor.setVersion("v00.00.0001");
		descriptor.setDescription("A tool for creating small character animations.");
		descriptor.setMouseCentered(false);

		// Open GL View

		GameAdapter adapter = new OpenGLGameAdapter(shaderRoot, textureRoot, meshRoot, animationRoot, false);
		GameEngine engine = GameEngine.create(descriptor, adapter);
		GameEngineFacade facade = engine.getFacade();

		UnitStore.registerInstance(GameEngineFacade.class, facade);

		AnimatorInitStep animatorInitStep = new AnimatorInitStep();

		AnimatorRenderStep animatorRenderStep = new AnimatorRenderStep();
		Component3DView view = UnitStore.instance(Component3DView.class);
		view.setRenderer(animatorRenderStep);

		engine.step(GameEngineStep.INIT, animatorInitStep);
		engine.step(GameEngineStep.RENDER, animatorRenderStep);


		// Swing View(s)

		AnimatorUIComponents components = UnitStore.instance(AnimatorUIComponents.class);
		components.gameWindow = adapter.createWindow(engine, env);
		components.gameWindow.setFullScreen(true);
		logger.info("Window/Frame instance created: " + components.gameWindow.getClass().getName());

		components.frame = (JFrame) components.gameWindow;
		Container contentPane = components.frame.getContentPane();
		AnimatorRootLayoutManager layout = new AnimatorRootLayoutManager();
		contentPane.setLayout(layout);

		AnimatorMenu.createMenuForJFrame(components.frame, facade);

		components.tabbedPane = new JTabbedPane();

		components.panelFront = new AnimatorViewCanvas(facade);
		components.panelFront.registerView(AnimationPersistent.class, new AnimatorAnimationView(facade, components.panelFront, 0, 1));
		components.panelFront.registerView(FreeTriangleMeshModel.class, new AnimatorMeshXYSideView(facade, components.panelFront));
		components.tabbedPane.addTab("Front", components.panelFront);

		components.panelTop = new AnimatorViewCanvas(facade);
		components.panelTop.registerView(AnimationPersistent.class, new AnimatorAnimationView(facade, components.panelFront, 0, 2));
		components.panelTop.registerView(FreeTriangleMeshModel.class, new AnimatorMeshXZSideView(facade, components.panelTop));
		components.tabbedPane.addTab("Top", components.panelTop);

		components.panelSide = new AnimatorViewCanvas(facade);
		components.panelSide.registerView(AnimationPersistent.class, new AnimatorAnimationView(facade, components.panelFront, 2, 1));
		components.panelSide.registerView(FreeTriangleMeshModel.class, new AnimatorMeshZYSideView(facade, components.panelSide));
		components.tabbedPane.addTab("Side", components.panelSide);

		components.panelWireFrame = new AnimatorWireFrameView(0, 1);
		components.tabbedPane.addTab("Wireframe", components.panelWireFrame);

		components.panel3D = new JPanel();
		components.panel3D.setLayout(new GridLayout(1, 1));
		components.tabbedPane.addTab("3D Canvas", components.panel3D);

		components.textureView = new AnimatorViewCanvas(facade);
		components.textureView.registerView(FreeTriangleMeshModel.class, new AnimatorMeshTextureView(facade, components.textureView, textureRoot));
		components.tabbedPane.addTab("Texture", components.textureView);

		contentPane.add(AnimatorRootLayoutManager.MULTI_TAB, components.tabbedPane);

		// Adds canvas to content pane, but the model doesn't exist at this point.
		engine.startIn(components.gameWindow, components.panel3D);

		components.partListModel = new AnimatorPartListModel();
		components.partList = new JList<>(components.partListModel);
		components.partList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		components.partListScrollPane = new JScrollPane(components.partList);
		components.partList.addListSelectionListener(components.partListModel);
		components.partListModel.setJList(components.partList);

		components.jointListModel = new AnimatorJointListModel();
		components.jointList = new JList<>(components.jointListModel);
		components.jointList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		components.jointListScrollPane = new JScrollPane(components.jointList);
		components.jointList.addListSelectionListener(components.jointListModel);
		components.jointListModel.setJList(components.jointList);

		components.partEditorPanel = new AnimatorPartEditorPanel();
		components.sceneSelectorPanel = new AnimatorSceneSelectorPanel();
		components.sceneLerpPanel = new AnimatorSceneLerpPanel();

		// Now the model exists.
		contentPane.add(AnimatorRootLayoutManager.PARTS_LIST, components.partListScrollPane);
		contentPane.add(AnimatorRootLayoutManager.PARTS_SETTINGS, components.partEditorPanel);
		contentPane.add(AnimatorRootLayoutManager.SCENE_SELECTOR, components.sceneSelectorPanel);
		contentPane.add(AnimatorRootLayoutManager.SCENE_LERP, components.sceneLerpPanel);

		layout.updateAfterAllComponentsAreAdded();

		refreshViews = UnitStore.instance(ComponentRefreshViews.class);


		// Mouse control.

		GameCanvas canvas = engine.getCanvas();
		AnimatorMouseController mouseController = new AnimatorMouseController(canvas, facade);

		Component component = ((OpenGLCanvas) canvas).getRealCanvas();
		component.addMouseListener(mouseController);
		component.addMouseMotionListener(mouseController);
		component.addMouseWheelListener(mouseController);

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
