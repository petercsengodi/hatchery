package hu.csega.superstition.game;

import hu.csega.superstition.SuperstitionGameStarter;
import hu.csega.superstition.game.map.MapTile;
import hu.csega.superstition.game.map.SuperstitionMap;
import hu.csega.superstition.game.play.MonsterData;
import hu.csega.superstition.game.play.SpellInProgress;
import hu.csega.superstition.game.play.SuperstitionTree;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SuperstitionSerializableModel implements Serializable {

	public SuperstitionPlayer player = new SuperstitionPlayer();
	public boolean sliding = true;

	public Set<SpellInProgress> spellsInProgress = new HashSet<>();
	public Set<SpellInProgress> monsterSpells = new HashSet<>();

    public SuperstitionMap map;
	public List<SuperstitionTree> trees = new ArrayList<>();

	public long lastTreeGrowthCalculated;

	private transient boolean invalidTreeGrowthTimestampLogged;

	public SuperstitionSerializableModel() {
		player.z = 0f;
	}

	public void initializeModel() {
		Random random = SuperstitionGameStarter.RANDOM;
		for(int i = 0; i < 10_000; i++) {
			MonsterData data;

			int rnd = random.nextInt(100);
			if(rnd < 30) {
				data = new MonsterData(SuperstitionGameElements.MONSTRUM_ANIMATION, SuperstitionGameElements.WOODGOLEM_DYING_ANIMATION);
			} else if(rnd < 60) {
				data = new MonsterData(SuperstitionGameElements.MONKEY_ANIMATION, SuperstitionGameElements.WOODGOLEM_DYING_ANIMATION);
			} else {
				data = new MonsterData(SuperstitionGameElements.RUNNING_ANIMATION, SuperstitionGameElements.WOODGOLEM_DYING_ANIMATION);
			}

			double px = 0.0, py = 0.0, pz = 0.0;
			MapTile mapTile = null;

			for(int attempt = 0; attempt < 10; attempt++) { // FIXME This is a very bad algorithm.
				px = map.ABSOLUTE_SIZE_X * SuperstitionGameStarter.RANDOM.nextDouble();
				py = 0.0;
				pz = map.ABSOLUTE_SIZE_Y * SuperstitionGameStarter.RANDOM.nextDouble();
				mapTile = map.loadMapTile(px, py, pz);
				if (mapTile != null /* && mapTile.??? */ ) {
					// FIXME Map width/height should be used.
					mapTile.monsters.add(data);
					break;
				} // FIXME Always generate on valid positions!
			}

			data.identifyPosition(px, py, pz, mapTile);

			double distanceFromPlayer = Math.sqrt((px - player.x)*(px - player.x) + (py - player.y)*(py - player.y));
			data.setLevel(Math.floor(Math.max(distanceFromPlayer / 10.0 * SuperstitionGameStarter.RANDOM.nextDouble(), 10)));
		}

		// Behemoth.
		MonsterData behemoth = new MonsterData(SuperstitionGameElements.BEHEMOTH_ANIMATION, SuperstitionGameElements.WOODGOLEM_DYING_ANIMATION, 1_000_000.0);
		double px = map.ABSOLUTE_SIZE_X - SuperstitionMap.TILE_SIZE_X / 2.0;
		double py = 100.0;
		double pz = map.ABSOLUTE_SIZE_Y - SuperstitionMap.TILE_SIZE_Y / 2.0;
		MapTile mapTile = map.loadMapTile(px, py, pz);

		if(mapTile != null) {
			// FIXME Actual map width/height should be used.
			mapTile.monsters.add(behemoth);
		}

		behemoth.identifyPosition(px, py, pz, mapTile);
		behemoth.scale = 1.0;
	}

	public void calculateTreeGrowth(long currentTime) {
		if(currentTime < lastTreeGrowthCalculated) {
			if(!invalidTreeGrowthTimestampLogged) {
				logger.warning("Invalid current time stamp: " + currentTime + " Last tree growth timestamp: " + lastTreeGrowthCalculated);
				invalidTreeGrowthTimestampLogged = true;
			}
			return;
		}

		// FIXME tree growth calculation
	}

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.createLogger(SuperstitionSerializableModel.class);
}
