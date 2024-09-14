package hu.csega.editors.ftm.layer1.presentation.swing.menu;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.units.UnitStore;

public class FreeTriangleMeshMenu {

	public static void createMenuForJFrame(JFrame frame, GameEngineFacade facade) {
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = createFileMenu(frame, facade);
		menuBar.add(fileMenu);

		JMenu textureMenu = createTextureMenu(frame, facade);
		menuBar.add(textureMenu);

		JMenu shapesMenu = createShapesMenu(frame, facade);
		menuBar.add(shapesMenu);

		JMenu exportMenu = createExportMenu(frame, facade);
		menuBar.add(exportMenu);

		JMenu viewsMenu = createViewsMenu(frame, facade);
		menuBar.add(viewsMenu);

		frame.setJMenuBar(menuBar);
	}

	private static JMenu createFileMenu(JFrame frame, GameEngineFacade facade) {
		ResourceAdapter resourceAdapter = UnitStore.instance(ResourceAdapter.class);

		JFileChooser saveDialog = new JFileChooser(resourceAdapter.meshFolder());
		saveDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
		saveDialog.setDialogTitle("Select file to save.");
		saveDialog.setMultiSelectionEnabled(false);
		saveDialog.setFileFilter(new FileNameExtensionFilter("FreeTriangleMesh file", "ftm"));
		saveDialog.setApproveButtonText("Save");

		JFileChooser openDialog = new JFileChooser(resourceAdapter.meshFolder());
		openDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
		openDialog.setDialogTitle("Select file to open.");
		openDialog.setMultiSelectionEnabled(false);
		openDialog.setFileFilter(new FileNameExtensionFilter("FreeTriangleMesh file", "ftm"));
		openDialog.setApproveButtonText("Open");

		JMenu fileMenu = new JMenu("File");

		JMenuItem fileNew = new JMenuItem("New");
		fileNew.addActionListener(new FileNew(frame, saveDialog, facade));
		fileMenu.add(fileNew);

		JMenuItem fileOpen = new JMenuItem("Open");
		fileOpen.addActionListener(new FileOpen(frame, openDialog, facade));
		fileMenu.add(fileOpen);

		JMenuItem fileSave = new JMenuItem("Save");
		fileSave.addActionListener(new FileSave(frame, saveDialog, facade));
		fileMenu.add(fileSave);

		JMenuItem fileExit = new JMenuItem("Exit");
		fileExit.addActionListener(new FileExit(frame, saveDialog, facade));
		fileMenu.add(fileExit);

		return fileMenu;
	}

	private static JMenu createTextureMenu(JFrame frame, GameEngineFacade facade) {
		ResourceAdapter resourceAdapter = UnitStore.instance(ResourceAdapter.class);

		JFileChooser loadTextureDialog = new JFileChooser(resourceAdapter.textureFolder());
		loadTextureDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
		loadTextureDialog.setDialogTitle("Select texture to load.");
		loadTextureDialog.setMultiSelectionEnabled(false);
		loadTextureDialog.setFileFilter(new FileNameExtensionFilter("Texture file", "jpg"));
		loadTextureDialog.setApproveButtonText("Load");

		JMenu textureMenu = new JMenu("Texture");

		JMenuItem textureLoadItem = new JMenuItem("Load");
		textureLoadItem.addActionListener(new TextureLoad(frame, loadTextureDialog, facade));
		textureMenu.add(textureLoadItem);

		return textureMenu;
	}

	private static JMenu createShapesMenu(JFrame frame, final GameEngineFacade facade) {
		JMenuItem cubeItem = new JMenuItem("Cube");
		cubeItem.addActionListener(event -> {
			FreeTriangleMeshModel model = (FreeTriangleMeshModel)facade.model();
			model.createBasicCube();
			facade.window().repaintEverything();
		});

		JMenuItem sphereItem = new JMenuItem("Sphere");
		sphereItem.addActionListener(event -> {
			FreeTriangleMeshModel model = (FreeTriangleMeshModel)facade.model();
			model.createBasicSphere();
			facade.window().repaintEverything();
		});

		JMenuItem patch10x10Item = new JMenuItem("10x10 patch");
		patch10x10Item.addActionListener(event -> {
			FreeTriangleMeshModel model = (FreeTriangleMeshModel)facade.model();
			model.createBasicPatch10x10();
			facade.window().repaintEverything();
		});

		JMenu shapesMenu = new JMenu("Basic Shapes");
		shapesMenu.add(cubeItem);
		// shapesMenu.add(sphereItem);
		shapesMenu.add(patch10x10Item);
		return shapesMenu;
	}

	private static JMenu createExportMenu(JFrame frame, GameEngineFacade facade) {
		ResourceAdapter resourceAdapter = UnitStore.instance(ResourceAdapter.class);

		JFileChooser meshExportDialog = new JFileChooser(resourceAdapter.meshFolder());
		meshExportDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
		meshExportDialog.setDialogTitle("Select file to export to.");
		meshExportDialog.setMultiSelectionEnabled(false);
		meshExportDialog.setFileFilter(new FileNameExtensionFilter("Mesh export", "mesh"));
		meshExportDialog.setApproveButtonText("Export");

		JMenuItem meshExportItem = new JMenuItem("To Mesh");
		meshExportItem.addActionListener(new ExportMesh(frame, meshExportDialog, facade));

		JFileChooser mwcExportDialog = new JFileChooser(resourceAdapter.meshFolder());
		mwcExportDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
		mwcExportDialog.setDialogTitle("Select file to export to.");
		mwcExportDialog.setMultiSelectionEnabled(false);
		mwcExportDialog.setFileFilter(new FileNameExtensionFilter("Mesh with collision map", "mwc"));
		mwcExportDialog.setApproveButtonText("Export");

		JMenuItem mwcExportItem = new JMenuItem("To Mesh With Collision");
		mwcExportItem.addActionListener(new ExportMWC(frame, mwcExportDialog, facade));

		JMenu exportMenu = new JMenu("Export");
		exportMenu.add(meshExportItem);
		exportMenu.add(mwcExportItem);

		return exportMenu;
	}

	private static JMenu createViewsMenu(final JFrame frame, GameEngineFacade facade) {
		JMenuItem refreshWindowItem = new JMenuItem("Refresh Window");
		refreshWindowItem.addActionListener(event -> {
			frame.invalidate();
			frame.validate();
			frame.repaint();
		});

		JMenu viewsMenu = new JMenu("Views");
		viewsMenu.add(refreshWindowItem);
		return viewsMenu;
	}

}
