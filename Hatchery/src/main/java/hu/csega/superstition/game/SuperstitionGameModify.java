package hu.csega.superstition.game;

import hu.csega.games.engine.intf.GameControl;
import hu.csega.superstition.game.play.CollisionUtil;
import hu.csega.superstition.game.play.MonsterData;

public class SuperstitionGameModify {

	private static final double PI2 = Math.PI * 2.0;
	private static final double PLAYER_MOUSE_HORIZONTAL_SENSITIVITY = 0.003;
	private static final double PLAYER_MOUSE_VERTICAL_SENSITIVITY = 0.001;
	private static final double PLAYER_MOUSE_SIGHT_LIMIT = Math.PI/2.0 - 0.01;
	private static final double PLAYER_ROTATION = 0.01;
	private static final double PLAYER_FORWARD = 1.0;

	private static final double MONSTER_SPEED = 40.0;

	public static void modify(SuperstitionSerializableModel model, GameControl control, double elapsedTime) {
		SuperstitionPlayer player = model.player;

		double speedModifier = (control.isControlOn() ? 15.0 : 1.0) * PLAYER_FORWARD;

		if(control.isLeftOn()) {
			if(control.isShiftOn() ^ model.sliding) {
				player.x += (speedModifier * Math.cos(player.movingRotation));
				player.z += (speedModifier * Math.sin(player.movingRotation));
			} else {
				player.movingRotation -= PLAYER_ROTATION;
				while(player.movingRotation < PI2) {
					player.movingRotation += PI2;
				}
			}
		}

		if(control.isRightOn()) {
			if(control.isShiftOn() ^ model.sliding) {
				player.x -= (speedModifier * Math.cos(player.movingRotation));
				player.z -= (speedModifier * Math.sin(player.movingRotation));
			} else {
				player.movingRotation += PLAYER_ROTATION;
				while(player.movingRotation >= PI2) {
					player.movingRotation -= PI2;
				}
			}
		}

		if(control.isUpOn()) {
			player.x += (-speedModifier * Math.sin(player.movingRotation));
			player.z += (speedModifier * Math.cos(player.movingRotation));
		}

		if(control.isDownOn()) {
			player.x -= (-speedModifier * Math.sin(player.movingRotation));
			player.z -= (speedModifier * Math.cos(player.movingRotation));
		}

		for(MonsterData monster : model.monstersAlive) {
			if(monster.target == null) {
				continue;
			}

			double dx = player.x - monster.x;
			double dz = player.z - monster.z;
			if(Math.abs(dx) > 5 || Math.abs(dz) > 5) {
				// TODO definitely wrong
				monster.x = monster.x + MONSTER_SPEED * Math.signum(dx) * elapsedTime;
				monster.z = monster.z + MONSTER_SPEED * Math.signum(dz) * elapsedTime;
			}
		}

		// TODO monsters colliding with each other
		for(MonsterData monster1 : model.monstersAlive) {
			for(MonsterData monster2 : model.monstersAlive) {
				if(monster1 == monster2) {
					continue;
				}

				// TODO definitely wrong
				double dx = monster2.x - monster1.x;
				double dz = monster2.z - monster1.z;
				double d = Math.sqrt(dx*dx + dz*dz);
				if(d < 10.0) {
					double ddx = (10.0-dx) / 2.0;
					monster2.x -= ddx;
					monster1.x += ddx;
					double ddz = (10.0-dz) / 2.0;
					monster2.z -= ddz;
					monster1.z += ddz;
				}
			}
		}

	}

	public static void pressed(SuperstitionSerializableModel model, int x, int y, boolean leftMouse, boolean rightMouse) {
		if(leftMouse) {
			SuperstitionPlayer player = model.player;
			player.startSpellCasting(75.0);
		}
	}

	public static void released(SuperstitionSerializableModel model, int x, int y, boolean leftMouse, boolean rightMouse) {
	}

	public static void clicked(SuperstitionSerializableModel model, int x, int y, boolean leftMouse, boolean rightMouse) {
	}

	public static void moved(SuperstitionSerializableModel model, int deltaX, int deltaY, boolean leftMouseDown, boolean rightMouseDown) {
		SuperstitionPlayer player = model.player;

		player.movingRotation += PLAYER_MOUSE_HORIZONTAL_SENSITIVITY * deltaX;
		while(player.movingRotation < PI2) {
			player.movingRotation += PI2;
		}

		player.sightVerticalRotation += PLAYER_MOUSE_VERTICAL_SENSITIVITY * deltaY;

		if(player.sightVerticalRotation > PLAYER_MOUSE_SIGHT_LIMIT) {
			player.sightVerticalRotation = PLAYER_MOUSE_SIGHT_LIMIT;
		}

		if(player.sightVerticalRotation < -PLAYER_MOUSE_SIGHT_LIMIT) {
			player.sightVerticalRotation = -PLAYER_MOUSE_SIGHT_LIMIT;
		}
	}

	public static void hit(SuperstitionSerializableModel model, char key) {
		if(key == 's' || key == 'S')
			model.sliding = !model.sliding;
	}

}
