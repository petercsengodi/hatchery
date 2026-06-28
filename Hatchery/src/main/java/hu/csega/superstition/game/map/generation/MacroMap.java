package hu.csega.superstition.game.map.generation;

import static hu.csega.superstition.game.map.generation.MacroMapDirection.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MacroMap {

    public MacroMap() {
        startX = startY = 100;
        maxX = maxY = 200;
        map = new MacroMapField[maxX + 1][maxY + 1];
    }

    public MacroMap(int maxX, int maxY, int startX, int startY) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.startX = startX;
        this.startY = startY;
        this.map = new MacroMapField[maxX + 1][maxY + 1];
    }

    public int startX;
    public int startY;
    public int maxX;
    public int maxY;

    private final MacroMapField[][] map;
    private final List<MacroMapFreePort> freePorts = new ArrayList<>();

    public MacroMapField[][] getMap() {
        return map;
    }

    public int sizeX() {
        return maxX + 1;
    }

    public int sizeY() {
        return maxY + 1;
    }

    public void start() {
        MacroMapFreePort freePortSummer = new MacroMapFreePort(SUMMER, startX + SUMMER.x, startY + SUMMER.y);
        MacroMapFreePort freePortSpring = new MacroMapFreePort(SPRING, startX + SPRING.x, startY + SPRING.y);
        MacroMapFreePort freePortFall = new MacroMapFreePort(FALL, startX + FALL.x, startY + FALL.y);
        MacroMapFreePort freePortWinter = new MacroMapFreePort(WINTER, startX + WINTER.x, startY + WINTER.y);

        freePorts.add(freePortSpring);
        freePorts.add(freePortSummer);
        freePorts.add(freePortFall);
        freePorts.add(freePortWinter);

        MacroMapField startField = new MacroMapField();
        startField.connections[SUMMER.xUnsigned][SUMMER.yUnsigned] = 1;
        startField.connections[SPRING.xUnsigned][SPRING.yUnsigned] = 1;
        startField.connections[FALL.xUnsigned][FALL.yUnsigned] = 1;
        startField.connections[WINTER.xUnsigned][WINTER.yUnsigned] = 1;
        startField.start = true;
        map[startX][startY] = startField;

        MacroMapField summerField = new MacroMapField();
        summerField.connections[2 - SUMMER.xUnsigned][2 - SUMMER.yUnsigned] = 1;
        map[freePortSummer.x][freePortSummer.y] = summerField;

        MacroMapField springField = new MacroMapField();
        springField.connections[2 - SPRING.xUnsigned][2 - SPRING.yUnsigned] = 1;
        map[freePortSpring.x][freePortSpring.y] = springField;

        MacroMapField fallField = new MacroMapField();
        fallField.connections[2 - FALL.xUnsigned][2 - FALL.yUnsigned] = 1;
        map[freePortFall.x][freePortFall.y] = fallField;

        MacroMapField winterField = new MacroMapField();
        winterField.connections[2 - WINTER.xUnsigned][2 - WINTER.yUnsigned] = 1;
        map[freePortWinter.x][freePortWinter.y] = winterField;

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

                MacroMapField currentField = map[freePort.x][freePort.y];

                // I don't know, something. No sense here.
                if(directions == 2 || directions == 4 || directions == 5 || directions == 7) {
                    directions = 1;
                }

                if(directions == 1 || directions == 4 || directions == 6 || directions == 7) {
                    int x = freePort.x + direction.x;
                    int y = freePort.y;
                    if(x >= 0 && y >= 0 && x <= maxX && y <= maxY) {
                        MacroMapField nextField = map[x][y];
                        if(nextField == null) {
                            nextField = map[x][y] = new MacroMapField();
                        }

                        currentField.connections[direction.xUnsigned][1] = 1;
                        nextField.connections[2 - direction.xUnsigned][1] = 1;

                        MacroMapFreePort newFreePort = new MacroMapFreePort(direction, x, y);
                        newFreePorts.add(newFreePort);
                    }
                }

                if(directions == 2 || directions == 4 || directions == 5 || directions == 7) {
                    int x = freePort.x + direction.x;
                    int y = freePort.y + direction.y;
                    if(x >= 0 && y >= 0 && x <= maxX && y <= maxY) {
                        MacroMapField nextField = map[x][y];
                        if(nextField == null) {
                            nextField = map[x][y] = new MacroMapField();
                        }

                        currentField.connections[direction.xUnsigned][direction.yUnsigned] = 1;
                        nextField.connections[2 - direction.xUnsigned][2 - direction.yUnsigned] = 1;

                        MacroMapFreePort newFreePort = new MacroMapFreePort(direction, x, y);
                        newFreePorts.add(newFreePort);
                    }
                }

                if(directions == 3 || directions == 5 || directions == 6 || directions == 7) {
                    int x = freePort.x;
                    int y = freePort.y + direction.y;
                    if(x >= 0 && y >= 0 && x <= maxX && y <= maxY) {
                        MacroMapField nextField = map[x][y];
                        if(nextField == null) {
                            nextField = map[x][y] = new MacroMapField();
                        }

                        currentField.connections[1][direction.yUnsigned] = 1;
                        nextField.connections[1][2 - direction.yUnsigned] = 1;

                        MacroMapFreePort newFreePort = new MacroMapFreePort(direction, x, y);
                        newFreePorts.add(newFreePort);
                    }
                }

            }

            freePorts.clear();
            freePorts.addAll(newFreePorts);
            newFreePorts.clear();
        }


        ////////////////////////////////////////////////////////////////////////////////////////////////////
        // Forest
        MacroMapFreePort treePort = new MacroMapFreePort(WINTER, startX, startY); // TODO

        for(int x = -2; x <= 2; x++) {
            for(int y = -2; y <= 2; y++) {
                if(x != 0 || y != 0) {
                    MacroMapField macroMapField = new MacroMapField();
                    macroMapField.tree = true;
                    // TODO Must connections be taken care of?
                    map[startX + x][startY + y] = macroMapField;
                }
            }
        }
    }

}
