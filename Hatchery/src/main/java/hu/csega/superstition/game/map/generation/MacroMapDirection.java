package hu.csega.superstition.game.map.generation;

class MacroMapDirection {

    MacroMapDirection(int minX, int minY, int maxX, int maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    int minX;
    int minY;
    int maxX;
    int maxY;

}
