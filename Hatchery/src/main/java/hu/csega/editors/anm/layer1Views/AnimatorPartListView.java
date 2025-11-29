package hu.csega.editors.anm.layer1Views;

import hu.csega.editors.anm.AnimatorUIComponents;
import hu.csega.editors.anm.components.ComponentExtractPartList;
import hu.csega.editors.anm.components.ComponentPartListView;
import hu.csega.editors.anm.layer1Views.swing.data.AnimatorPartListItem;
import hu.csega.editors.anm.layer4Data.model.AnimatorModel;
import hu.csega.games.units.Dependency;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.List;

public class AnimatorPartListView implements ComponentPartListView, ListModel<AnimatorPartListItem>, ListSelectionListener {

	///////////////////////////////////////////////////////////////////////
	// Dependencies
	private AnimatorModel animatorModel;
	private AnimatorUIComponents components;
	private ComponentExtractPartList extractor;

	@Override
	public void invalidate() {
		if(components.partList != null) {
			components.partList.updateUI();
		}
	}

	@Override
	public int getSize() {
		return extractor.extractPartList().size();
	}

	@Override
	public AnimatorPartListItem getElementAt(int index) {
		return extractor.extractPartList().get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(components.partList != null) {
			int selectedIndex = components.partList.getSelectedIndex();

			List<AnimatorPartListItem> items = extractor.extractPartList();
			if(selectedIndex < items.size()) {
				AnimatorPartListItem partItem = items.get(selectedIndex);
				animatorModel.selectPart(partItem.getIdentifier());
			}
		}
	}

	@Dependency
	public void setAnimatorModel(AnimatorModel animatorModel) {
		this.animatorModel = animatorModel;
	}

	@Dependency
	public void setComponents(AnimatorUIComponents components) {
		this.components = components;
	}

	@Dependency
	public void setExtractor(ComponentExtractPartList extractor) {
		this.extractor = extractor;
	}
}
