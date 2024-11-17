package hu.csega.editors.ftm.layer1.presentation.swing.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.env.Environment;
import hu.csega.games.units.UnitStore;

@SuppressWarnings("unused")
class FileExit implements ActionListener {

	private final JFrame frame;
	private final JFileChooser saveDialog;
	private final GameEngineFacade facade;

	public FileExit(JFrame frame, JFileChooser saveDialog, GameEngineFacade facade) {
		this.frame = frame;
		this.saveDialog = saveDialog;
		this.facade = facade;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Environment env = UnitStore.instance(Environment.class);
		env.notifyExiting();
	}
}
