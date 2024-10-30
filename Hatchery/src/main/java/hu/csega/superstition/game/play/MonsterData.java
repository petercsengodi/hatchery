package hu.csega.superstition.game.play;

import java.io.Serializable;

public class MonsterData implements Serializable {

    public double x;
    public double y;
    public double z;
    public double movingRotation;

    public double health = 100.0;

    public Object target;

    private static final long serialVersionUID = 1L;
}
