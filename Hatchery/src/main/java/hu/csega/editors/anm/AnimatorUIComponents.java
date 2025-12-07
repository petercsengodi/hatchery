package hu.csega.editors.anm;

import hu.csega.editors.anm.components.ComponentAnimationXYSideView;
import hu.csega.editors.anm.components.ComponentAnimationXZSideView;
import hu.csega.editors.anm.components.ComponentAnimationZYSideView;
import hu.csega.editors.anm.components.ComponentMeshTextureView;
import hu.csega.editors.anm.components.ComponentMeshXYSideView;
import hu.csega.editors.anm.components.ComponentMeshXZSideView;
import hu.csega.editors.anm.components.ComponentMeshZYSideView;
import hu.csega.editors.anm.layer1Views.AnimatorJointListView;
import hu.csega.editors.anm.layer1Views.AnimatorPartListView;
import hu.csega.editors.anm.layer1Views.swing.data.AnimatorJointListItem;
import hu.csega.editors.anm.layer1Views.swing.data.AnimatorPartListItem;
import hu.csega.editors.anm.layer1Views.swing.controllers.AnimatorPartEditorPanel;
import hu.csega.editors.anm.layer1Views.swing.controllers.AnimatorSceneLerpPanel;
import hu.csega.editors.anm.layer1Views.swing.controllers.AnimatorSceneSelectorPanel;
import hu.csega.editors.anm.layer1Views.swing.menu.AnimatorMenu;
import hu.csega.editors.anm.layer1Views.swing.views.*;
import hu.csega.editors.anm.layer1Views.swing.wireframe.AnimatorWireFrameView;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.intf.GameWindow;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;

import javax.swing.*;
import java.awt.*;

import hu.csega.editors.anm.layer1Views.swing.layout.root.AnimatorRootLayoutManager;
import hu.csega.games.units.UnitStore;

public class AnimatorUIComponents {

	//////////////////////////////////////////////////////////////////////////////
	// 3D engine

	public GameWindow gameWindow;

	//////////////////////////////////////////////////////////////////////////////
	// Dialogs

	public JFrame frame;
	public JFileChooser addNewPartFile;

	//////////////////////////////////////////////////////////////////////////////
	// Part list

	public JList<AnimatorPartListItem> partList;
	public JScrollPane partListScrollPane;

	//////////////////////////////////////////////////////////////////////////////
	// Joint list

	public JList<AnimatorJointListItem> jointList;
	public JScrollPane jointListScrollPane;

	//////////////////////////////////////////////////////////////////////////////
	// Tabs / Views

	public JTabbedPane tabbedPane;
	public AnimatorViewCanvas panelFront;
	public AnimatorViewCanvas panelTop;
	public AnimatorViewCanvas panelSide;
	public AnimatorWireFrameView panelWireFrame;
	public JPanel panel3D;
	public AnimatorViewCanvas textureView;
	public AnimatorPartEditorPanel partEditorPanel;
	public AnimatorSceneSelectorPanel sceneSelectorPanel;
	public AnimatorSceneLerpPanel sceneLerpPanel;

	public AnimatorAnimationView animationXYSideView;
	public AnimatorAnimationView animationXZSideView;
	public AnimatorAnimationView animationZYSideView;
	public AnimatorMeshXYSideView meshXYSideView;
	public AnimatorMeshXZSideView meshXZSideView;
	public AnimatorMeshZYSideView meshZYSideView;
	public AnimatorMeshTextureView meshTextureView;

