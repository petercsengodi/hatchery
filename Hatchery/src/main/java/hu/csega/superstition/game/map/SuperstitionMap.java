package hu.csega.superstition.game.map;

import java.io.Serializable;

public class SuperstitionMap implements Serializable {

    public static final int SIZE_X = 100;
    public static final int SIZE_Y = 100;
    public static final int CENTER_X = 50;
    public static final int CENTER_Y = 50;

    public static MapTile[][] mapTiles = new MapTile[SIZE_X][SIZE_Y];

    private static final long serialVersionUID = 1L;
}
