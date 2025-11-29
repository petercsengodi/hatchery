package hu.csega.editors.anm.layer2Transformation.parts;

import hu.csega.editors.anm.layer1Views.swing.wireframe.AnimatorWireFramePoint;
import hu.csega.games.engine.g3d.GameTransformation;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;

import java.util.List;

public class AnimatorSetPart {

	private String identifier;
	private String mesh;
	private FreeTriangleMeshModel meshModel;
	private GameTransformation transformation;
	private boolean flipped;
	private List<AnimatorWireFramePoint> jointPoints;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getMesh() {
		return mesh;
	}

	public void setMesh(String mesh) {
		this.mesh = mesh;
	}

	public FreeTriangleMeshModel getMeshModel() {
		return meshModel;
	}

	public void setMeshModel(FreeTriangleMeshModel meshModel) {
		this.meshModel = meshModel;
	}

	public GameTransformation getTransformation() {
		return transformation;
	}

	public void setTransformation(GameTransformation transformation) {
		this.transformation = transformation;
	}

	public boolean isFlipped() {
		return flipped;
	}

	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}

	public List<AnimatorWireFramePoint> getJointPoints() {
		return jointPoints;
	}

	public void setJointPoints(List<AnimatorWireFramePoint> jointPoints) {
		this.jointPoints = jointPoints;
	}
}
