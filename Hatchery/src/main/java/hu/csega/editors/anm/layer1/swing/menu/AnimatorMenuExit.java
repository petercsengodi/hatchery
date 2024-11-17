package hu.csega.editors.anm.layer1.swing.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.env.Environment;
import hu.csega.games.units.UnitStore;

@SuppressWarnings("unused")
class AnimatorMenuExit implements ActionListener {

	private JFrame frame;
	private JFileChooser saveDialog;
	private GameEngineFacade facade;

	public AnimatorMenuExit(JFrame frame, JFileChooser saveDialog, GameEngineFacade facade) {
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
