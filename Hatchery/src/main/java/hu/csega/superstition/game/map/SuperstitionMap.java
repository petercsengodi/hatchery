package hu.csega.superstition.game.map;

import hu.csega.superstition.game.SuperstitionGameElements;
import hu.csega.superstition.game.play.MonsterData;

import java.io.Serializable;
import java.util.List;

public class SuperstitionMap implements Serializable {

    public static final double TILE_SIZE_X = SuperstitionGameElements.GROUND_SIZE;
    public static final double TILE_SIZE_Y = SuperstitionGameElements.GROUND_SIZE;

    public static final int SIZE_X = 100;
    public static final int SIZE_Y = 100;

    public static final double ABSOLUTE_SIZE_X = SIZE_X * TILE_SIZE_X;
    public static final double ABSOLUTE_SIZE_Y = SIZE_Y * TILE_SIZE_Y;

    public static MapTile[][] mapTiles = new MapTile[SIZE_X][SIZE_Y];

    public static int xIndexOf(double x) {
        return (int) Math.floor(x / TILE_SIZE_X);
    }

    public static int yIndexOf(double y) {
        return (int) Math.floor(y / TILE_SIZE_Y);
    }

    public static MapTile loadMapTile(int xIndex, int yIndex) {
        if(xIndex < 0 || yIndex < 0 || xIndex >= SIZE_X || yIndex >= SIZE_Y)
            return null;

        return mapTiles[xIndex][yIndex];
    }

    public static MapTile loadMapTile(double x, double y, double z) {
        int xIndex = xIndexOf(x);
        int yIndex = yIndexOf(z); /// z !!!
        return loadMapTile(xIndex, yIndex);
    }

    public void loadMonstersAround(double x, double y, double z, List<MonsterData> monstersAround) {
        int xIndex = (int) Math.floor(x / TILE_SIZE_X);
        int yIndex = (int) Math.floor(z / TILE_SIZE_Y); /// z !!!
        for(int ix = xIndex - 3; ix <= xIndex + 3; ix++) {
            if(ix < 0 || ix >= SIZE_X)
                continue;

            for(int iy = yIndex - 3; iy <= yIndex + 3; iy++) {
                if(iy < 0 || iy >= SIZE_Y)
                    continue;

                MapTile mapTile = mapTiles[ix][iy];
                if(mapTile != null) {
                    // FIXME Actual map width/height should be used.
                    monstersAround.addAll(mapTile.monsters);
                }
            }
        }
    }

    private static final long serialVersionUID = 1L;
}
