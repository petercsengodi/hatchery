package hu.csega.games.adapters.opengl;

import java.awt.image.BufferedImage;

import com.jogamp.opengl.GLAutoDrawable;

import hu.csega.games.adapters.opengl.models.OpenGLModelContainer;
import hu.csega.games.adapters.opengl.models.OpenGLModelStoreImpl;
import hu.csega.games.engine.anm.GameAnimation;
import hu.csega.games.engine.anm.GameAnimationScene;
import hu.csega.games.engine.g2d.GameColor;
import hu.csega.games.engine.g2d.GameHitShape;
import hu.csega.games.engine.g2d.GamePoint;
import hu.csega.games.engine.g2d.GameSprite;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.games.engine.g3d.GameSelectionLine;
import hu.csega.games.engine.g3d.GameTransformation;
import hu.csega.games.engine.intf.GameGraphics;

public class OpenGLGraphics implements GameGraphics {

	private GLAutoDrawable glAutodrawable;
	private OpenGLModelStoreImpl store;
	private int width;
	private int height;

	public void setAutoDrawable(GLAutoDrawable glAutodrawable, int surfaceWidth, int surfaceHeight) {
		this.glAutodrawable = glAutodrawable;
		this.width = surfaceWidth;
		this.height = surfaceHeight;
	}

	public void setStore(OpenGLModelStoreImpl store) {
		this.store = store;
	}

	public void clean() {
		glAutodrawable = null;
		store = null;
	}

	@Override
	public int screenWidth() {
		return width;
	}

	@Override
	public int screenHeight() {
		return height;
	}

	@Override
	public void rotate(double angle) {
	}

	@Override
	public void translate(double tx, double ty) {
	}

	@Override
	public void crossHair(double x, double y) {
	}

	@Override
	public void drawTriangleStrip(GameColor color, GamePoint... gamePoints) {
	}

	@Override
	public void drawTriangles(GameColor[] colors, GamePoint[] gamePoints) {
	}

	@Override
	public void drawSprite(GameSprite sprite, double x, double y) {
	}

	@Override
	public void drawSprite(BufferedImage image, double x, double y) {
	}

	@Override
	public void drawHitShape(GameHitShape hitShape, double x, double y, GameColor color) {
	}

	@Override
	public void startFrame() {
		store.startFrame(glAutodrawable);
	}

	@Override
	public void placeCamera(GameObjectPlacement cameraLocation) {
		if(cameraLocation == null) {
			return;
		}

		store.placeCamera(glAutodrawable, cameraLocation);
	}

	@Override
	public void drawOnScreen(GameObjectHandler modelReference, double x, double y) {
		if(modelReference == null) {
			return;
		}

		OpenGLModelContainer resolvedModel = store.resolveModel(modelReference);
		resolvedModel.drawOnScreen(glAutodrawable, x, y);
	}

	@Override
	public void drawModel(GameObjectHandler modelReference, GameObjectPlacement modelPlacement) {
		if(modelReference == null || modelPlacement == null) {
			return;
		}

		OpenGLModelContainer resolvedModel = store.resolveModel(modelReference);
		resolvedModel.draw(glAutodrawable, modelPlacement);
	}

	@Override
	public void drawModel(GameObjectHandler modelReference, GameTransformation transformation, boolean flipped) {
		if(modelReference == null || transformation == null) {
			return;
		}

		OpenGLModelContainer resolvedModel = store.resolveModel(modelReference);
		resolvedModel.draw(glAutodrawable, transformation, flipped);
	}

	@Override
	public void drawAnimation(GameObjectHandler animationReference, int state, GameObjectPlacement modelPlacement) {
		if(animationReference == null || modelPlacement == null) {
			return;
		}

		GameAnimation gameAnimation = store.resolveAnimation(animationReference);
		String[] meshes = gameAnimation.getMeshes();
		int numberOfMeshes = meshes.length;

		GameAnimationScene scene = gameAnimation.getScenes()[state];
		if(scene != null) {
            for (int i = 0; i < numberOfMeshes; i++) {
                if (scene.getVisible()[i]) {
                    GameObjectHandler mesh = store.loadMesh(meshes[i]); // FIXME at this point we should have a reference
                    boolean flipped = scene.getFlipped()[i];
                    GameTransformation transformation = scene.getTransformations()[i];
                    OpenGLModelContainer resolvedModel = store.resolveModel(mesh);
                    resolvedModel.draw(glAutodrawable, modelPlacement, transformation, flipped);
                }
            }
        }
	}

	@Override
	public void endFrame() {
		store.endFrame(glAutodrawable);
	}

	@Override
	public void setBaseMatricesAndViewPort(GameSelectionLine selectionLine) {
		store.setBaseMatricesAndViewPort(selectionLine);
	}

}
