package hu.csega.games.library.animation.v1.anm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AnimationPart implements Serializable {

	private String identifier;
	private String displayName;
	private String mesh;
	private AnimationTransformation basicTransformation = new AnimationTransformation();
	private List<AnimationPartJoint> joints = new ArrayList<>();

	public AnimationPart(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getMesh() {
		return mesh;
	}

	public void setMesh(String mesh) {
		this.mesh = mesh;
	}

	public AnimationTransformation getBasicTransformation() {
		return basicTransformation;
	}

	public void setBasicTransformation(AnimationTransformation basicTransformation) {
		this.basicTransformation = basicTransformation;
	}

	public List<AnimationPartJoint> getJoints() {
		return joints;
	}

	public void setJoints(List<AnimationPartJoint> joints) {
		this.joints = joints;
	}

	private static final long serialVersionUID = 1L;

}
