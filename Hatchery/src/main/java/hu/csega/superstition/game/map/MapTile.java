package hu.csega.superstition.game.map;

import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.superstition.game.play.MonsterData;
import hu.csega.superstition.game.play.SuperstitionTree;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class MapTile implements Serializable {

    public GameObjectPlacement groundPlacement;

    public float height;
    public String mapElementName;
    public transient GameObjectHandler mapElementHandler;

    public Set<MonsterData> monsters = new HashSet<>();
    public SuperstitionTree tree;

    public MapTile(float x, float y, float z) {
        groundPlacement = new GameObjectPlacement().moveTo(x, y, z);
    }

    private static final long serialVersionUID = 1L;

}
