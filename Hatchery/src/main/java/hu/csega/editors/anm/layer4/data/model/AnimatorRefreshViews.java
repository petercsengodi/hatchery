package hu.csega.editors.anm.layer4.data.model;

import hu.csega.editors.anm.components.ComponentExtractJointList;
import hu.csega.editors.anm.components.ComponentExtractPartList;
import hu.csega.editors.anm.components.ComponentOpenGLExtractor;
import hu.csega.editors.anm.components.ComponentRefreshViews;
import hu.csega.editors.anm.components.ComponentWireFrameConverter;
import hu.csega.editors.anm.layer1.swing.AnimatorUIComponents;
import hu.csega.editors.anm.layer1.swing.wireframe.AnimatorWireFramePoint;
import hu.csega.editors.anm.layer1.view3d.AnimatorSetPart;
import hu.csega.games.engine.anm.GameAnimation;
import hu.csega.games.engine.anm.GameAnimationScene;
import hu.csega.games.engine.g3d.GameTransformation;
import hu.csega.games.library.animation.v1.anm.Animation;
import hu.csega.games.library.animation.v1.anm.AnimationPart;
import hu.csega.games.library.animation.v1.anm.AnimationPartJoint;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.library.animation.v1.anm.AnimationScenePart;
import hu.csega.games.library.animation.v1.anm.AnimationVector;
import hu.csega.games.units.Dependency;
import hu.csega.games.units.UnitStore;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector4f;

public class AnimatorRefreshViews implements ComponentRefreshViews {

	private AnimatorModel model;

	private ComponentExtractPartList partListExtractor;
	private ComponentExtractJointList jointListExtractor;
	private ComponentWireFrameConverter wireFrameConverter;
	private ComponentOpenGLExtractor openGLExtractor;
	private AnimatorUIComponents components;

	private Matrix4f baseTransformation = new Matrix4f();

	@Override
	public void refreshAll() {
		if(partListExtractor == null) {
			partListExtractor = UnitStore.instance(ComponentExtractPartList.class);
		}

		if(jointListExtractor == null) {
			jointListExtractor = UnitStore.instance(ComponentExtractJointList.class);
		}

		if(wireFrameConverter == null) {
			wireFrameConverter = UnitStore.instance(ComponentWireFrameConverter.class);
		}

		if(openGLExtractor == null) {
			openGLExtractor = UnitStore.instance(ComponentOpenGLExtractor.class);
		}

		if(components == null) {
			components = UnitStore.instance(AnimatorUIComponents.class);
		}

		synchronized (model) {
			List<AnimatorSetPart> parts = new ArrayList<>();
			AnimationPersistent persistent = model.getPersistent();
			Animation animation = persistent.getAnimation();
			int selectedScene = persistent.getSelectedScene();

			if(partListExtractor != null) {
				partListExtractor.accept(animation);
			}

			String selectedPart = persistent.getSelectedPart();
			if(selectedPart != null && !selectedPart.isEmpty()) {
				jointListExtractor.accept(animation.getParts().get(selectedPart));
			} else {
				jointListExtractor.accept(null);
			}

			Map<String, AnimationPart> map = animation.getParts();
			if(map != null && map.size() > 0) {
				int numberOfScenes = animation.getNumberOfScenes();
				if(selectedScene < 0 || selectedScene >= numberOfScenes) {
					selectedScene = 0;
				}

				generateParts(animation, selectedScene, baseTransformation, parts);
			}

			if(openGLExtractor != null) {
				openGLExtractor.accept(parts);
			}

			if(wireFrameConverter != null) {
				wireFrameConverter.accept(parts);
			}

			components.sceneLerpPanel.updateUI();
			components.sceneSelectorPanel.updateUI();
		}

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
				generateParts(animation, sceneIndex, baseTransformation, parts);
				GameAnimationScene scene = gameAnimation.getScenes()[sceneIndex];
				fill(scene, parts, indexes);
			}

