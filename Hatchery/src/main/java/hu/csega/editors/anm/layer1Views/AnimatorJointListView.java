package hu.csega.editors.anm.layer1Views;

import hu.csega.editors.anm.AnimatorUIComponents;
import hu.csega.editors.anm.components.ComponentExtractJointList;
import hu.csega.editors.anm.components.ComponentJointListView;
import hu.csega.editors.anm.layer1Views.swing.data.AnimatorJointListItem;
import hu.csega.editors.anm.layer4Data.model.AnimatorModel;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.units.Dependency;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.List;

public class AnimatorJointListView implements ComponentJointListView, ListModel<AnimatorJointListItem>, ListSelectionListener {

	///////////////////////////////////////////////////////////////////////
	// Dependencies
	private AnimatorUIComponents components;
	private AnimatorModel animatorModel;
	private ComponentExtractJointList extractor;

	@Override
	public void invalidate() {
		if(components.jointList != null) {
			components.jointList.updateUI();
		}
	}

	@Override
	public int getSize() {
		return extractor.extractJointList().size();
	}

	@Override
	public AnimatorJointListItem getElementAt(int index) {
		return extractor.extractJointList().get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(components.jointList != null) {
			int selectedIndex = components.jointList.getSelectedIndex();

			List<AnimatorJointListItem> items = extractor.extractJointList();
			if(selectedIndex < items.size()) {
				AnimatorJointListItem partItem = items.get(selectedIndex);
				AnimationPersistent persistent = animatorModel.getPersistent();
				persistent.setSelectedJoint(partItem.getIdentifier());
			}
		}
	}

	@Dependency
	public void setComponents(AnimatorUIComponents components) {
		this.components = components;
	}

	@Dependency
	public void setAnimatorModel(AnimatorModel animatorModel) {
		this.animatorModel = animatorModel;
	}

	@Dependency
	public void setExtractor(ComponentExtractJointList extractor) {
		this.extractor = extractor;
	}
}
