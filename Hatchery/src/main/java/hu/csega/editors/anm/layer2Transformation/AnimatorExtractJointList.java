package hu.csega.editors.anm.layer2Transformation;

import hu.csega.editors.anm.common.CommonComponent;
import hu.csega.editors.anm.common.CommonInvalidatable;
import hu.csega.editors.anm.components.ComponentExtractJointList;
import hu.csega.editors.anm.components.ComponentJointListView;
import hu.csega.editors.anm.layer1Views.swing.data.AnimatorJointListItem;
import hu.csega.editors.anm.layer4Data.model.AnimatorModel;
import hu.csega.games.library.animation.v1.anm.AnimationPart;
import hu.csega.games.library.animation.v1.anm.AnimationPartJoint;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.units.Dependency;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnimatorExtractJointList implements ComponentExtractJointList {

	private final Set<CommonInvalidatable> dependents = new HashSet<>();

	private List<AnimatorJointListItem> items;

	///////////////////////////////////////////////////////////////////////
	// Dependencies
	private AnimatorModel animatorModel;


	///////////////////////////////////////////////////////////////////////
	// Dependents
	private ComponentJointListView view;

	@Override
	public synchronized List<AnimatorJointListItem> extractJointList() {
		if(items != null) {
			return items;
		}

		items = new ArrayList<>();

		AnimationPersistent persistent = animatorModel.getPersistent();
		String selectedPartId = persistent.getSelectedPart();
		AnimationPart part = persistent.getAnimation().getParts().get(selectedPartId);

		if(part == null) {
			return items;
		}

		List<AnimationPartJoint> joints = part.getJoints();
		if(joints == null || joints.size() == 0)
			return items;

		for(AnimationPartJoint joint : joints) {
			AnimatorJointListItem item = new AnimatorJointListItem();
			item.setIdentifier(joint.getIdentifier());
			item.setLabel(joint.getDisplayName());

			items.add(item);
		}

		return items;
	}

	@Override
	public synchronized void invalidate() {
		this.items = null;
		view.invalidate();

		for(CommonInvalidatable dependent : dependents) {
			dependent.invalidate();
		}
	}

	@Override
	public void addDependent(CommonInvalidatable dependent) {
		dependents.add(dependent);
	}

	@Dependency
	public void setView(ComponentJointListView view) {
		this.view = view;
	}

	@Dependency
	public void setAnimatorModel(AnimatorModel animatorModel) {
		this.animatorModel = animatorModel;
	}
}
