package hu.csega.editors.anm.layer1.swing;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import hu.csega.editors.anm.layer1.swing.components.jointlist.AnimatorJointListItem;
import hu.csega.editors.anm.layer1.swing.components.jointlist.AnimatorJointListModel;
import hu.csega.editors.anm.layer1.swing.controllers.AnimatorPartEditorPanel;
import hu.csega.editors.anm.layer1.swing.components.partlist.AnimatorPartListItem;
import hu.csega.editors.anm.layer1.swing.components.partlist.AnimatorPartListModel;
import hu.csega.editors.anm.layer1.swing.controllers.AnimatorSceneLerpPanel;
import hu.csega.editors.anm.layer1.swing.controllers.AnimatorSceneSelectorPanel;
import hu.csega.editors.anm.layer1.swing.wireframe.AnimatorWireFrameView;
import hu.csega.editors.ftm.layer1.presentation.swing.view.FreeTriangleMeshTextureView;
import hu.csega.games.engine.intf.GameWindow;

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

	public AnimatorPartListModel partListModel;
	public JList<AnimatorPartListItem> partList;
	public JScrollPane partListScrollPane;

	//////////////////////////////////////////////////////////////////////////////
	// Joint list

	public AnimatorJointListModel jointListModel;
	public JList<AnimatorJointListItem> jointList;
	public JScrollPane jointListScrollPane;

	//////////////////////////////////////////////////////////////////////////////
	// Tabs / Views

	public JTabbedPane tabbedPane;
	public AnimatorWireFrameView panelFront;
	public AnimatorWireFrameView panelTop;
	public AnimatorWireFrameView panelSide;
	public AnimatorWireFrameView panelWireFrame;
	public JPanel panel3D;
	public FreeTriangleMeshTextureView textureView;
	public AnimatorPartEditorPanel partEditorPanel;
	public AnimatorSceneSelectorPanel sceneSelectorPanel;
	public AnimatorSceneLerpPanel sceneLerpPanel;

}
