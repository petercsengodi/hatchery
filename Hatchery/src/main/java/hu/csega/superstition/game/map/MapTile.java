package hu.csega.superstition.game.map;

import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.superstition.game.play.MonsterData;

import java.util.HashSet;
import java.util.Set;

public class MapTile {

    public GameObjectPlacement groundPlacement;

    public float height;
    public GameObjectHandler handler;

    public Set<MonsterData> monsters = new HashSet<>();

    public MapTile(float x, float y, float z) {
        groundPlacement = new GameObjectPlacement().moveTo(x, y, z);
    }

}
