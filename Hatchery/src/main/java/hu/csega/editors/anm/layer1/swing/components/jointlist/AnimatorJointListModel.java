package hu.csega.editors.anm.layer1.swing.components.jointlist;

import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.units.UnitStore;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.util.List;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class AnimatorJointListModel implements ListModel<AnimatorJointListItem>, ListSelectionListener {

	private JList<AnimatorJointListItem> jointList = null;
	private List<AnimatorJointListItem> items = null;
	private AnimatorModel model;

	public void setJList(JList<AnimatorJointListItem> jointList) {
		this.jointList = jointList;
	}

	public void setItems(List<AnimatorJointListItem> items) {
		this.items = items;
	}

	@Override
	public int getSize() {
		if(items == null)
			return 0;
		return items.size();
	}

	@Override
	public AnimatorJointListItem getElementAt(int index) {
		if(items == null)
			return null;
		return items.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(jointList != null) {
			int selectedIndex = jointList.getSelectedIndex();
			logger.debug("Selected index: " + selectedIndex);

			if(selectedIndex < items.size()) {
				AnimatorJointListItem partItem = items.get(selectedIndex);

				if(model == null) {
					model = UnitStore.instance(AnimatorModel.class);
				}

				AnimationPersistent persistent = model.getPersistent();
				persistent.setSelectedPart(partItem.getIdentifier());
			}
		}
	}

	private static final Logger logger = LoggerFactory.createLogger(AnimatorJointListModel.class);
}
