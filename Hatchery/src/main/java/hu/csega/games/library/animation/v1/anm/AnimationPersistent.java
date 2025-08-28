package hu.csega.games.library.animation.v1.anm;

import hu.csega.editors.anm.common.CommonEditorModel;
import hu.csega.editors.anm.layer1.swing.views.AnimatorObject;
import hu.csega.games.engine.g3d.GameObjectDirection;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.games.engine.g3d.GameObjectPosition;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AnimationPersistent implements Serializable, CommonEditorModel {

	private String name = "Unnamed";
	private Animation animation;
	private AnimationMisc misc;

	private Map<String, FreeTriangleMeshModel> meshes;

	private String selectedPart;
	private String selectedJoint;
	private int selectedScene;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Animation getAnimation() {
		if(animation == null) {
			animation = new Animation();
		}

		return animation;
	}

	@Override
	public GameObjectPlacement cameraPlacement() {
		AnimationPlacement camera = getMisc().getCamera();

		GameObjectPlacement cameraPlacement = new GameObjectPlacement();
		cameraPlacement.setPositionTargetUp(
				convertPosition(camera.getPosition()),
				convertPosition(camera.getTarget()),
				convertDirection(camera.getUp())
		);

		return cameraPlacement;
	}

	@Override
	public long getSelectionLastChanged() {
		return 0;
	}

	@Override
	public Collection<AnimatorObject> getSelectedObjects() {
		return null;
	}

	@Override
	public void finalizeMove() {
		// moved = false;
	}

	public FreeTriangleMeshModel locateMesh(String meshIdentifier) {
		if(meshes == null || meshes.isEmpty()) {
			return null;
		} else {
			return meshes.get(meshIdentifier);
		}
	}

	public void putMeshModel(String identifier, FreeTriangleMeshModel freeTriangleMeshModel) {
		if(meshes == null)
			meshes = new HashMap<>();
		meshes.put(identifier, freeTriangleMeshModel);
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public String getSelectedPart() {
		return selectedPart;
	}

	public void setSelectedPart(String selectedPart) {
		this.selectedPart = selectedPart;
	}

	public String getSelectedJoint() {
		return selectedJoint;
	}

	public void setSelectedJoint(String selectedJoint) {
		this.selectedJoint = selectedJoint;
	}

	public int getSelectedScene() {
		return selectedScene;
	}

	public void setSelectedScene(int selectedScene) {
		this.selectedScene = selectedScene;
	}

	public AnimationMisc getMisc() {
		if(misc == null) {
			misc = new AnimationMisc();
		}

		return misc;
	}

	public void setMisc(AnimationMisc misc) {
		this.misc = misc;
	}

	private GameObjectPosition convertPosition(AnimationVector vector) {
		float[] v = vector.getV();
		return new GameObjectPosition(v[0]/v[3], v[1]/v[3], v[2]/v[3]);
	}

	private GameObjectDirection convertDirection(AnimationVector vector) {
		float[] v = vector.getV();
		return new GameObjectDirection(v[0]/v[3], v[1]/v[3], v[2]/v[3]);
	}

	private static final long serialVersionUID = 1L;
}
