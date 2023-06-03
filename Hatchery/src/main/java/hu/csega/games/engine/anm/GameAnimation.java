package hu.csega.games.engine.anm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameAnimation {

    private String[] meshes;
    private GameAnimationScene[] scenes;

    public GameAnimation() {
    }

    public GameAnimation(int numberOfMeshes, int numberOfScenes) {
        meshes = new String[numberOfMeshes];
        scenes = new GameAnimationScene[numberOfScenes];

        for(int i = 0; i < numberOfScenes; i++) {
            scenes[i] = new GameAnimationScene(numberOfMeshes);
        }
    }

    public String[] getMeshes() {
        return meshes;
    }

    public void setMeshes(String[] meshes) {
        this.meshes = meshes;
    }

    public GameAnimationScene[] getScenes() {
        return scenes;
    }

    public void setScenes(GameAnimationScene[] scenes) {
        this.scenes = scenes;
    }

    public JSONObject toJSONObject() {
        try {
            JSONObject json = new JSONObject();
            JSONArray meshArray = new JSONArray();
            JSONArray sceneArray = new JSONArray();

            int len = meshes.length;
            json.put("numberOfMeshes", len);
            for (int i = 0; i < len; i++) {
                meshArray.put(meshes[i]);
            }

            int l = scenes.length;
            json.put("numberOfScenes", l);
            for (int j = 0; j < l; j++) {
                sceneArray.put(scenes[j].toJSONArray());
            }

            json.put("meshes", meshArray);
            json.put("scenes", sceneArray);
            return json;
        } catch(JSONException ex) {
            throw new RuntimeException("Could not construct animation JSON!", ex);
        }
    }

    public void fromJSONObject(JSONObject json) {
        try {
            int numberOfMeshes = json.getInt("numberOfMeshes");
            meshes = new String[numberOfMeshes];
            JSONArray meshArray = json.getJSONArray("meshes");
            for(int i = 0; i < numberOfMeshes; i++) {
                meshes[i] = meshArray.getString(i);
            }

            int numberOfScenes = json.getInt("numberOfScenes");
            scenes = new GameAnimationScene[numberOfScenes];
            JSONArray sceneArray = json.getJSONArray("scenes");
            for(int i = 0; i < numberOfScenes; i++) {
                JSONArray array = sceneArray.getJSONArray(i);
                GameAnimationScene scene = new GameAnimationScene();
                scene.fromJSONArray(array, numberOfMeshes);
                scenes[i] = scene;
            }
        } catch(JSONException ex) {
            throw new RuntimeException("Could not construct animation JSON!", ex);
        }
    }
}