			if(components == null) {
				components = UnitStore.instance(AnimatorUIComponents.class);
			}
		}
	}

	public static void generateParts(Animation animation, int currentScene, Matrix4f baseTransformation, List<AnimatorSetPart> parts) {
		Map<String, String> connections = animation.getConnections();
		for(Map.Entry<String, AnimationPart> entry : animation.getParts().entrySet()) {
			String partIdentifier = entry.getKey();
			if(connections.containsKey(partIdentifier)) {
				continue;
			}

			AnimationPart part = entry.getValue();
			transformPart(animation, currentScene, baseTransformation, part, null, parts);
		} // end for entry
	}

	private static void generateParts(Animation animation, int currentScene, Matrix4f baseTransformation, String jointKey, Matrix4f m, List<AnimatorSetPart> parts) {
		Map<String, String> connections = animation.getConnections();
		for(Map.Entry<String, String> entry : connections.entrySet()) {
			if(jointKey.equals(entry.getValue())) {
				AnimationPart part = animation.getParts().get(entry.getKey());
				transformPart(animation, currentScene, baseTransformation, part, m, parts);
			}
		} // end for entry
	}

	private static void transformPart(Animation animation, int currentScene, Matrix4f baseTransformation, AnimationPart part, Matrix4f commulativeTransformation, List<AnimatorSetPart> parts) {
		if(part != null) {
			AnimationScenePart scenePart = animation.createOrGetScenePart(currentScene, part.getIdentifier());
			if(!scenePart.isVisible()) {
				return;
			}

			Matrix4f commulativeAndBaseTransformation = new Matrix4f();
			if(commulativeTransformation == null) {
				commulativeAndBaseTransformation.set(part.getBasicTransformation().getM());
			} else {
				baseTransformation.set(part.getBasicTransformation().getM());
				commulativeTransformation.mul(baseTransformation, commulativeAndBaseTransformation);
			}

			Matrix4f modelTransformation = new Matrix4f();
			scenePart.getTransformation().createModelMatrix(modelTransformation);

			Matrix4f setPartTransformation = new Matrix4f();
			commulativeAndBaseTransformation.mul(modelTransformation, setPartTransformation);

			GameTransformation convertedTransformation = new GameTransformation();
			convertedTransformation.importFrom(setPartTransformation);

			float[] flip = scenePart.getTransformation().getFlip().getV();
			boolean flipped = (flip[0] < 0f) ^ (flip[1] < 0f) ^ (flip[2] < 0f);

			AnimatorSetPart setPart = new AnimatorSetPart();
			setPart.setIdentifier(part.getIdentifier());
			setPart.setMesh(part.getMesh());
			setPart.setTransformation(convertedTransformation);
			setPart.setFlipped(flipped);

			List<AnimationPartJoint> joints = part.getJoints();
			if(joints != null && !joints.isEmpty()) {
				List<AnimatorWireFramePoint> jointPoints = new ArrayList<>(joints.size());
				for(AnimationPartJoint joint : joints) {
					AnimationVector pos = joint.getRelativePosition();
					float[] v = pos.getV();
					double x = v[0] / v[3];
					double y = v[1] / v[3];
					double z = v[2] / v[3];
					AnimatorWireFramePoint p = new AnimatorWireFramePoint(x, y, z, Color.GREEN, false);
					jointPoints.add(p);
				}

				setPart.setJointPoints(jointPoints);
			}

			parts.add(setPart);

			if(joints != null && joints.size() > 0) {
				// scenePart.getTransformation().createJointMatrix(modelTransformation);
				Matrix4f base = new Matrix4f();
				Vector4f tv = new Vector4f();

				for (AnimationPartJoint joint : part.getJoints()) {
					tv.set(0f, 0f, 0f, 1f);
					tv.mul(setPartTransformation);
					setPartTransformation.translateLocal(-tv.x / tv.w, -tv.y / tv.w, -tv.z / tv.w, base);

					float[] v = joint.getRelativePosition().getV();
					tv.set(v[0], v[1], v[2], v[3]);
					tv.mul(setPartTransformation);
					base.translateLocal(tv.x / tv.w, tv.y / tv.w, tv.z / tv.w, base);

					generateParts(animation, currentScene, baseTransformation, joint.getIdentifier(), base, parts);
				}
			} // end for each joint
		} // end if part not null
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

}
