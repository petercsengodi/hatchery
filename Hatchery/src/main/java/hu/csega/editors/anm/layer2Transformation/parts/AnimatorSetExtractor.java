package hu.csega.editors.anm.layer2Transformation.parts;

import hu.csega.editors.anm.common.CommonComponent;
import hu.csega.editors.anm.components.ComponentOpenGLSetExtractor;
import hu.csega.editors.anm.components.ComponentSetExtractor;
import hu.csega.editors.anm.components.ComponentWireFrameConverter;
import hu.csega.editors.anm.layer1Views.swing.wireframe.AnimatorWireFramePoint;
import hu.csega.editors.anm.layer4Data.model.AnimatorModel;
import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameModelStore;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameTransformation;
import hu.csega.games.library.animation.v1.anm.*;
import hu.csega.games.units.Dependency;
import hu.csega.games.units.UnitStore;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnimatorSetExtractor implements ComponentSetExtractor {

	private Set<CommonComponent> dependents = new HashSet<>();

	private List<AnimatorSetPart> set;
	private Matrix4f baseTransformation = new Matrix4f();

	///////////////////////////////////////////////////////////////////////
	// Dependencies
	private AnimatorModel animatorModel;

	///////////////////////////////////////////////////////////////////////
	// Dependents
	private ComponentOpenGLSetExtractor openGLSetExtractor;
	private ComponentWireFrameConverter wireFrameConverter;

	@Override
	public synchronized List<AnimatorSetPart> extractSetParts() {
		if(this.set != null) {
			return this.set;
		}

		AnimationPersistent persistent = this.animatorModel.getPersistent();
		this.set = new ArrayList<>();

		Animation animation = persistent.getAnimation();
		int selectedScene = persistent.getSelectedScene();

		Map<String, AnimationPart> map = animation.getParts();
		if(map != null && !map.isEmpty()) {
			int numberOfScenes = animation.getNumberOfScenes();
			if(selectedScene < 0 || selectedScene >= numberOfScenes) {
				selectedScene = 0;
			}

			generateParts(persistent, selectedScene, baseTransformation, this.set);
		}

		return this.set;
	}

	public static void generateParts(AnimationPersistent persistent, int currentScene, Matrix4f baseTransformation, List<AnimatorSetPart> parts) {
		Animation animation = persistent.getAnimation();
		Map<String, String> connections = animation.getConnections();
		for(Map.Entry<String, AnimationPart> entry : animation.getParts().entrySet()) {
			String partIdentifier = entry.getKey();
			if(connections.containsKey(partIdentifier)) {
				continue;
			}

			AnimationPart part = entry.getValue();
			transformPart(persistent, currentScene, baseTransformation, part, null, parts);
		} // end for entry
	}

	private static void generateParts(AnimationPersistent persistent, int currentScene, Matrix4f baseTransformation, String jointKey, Matrix4f m, List<AnimatorSetPart> parts) {
		Animation animation = persistent.getAnimation();
		Map<String, String> connections = animation.getConnections();
		for(Map.Entry<String, String> entry : connections.entrySet()) {
			if(jointKey.equals(entry.getValue())) {
				AnimationPart part = animation.getParts().get(entry.getKey());
				transformPart(persistent, currentScene, baseTransformation, part, m, parts);
			}
		} // end for entry
	}

	private static void transformPart(AnimationPersistent persistent, int currentScene, Matrix4f baseTransformation, AnimationPart part, Matrix4f commulativeTransformation, List<AnimatorSetPart> collectedParts) {
		if(part != null) {
			Animation animation = persistent.getAnimation();
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
			setPart.setMeshModel(persistent.locateMesh(part.getIdentifier()));

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

			collectedParts.add(setPart);

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

					generateParts(persistent, currentScene, baseTransformation, joint.getIdentifier(), base, collectedParts);
				}
			} // end for each joint
		} // end if part not null
	}

	@Override
	public synchronized void invalidate() {
		this.set = null;
		openGLSetExtractor.invalidate();
		wireFrameConverter.invalidate();

		for(CommonComponent dependent : dependents) {
			dependent.invalidate();
		}
	}

	@Override
	public void addDependent(CommonComponent dependent) {
		dependents.add(dependent);
	}

	@Dependency
	public void setAnimatorModel(AnimatorModel animatorModel) {
		this.animatorModel = animatorModel;
	}

	@Dependency
	public void setOpenGLSetExtractor(ComponentOpenGLSetExtractor openGLSetExtractor) {
		this.openGLSetExtractor = openGLSetExtractor;
	}

	@Dependency
	public void setWireFrameConverter(ComponentWireFrameConverter wireFrameConverter) {
		this.wireFrameConverter = wireFrameConverter;
	}
}
