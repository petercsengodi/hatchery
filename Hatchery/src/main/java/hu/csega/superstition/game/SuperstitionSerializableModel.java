package hu.csega.superstition.game;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import hu.csega.superstition.SuperstitionGameStarter;
import hu.csega.superstition.game.map.MapTile;
import hu.csega.superstition.game.map.SuperstitionMap;
import hu.csega.superstition.game.play.MonsterData;
import hu.csega.superstition.game.play.SpellInProgress;

public class SuperstitionSerializableModel implements Serializable {

	SuperstitionPlayer player = new SuperstitionPlayer();
	boolean sliding = true;

	public Set<SpellInProgress> spellsInProgress = new HashSet<>();
	public Set<SpellInProgress> monsterSpells = new HashSet<>();

    SuperstitionMap map = new SuperstitionMap();

	public SuperstitionSerializableModel() {
		player.z = 0f;
	}

	public void initializeMonsters() {
		Random random = SuperstitionGameStarter.RANDOM;
		for(int i = 0; i < 10_000; i++) {
			MonsterData data;

			int rnd = random.nextInt(100);
			if(rnd < 30) {
				data = new MonsterData(SuperstitionGameElements.MONSTRUM_ANIMATION);
			} else if(rnd < 60) {
				data = new MonsterData(SuperstitionGameElements.MONKEY_ANIMATION);
			} else {
				data = new MonsterData(SuperstitionGameElements.RUNNING_ANIMATION);
			}

			double px = map.ABSOLUTE_SIZE_X * SuperstitionGameStarter.RANDOM.nextDouble();
			double py = 0.0;
			double pz = map.ABSOLUTE_SIZE_Y * SuperstitionGameStarter.RANDOM.nextDouble();
			MapTile mapTile = map.loadMapTile(px, py, pz);
			mapTile.monsters.add(data);
			data.identifyPosition(px, py, pz, mapTile);
			data.setLevel(Math.floor(Math.max(pz / 10.0 * SuperstitionGameStarter.RANDOM.nextDouble(), 10)));
		}

		// Behemoth.
		MonsterData behemoth = new MonsterData(SuperstitionGameElements.BEHEMOTH_ANIMATION, 1_000_000.0);
		double px = SuperstitionMap.ABSOLUTE_SIZE_X - SuperstitionMap.TILE_SIZE_X / 2.0;
		double py = 100.0;
		double pz = SuperstitionMap.ABSOLUTE_SIZE_Y - SuperstitionMap.TILE_SIZE_Y / 2.0;
		MapTile mapTile = map.loadMapTile(px, py, pz);
		mapTile.monsters.add(behemoth);
		behemoth.identifyPosition(px, py, pz, mapTile);
		behemoth.scale = 1.0;
	}

	private static final long serialVersionUID = 1L;
}
