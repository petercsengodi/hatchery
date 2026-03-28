package hu.csega.superstition.game.map.generation;

class MacroMapFreePort {

    MacroMapFreePort(MacroMapDirection direction, int x, int y) {
        this.direction = direction;
        this.x = x;
        this.y = y;
    }

    MacroMapDirection direction;
    int x;
    int y;

}
