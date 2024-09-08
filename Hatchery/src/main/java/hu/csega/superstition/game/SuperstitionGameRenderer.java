package hu.csega.superstition.game;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.games.engine.intf.GameGraphics;
import hu.csega.superstition.game.play.CollisionUtil;
import hu.csega.superstition.game.play.MonsterData;
import hu.csega.superstition.game.play.SpellInProgress;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Iterator;

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

		long timestamp = System.currentTimeMillis();
		player.animate(timestamp);
		int sceneIndex = player.spellCastingIndex();

		if(player.shouldCastNow()) {
			SpellInProgress spell = new SpellInProgress(timestamp, player.x, player.y - 5.0, player.z,
					player.x + target.x * 1000.0, player.y - 5.0, player.z + target.z * 1000.0);
			universe.spellsInProgress.add(spell);
		}

		g.drawAnimation(elements.testAnimationHandler, sceneIndex, playerPlacement);

		Iterator<SpellInProgress> iterator = universe.spellsInProgress.iterator();
		while(iterator.hasNext()) {
			SpellInProgress spell = iterator.next();
			spell.animate(timestamp);
			if(spell.isOver()) {
				iterator.remove();
			} else {
				GameObjectPlacement spellPlacement = new GameObjectPlacement();
				spellPlacement.position.set((float) spell.getCurrentX(), (float) spell.getCurrentY(), (float) spell.getCurrentZ());
				spellPlacement.target.set((float) (spell.getCurrentX() + target.x), (float) spell.getCurrentY(), (float) (spell.getCurrentZ() + target.z));
				spellPlacement.up.set(0f, 1f, 0f);
				spellPlacement.scale.set(0.3f, 0.3f, 0.3f);
				g.drawModel(elements.spellModel, spellPlacement);
			}
		}

		Iterator<MonsterData> monsters = universe.monstersAlive.iterator();
		while(monsters.hasNext()) {
			MonsterData monster = monsters.next();
			boolean dead = false;

			iterator = universe.spellsInProgress.iterator();
			while(iterator.hasNext()) {
				SpellInProgress spell = iterator.next();
				if(CollisionUtil.close(spell.getCurrentX(), spell.getCurrentZ(), monster.x, monster.z)) {
					iterator.remove();
					dead = true;
					break;
				}
			}

			if(dead) {
				monsters.remove();
				continue;
			}

			GameObjectPlacement monsterPlacement = new GameObjectPlacement();
			monsterPlacement.position.set((float) monster.x, (float) monster.y, (float) monster.z);
			monsterPlacement.target.set((float) monster.x, (float) monster.y, (float) monster.z + 10f);
			monsterPlacement.up.set(0f, 1f, 0f);
			monsterPlacement.scale.set(0.1f, 0.1f, 0.1f);
			g.drawAnimation(elements.testAnimationHandler, 0, monsterPlacement);
		}

		g.drawOnScreen(elements.alphabet[0], 0, 0);
		g.drawOnScreen(elements.alphabet[1], 0, 1);
		g.drawOnScreen(elements.alphabet[2], 0, 2);

		g.drawOnScreen(elements.numbers[0], 2, 0);
		g.drawOnScreen(elements.numbers[1], 2, 1);

		g.drawOnScreen(elements.dot, 3, 0);
		g.drawOnScreen(elements.colon, 3, 1);
		g.drawOnScreen(elements.exclamation, 3, 2);
		g.drawOnScreen(elements.comma, 3, 3);
		g.drawOnScreen(elements.question, 3, 4);

		drawString(g, elements, 4, 0, "Hello!\nAnybody there?\nZ is the last letter...");

		// drawString(g, elements, 4, 6, "0123456789");

		hackBlockScreenSaverActivation();
	}

	private void drawString(GameGraphics g, SuperstitionGameElements elements, int x, int y, String s) {
		int pos = x;
		int line = y;

		for(int i = 0; i < s.length(); i++) {
			GameObjectHandler character = elements.question;
			char c = s.charAt(i);
			if(c == '\n') {
				line ++;
				pos = x;
			}

			if(c != '\n' && c != '\r' && c <= ' ')
				pos ++;

			if(c <= ' ')
				continue;

			int maybe = (int)(c - 'a');
			if(maybe >= 0 && maybe < elements.numberOfLetters) {
				character = elements.alphabet[maybe];
			} else {
				maybe = (int)(c - 'A');
				if(maybe >= 0 && maybe < elements.numberOfLetters) {
					character = elements.alphabet[maybe];
				} else {
					maybe = (int)(c - '0');
					if(maybe >= 0 && maybe < elements.numberOfNumbers) {
						character = elements.numbers[maybe];
					} else {
						switch(c) {
							case '.': character = elements.dot; break;
							case ',': character = elements.comma; break;
							case ':': character = elements.colon; break;
							case '!': character = elements.exclamation; break;
							case '?': character = elements.question; break;
						}
					}
				}
			}

			g.drawOnScreen(character, pos ++, line);
		}
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
