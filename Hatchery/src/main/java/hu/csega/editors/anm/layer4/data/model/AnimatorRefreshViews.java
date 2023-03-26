package hu.csega.editors.anm.layer4.data.model;

import hu.csega.editors.anm.components.ComponentExtractPartList;
import hu.csega.editors.anm.components.ComponentOpenGLExtractor;
import hu.csega.editors.anm.components.ComponentRefreshViews;
import hu.csega.editors.anm.components.ComponentWireFrameConverter;
import hu.csega.games.library.animation.v1.anm.Animation;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.units.Dependency;
import hu.csega.games.units.UnitStore;

public class AnimatorRefreshViews implements ComponentRefreshViews {

	private AnimatorModel model;
	private ComponentExtractPartList partListExtractor;
	private ComponentWireFrameConverter wireFrameConverter;
	private ComponentOpenGLExtractor openGLExtractor;

	@Override
	public void refreshAll() {
		if(partListExtractor == null) {
			partListExtractor = UnitStore.instance(ComponentExtractPartList.class);
		}

		if(wireFrameConverter == null) {
			wireFrameConverter = UnitStore.instance(ComponentWireFrameConverter.class);
		}

		if(openGLExtractor == null) {
			openGLExtractor = UnitStore.instance(ComponentOpenGLExtractor.class);
		}

		AnimationPersistent persistent = model.getPersistent();
		Animation animation = null;

		if(persistent != null) {
			animation = persistent.getAnimation();
		}

		if(partListExtractor != null) {
			synchronized (model) {
				partListExtractor.accept(animation);
			}
		}

		if(openGLExtractor != null) {
			openGLExtractor.accept(model);
		}

		if(wireFrameConverter != null) {
			wireFrameConverter.accept(model);
		}

	} // end of refreshAll

	@Dependency
	public void setAnimatorModel(AnimatorModel animatorModel) {
		this.model = animatorModel;
	}

}
