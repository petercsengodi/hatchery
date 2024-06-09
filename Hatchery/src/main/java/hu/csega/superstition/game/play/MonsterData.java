package hu.csega.superstition.game.play;

import java.io.Serializable;

public class MonsterData implements Serializable {

    public double x;
    public double y;
    public double z;
    public double movingRotation;

    private int health;

    private static final long serialVersionUID = 1L;
}
