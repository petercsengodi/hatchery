package hu.csega.superstition.game.map.generation;

import java.util.ArrayList;
import java.util.List;

public class MacroMap {

    private int startX = 100;
    private int startY = 100;
    private int maxX = 200;
    private int maxY = 200;

    private MacroMapDirection summer = new MacroMapDirection(0, 0, 1, 1);
    private MacroMapDirection spring = new MacroMapDirection(0, -1, 1, 0);
    private MacroMapDirection fall = new MacroMapDirection(-1, 0, 0, 1);
    private MacroMapDirection winter = new MacroMapDirection(-1, -1, 0, 0);

    private int[][] map = new int[maxX + 1][maxY + 1];

    private List<MacroMapFreePort> freePorts = new ArrayList<>();

    public void start() {
        MacroMapFreePort freePortSummer = new MacroMapFreePort(summer, startX + 1, startY + 1);
        MacroMapFreePort freePortSpring = new MacroMapFreePort(summer, startX + 1, startY - 1);
        MacroMapFreePort freePortFall = new MacroMapFreePort(summer, startX - 1, startY + 1);
        MacroMapFreePort freePortWinter = new MacroMapFreePort(summer, startX - 1, startY - 1);
    }

}
