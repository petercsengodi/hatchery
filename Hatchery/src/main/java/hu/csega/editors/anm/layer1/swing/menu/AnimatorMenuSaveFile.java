package hu.csega.editors.anm.layer1.swing.menu;

import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.editors.common.SerializationUtil;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.*;

@SuppressWarnings("unused")
class AnimatorMenuSaveFile implements ActionListener {

	private JFrame frame;
	private JFileChooser saveDialog;
	private GameEngineFacade facade;

	public AnimatorMenuSaveFile(JFrame frame, JFileChooser saveDialog, GameEngineFacade facade) {
		this.frame = frame;
		this.saveDialog = saveDialog;
		this.facade = facade;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int result = saveDialog.showDialog(frame, "Save");

		switch(result) {
		case JFileChooser.APPROVE_OPTION:
			File file = saveDialog.getSelectedFile();
			AnimatorModel model = (AnimatorModel) facade.model();
			byte[] serialized = SerializationUtil.serialize(model.getPersistent());

			try (FileOutputStream stream = new FileOutputStream(file)){
				stream.write(serialized);
			} catch(IOException ex) {
				throw new RuntimeException("Could not write output file!", ex);
			}
			break;

		default:
			break;
		}
	}

	private static final Logger logger = LoggerFactory.createLogger(AnimatorMenuSaveFile.class);
}
