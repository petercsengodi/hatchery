package hu.csega.superstition.game.map.generation;

class MacroMapDirection {

    public static final MacroMapDirection SUMMER = new MacroMapDirection(1, 1);
    public static final MacroMapDirection SPRING = new MacroMapDirection(1, -1);
    public static final MacroMapDirection FALL = new MacroMapDirection(-1, 1);
    public static final MacroMapDirection WINTER = new MacroMapDirection(-1, -1);

    public final int x;
    public final int y;
    public final int xUnsigned;
    public final int yUnsigned;

    private MacroMapDirection(int x, int y) {
        this.x = x;
        this.y = y;
        this.xUnsigned = x + 1;
        this.yUnsigned = y + 1;
    }

}
