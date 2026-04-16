package hu.csega.superstition.game.map.generation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class MapGenerator {

    public static void main(String[] args) throws Exception {
        MacroMap mm = new MacroMap(200, 200, 100, 100);
        mm.start();

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("target/map.csv"))) {
            MacroMapField[][] map = mm.getMap();
            for (MacroMapField[] row : map) {
                for (MacroMapField field : row) {
                    if (field == null) {
                        writer.write(" ");
                    } else {
                        writer.write("\"#\"");
                    }
                    writer.write(",");
                }

                writer.write("\n");
                writer.flush();
            }

            writer.flush();
        }

        File file = new File("map.csv");
        System.out.println("Written: " + file.getAbsolutePath());
    }

    public static MacroMap generateMap() {
        MacroMap macroMap = new MacroMap();
        macroMap.start();
        return macroMap;
    }
}
