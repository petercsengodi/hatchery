package hu.csega.editors.anm.layer1.swing.menu;

import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.units.UnitStore;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class AnimatorMenu {

	public static final JMenuBar ANIMATION_MENU_BAR = new JMenuBar();
	public static final JMenuBar MESH_MENU_BAR = new JMenuBar();

	public static void createMenuForJFrame(JFrame frame, GameEngineFacade facade) {
		MESH_MENU_BAR.add(createMeshMenu(frame, facade));

		ANIMATION_MENU_BAR.add(createFileMenu(frame, facade));
		ANIMATION_MENU_BAR.add(createModelMenu(frame, facade));
		ANIMATION_MENU_BAR.add(createViewMenu(frame, facade));

		frame.setJMenuBar(ANIMATION_MENU_BAR);
	}

	private static JMenu createFileMenu(JFrame frame, GameEngineFacade facade) {
		ResourceAdapter resourceAdapter = UnitStore.instance(ResourceAdapter.class);

		JFileChooser saveDialog = new JFileChooser(resourceAdapter.animationFolder());
		saveDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
		saveDialog.setDialogTitle("Select file to save.");
		saveDialog.setMultiSelectionEnabled(false);
		saveDialog.setFileFilter(new FileNameExtensionFilter("Animator file", "anm"));
		saveDialog.setApproveButtonText("Save");

		JFileChooser openDialog = new JFileChooser(resourceAdapter.animationFolder());
		openDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
		openDialog.setDialogTitle("Select file to open.");
		openDialog.setMultiSelectionEnabled(false);
		openDialog.setFileFilter(new FileNameExtensionFilter("Animator file", "anm"));
		openDialog.setApproveButtonText("Open");

		JMenu menu = new JMenu("File");

		JMenuItem fileNew = new JMenuItem("New");
		fileNew.addActionListener(new AnimatorMenuNewFile(frame, facade));
		menu.add(fileNew);

		JMenuItem fileOpen = new JMenuItem("Open");
		fileOpen.addActionListener(new AnimatorMenuOpenFile(frame, openDialog, facade));
		menu.add(fileOpen);

		JMenuItem fileSave = new JMenuItem("Save");
		fileSave.addActionListener(new AnimatorMenuSaveFile(frame, saveDialog, facade));
		menu.add(fileSave);

		JMenuItem fileExit = new JMenuItem("Exit");
		fileExit.addActionListener(new AnimatorMenuExit(frame, saveDialog, facade));
		menu.add(fileExit);

		return menu;
	}

	private static JMenu createModelMenu(JFrame frame, GameEngineFacade facade) {
		JMenu menu = new JMenu("Model");

		JMenuItem addNewPart = new JMenuItem("Add New Part");
		addNewPart.addActionListener(new AnimatorMenuAddNewPart());
		menu.add(addNewPart);

		JMenuItem connectPart = new JMenuItem("Connect Part");
		connectPart.addActionListener(new AnimatorMenuConnectPart(frame));
		menu.add(connectPart);

		JMenuItem changePart = new JMenuItem("Change Part");
		changePart.addActionListener(new AnimatorMenuChangePart());
		menu.add(changePart);

		JMenuItem editPart = new JMenuItem("Edit Part");
		editPart.addActionListener(new AnimatorMenuEditPart());
		menu.add(editPart);

		return menu;
	}

	private static JMenu createViewMenu(JFrame frame, GameEngineFacade facade) {
		JMenu menu = new JMenu("View");

		JMenuItem refreshViews = new JMenuItem("Refresh All Views");
		refreshViews.addActionListener(new AnimatorMenuRefreshAllViews());
		menu.add(refreshViews);

		JMenuItem generateJSON = new JMenuItem("Generate JSON");
		generateJSON.addActionListener(new AnimatorMenuGenerateJSON());
		menu.add(generateJSON);

		JMenuItem refreshWindow = new JMenuItem("Refresh Window");
		refreshWindow.addActionListener(new AnimatorMenuRefreshWindow(frame));
		menu.add(refreshWindow);

		return menu;
	}

	private static JMenu createMeshMenu(JFrame frame, GameEngineFacade facade) {
		JMenu menu = new JMenu("Mesh");

		JMenuItem backToAnimation = new JMenuItem("Back to Animation");
		backToAnimation.addActionListener(new AnimatorMenuEditAnimation());
		menu.add(backToAnimation);

		return menu;
	}
}
