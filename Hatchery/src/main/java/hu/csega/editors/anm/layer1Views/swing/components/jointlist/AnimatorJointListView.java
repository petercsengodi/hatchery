package hu.csega.editors.anm.layer1Views.swing.components.jointlist;

import hu.csega.editors.anm.components.ComponentJointListView;
import hu.csega.editors.anm.layer1Views.swing.AnimatorUIComponents;
import hu.csega.games.units.UnitStore;

import java.util.List;

public class AnimatorJointListView implements ComponentJointListView {

	private AnimatorUIComponents ui;

	@Override
	public Void provide() {
		return null;
	}

	@Override
	public void accept(List<AnimatorJointListItem> items) {
		if(ui == null) {
			ui = UnitStore.instance(AnimatorUIComponents.class);
			if(ui == null) {
				return;
			}
		}

		if(ui.jointListModel != null) {
			ui.jointListModel.setItems(items);
			if(ui.jointList != null) {
				ui.jointList.updateUI();
			}
		}

	}

}
