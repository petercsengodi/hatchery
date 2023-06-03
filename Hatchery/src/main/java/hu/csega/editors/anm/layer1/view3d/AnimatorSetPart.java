package hu.csega.editors.anm.layer1.view3d;

import hu.csega.editors.anm.layer1.swing.wireframe.AnimatorWireFramePoint;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameTransformation;

import java.util.List;

public class AnimatorSetPart {

	private String identifier;
	private String mesh;
	private GameObjectHandler handler;
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

	public GameObjectHandler getHandler() {
		return handler;
	}

	public void setHandler(GameObjectHandler handler) {
		this.handler = handler;
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
