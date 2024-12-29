package hu.csega.superstition.game.play;

import hu.csega.superstition.game.map.MapTile;

import java.io.Serializable;

public class MonsterData implements Serializable {

    public MonsterData(String animation) {
        this(animation, 100.0);
    }
    public MonsterData(String animation, double initialHealth) {
        this.animation = animation;
        this.health = initialHealth;
        this.scale = 0.1;
    }

    public void identifyPosition(double x, double y, double z, MapTile mapTile) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.mapTile = mapTile;
    }

    public String animation;

    public double x;
    public double y;
    public double z;
    public double scale;
    public double movingRotation;

    public double health;
    public long expectedXP;

    public MapTile mapTile;

    /* FIXME this is not serializable. */
    public Object target;

    private static final long serialVersionUID = 1L;
}
