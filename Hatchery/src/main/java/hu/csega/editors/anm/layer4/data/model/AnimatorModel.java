package hu.csega.editors.anm.layer4.data.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hu.csega.editors.anm.layer1.opengl.AnimatorMouseController;
import hu.csega.games.library.animation.v1.anm.Animation;
import hu.csega.games.library.animation.v1.anm.AnimationMisc;
import hu.csega.games.library.animation.v1.anm.AnimationPlacement;
import hu.csega.games.library.animation.v1.anm.AnimationVector;

public class AnimatorModel {

	private AnimationPersistent persistent;
	private List<AnimationSnapshot> previousStates;
	private List<AnimationSnapshot> nextStates;

	public AnimatorModel() {
		this.previousStates = new ArrayList<>();
		this.nextStates = new ArrayList<>();
	}

	public void finalizeMoves() {
	}

	public AnimationPersistent getPersistent() {
		return persistent;
	}

	public void loadAnimation(String filename, Animation animation) {
		AnimationMisc misc = new AnimationMisc();
		misc.setFilename(filename);

		AnimationPersistent persistent = new AnimationPersistent();
		persistent.setAnimation(animation);
		persistent.setName(filename);
		persistent.setMisc(misc);
		setPersistent(persistent);
	}

	public void setPersistent(AnimationPersistent persistent) {
		this.persistent = persistent;
		this.previousStates.clear();
		this.nextStates.clear();
	}

	public void addNewState(Animation animation) {
		AnimationSnapshot snaphost = new AnimationSnapshot(serialize(animation));
		previousStates.add(snaphost);
		nextStates.clear();
	}

	public Animation undo() {
		if(previousStates.isEmpty())
			return null;

		AnimationSnapshot snapshot = previousStates.remove(previousStates.size() - 1);
		nextStates.add(snapshot);
		return (Animation) deserialize(snapshot.getBytes());
	}

	public Animation redo() {
		if(nextStates.isEmpty())
			return null;

		AnimationSnapshot snapshot = nextStates.remove(nextStates.size() - 1);
		previousStates.add(snapshot);
		return (Animation) deserialize(snapshot.getBytes());
	}

	public static byte[] serialize(Serializable object) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
			out.writeObject(object);
		} catch (IOException ex) {
			throw new RuntimeException("Error occurred when trying to serialize object!", ex);
		}
		return baos.toByteArray();
	}

	public static Serializable deserialize(byte[] array) {
		Serializable ret = null;

		ByteArrayInputStream bais = new ByteArrayInputStream(array);
		try (ObjectInputStream in = new ObjectInputStream(bais)) {
			ret = (Serializable) in.readObject();
		} catch (IOException | ClassNotFoundException ex) {
			throw new RuntimeException("Error occurred when trying to deserialize object!", ex);
		}

		return ret;
	}

	public void refreshCamera(AnimatorMouseController mouseController) {
		if(persistent == null) {
			return;
		}

		AnimationMisc misc = persistent.getMisc();
		if(misc == null) {
			misc = new AnimationMisc();
			persistent.setMisc(misc);
		}

		AnimationPlacement camera = misc.getCamera();
		if(camera == null) {
			return;
		}

		AnimationVector position = camera.getPosition();
		if(position == null) {
			return;
		}

		double alfa = 0.0;
		double beta = 0.0;
		double distance = 100.0;

		if(mouseController != null) {
			double scaling = mouseController.getScaling();
			distance *= scaling;
			alfa = mouseController.getAlfa();
			beta = mouseController.getBeta();
		}

		double y = distance * Math.sin(beta);
		double distanceReduced = distance * Math.cos(beta);

		float[] p = position.getV();

		p[0] = (float)(Math.cos(alfa) * distanceReduced);
		p[1] = (float) y;
		p[2] = (float)(Math.sin(alfa) * distanceReduced);
		p[3] = 1f;
	}

}
