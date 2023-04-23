package hu.csega.editors.anm.layer2.transformation;

import hu.csega.editors.anm.components.ComponentExtractJointList;
import hu.csega.editors.anm.components.ComponentJointListView;
import hu.csega.editors.anm.layer1.swing.components.jointlist.AnimatorJointListItem;
import hu.csega.games.library.animation.v1.anm.AnimationPart;
import hu.csega.games.library.animation.v1.anm.AnimationPartJoint;
import hu.csega.games.units.UnitStore;

import java.util.ArrayList;
import java.util.List;

public class AnimatorExtractJointList implements ComponentExtractJointList {

	private ComponentJointListView view;
	private List<AnimatorJointListItem> items;

	@Override
	public List<AnimatorJointListItem> transform(AnimationPart part) {
		if(part == null)
			return null;

		List<AnimationPartJoint> joints = part.getJoints();
		if(joints == null || joints.size() == 0)
			return null;

		items = new ArrayList<>();
		for(AnimationPartJoint joint : joints) {
			AnimatorJointListItem item = new AnimatorJointListItem();
			item.setIdentifier(joint.getIdentifier());
			item.setLabel(joint.getDisplayName());

			items.add(item);
		}

		return items;
	}

	@Override
	public void accept(AnimationPart part) {
		if(view == null) {
			view = UnitStore.instance(ComponentJointListView.class);
			if(view == null) {
				return;
			}
		}

		items = transform(part);
		view.accept(items);
	}

	@Override
	public List<AnimatorJointListItem> provide() {
		return items;
	}

}
