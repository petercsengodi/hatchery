package hu.csega.editors.anm.layer1Views.swing.menu;

import hu.csega.editors.anm.components.ComponentRefreshViews;
import hu.csega.editors.anm.AnimatorUIComponents;
import hu.csega.editors.anm.layer4Data.model.AnimatorModel;
import hu.csega.games.units.UnitStore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnimatorMenuEditAnimation implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		AnimatorModel model = UnitStore.instance(AnimatorModel.class);
		model.setPartAsModel(null);

		AnimatorUIComponents components = UnitStore.instance(AnimatorUIComponents.class);
		components.frame.setJMenuBar(AnimatorMenu.ANIMATION_MENU_BAR);

		ComponentRefreshViews refreshViews = UnitStore.instance(ComponentRefreshViews.class);
		refreshViews.refreshAll();
	}

}
