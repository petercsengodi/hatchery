package hu.csega.superstition.game;

import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameModelBuilder;
import hu.csega.games.engine.g3d.GameModelStore;
import hu.csega.games.engine.g3d.GameObjectDirection;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameObjectPosition;
import hu.csega.games.engine.g3d.GameObjectVertex;
import hu.csega.games.engine.g3d.GameTexturePosition;
import hu.csega.superstition.game.map.MapTile;
import hu.csega.superstition.game.map.SuperstitionMap;

import java.util.HashMap;
import java.util.Map;

public class SuperstitionGameElements {

	public static final float GROUND_DEPTH = -12f;
	public static final float GROUND_SIZE = 200f;

	public static String WIZARD_ANIMATION = "wizard2.json";
	public static String RUNNING_ANIMATION = "run_2.json";
	public static String MONKEY_ANIMATION = "monkey_2.json";
	public static String SNAKE_ANIMATION = "snake.json";
	public static String MONSTRUM_ANIMATION = "monstrum.json";
	public static String BEHEMOTH_ANIMATION = "behemoth.json";

	Map<String, GameObjectHandler> monsterAnimations = new HashMap<>();

	GameObjectHandler groundTexture;
	GameObjectHandler boxModel;
	GameObjectHandler[] spellModel;
	GameObjectHandler[] alphabet;
	int numberOfLetters = 26;
	GameObjectHandler[] numbers;
	int numberOfNumbers = 10;
	GameObjectHandler dot;
	GameObjectHandler colon;
	GameObjectHandler exclamation;
	GameObjectHandler comma;
	GameObjectHandler question;

	public void loadElements(GameEngineFacade facade) {
		GameModelStore store = facade.store();

		buildGround(store, "grass-texture.png");

		boxModel = buildBox(store, -100f, -100f, -100f, 100f, 100f, 100f, "wood-texture.jpg");

		monsterAnimations.put(WIZARD_ANIMATION, loadAnimation(store, WIZARD_ANIMATION));
		monsterAnimations.put(RUNNING_ANIMATION, loadAnimation(store, RUNNING_ANIMATION));
		monsterAnimations.put(MONKEY_ANIMATION, loadAnimation(store, MONKEY_ANIMATION));
		monsterAnimations.put(SNAKE_ANIMATION, loadAnimation(store, SNAKE_ANIMATION));
		monsterAnimations.put(MONSTRUM_ANIMATION, loadAnimation(store, MONSTRUM_ANIMATION));
		monsterAnimations.put(BEHEMOTH_ANIMATION, loadAnimation(store, BEHEMOTH_ANIMATION));

		alphabet = new GameObjectHandler[numberOfLetters];
		for(int i = 0; i < numberOfLetters; i++) {
			alphabet[i] = store.loadMesh("alphabet-" + (char)((int)'a' + i) + ".json");
		}

		numbers = new GameObjectHandler[numberOfNumbers];
		for(int i = 0; i < numberOfNumbers; i++) {
			numbers[i] = store.loadMesh("number-" + i + ".json");
		}

		dot = store.loadMesh("alphabet-dot.json");
		colon = store.loadMesh("alphabet-column.json");
		exclamation = store.loadMesh("alphabet-exclamation.json");
		comma = store.loadMesh("alphabet-comma.json");
		question = store.loadMesh("alphabet-question.json");

		spellModel = new GameObjectHandler[2];
		spellModel[0] = store.loadMesh("fireball.json");
		spellModel[1] = store.loadMesh("ice.json");
	}

	private GameObjectHandler loadAnimation(GameModelStore store, String filename) {
		return store.loadAnimation(filename);
	}

