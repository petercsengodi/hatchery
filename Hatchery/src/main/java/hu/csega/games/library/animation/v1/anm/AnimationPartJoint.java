package hu.csega.games.library.animation.v1.anm;

import java.io.Serializable;

public class AnimationPartJoint implements Serializable {

	private String identifier;
	private String displayName;

	private String partIdentifier;
	private AnimationVector relativePosition;
	private AnimationTransformation relativeTransformation;

	public AnimationPartJoint(String parentIdentifier, String identifier) {
		this.partIdentifier = parentIdentifier;
		this.identifier = identifier;
		this.displayName = "Joint";
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

	public String getPartIdentifier() {
		return partIdentifier;
	}

	public void setPartIdentifier(String partIdentifier) {
		this.partIdentifier = partIdentifier;
	}

	public AnimationVector getRelativePosition() {
		return relativePosition;
	}

	public void setRelativePosition(AnimationVector relativePosition) {
		this.relativePosition = relativePosition;
	}

	public AnimationTransformation getRelativeTransformation() {
		return relativeTransformation;
	}

	public void setRelativeTransformation(AnimationTransformation relativeTransformation) {
		this.relativeTransformation = relativeTransformation;
	}

	private static final long serialVersionUID = 1L;

}
