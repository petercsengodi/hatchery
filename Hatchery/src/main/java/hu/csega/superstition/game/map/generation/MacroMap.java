package hu.csega.superstition.game.map.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    public int[][] getMap() {
        return map;
    }

    public int sizeX() {
        return maxX + 1;
    }

    public int sizeY() {
        return maxY + 1;
    }

    public void start() {
        MacroMapFreePort freePortSummer = new MacroMapFreePort(summer, startX + 1, startY + 1);
        MacroMapFreePort freePortSpring = new MacroMapFreePort(spring, startX + 1, startY - 1);
        MacroMapFreePort freePortFall = new MacroMapFreePort(fall, startX - 1, startY + 1);
        MacroMapFreePort freePortWinter = new MacroMapFreePort(winter, startX - 1, startY - 1);

        freePorts.add(freePortSpring);
        freePorts.add(freePortSummer);
        freePorts.add(freePortFall);
        freePorts.add(freePortWinter);

        map[startX][startY] = 1;
        map[freePortSpring.x][freePortSpring.y] = 1;
        map[freePortSummer.x][freePortSummer.y] = 1;
        map[freePortFall.x][freePortFall.y] = 1;
        map[freePortWinter.x][freePortWinter.y] = 1;

        List<MacroMapFreePort> newFreePorts = new ArrayList<>();
        Random random = new Random();

        for(int i = 0; i < startX - 2; i++) {
            for(MacroMapFreePort freePort : freePorts) {
                MacroMapDirection direction = freePort.direction;

                int directions;
                if(random.nextInt(100) < 80) {
                    directions = random.nextInt(3) + 1;
                } else {
                    directions = random.nextInt(7) + 1;
                }

                if(directions == 1 || directions == 4 || directions == 6 || directions == 7) {
                    int x = freePort.x + direction.minX;
                    int y = freePort.y + direction.maxY;
                    if(x >= 0 && y >= 0 && x <= maxX && y <= maxY) {
                        map[x][y] = 1;
                        MacroMapFreePort newFreePort = new MacroMapFreePort(direction, x, y);
                        newFreePorts.add(newFreePort);
                    }
                }

                if(directions == 2 || directions == 4 || directions == 5 || directions == 7) {
                    int x = freePort.x + direction.minX;
                    int y = freePort.y + direction.minY;
                    if(x >= 0 && y >= 0 && x <= maxX && y <= maxY) {
                        map[x][y] = 1;
                        MacroMapFreePort newFreePort = new MacroMapFreePort(direction, x, y);
                        newFreePorts.add(newFreePort);
                    }
                }

                if(directions == 3 || directions == 5 || directions == 6 || directions == 7) {
                    int x = freePort.x + direction.maxX;
                    int y = freePort.y + direction.minY;
                    if(x >= 0 && y >= 0 && x <= maxX && y <= maxY) {
                        map[x][y] = 1;
                        MacroMapFreePort newFreePort = new MacroMapFreePort(direction, x, y);
                        newFreePorts.add(newFreePort);
                    }
                }

            }

            freePorts.clear();
            freePorts.addAll(newFreePorts);
            newFreePorts.clear();
        }
    }

}
