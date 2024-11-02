package hu.csega.superstition.game;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.games.engine.intf.GameGraphics;
import hu.csega.superstition.game.map.MapTile;
import hu.csega.superstition.game.map.SuperstitionMap;
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

	int lastAnimIndex;

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

		for(int ix = 0; ix < SuperstitionMap.SIZE_X; ix++) {
			for(int iy = 0; iy < SuperstitionMap.SIZE_Y; iy++) {
				MapTile mt = SuperstitionMap.mapTiles[ix][iy];
				if(mt == null) {
					continue;
				}

				// Bad AF.
				float x = (ix - SuperstitionMap.CENTER_X) * 100f;
				float y = (iy - SuperstitionMap.CENTER_Y) * 100f;
				if(distance(x + 50f, y + 50f, -player.x, -player.z) > 1000f) {
					continue;
				}

				g.drawModel(mt.handler, universe.groundPlacement);
			}
		}

		g.drawModel(elements.boxModel, universe.boxPlacement4);

		long timestamp = System.currentTimeMillis();
		player.animate(timestamp);
		int sceneIndex = player.spellCastingIndex();

		if(player.shouldCastNow()) {
			double dx = target.x;
			double dz = target.z;
			double l = Math.sqrt(dx*dx + dz*dz);
			double rl = 1000.0 / l;
			double nx = dx * rl;
			double nz = dz * rl;
			SpellInProgress spell = new SpellInProgress(timestamp, player.x, player.y - 5.0, player.z,
					player.x + nx, player.y - 5.0, player.z + nz);
			universe.spellsInProgress.add(spell);
		}

		g.drawAnimation(elements.wizardShootingAnimationHandler, sceneIndex, playerPlacement);

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
				spellPlacement.scale.set(0.05f, 0.05f, 0.05f);
				g.drawModel(elements.spellModel, spellPlacement);
			}
		}

		lastAnimIndex+=8;
		if(lastAnimIndex > 999)
			lastAnimIndex = 0;

		Iterator<MonsterData> monsters = universe.monstersAlive.iterator();
		while(monsters.hasNext()) {
			MonsterData monster = monsters.next();
			boolean hit = false;

			iterator = universe.spellsInProgress.iterator();
			while(iterator.hasNext()) {
				SpellInProgress spell = iterator.next();
				if(CollisionUtil.close(spell.getCurrentX(), spell.getCurrentZ(), monster.x, monster.z)) {
					iterator.remove();
					hit = true;
					break;
				}
			}

			if(hit) {
				monster.health -= 40.0;
				if(monster.health < 0.0) {
					monsters.remove();
					continue;
				}

				if(monster.target == null) {
					monster.target = player;
				}
			}

			GameObjectPlacement monsterPlacement = new GameObjectPlacement();
			monsterPlacement.position.set((float) monster.x, (float) monster.y + 25f, (float) monster.z);
			monsterPlacement.target.set((float) monster.x, (float) monster.y + 25f, (float) monster.z + 10f);
			monsterPlacement.up.set(0f, 1f, 0f);
			monsterPlacement.scale.set(0.1f, 0.1f, 0.1f);

			int animationIndex = (monster.target == null ? 0 : lastAnimIndex / 10);
			g.drawAnimation(elements.enemyRunningAnimationHandler, animationIndex, monsterPlacement);
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

	private static double distance(double x1, double y1, double x2, double y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		return Math.sqrt(dx*dx + dy*dy);
	}

	private static final Logger logger = LoggerFactory.createLogger(SuperstitionGameRenderer.class);
}