	public void buildUI(GameWindow gameWindow, GameEngineFacade facade, String textureRoot) {
		this.gameWindow = gameWindow;
		this.gameWindow.setFullScreen(true);
		if(gameWindow instanceof JFrame) {
			this.frame = (JFrame) gameWindow;
		}

		this.animationXYSideView = new AnimatorAnimationView(facade, this.panelFront, 0, 1);
		this.animationXZSideView = new AnimatorAnimationView(facade, this.panelFront, 0, 2);
		this.animationZYSideView = new AnimatorAnimationView(facade, this.panelFront, 2, 1);
		this.meshXYSideView = new AnimatorMeshXYSideView(facade, this.panelFront);
		this.meshXZSideView = new AnimatorMeshXZSideView(facade, this.panelTop);
		this.meshZYSideView = new AnimatorMeshZYSideView(facade, this.panelSide);
		this.meshTextureView = new AnimatorMeshTextureView(facade, this.textureView, textureRoot);

		UnitStore.registerInstance(ComponentAnimationXYSideView.class, animationXYSideView);
		UnitStore.registerInstance(ComponentAnimationXZSideView.class, animationXZSideView);
		UnitStore.registerInstance(ComponentAnimationZYSideView.class, animationZYSideView);
		UnitStore.registerInstance(ComponentMeshXYSideView.class, meshXYSideView);
		UnitStore.registerInstance(ComponentMeshXZSideView.class, meshXZSideView);
		UnitStore.registerInstance(ComponentMeshZYSideView.class, meshZYSideView);
		UnitStore.registerInstance(ComponentMeshTextureView.class, meshTextureView);

		Container contentPane = this.frame.getContentPane();
		AnimatorRootLayoutManager layout = new AnimatorRootLayoutManager();
		contentPane.setLayout(layout);

		AnimatorMenu.createMenuForJFrame(this.frame, facade);

		this.tabbedPane = new JTabbedPane();

		this.panelFront = new AnimatorViewCanvas(facade);
		this.panelFront.registerView(AnimationPersistent.class, animationXYSideView);
		this.panelFront.registerView(FreeTriangleMeshModel.class, meshXYSideView);
		this.tabbedPane.addTab("Front", this.panelFront);

		this.panelTop = new AnimatorViewCanvas(facade);
		this.panelTop.registerView(AnimationPersistent.class, animationXZSideView);
		this.panelTop.registerView(FreeTriangleMeshModel.class, meshXZSideView);
		this.tabbedPane.addTab("Top", this.panelTop);

		this.panelSide = new AnimatorViewCanvas(facade);
		this.panelSide.registerView(AnimationPersistent.class, animationZYSideView);
		this.panelSide.registerView(FreeTriangleMeshModel.class, meshZYSideView);
		this.tabbedPane.addTab("Side", this.panelSide);

		this.panelWireFrame = new AnimatorWireFrameView(0, 1);
		this.tabbedPane.addTab("Wireframe", this.panelWireFrame);

		this.panel3D = new JPanel();
		this.panel3D.setLayout(new GridLayout(1, 1));
		this.tabbedPane.addTab("3D Canvas", this.panel3D);

		this.textureView = new AnimatorViewCanvas(facade);
		this.textureView.registerView(FreeTriangleMeshModel.class, meshTextureView);
		this.tabbedPane.addTab("Texture", this.textureView);

		contentPane.add(AnimatorRootLayoutManager.MULTI_TAB, this.tabbedPane);

		AnimatorPartListView partListView = UnitStore.instance(AnimatorPartListView.class);
		this.partList = new JList<>(partListView);
		this.partList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.partListScrollPane = new JScrollPane(this.partList);
		this.partList.addListSelectionListener(partListView);

		AnimatorJointListView jointListView = UnitStore.instance(AnimatorJointListView.class);
		this.jointList = new JList<>(jointListView);
		this.jointList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.jointListScrollPane = new JScrollPane(this.jointList);
		this.jointList.addListSelectionListener(jointListView);

		this.partEditorPanel = new AnimatorPartEditorPanel();
		this.sceneSelectorPanel = new AnimatorSceneSelectorPanel();
		this.sceneLerpPanel = new AnimatorSceneLerpPanel();

		// Now the model exists.
		contentPane.add(AnimatorRootLayoutManager.PARTS_LIST, this.partListScrollPane);
		contentPane.add(AnimatorRootLayoutManager.PARTS_SETTINGS, this.partEditorPanel);
		contentPane.add(AnimatorRootLayoutManager.SCENE_SELECTOR, this.sceneSelectorPanel);
		contentPane.add(AnimatorRootLayoutManager.SCENE_LERP, this.sceneLerpPanel);

		layout.updateAfterAllComponentsAreAdded();
	}
	

}
