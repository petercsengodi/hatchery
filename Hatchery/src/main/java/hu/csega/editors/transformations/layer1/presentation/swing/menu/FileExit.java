package hu.csega.editors.transformations.layer1.presentation.swing.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.env.Environment;
import hu.csega.games.units.UnitStore;

@SuppressWarnings("unused")
public class FileExit implements ActionListener {

	private JFrame frame;
	private GameEngineFacade facade;

	public FileExit(JFrame frame, GameEngineFacade facade) {
		this.frame = frame;
		this.facade = facade;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Environment env = UnitStore.instance(Environment.class);
		env.notifyExiting();
	}
}
