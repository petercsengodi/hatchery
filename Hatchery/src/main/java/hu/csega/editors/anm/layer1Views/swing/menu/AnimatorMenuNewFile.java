package hu.csega.editors.anm.layer1Views.swing.menu;

import hu.csega.editors.anm.components.ComponentRefreshViews;
import hu.csega.editors.anm.layer4Data.model.AnimatorModel;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.units.UnitStore;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

@SuppressWarnings("unused")
class AnimatorMenuNewFile implements ActionListener {

	private JFrame frame;
	private GameEngineFacade facade;

	public AnimatorMenuNewFile(JFrame frame, GameEngineFacade facade) {
		this.frame = frame;
		this.facade = facade;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		AnimatorModel model = (AnimatorModel) facade.model();
		model.setPersistent(null);
		model.clearSnapshots();
		ComponentRefreshViews refreshViews = UnitStore.instance(ComponentRefreshViews.class);
		refreshViews.refreshAll();
	}

	private static final Logger logger = LoggerFactory.createLogger(AnimatorMenuNewFile.class);
}
