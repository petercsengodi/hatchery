package hu.csega.editors.anm.layer1.view3d;

import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameTransformation;

public class AnimatorSetPart {

	private GameObjectHandler model;
	private GameTransformation transformation;
	private boolean flipped;

	public GameObjectHandler getModel() {
		return model;
	}

	public void setModel(GameObjectHandler model) {
		this.model = model;
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
}
