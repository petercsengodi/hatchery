package hu.csega.editors.anm.layer1Views.swing.menu;

import hu.csega.editors.anm.layer1Views.swing.AnimatorUIComponents;
import hu.csega.editors.anm.layer4Data.model.AnimatorModel;
import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.games.units.UnitStore;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

public class AnimatorMenuChangePart implements ActionListener {

	private AnimatorUIComponents ui;
	private ResourceAdapter resourceAdapter;

	@Override
	public void actionPerformed(ActionEvent e) {
		if(ui == null) {
			ui = UnitStore.instance(AnimatorUIComponents.class);
			if(ui == null || ui.frame == null) {
				logger.error("Missing component: " + AnimatorUIComponents.class.getSimpleName() + ".frame");
				return;
			}
		}

		if(resourceAdapter == null) {
			resourceAdapter = UnitStore.instance(ResourceAdapter.class);
			if(resourceAdapter == null) {
				logger.error("Missing component: " + ResourceAdapter.class.getSimpleName());
				return;
			}
		}

		if(ui.addNewPartFile == null) {
			ResourceAdapter resourceAdapter = UnitStore.instance(ResourceAdapter.class);
			ui.addNewPartFile = new JFileChooser();
			ui.addNewPartFile.setCurrentDirectory(new File(resourceAdapter.meshFolder()));
		}

		int result = ui.addNewPartFile.showDialog(ui.frame, "Select Mesh");

		switch(result) {
		case JFileChooser.APPROVE_OPTION:
			File file = ui.addNewPartFile.getSelectedFile();
			String filename = file.getAbsolutePath();
			String projectPath = resourceAdapter.projectRoot();
			if(filename.startsWith(projectPath)) {
				filename = filename.substring(projectPath.length());
			}

			logger.info("Selected file: " + filename);
			AnimatorModel model = UnitStore.instance(AnimatorModel.class);
			model.changePart(filename);

			break;

		default:
			break;
		}
	}

	private static final Logger logger = LoggerFactory.createLogger(AnimatorMenuChangePart.class);
}
