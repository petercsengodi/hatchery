package hu.csega.demo.dreamland;

import hu.csega.editors.AnimatorStarter;
import hu.csega.editors.anm.layer1Views.view3d.AnimatorSetPart;
import hu.csega.editors.anm.layer4Data.model.AnimatorRefreshViews;
import hu.csega.editors.common.SerializationUtil;
import hu.csega.editors.common.resources.FileResourceAdapter;
import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshSnapshots;
import hu.csega.games.library.animation.v1.anm.Animation;
import hu.csega.games.library.animation.v1.anm.AnimationPart;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshTriangle;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;
import hu.csega.toolshed.logging.Level;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joml.Matrix4f;

public class GenerateDreamlandDemo {

    private static final Level LOGGING_LEVEL = Level.INFO;
    private static Logger logger;

    public static final String DEMO_PROJECT = "dreamland";

    private static ResourceAdapter resourceAdapter;

    private static final Map<String, File> meshNameToFile = new LinkedHashMap<>();

    public static void main(String[] args) throws Exception {
        ////////////////////////////////////////////////////////////////////////////////////////////////
        // 1. Initialize logging:

        LoggerFactory.setDefaultLevel(LOGGING_LEVEL);
        logger = LoggerFactory.createLogger(AnimatorStarter.class);
        logger.info("Starting demo install...");


        ////////////////////////////////////////////////////////////////////////////////////////////////
        // 4. Checking current directory

        resourceAdapter = new FileResourceAdapter("Hatchery");
        File mainJS = new File(resourceAdapter.projectRoot() + "demo" + File.separator + DEMO_PROJECT + File.separator + "main.js");
        logger.info("main.js currently exists: " + mainJS.exists() + " Absolute path: " + mainJS.getAbsolutePath());

        if(mainJS.exists()) {
            logger.info("Deleting main.js ...");
            if(mainJS.delete()) {
                logger.info("Deleted main.js.");
            } else {
                logger.info("Could not delete main.js.");
            }
        }

        File head = new File(resourceAdapter.projectRoot() + "src" + File.separator + "main" + File.separator + "java" +
                File.separator + "hu" + File.separator + "csega" + File.separator + "demo" + File.separator + DEMO_PROJECT +
                File.separator + DEMO_PROJECT + ".head.js");
        File tail = new File(resourceAdapter.projectRoot() + "src" + File.separator + "main" + File.separator + "java" +
                File.separator + "hu" + File.separator + "csega" + File.separator + "demo" + File.separator + DEMO_PROJECT +
                File.separator + DEMO_PROJECT + ".tail.js");

        File dragonAnm = new File(resourceAdapter.animationFolder() + File.separator + /* "dragon.anm" */ "run_2.anm");

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(mainJS))) {

            write(writer, head);

            generateFromFtm2(writer, "Hat", new File(resourceAdapter.meshRoot() + "hat.ftm"));

            generateFromAnm(writer, "Dragon", dragonAnm);

            for(Map.Entry<String, File> entry : meshNameToFile.entrySet()) {
                generateFromFtm(writer, entry.getKey(), entry.getValue());
            }

            for(Map.Entry<String, File> entry : meshNameToFile.entrySet()) {
                addToParts(writer, entry.getKey());
            }

            write(writer, tail);
        }
    }

    private static String pathToFilename(String filename) {
        int lastIndex = filename.lastIndexOf(File.separator);
        if(lastIndex >= 0 && lastIndex < filename.length() - 1) {
            filename = filename.substring(lastIndex + 1);
        }

        lastIndex = filename.lastIndexOf('.');
        if(lastIndex >= 1 && lastIndex < filename.length()) {
            filename = filename.substring(0, lastIndex);
        }

        return filename;
    }

    private static void write(OutputStreamWriter writer, String lines) throws IOException {
        writer.write(lines);
        writer.write("\n");
        writer.flush();
    }

    private static void write(OutputStreamWriter writer, InputStream stream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while((line = reader.readLine()) != null) {
                writer.write(line);
                writer.write("\n");
            }
        }

        writer.flush();
    }

    private static void write(OutputStreamWriter writer, File file) throws IOException {
        write(writer, new FileInputStream(file));
    }

    private static void generateFromFtm(OutputStreamWriter writer, String name, File ftm) throws IOException {
        byte[] bytes = FreeTriangleMeshSnapshots.readAllBytes(ftm);
        FreeTriangleMeshModel deserialized = SerializationUtil.deserialize(bytes, FreeTriangleMeshModel.class);
        logger.info(ftm.getAbsolutePath() + ": " + deserialized.getClass().getName());

        writer.write("const geometry" + name + " = new THREE.BufferGeometry();\n\n");
        writer.write("const vertices" + name + " = new Float32Array([\n");

        List<FreeTriangleMeshVertex> vertices = deserialized.getVertices();
        for(FreeTriangleMeshVertex vertex : vertices) {
            writer.write("\t" + vertex.getPX() + ", " + vertex.getPY() + ",  " + vertex.getPZ()+ ",\n");
        }

        writer.write("]);\n\n");
        writer.write("const indices" + name + " = [\n");

        List<FreeTriangleMeshTriangle> triangles = deserialized.getTriangles();
        for(FreeTriangleMeshTriangle triangle : triangles) {
            writer.write("\t" + triangle.getVertex1() + ", " + triangle.getVertex2() + ", " + triangle.getVertex3() + ",\n");
        }

        writer.write("];\n\n");
        writer.write("geometry" + name + ".setIndex(indices" + name + ");\n");
        writer.write("geometry" + name + ".setAttribute('position', new THREE.BufferAttribute(vertices" + name + ", 3));\n");
        writer.write("const material" + name + " = new THREE.MeshBasicMaterial({color: 0xff0000});\n");
        writer.write("const mesh" + name + " = new THREE.Mesh(geometry" + name + ", material" + name + ");\n");
        writer.write("mesh" + name + ".matrixAutoUpdate = false;\n\n");
        writer.flush();
    }

    private static void generateFromFtm2(OutputStreamWriter writer, String name, File ftm) throws IOException {
        byte[] bytes = FreeTriangleMeshSnapshots.readAllBytes(ftm);
        FreeTriangleMeshModel deserialized = SerializationUtil.deserialize(bytes, FreeTriangleMeshModel.class);
        logger.info(ftm.getAbsolutePath() + ": " + deserialized.getClass().getName());

        writer.write("const geometry" + name + " = new THREE.BufferGeometry();\n\n");
        writer.write("const vertices" + name + " = new Float32Array([\n");

        List<FreeTriangleMeshVertex> vertices = deserialized.getVertices();
        for(FreeTriangleMeshVertex vertex : vertices) {
            writer.write("\t" + vertex.getPX() + ", " + vertex.getPY() + ",  " + vertex.getPZ()+ ",\n");
        }

        writer.write("]);\n\n");
        writer.write("const indices" + name + " = [\n");

        List<FreeTriangleMeshTriangle> triangles = deserialized.getTriangles();
        for(FreeTriangleMeshTriangle triangle : triangles) {
            writer.write("\t" + triangle.getVertex1() + ", " + triangle.getVertex2() + ", " + triangle.getVertex3() + ",\n");
        }

        writer.write("];\n\n");
        writer.write("const colors" + name + " = [\n");

        for(FreeTriangleMeshTriangle triangle : triangles) {
            FreeTriangleMeshVertex v1 = vertices.get(triangle.getVertex1());
            FreeTriangleMeshVertex v2 = vertices.get(triangle.getVertex2());
            FreeTriangleMeshVertex v3 = vertices.get(triangle.getVertex3());

            double x = (v1.getTX() + v2.getTX() + v3.getTX()) / 3.0;
            double y = (v1.getTY() + v2.getTY() + v3.getTY()) / 3.0;
            int colorIndex = (int)(Math.floor(9.0 * x) + Math.floor(9.0 * y) * 3);
            writer.write("\t" + colorIndex + ",\n");
        }

        writer.write("];\n\n");
        writer.flush();
    }

    private static void generateFromAnm(OutputStreamWriter writer, String name, File anm) throws IOException {
        byte[] bytes = FreeTriangleMeshSnapshots.readAllBytes(anm);
        AnimationPersistent deserialized = SerializationUtil.deserialize(bytes, AnimationPersistent.class);
        logger.info(anm.getAbsolutePath() + ": " + deserialized.getClass().getName());

        Animation animation = deserialized.getAnimation();
        Map<String, AnimationPart> parts = animation.getParts();

        final Map<String, Integer> idToIndex = new HashMap<>();
        for(AnimationPart part : parts.values()) {
            int index = meshNameToFile.size();

            String filename = pathToFilename(part.getMesh());
            File meshFile = new File(resourceAdapter.meshRoot() + filename + ".ftm");

            String meshName = "Part" + index;
            meshNameToFile.put(meshName, meshFile);

            idToIndex.put(part.getIdentifier(), index);
        }

        int numberOfScenes = animation.getNumberOfScenes();
        writer.write("const numberOfScenes" + name + " = " + numberOfScenes + ";\n");
        writer.write("const animation" + name + " = [\n");

        for(int sceneIndex = 0; sceneIndex < numberOfScenes; sceneIndex++) {
            writer.write("\t[\n");

            Matrix4f baseTransformation = new Matrix4f();
            List<AnimatorSetPart> resultParts = new ArrayList<>();
            AnimatorRefreshViews.generateParts(deserialized, sceneIndex, baseTransformation, resultParts);

            Map<Integer, AnimatorSetPart> map = new TreeMap<>();
            for(AnimatorSetPart part : resultParts) {
                String id = part.getIdentifier();
                Integer index = idToIndex.get(id);
                if(index != null) {
                    map.put(index, part);
                }
            }

            for(int partIndex = 0; partIndex < idToIndex.size(); partIndex++) {
                AnimatorSetPart part = map.get(partIndex);
                if(part == null)
                    continue;

                float[] floats = part.getTransformation().getFloats();
                writer.write("\t\tnew THREE.Matrix4(");

                for(int i = 0; i < 16; i++) {
                    if(i > 0) {
                        writer.write(", ");
                    }

                    int row = i % 4;
                    int col = i / 4;
                    int j = row * 4 + col;

                    writer.write(String.valueOf(floats[j]));
                }


                writer.write("),\n");
                writer.flush();
            }

            writer.write("\t],\n");
        }

        writer.write("]; // end of animation" + name + "\n");
        writer.flush();
    }

    private static void addToParts(OutputStreamWriter writer, String... names) throws IOException {
        for(String name : names) {
            writer.write("parts.push(mesh" + name + ");\n");
            writer.write("verticesArray.push(vertices" + name + ");\n");
            writer.write("indicesArray.push(indices" + name + ");\n");
        }

        writer.write("\n");
        writer.flush();
    }

    private static void addToScene(OutputStreamWriter writer, String... names) throws IOException {
        for(String name : names) {
            writer.write("scene.add(mesh" + name + ");\n");
            writer.write("parts.push(mesh" + name + ");\n");
        }

        writer.write("\n");
        writer.flush();
    }

}
