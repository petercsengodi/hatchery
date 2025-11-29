package hu.csega.editors.anm.layer1Views.swing.menu;

import hu.csega.editors.anm.components.ComponentRefreshViews;
import hu.csega.games.units.UnitStore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class AnimatorMenuGenerateJSON implements ActionListener {

	private ComponentRefreshViews refreshViews;

	@Override
	public void actionPerformed(ActionEvent e) {
		if(refreshViews == null) {
			refreshViews = UnitStore.instance(ComponentRefreshViews.class);
			if(refreshViews == null) {
				return;
			}
		}

		refreshViews.generateJSON();
	}

}
