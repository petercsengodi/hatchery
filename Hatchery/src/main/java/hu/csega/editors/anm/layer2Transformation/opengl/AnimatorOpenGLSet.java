package hu.csega.editors.anm.layer2Transformation.opengl;

import java.util.ArrayList;
import java.util.List;

import hu.csega.games.engine.g3d.GameObjectPlacement;

public class AnimatorOpenGLSet {

	private GameObjectPlacement camera; // FIXME to be deleted?
	private List<AnimatorOpenGLSetPart> parts;

	public GameObjectPlacement getCamera() {
		return camera;
	}

	public void setCamera(GameObjectPlacement camera) {
		this.camera = camera;
	}

	public List<AnimatorOpenGLSetPart> getParts() {
		return parts;
	}

	public void setParts(List<AnimatorOpenGLSetPart> parts) {
		this.parts = parts;
	}

	public void addPart(AnimatorOpenGLSetPart part) {
	    if(parts == null) {
            parts = new ArrayList<>();
        }

	    parts.add(part);
    }

}
