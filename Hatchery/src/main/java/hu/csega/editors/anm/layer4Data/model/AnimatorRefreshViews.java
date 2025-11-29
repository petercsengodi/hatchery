package hu.csega.editors.anm.layer4Data.model;

import hu.csega.editors.anm.AnimatorUIComponents;
import hu.csega.editors.anm.components.*;
import hu.csega.editors.anm.layer2Transformation.parts.AnimatorSetExtractor;
import hu.csega.editors.anm.layer2Transformation.parts.AnimatorSetPart;
import hu.csega.games.engine.anm.GameAnimation;
import hu.csega.games.engine.anm.GameAnimationScene;
import hu.csega.games.library.animation.v1.anm.Animation;
import hu.csega.games.library.animation.v1.anm.AnimationPart;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.units.Dependency;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimatorRefreshViews implements ComponentRefreshViews {

	private AnimatorModel model;

	private ComponentExtractPartList partListExtractor;
	private ComponentExtractJointList jointListExtractor;
	private ComponentWireFrameConverter wireFrameConverter;
	private ComponentOpenGLSetExtractor openGLExtractor;
	private AnimatorUIComponents components;

	private Matrix4f baseTransformation = new Matrix4f();

	@Override
	public void refreshAll() {
		partListExtractor.invalidate();
		jointListExtractor.invalidate();
		wireFrameConverter.invalidate();
		openGLExtractor.invalidate();
		components.sceneLerpPanel.updateUI();
		components.sceneSelectorPanel.updateUI();
	} // end of refreshAll

	@Override
	public void generateJSON() {
		synchronized (model) {
			AnimationPersistent persistent = model.getPersistent();
			Animation animation = persistent.getAnimation();
			int numberOfScenes = animation.getNumberOfScenes();

			int numberOfMeshes = 0;
			Map<String, Integer> indexes = new HashMap<>();
			for(AnimationPart part : animation.getParts().values()) {
				indexes.put(part.getIdentifier(), numberOfMeshes);
				numberOfMeshes++;
			}

			GameAnimation gameAnimation = new GameAnimation(numberOfMeshes, numberOfScenes);
			for(AnimationPart part : animation.getParts().values()) {
				int partIndex = indexes.get(part.getIdentifier());
				gameAnimation.getMeshes()[partIndex] = part.getMesh();
			}

			for(int sceneIndex = 0; sceneIndex < numberOfScenes; sceneIndex++) {
				List<AnimatorSetPart> parts = new ArrayList<>();
				AnimatorSetExtractor.generateParts(persistent, sceneIndex, baseTransformation, parts);
				GameAnimationScene scene = gameAnimation.getScenes()[sceneIndex];
				fill(scene, parts, indexes);
			}
		}
	}
	private void fill(GameAnimationScene scene, List<AnimatorSetPart> parts, Map<String, Integer> indexes) {
		for(AnimatorSetPart part : parts) {
			int index = indexes.get(part.getIdentifier());
			scene.getVisible()[index] = true;
			scene.getFlipped()[index] = part.isFlipped();
			scene.getTransformations()[index] = part.getTransformation();
		}
	}

	@Dependency
	public void setAnimatorModel(AnimatorModel animatorModel) {
		this.model = animatorModel;
	}

	@Dependency
	public void setPartListExtractor(ComponentExtractPartList partListExtractor) {
		this.partListExtractor = partListExtractor;
	}

	@Dependency
	public void setJointListExtractor(ComponentExtractJointList jointListExtractor) {
		this.jointListExtractor = jointListExtractor;
	}

	@Dependency
	public void setWireFrameConverter(ComponentWireFrameConverter wireFrameConverter) {
		this.wireFrameConverter = wireFrameConverter;
	}

	@Dependency
	public void setOpenGLExtractor(ComponentOpenGLSetExtractor openGLExtractor) {
		this.openGLExtractor = openGLExtractor;
	}

	@Dependency
	public void setComponents(AnimatorUIComponents components) {
		this.components = components;
	}
}
