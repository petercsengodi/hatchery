package hu.csega.editors.anm.layer2Transformation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hu.csega.editors.anm.common.CommonComponent;
import hu.csega.editors.anm.common.CommonInvalidatable;
import hu.csega.editors.anm.components.ComponentExtractPartList;
import hu.csega.editors.anm.components.ComponentPartListView;
import hu.csega.editors.anm.layer1Views.swing.data.AnimatorPartListItem;
import hu.csega.editors.anm.layer4Data.model.AnimatorModel;
import hu.csega.games.library.animation.v1.anm.Animation;
import hu.csega.games.library.animation.v1.anm.AnimationPart;
import hu.csega.games.units.Dependency;

public class AnimatorExtractPartList implements ComponentExtractPartList {

	private final Set<CommonInvalidatable> dependents = new HashSet<>();

	private List<AnimatorPartListItem> items;

	///////////////////////////////////////////////////////////////////////
	// Dependencies
	private AnimatorModel animatorModel;

	///////////////////////////////////////////////////////////////////////
	// Dependents
	private ComponentPartListView view;

	@Override
	public synchronized List<AnimatorPartListItem> extractPartList() {
		if(items != null)
			return items;

		items = new ArrayList<>();

		Animation animation = animatorModel.getPersistent().getAnimation();
		Map<String, AnimationPart> parts = animation.getParts();
		if(parts == null || parts.size() == 0)
			return items;

		for(Map.Entry<String, AnimationPart> entry : parts.entrySet()) {
			String partIdentifier = entry.getKey();
			AnimationPart value = entry.getValue();
			String displayName = value.getDisplayName();
			String mesh = value.getMesh();

			AnimatorPartListItem item = new AnimatorPartListItem();
			item.setIdentifier(partIdentifier);
			item.setLabel(displayName);
			item.setMesh(mesh);

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
	public void setAnimatorModel(AnimatorModel animatorModel) {
		this.animatorModel = animatorModel;
	}

	@Dependency
	public void setView(ComponentPartListView view) {
		this.view = view;
	}
}
