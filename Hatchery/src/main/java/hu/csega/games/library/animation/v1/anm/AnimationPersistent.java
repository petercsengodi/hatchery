package hu.csega.games.library.animation.v1.anm;

import java.io.Serializable;

public class AnimationPersistent implements Serializable {

	private String name;
	private Animation animation;
	private AnimationMisc misc;

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
		return animation;
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
		return misc;
	}

	public void setMisc(AnimationMisc misc) {
		this.misc = misc;
	}

	private static final long serialVersionUID = 1L;

}
