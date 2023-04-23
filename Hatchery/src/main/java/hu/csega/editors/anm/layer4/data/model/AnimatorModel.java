package hu.csega.editors.anm.layer4.data.model;

import hu.csega.editors.anm.layer1.opengl.AnimatorMouseController;
import hu.csega.editors.anm.layer4.data.model.manipulators.AnimatorCameraManipulator;
import hu.csega.editors.anm.layer4.data.model.manipulators.AnimatorFileManipulator;
import hu.csega.editors.anm.layer4.data.model.manipulators.AnimatorPartManipulator;
import hu.csega.editors.anm.layer4.data.model.manipulators.AnimatorSnapshotManipulator;
import hu.csega.games.library.animation.v1.anm.Animation;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.units.Dependency;

public class AnimatorModel {

	private AnimationPersistent persistent;

	private AnimatorFileManipulator files;
	private AnimatorSnapshotManipulator snapshots;
	private AnimatorCameraManipulator camera;
	private AnimatorPartManipulator parts;

	private int selectedAnimationPart;
	private int selectedAnimationJoint;
	private int selectedAnimationScene;

	public AnimationPersistent getPersistent() {
		return persistent;
	}

	public void setPersistent(AnimationPersistent persistent) {
		this.persistent = persistent;
		snapshots.clear();
	}

	public void loadAnimation(String filename, Animation animation) {
		files.loadAnimation(filename, animation);
	}

	public void finalizeMoves() {
		snapshots.createNewSnapshot();
	}

	public int getSelectedAnimationPart() {
		return selectedAnimationPart;
	}

	public void setSelectedAnimationPart(int selectedAnimationPart) {
		this.selectedAnimationPart = selectedAnimationPart;
	}

	public int getSelectedAnimationJoint() {
		return selectedAnimationJoint;
	}

	public void setSelectedAnimationJoint(int selectedAnimationJoint) {
		this.selectedAnimationJoint = selectedAnimationJoint;
	}

	public int getSelectedAnimationScene() {
		return selectedAnimationScene;
	}

	public void setSelectedAnimationScene(int selectedAnimationScene) {
		this.selectedAnimationScene = selectedAnimationScene;
	}

	public void undo() {
		snapshots.undo();
	}

	public void redo() {
		snapshots.redo();
	}

	public void changeJSON(String text) {
		snapshots.changeJSON(text);
	}

	public void refreshCamera(AnimatorMouseController mouseController) {
		camera.refreshCamera(mouseController);
	}

	public void addNewPart(String filename) {
		parts.addNewPart(filename);
	}

	public void addJointToSelectedPart(double x, double y, double z) {

	}

	public void modifySelectedJoint(double x, double y, double z) {

	}

	public void deleteSelectedJoint() {

	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// DEPENDENCIES

	@Dependency
	public void setAnimatorFileManipulator(AnimatorFileManipulator animatorFileManipulator) {
		this.files = animatorFileManipulator;
	}

	@Dependency
	public void setAnimatorSnapshotManipulator(AnimatorSnapshotManipulator animatorSnapshotManipulator) {
		this.snapshots = animatorSnapshotManipulator;
	}

	@Dependency
	public void setAnimatorCameraManipulator(AnimatorCameraManipulator animatorCameraManipulator) {
		this.camera = animatorCameraManipulator;
	}

	@Dependency
	public void setAnimatorPartManipulator(AnimatorPartManipulator animatorPartManipulator) {
		this.parts = animatorPartManipulator;
	}
}
