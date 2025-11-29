package hu.csega.editors.anm.components;

import hu.csega.editors.anm.layer1Views.opengl.AnimatorAnimationRenderer;
import hu.csega.editors.anm.layer1Views.view3d.AnimatorSet;
import hu.csega.games.common.CommonUIComponent;

public interface Component3DView extends CommonUIComponent<AnimatorSet, AnimatorSet> {

	void setRenderer(AnimatorAnimationRenderer renderer);

}
