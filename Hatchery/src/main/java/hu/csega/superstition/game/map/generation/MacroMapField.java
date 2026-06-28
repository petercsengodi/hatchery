package hu.csega.superstition.game.map.generation;

public class MacroMapField {

    public MacroMapField() {
        connections[1][1] = 1;
    }

    public int[][] connections = new int[3][3];
    public boolean start;
    public boolean tree;

}
