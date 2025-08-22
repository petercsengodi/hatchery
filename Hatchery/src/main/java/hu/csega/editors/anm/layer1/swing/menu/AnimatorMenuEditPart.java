package hu.csega.editors.anm.layer1.swing.menu;

import hu.csega.editors.anm.components.ComponentRefreshViews;
import hu.csega.editors.anm.layer1.swing.AnimatorUIComponents;
import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.games.units.UnitStore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnimatorMenuEditPart implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		AnimatorModel model = UnitStore.instance(AnimatorModel.class);
		String identifier = model.currentPartIdentifier();
		model.setPartAsModel(identifier);

		if(identifier != null) {
			AnimatorUIComponents components = UnitStore.instance(AnimatorUIComponents.class);
			components.frame.setJMenuBar(AnimatorMenu.MESH_MENU_BAR);

			ComponentRefreshViews refreshViews = UnitStore.instance(ComponentRefreshViews.class);
			refreshViews.refreshAll();
		}
	}

}
