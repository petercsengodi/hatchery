package hu.csega.editors.anm.layer1.swing.components.partlist;

import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.games.units.UnitStore;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.util.List;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class AnimatorPartListModel implements ListModel<AnimatorPartListItem>, ListSelectionListener {

	private JList<AnimatorPartListItem> partList = null;
	private List<AnimatorPartListItem> items = null;
	private AnimatorModel model;

	public void setJList(JList<AnimatorPartListItem> partList) {
		this.partList = partList;
	}

	public void setItems(List<AnimatorPartListItem> items) {
		this.items = items;
	}

	@Override
	public int getSize() {
		if(items == null)
			return 0;
		return items.size();
	}

	@Override
	public AnimatorPartListItem getElementAt(int index) {
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
		if(partList != null) {
			int selectedIndex = partList.getSelectedIndex();
			logger.debug("Selected index: " + selectedIndex);

			if(selectedIndex < items.size()) {
				AnimatorPartListItem partItem = items.get(selectedIndex);

				if(model == null) {
					model = UnitStore.instance(AnimatorModel.class);
				}

				model.setSelectedAnimationPart(partItem.getIndex());
			}
		}
	}

	private static final Logger logger = LoggerFactory.createLogger(AnimatorPartListModel.class);
}
