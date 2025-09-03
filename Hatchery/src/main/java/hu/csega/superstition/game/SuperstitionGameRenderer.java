package hu.csega.superstition.game;

import hu.csega.common.math.ScalarUtil;
import hu.csega.superstition.SuperstitionGameStarter;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SuperstitionGameRenderer {

	private final Matrix4f rotation = new Matrix4f();
	private final Vector4f target = new Vector4f();
	private final Vector4f up = new Vector4f();

	private Robot robot;
	private long lastCheck;

	int lastAnimIndex;

	private final List<String> logsOnScreen = new ArrayList<>();

	public List<MonsterData> monstersAround = new ArrayList<>();

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

		int playerXIndex = SuperstitionMap.xIndexOf(player.x);
		int playerYIndex = SuperstitionMap.yIndexOf(player.z); // z !!!!

		for(int ix = playerXIndex - 3; ix <= playerXIndex + 3; ix++) {
			for(int iy = playerYIndex - 3; iy <= playerYIndex + 3; iy++) {
				MapTile mt = SuperstitionMap.loadMapTile(ix, iy);
				if(mt == null) {
					continue;
				}

				/* FIXME
				// Bad AF.
				float half = SuperstitionGameElements.GROUND_SIZE / 2f;
				float x = ix * SuperstitionGameElements.GROUND_SIZE;
				float y = iy * SuperstitionGameElements.GROUND_SIZE;
				if(distance(x + half, y + half, -player.x, -player.z) > 1000f) {
					continue;
				}
				*/

				g.drawModel(mt.handler, mt.groundPlacement);
			}
		}

		long timestamp = System.currentTimeMillis();
		player.animate(timestamp);
		int sceneIndex = player.spellCastingIndex();

		SuperstitionSpellType shouldCast = player.shouldCastNow();
		if(shouldCast != null) {
			switch(shouldCast) {
				case FIREBALL: {
					double dx = target.x;
					double dz = target.z;
					double l = Math.sqrt(dx*dx + dz*dz);
					double rl = 1000.0 / l;
					double nx = dx * rl;
					double nz = dz * rl;
					SpellInProgress spell = new SpellInProgress(
							SuperstitionSpellType.FIREBALL, timestamp,
							player.x, player.y - 5.0, player.z,
							player.x + nx, player.y - 5.0, player.z + nz);
					spell.setHitPoint(player.xp * SuperstitionGameStarter.RANDOM.nextDouble() + 20);
					universe.spellsInProgress.add(spell);
					break;
				}
				case ICE_RAIN: {
					double sx = (SuperstitionGameStarter.RANDOM.nextInt(2) - 0.5) * 100;
					double sz = (SuperstitionGameStarter.RANDOM.nextInt(2) - 0.5) * 100;
					double txdiff = sx + (SuperstitionGameStarter.RANDOM.nextDouble() - 0.5) * 50;
					double tzdiff = sz + (SuperstitionGameStarter.RANDOM.nextDouble() - 0.5) * 50;
					for(int i = 0; i < 100; i++) {
						double dx = target.x;
						double dz = target.z;
						double l = Math.sqrt(dx * dx + dz * dz);
						double rl = 200.0 / l;
						double nx = dx * rl + (SuperstitionGameStarter.RANDOM.nextDouble() - 0.5) * 200;
						double nz = dz * rl + (SuperstitionGameStarter.RANDOM.nextDouble() - 0.5) * 200;
						double dy = (SuperstitionGameStarter.RANDOM.nextDouble() - 0.5) * 500;
						SpellInProgress spell = new SpellInProgress(
								SuperstitionSpellType.ICE_RAIN, timestamp,
								player.x + nx + txdiff, player.y + 300.0 + dy, player.z + nz + tzdiff,
								player.x + nx, player.y - 50.0 + dy, player.z + nz);
						spell.setHitPoint(player.xp * SuperstitionGameStarter.RANDOM.nextDouble() / 100.0 + 1);
						universe.spellsInProgress.add(spell);
					}
					break;
				}
			}
		}

		g.drawAnimation(elements.monsterAnimations.get(SuperstitionGameElements.WIZARD_ANIMATION), sceneIndex, playerPlacement);

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
				g.drawModel(elements.spellModel[spell.spellModelIndex()], spellPlacement);
			}
		}

		// TODO: basically copied code
		iterator = universe.monsterSpells.iterator();
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
				g.drawModel(elements.spellModel[spell.spellModelIndex()], spellPlacement);
			}
		}

		lastAnimIndex+=8;
		if(lastAnimIndex > 999)
			lastAnimIndex = 0;

		monstersAround.clear();
		universe.map.loadMonstersAround(player.x, player.y, player.z, monstersAround);

		for(MonsterData monster : monstersAround) {

			// TODO: So no calculation needed with monsters too far away?
			// Optimization: If distance in one dimension is too far, then we don't need to calculate full distance.
			double absXDiff = Math.abs(monster.x - player.x);
			double absZDiff = Math.abs(monster.z - player.z);
			if(absXDiff > 1000f || absZDiff > 1000f) {
				continue;
			}

			// Optimization, if both dimensions are less than 200f, the distance does not need to be calculated.
			if(absXDiff > 200f && absZDiff > 200f && ScalarUtil.distance(monster.x, monster.z, player.x, player.z) > 1000f) {
				continue;
			}

			iterator = universe.spellsInProgress.iterator();
			while(iterator.hasNext()) {
				SpellInProgress spell = iterator.next();
				if(CollisionUtil.close(spell.getCurrentX(), spell.getCurrentZ(), monster.x, monster.z)) {
					iterator.remove();

					String earlierHealth = doubleToIntString(monster.health);
					monster.health -= spell.getHitPoint();
					if(monster.health <= 0.0) {
						monster.health = 0.0;
						double earlierXP = (player.xp + 100);
						player.xp += monster.expectedXP;
						player.health = player.health * (player.xp + 100) / earlierXP;
						addToLog(earlierHealth + " => Dies! XP: " + player.xp + " Health: " + doubleToIntString(player.health));
						monster.mapTile.monsters.remove(monster);
						monster.mapTile = null;
					} else {
						if (monster.target == null) {
							monster.target = player;
						}

						addToLog("Hit! HP: " + earlierHealth + " => " + doubleToIntString(monster.health));
					}
				}
			}

			GameObjectPlacement monsterPlacement = new GameObjectPlacement();
			monsterPlacement.position.set((float) monster.x, (float) monster.y + 25f, (float) monster.z);
			monsterPlacement.target.set((float) monster.x, (float) monster.y + 25f, (float) monster.z + 10f);
			monsterPlacement.up.set(0f, 1f, 0f);
			monsterPlacement.scale.set((float) monster.scale, (float) monster.scale, (float) monster.scale);

			int animationIndex = (monster.target == null ? 0 : lastAnimIndex / 10);
			GameObjectHandler animation = elements.monsterAnimations.get(monster.animation);
			g.drawAnimation(animation, animationIndex, monsterPlacement);
		}

		monstersAround.clear();

		iterator = universe.monsterSpells.iterator();
		while(iterator.hasNext()) {
			SpellInProgress spell = iterator.next();
			if(CollisionUtil.close(spell.getCurrentX(), spell.getCurrentZ(), player.x, player.z)) {
				iterator.remove();

				player.health -= spell.getHitPoint();
				if(player.health <= 0.0) {
					player.health = 0.0;
					addToLog("You died!");
				} else {
					addToLog("Hurt! Health: " + doubleToIntString(player.health));
				}
			}
		}

		/*
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
		*/

		int y = -1;
		for(String line : logsOnScreen)
			drawString(g, elements, 0, y++, line);

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

	private void addToLog(String line) {
		while(logsOnScreen.size() > 4)
			logsOnScreen.remove(0);
		logsOnScreen.add(line);
	}

	private String doubleToIntString(double doubleValue) {
		return String.valueOf((int) Math.floor(doubleValue));
	}

	private static final Logger logger = LoggerFactory.createLogger(SuperstitionGameRenderer.class);
}
