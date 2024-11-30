package hu.csega.superstition.game.play;

import hu.csega.games.engine.g3d.GameObjectHandler;

import java.io.Serializable;

public class MonsterData implements Serializable {

    public MonsterData(String animation) {
        this.animation = animation;
    }

    public String animation;

    public double x;
    public double y;
    public double z;
    public double movingRotation;

    public double health = 100.0;

    /* FIXME this is not serializable. */
    public Object target;

    private static final long serialVersionUID = 1L;
}
