package hu.csega.editors.anm.layer2Transformation.opengl;

import hu.csega.editors.anm.layer2Transformation.parts.AnimatorSetPart;
import hu.csega.games.engine.g3d.GameObjectHandler;

public class AnimatorOpenGLSetPart {

	private AnimatorSetPart originalPart;
	private GameObjectHandler handler;

	public AnimatorOpenGLSetPart() {
	}

	public AnimatorOpenGLSetPart(AnimatorSetPart originalPart, GameObjectHandler handler) {
		this.originalPart = originalPart;
		this.handler = handler;
	}

	public GameObjectHandler getHandler() {
		return handler;
	}

	public void setHandler(GameObjectHandler handler) {
		this.handler = handler;
	}

	public AnimatorSetPart getOriginalPart() {
		return originalPart;
	}

	public void setOriginalPart(AnimatorSetPart originalPart) {
		this.originalPart = originalPart;
	}
}
