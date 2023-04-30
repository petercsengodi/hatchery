package hu.csega.editors.anm.layer4.data.model;

import hu.csega.editors.anm.layer1.opengl.AnimatorMouseController;
import hu.csega.editors.anm.layer4.data.model.manipulators.AnimatorCameraManipulator;
import hu.csega.editors.anm.layer4.data.model.manipulators.AnimatorFileManipulator;
import hu.csega.editors.anm.layer4.data.model.manipulators.AnimatorPartManipulator;
import hu.csega.editors.anm.layer4.data.model.manipulators.AnimatorSceneManipulator;
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
	private AnimatorSceneManipulator scenes;
	private AnimatorRefreshViews refreshViews;

	public AnimationPersistent getPersistent() {
		if(persistent == null) {
			persistent = new AnimationPersistent();
		}

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

	public void undo() {
		snapshots.undo();
	}

	public void redo() {
		snapshots.redo();
	}

	public void clearSnapshots() {
		snapshots.clear();
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

	public void editPartName(String displayName) {

	}

	public void deleteSelectedPart(String displayName) {

	}

	public void selectPart(String identifier) {
		parts.selectPart(identifier);
	}

	public void flipSelectedPart(double x, double y, double z) {
		parts.flipSelectedPart(x, y, z);
	}

	public void rotateSelectedPart(double x, double y, double z) {
		parts.rotateSelectedPart(x, y, z);
	}

	public void addJointToSelectedPart(String name, double x, double y, double z) {
		parts.addNewJoint(name, x, y, z);
	}

	public void modifySelectedJoint(String name, double x, double y, double z) {

	}

	public void deleteSelectedJoint() {

	}

	public void connectSelectedPart(String jointIdentifier) {
		parts.connectSelectedPart(jointIdentifier);
	}

	public void changeNumberOfScenes(int numberOfScenes) {
		scenes.changeNumberOfScenes(numberOfScenes);
	}

	public void selectScene(int sceneIndex) {
		scenes.selectScene(sceneIndex);
	}

	public void previouseScene() {
		scenes.previousScene();
	}

	public void nextScene() {
		scenes.nextScene();
	}

	public void copySelectedSceneFrom(int otherSceneIndex) {
		scenes.copySelectedSceneFrom(otherSceneIndex);
	}

	public void copyFromThePrevious() {
		scenes.copyFromThePrevious();
	}

	public void lerp(int start, int end, int writeStart, int writeEnd) {
		scenes.lerp(start, end, writeStart, writeEnd);
	}

	public void justRefreshViews() {
		refreshViews.refreshAll();
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

	@Dependency
	public void setAnimatorSceneManipulator(AnimatorSceneManipulator animatorSceneManipulator) {
		this.scenes = animatorSceneManipulator;
	}

	@Dependency
	public void setAnimatorRefreshViews(AnimatorRefreshViews animatorRefreshViews) {
		this.refreshViews = animatorRefreshViews;
	}
}
