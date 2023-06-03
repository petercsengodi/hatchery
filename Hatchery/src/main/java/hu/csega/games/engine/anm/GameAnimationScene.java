package hu.csega.games.engine.anm;

import hu.csega.games.engine.g3d.GameTransformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameAnimationScene {

    private boolean[] visible;
    private boolean[] flipped;
    private GameTransformation[] transformations;

    public GameAnimationScene() {
    }

    public GameAnimationScene(int numberOfMeshes) {
        visible = new boolean[numberOfMeshes];
        flipped = new boolean[numberOfMeshes];
        transformations = new GameTransformation[numberOfMeshes];

        for(int i = 0; i < numberOfMeshes; i++) {
            visible[i] = true;
            flipped[i] = false;
            transformations[i] = new GameTransformation();
        }
    }

    public void setVisible(boolean[] visible) {
        this.visible = visible;
    }

    public boolean[] getVisible() {
        return visible;
    }

    public void setFlipped(boolean[] flipped) {
        this.flipped = flipped;
    }

    public boolean[] getFlipped() {
        return flipped;
    }

    public void setTransformations(GameTransformation[] transformations) {
        this.transformations = transformations;
    }

    public GameTransformation[] getTransformations() {
        return transformations;
    }

    public JSONArray toJSONArray() {
        try {
            JSONArray array = new JSONArray();

            int len = visible.length;
            for(int i = 0; i < len; i++) {
                JSONObject json = new JSONObject();
                json.put("visible", visible[i]);
                json.put("flipped", flipped[i]);
                json.put("m", convertTransformationToArray(transformations[i]));
                array.put(json);
            }

            return array;
        } catch(JSONException ex) {
            throw new RuntimeException("Could not construct animation JSON!", ex);
        }
    }

    private JSONArray convertTransformationToArray(GameTransformation tr) throws JSONException {
        JSONArray result = new JSONArray();
        float[] floats = tr.getFloats();

        for(float f : floats) {
            result.put(f);
        }

        return result;
    }

    public void fromJSONArray(JSONArray array, int numberOfMeshes) {
        try {
            visible = new boolean[numberOfMeshes];
            flipped = new boolean[numberOfMeshes];
            transformations = new GameTransformation[numberOfMeshes];
            for(int i = 0; i < numberOfMeshes; i++) {
                JSONObject json = array.getJSONObject(i);
                visible[i] = json.getBoolean("visible");
                flipped[i] = json.getBoolean("flipped");
                transformations[i] = convertArrayToTransformation(json.getJSONArray("m"));
            }
        } catch(JSONException ex) {
            throw new RuntimeException("Could not construct animation JSON!", ex);
        }
    }

    private GameTransformation convertArrayToTransformation(JSONArray array) throws JSONException {
        GameTransformation t = new GameTransformation();
        float[] m = t.getFloats();

        for(int i = 0; i < 16; i++) {
            m[i] = (float) array.getDouble(i);
        }

        return t;
    }
}
