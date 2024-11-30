package hu.csega.superstition.game;

import java.io.Serializable;
import java.util.HashSet;
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
	GameObjectPlacement testFTMPlacement = new GameObjectPlacement();
	GameObjectPlacement figureFTMPlacement = new GameObjectPlacement();
	GameObjectPlacement faceFTMPlacement = new GameObjectPlacement();

	GameObjectPlacement boxPlacement1 = new GameObjectPlacement();
	GameObjectPlacement boxPlacement2 = new GameObjectPlacement();
	GameObjectPlacement boxPlacement3 = new GameObjectPlacement();
	GameObjectPlacement boxPlacement4 = new GameObjectPlacement();

    SuperstitionMap map = new SuperstitionMap();

	public SuperstitionSerializableModel() {
		player.z = -500f;

		groundPlacement.moveTo(0f, 0f, 0f);

		testFTMPlacement.moveTo(0f, 0f, 0f);

		figureFTMPlacement.moveTo(20f, 0f, 0f);

		faceFTMPlacement.moveTo(100f, 100f, 100f);

		boxPlacement1.moveTo(-120f, 10f, -20f);

		boxPlacement2.moveTo(-130f, -10f, -20f);

		boxPlacement3.moveTo(-140f, -30f, -20f);

		boxPlacement4.moveTo(-150f, -50f, -20f);

		for(int i = 0; i < 10; i++) {
			MonsterData data = new MonsterData(i < 8 ? "run_2.json" : "snake.json");
			data.x = 30.0*i;
			monstersAlive.add(data);
		}
	}

	private static final long serialVersionUID = 1L;
}