	private void buildGround(GameModelStore store, String texture) {
		groundTexture = store.loadTexture(texture);

		GameObjectPosition p;
		GameObjectDirection d;
		GameTexturePosition tex;

		d = new GameObjectDirection(0f, 1f, 0f);

		float x = -GROUND_SIZE;
		float y = -GROUND_SIZE;

		GameModelBuilder groundBuilder = new GameModelBuilder();
		groundBuilder.setTextureHandler(groundTexture);

		p = new GameObjectPosition(x, GROUND_DEPTH, y);
		tex = new GameTexturePosition(0f, 0f);
		groundBuilder.getVertices().add(new GameObjectVertex(p, d, tex));

		p = new GameObjectPosition(x + GROUND_SIZE, GROUND_DEPTH, y);
		tex = new GameTexturePosition(1f, 0f);
		groundBuilder.getVertices().add(new GameObjectVertex(p, d, tex));

		p = new GameObjectPosition(x + GROUND_SIZE, GROUND_DEPTH, y + GROUND_SIZE);
		tex = new GameTexturePosition(1f, 1f);
		groundBuilder.getVertices().add(new GameObjectVertex(p, d, tex));

		p = new GameObjectPosition(x, GROUND_DEPTH, y + GROUND_SIZE);
		tex = new GameTexturePosition(0f, 1f);
		groundBuilder.getVertices().add(new GameObjectVertex(p, d, tex));

		groundBuilder.getIndices().add(0);
		groundBuilder.getIndices().add(2);
		groundBuilder.getIndices().add(1);
		groundBuilder.getIndices().add(0);
		groundBuilder.getIndices().add(3);
		groundBuilder.getIndices().add(2);
		GameObjectHandler groundTileHandler = store.buildMesh(groundBuilder);

		for(int ix = 0; ix < SuperstitionMap.SIZE_X; ix++) {
			for(int iy = 0; iy < SuperstitionMap.SIZE_Y; iy++) {
				MapTile mt = new MapTile(ix * GROUND_SIZE, 0f, iy * GROUND_SIZE);
				mt.handler = groundTileHandler;
				SuperstitionMap.mapTiles[ix][iy] = mt;
			}
		}
	}

	private GameObjectHandler buildBox(GameModelStore store, float x1, float y1, float z1, float x2, float y2, float z2, String texture) {
		GameObjectHandler textureHandler = store.loadTexture(texture);
		GameModelBuilder builder = new GameModelBuilder();
		builder.setTextureHandler(textureHandler);

		int offset = 0;

		offset = buildRectangle(builder, offset, x1, y1, z1, x2, y1, z1, x2, y2, z1, x1, y2, z1, 0f, 0f, -1f, true);
		offset = buildRectangle(builder, offset, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2, 0f, 0f, 1f, false);
		offset = buildRectangle(builder, offset, x1, y1, z1, x1, y2, z1, x1, y2, z2, x1, y1, z2, -1f, 0f, 0f, true);
		offset = buildRectangle(builder, offset, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2, 1f, 0f, 0f, false);
		offset = buildRectangle(builder, offset, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2, 0f, -1f, 0f, false);
		offset = buildRectangle(builder, offset, x1, y2, z1, x2, y2, z1, x2, y2, z2, x1, y2, z2, 0f, 1f, 0f, true);

		return store.buildMesh(builder);
	}

	private int buildRectangle(GameModelBuilder builder, int offset, float x1, float y1, float z1, float x2, float y2, float z2,
			float x3, float y3, float z3, float x4, float y4, float z4, float nx, float ny, float nz, boolean reverse) {

		GameObjectPosition p;
		GameTexturePosition tex;
		GameObjectDirection d = new GameObjectDirection(nx, ny, nz);

		p = new GameObjectPosition(x1, y1, z1);
		tex = new GameTexturePosition(0f, 0f);
		builder.getVertices().add(new GameObjectVertex(p, d, tex));

		p = new GameObjectPosition(x2, y2, z2);
		tex = new GameTexturePosition(1f, 0f);
		builder.getVertices().add(new GameObjectVertex(p, d, tex));

		p = new GameObjectPosition(x3, y3, z3);
		tex = new GameTexturePosition(1f, 1f);
		builder.getVertices().add(new GameObjectVertex(p, d, tex));

		p = new GameObjectPosition(x4, y4, z4);
		tex = new GameTexturePosition(0f, 1f);
		builder.getVertices().add(new GameObjectVertex(p, d, tex));

		if(reverse) {
			builder.getIndices().add(offset + 0);
			builder.getIndices().add(offset + 2);
			builder.getIndices().add(offset + 1);
			builder.getIndices().add(offset + 0);
			builder.getIndices().add(offset + 3);
			builder.getIndices().add(offset + 2);
		} else {
			builder.getIndices().add(offset + 0);
			builder.getIndices().add(offset + 1);
			builder.getIndices().add(offset + 2);
			builder.getIndices().add(offset + 0);
			builder.getIndices().add(offset + 2);
			builder.getIndices().add(offset + 3);
		}

		offset += 4;
		return offset;
	}
}
