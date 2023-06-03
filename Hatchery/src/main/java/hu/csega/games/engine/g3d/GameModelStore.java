package hu.csega.games.engine.g3d;

public interface GameModelStore {

	GameObjectHandler loadTexture(String filename);

	GameObjectHandler buildMesh(GameModelBuilder builder);

	GameObjectHandler loadMesh(String filename);

	GameObjectHandler loadAnimation(String filename);

	void dispose(GameObjectHandler handler);

	void disposeAll();

}
