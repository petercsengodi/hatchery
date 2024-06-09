package hu.csega.superstition.game;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.games.engine.intf.GameGraphics;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;

public class SuperstitionGameRenderer {

	private final Matrix4f rotation = new Matrix4f();
	private final Vector4f target = new Vector4f();
	private final Vector4f up = new Vector4f();

	private Robot robot;
	private long lastCheck;

	public void renderGame(GameEngineFacade facade, SuperstitionSerializableModel universe, SuperstitionGameElements elements) {
		GameGraphics g = facade.graphics();

		SuperstitionPlayer player = universe.player;

		rotation.identity();
		rotation.rotateAffineXYZ(0f,
				-(float)(player.movingRotation + player.sightHorizontalRotation),
				0f);
		rotation.rotateAffineXYZ((float)(player.sightVerticalRotation),
				0f,
				0f);

		up.set(0f, 1f, 0f, 1f);
		rotation.transform(up);

		target.set(0f, 0f, 1f, 1f);
		rotation.transform(target);

		GameObjectPlacement playerPlacement = new GameObjectPlacement();
		playerPlacement.position.set((float)player.x, (float)player.y, (float)player.z);
		playerPlacement.target.set(
				playerPlacement.position.x + target.x,
				playerPlacement.position.y /* + target.y */,
				playerPlacement.position.z + target.z
		);
		playerPlacement.up.set(0f, 1f, 0f);
		playerPlacement.scale.set(0.1f, 0.1f, 0.1f);

		float dist = 400.0f;

		GameObjectPlacement cameraLocation = new GameObjectPlacement();
		cameraLocation.target.copyValuesFrom(playerPlacement.position);
		cameraLocation.up.set(up.x, up.y, up.z);
		cameraLocation.position.set(
				playerPlacement.position.x - target.x * dist,
				playerPlacement.position.y - target.y * dist,
				playerPlacement.position.z - target.z * dist);

		g.placeCamera(cameraLocation);

		g.drawModel(elements.groundHandler, universe.groundPlacement);

		g.drawModel(elements.boxModel, universe.boxPlacement4);

		player.animate(System.currentTimeMillis());
		int sceneIndex = player.spellCastingIndex();

		System.out.println("Scene index: " + sceneIndex);

		g.drawAnimation(elements.testAnimationHandler, sceneIndex, playerPlacement);

		hackBlockScreenSaverActivation();
	}

	/**
	 * This is needed so the screen saver isn't activated when user doesn't do anything.
	 */
	private void hackBlockScreenSaverActivation() {
		long now = System.currentTimeMillis();
		if(now - lastCheck < 15_000) {
			return;
		}

		lastCheck = now;

		try {
			if (robot == null) {
				robot = new Robot();
			}

			robot.keyPress(KeyEvent.VK_SCROLL_LOCK);
			robot.keyRelease(KeyEvent.VK_SCROLL_LOCK);
			robot.keyPress(KeyEvent.VK_SCROLL_LOCK);
			robot.keyRelease(KeyEvent.VK_SCROLL_LOCK);
		} catch(Exception ex) {
			logger.error("Could not reset screensaver timer: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
		}
	}

	private static final Logger logger = LoggerFactory.createLogger(SuperstitionGameRenderer.class);
}
