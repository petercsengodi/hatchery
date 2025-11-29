package hu.csega.editors.anm.layer1Views.swing;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import hu.csega.editors.anm.layer1Views.swing.components.jointlist.AnimatorJointListItem;
import hu.csega.editors.anm.layer1Views.swing.components.jointlist.AnimatorJointListModel;
import hu.csega.editors.anm.layer1Views.swing.controllers.AnimatorPartEditorPanel;
import hu.csega.editors.anm.layer1Views.swing.components.partlist.AnimatorPartListItem;
import hu.csega.editors.anm.layer1Views.swing.components.partlist.AnimatorPartListModel;
import hu.csega.editors.anm.layer1Views.swing.controllers.AnimatorSceneLerpPanel;
import hu.csega.editors.anm.layer1Views.swing.controllers.AnimatorSceneSelectorPanel;
import hu.csega.editors.anm.layer1Views.swing.views.AnimatorViewCanvas;
import hu.csega.editors.anm.layer1Views.swing.wireframe.AnimatorWireFrameView;
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
	public AnimatorViewCanvas panelFront;
	public AnimatorViewCanvas panelTop;
	public AnimatorViewCanvas panelSide;
	public AnimatorWireFrameView panelWireFrame;
	public JPanel panel3D;
	public AnimatorViewCanvas textureView;
	public AnimatorPartEditorPanel partEditorPanel;
	public AnimatorSceneSelectorPanel sceneSelectorPanel;
	public AnimatorSceneLerpPanel sceneLerpPanel;

}
