package hu.csega.superstition.game;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.superstition.game.map.SuperstitionMap;
import hu.csega.superstition.game.play.MonsterData;
import hu.csega.superstition.game.play.SpellInProgress;

public class SuperstitionSerializableModel implements Serializable {

	SuperstitionPlayer player = new SuperstitionPlayer();
	boolean sliding = true;

	public Set<MonsterData> monstersAlive = new HashSet<>();
	public Set<SpellInProgress> spellsInProgress = new HashSet<>();

	GameObjectPlacement groundPlacement = new GameObjectPlacement();

    SuperstitionMap map = new SuperstitionMap();

	public SuperstitionSerializableModel() {
		player.z = -500f;

		groundPlacement.moveTo(0f, 0f, 0f);

		Random random = new Random(System.currentTimeMillis());
		for(int i = 0; i < 10; i++) {
			MonsterData data;

			int rnd = random.nextInt(100);
			if(rnd < 30) {
				data = new MonsterData(SuperstitionGameElements.MONSTRUM_ANIMATION);
			} else if(rnd < 60) {
				data = new MonsterData(SuperstitionGameElements.MONKEY_ANIMATION);
			} else {
				data = new MonsterData(SuperstitionGameElements.RUNNING_ANIMATION);
			}

			data.x = 30.0*i;
			monstersAlive.add(data);
		}
	}

	private static final long serialVersionUID = 1L;
}
