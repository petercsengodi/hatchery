package hu.csega.games.adapters.opengl.models;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.jogamp.opengl.GLAutoDrawable;

import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.games.adapters.opengl.OpenGLProfileAdapter;
import hu.csega.games.engine.anm.GameAnimation;
import hu.csega.games.engine.ftm.GameMesh;
import hu.csega.games.engine.g3d.GameModelBuilder;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.games.engine.g3d.GameObjectType;
import hu.csega.games.engine.g3d.GameSelectionLine;
import hu.csega.games.units.UnitStore;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

public class OpenGLModelStoreImpl implements OpenGLModelStore {

	private OpenGLProfileAdapter adapter;
	private ResourceAdapter resourceAdapter;

	private String shaderRoot;
	private String textureRoot;
	private String meshRoot;
	private String animationRoot;

	private long identifierCounter = 1;
	private boolean disposeAllWhenPossible = false;

	private Map<String, GameObjectHandler> handlers = new HashMap<>();
	private Map<String, GameObjectHandler> toDispose = new HashMap<>();
	private Set<GameObjectHandler> toInitialize = new HashSet<>();
	private Map<GameObjectHandler, OpenGLObjectContainer> containers = new HashMap<>();
    private Map<GameObjectHandler, GameAnimation> animations = new HashMap<>(); // FIXME: should be in containers map

	private boolean programInitialized = false;

	private byte[] buffer = new byte[4096];

	public void setAdapter(OpenGLProfileAdapter adapter) {
		this.adapter = adapter;
	}

	public void setShaderRoot(String shaderRoot) {
		this.shaderRoot = shaderRoot;
	}

	public void setTextureRoot(String textureRoot) {
		this.textureRoot = textureRoot;
	}

	public void setMeshRoot(String meshRoot) {
		this.meshRoot = meshRoot;
	}

	public void setAnimationRoot(String animationRoot) {
		this.animationRoot = animationRoot;
	}

	@Override
	public void setupScreen(GLAutoDrawable glAutoDrawable, int width, int height) {
		ensureOpenGLProgramIsInitialized(glAutoDrawable);
		if(disposeEnqueued())
			disposeEnqueuedObjects(glAutoDrawable);

		adapter.viewPort(glAutoDrawable, width, height);
	}

	@Override
	public void reset(GLAutoDrawable glAutoDrawable) {
		disposeOpenGLProgram(glAutoDrawable);
		ensureOpenGLProgramIsInitialized(glAutoDrawable);

		if(disposeEnqueued())
			disposeEnqueuedObjects(glAutoDrawable);

		for(OpenGLObjectContainer container : containers.values()) {
			if(container.isInitialized())
				container.dispose(glAutoDrawable);
			container.initialize(glAutoDrawable);
		}

		toInitialize.clear();
	}

	@Override
	public boolean needsInitialization() {
		return !toInitialize.isEmpty() || disposeAllWhenPossible;
	}

	@Override
	public void initializeModels(GLAutoDrawable glAutoDrawable) {
		ensureOpenGLProgramIsInitialized(glAutoDrawable);
		if(disposeEnqueued())
			disposeEnqueuedObjects(glAutoDrawable);

		for(GameObjectHandler handler : toInitialize) {
			OpenGLObjectContainer container = containers.get(handler);
			container.initialize(glAutoDrawable);
		}

		toInitialize.clear();
	}

	@Override
	public void disposeUnderlyingObjects(GLAutoDrawable glAutoDrawable) {
		disposeOpenGLProgram(glAutoDrawable);
		if(disposeEnqueued())
			disposeEnqueuedObjects(glAutoDrawable);

		for(Entry<GameObjectHandler, OpenGLObjectContainer> entry : containers.entrySet()) {
			GameObjectHandler handler = entry.getKey();
			OpenGLObjectContainer container = entry.getValue();
			if(container.isInitialized())
				container.dispose(glAutoDrawable);
			toInitialize.add(handler);
		}
	}

	@Override
	public GameObjectHandler loadTexture(String filename) {
		String absolutePath = textureRoot + filename;
		GameObjectHandler handler = handlers.get(absolutePath);

		if(handler == null) {
			handler = nextHandler(GameObjectType.TEXTURE);
			handlers.put(absolutePath, handler);
		}

		OpenGLObjectContainer container = containers.get(handler);
		if(container == null) {
			container = new OpenGLTextureContainer(adapter, absolutePath);
			containers.put(handler, container);
			toInitialize.add(handler);
		}

		return handler;
	}

	@Override
	public GameObjectHandler buildMesh(GameModelBuilder builder) {
		String absolutePath = "__id:" + identifierCounter;
		GameObjectHandler handler = nextHandler(GameObjectType.MESH);
		handlers.put(absolutePath, handler);

		OpenGLMeshBuilder modelBuilder = new OpenGLMeshBuilder(builder, this);

		OpenGLObjectContainer container = new OpenGLCustomMeshContainer(absolutePath, this, adapter, modelBuilder);
		containers.put(handler, container);

		toInitialize.add(handler);
		return handler;
	}

	@Override
	public GameObjectHandler loadMesh(String filename) {
		if(resourceAdapter == null) { // FIXME: Get this out of here!
			resourceAdapter = UnitStore.instance(ResourceAdapter.class);
		}

		String absolutePath = meshRoot + resourceAdapter.cleanUpResourceFilename(filename);
		GameObjectHandler handler = handlers.get(absolutePath);

		if(handler == null) {
			handler = nextHandler(GameObjectType.MESH);
			handlers.put(absolutePath, handler);
		}

		OpenGLObjectContainer container = containers.get(handler);
		if(container == null) {

			File file = new File(absolutePath);
			byte[] bytes = load(file);
			String string = new String(bytes, UTF_8);

			OpenGLMeshBuilder modelBuilder;
			try {
                JSONObject json = new JSONObject(string);
				GameMesh mesh = new GameMesh();
				mesh.fromJSONObject(json);
                mesh.setTexture(resourceAdapter.cleanUpResourceFilename(mesh.getTexture()));
				modelBuilder = new OpenGLMeshBuilder(mesh, this);
			} catch(JSONException ex) {
				throw new RuntimeException("Couldn't parse file: " + absolutePath);
			}

			container = new OpenGLCustomMeshContainer(absolutePath, this, adapter, modelBuilder);
			containers.put(handler, container);
			toInitialize.add(handler);
		}

		return handler;
	}

	@Override
	public GameObjectHandler loadAnimation(String filename) {
		String absolutePath = animationRoot + filename;
		GameObjectHandler handler = handlers.get(absolutePath);

		if(handler == null) {
			handler = nextHandler(GameObjectType.ANIMATION);
			handlers.put(absolutePath, handler);
		}

		GameAnimation gameAnimation = animations.get(handler);
		if(gameAnimation == null) {

            File file = new File(absolutePath);
            byte[] bytes = load(file);
            String string = new String(bytes, UTF_8);

            try {
                JSONObject json = new JSONObject(string);
                gameAnimation = new GameAnimation();
                gameAnimation.fromJSONObject(json);
            } catch(JSONException ex) {
                throw new RuntimeException("Couldn't parse file: " + absolutePath);
            }

            String[] meshes = gameAnimation.getMeshes();
            for(String mesh : meshes) {
                loadMesh(mesh);
            }

			animations.put(handler, gameAnimation);
        }

		return handler;
	}

	@Override
	public void dispose(GameObjectHandler handler) {
		OpenGLObjectContainer container = containers.get(handler);
		String filename = container.filename();
		toDispose.put(filename, handler);
	}

	@Override
	public void disposeAll() {
		disposeAllWhenPossible = true;
	}

	public void placeCamera(GLAutoDrawable glAutodrawable, GameObjectPlacement cameraPlacement) {
		adapter.placeCamera(glAutodrawable, cameraPlacement);
	}

	public OpenGLTextureContainer resolveTexture(GameObjectHandler textureReference) {
		OpenGLTextureContainer texture = (OpenGLTextureContainer)containers.get(textureReference);
		return texture;
	}

	public OpenGLModelContainer resolveModel(GameObjectHandler modelReference) {
		OpenGLModelContainer model = (OpenGLModelContainer)containers.get(modelReference);
		return model;
	}

	public GameAnimation resolveAnimation(GameObjectHandler animationReference) {
		return animations.get(animationReference);
	}

	private boolean disposeEnqueued() {
		return disposeAllWhenPossible || !toDispose.isEmpty();
	}

	private void disposeEnqueuedObjects(GLAutoDrawable glAutoDrawable) {
		if(disposeAllWhenPossible) {

			for(OpenGLObjectContainer container : containers.values()) {
				container.dispose(glAutoDrawable);
			}

			toInitialize.clear();
			containers.clear();
			handlers.clear();
			disposeAllWhenPossible = false;

		} else {
			for(Entry<String, GameObjectHandler> entry : toDispose.entrySet()) {
				String filename = entry.getKey();
				GameObjectHandler handler = entry.getValue();
				OpenGLObjectContainer container = containers.get(handler);

				container.dispose(glAutoDrawable);

				toInitialize.remove(handler);
				containers.remove(handler);
				handlers.remove(filename);
			}

			toDispose.clear();
		}
	}

	private void ensureOpenGLProgramIsInitialized(GLAutoDrawable glAutoDrawable) {
		if(!programInitialized) {
			logger.info("Initializing program.");

			adapter.initializeProgram(glAutoDrawable, shaderRoot);

			programInitialized = true;
			logger.info("Initialized program.");
		}
	}

	private void disposeOpenGLProgram(GLAutoDrawable glAutoDrawable) {
		if(programInitialized) {
			logger.info("Releasing program.");

			adapter.disposeProgram(glAutoDrawable);

			programInitialized = false;
			logger.info("Released program.");
		}
	}

	public void startFrame(GLAutoDrawable glAutoDrawable) {
		if(!programInitialized)
			return;

		adapter.startFrame(glAutoDrawable);
	}

	public void endFrame(GLAutoDrawable glAutoDrawable) {
		if(!programInitialized)
			return;

		adapter.endFrame(glAutoDrawable);
	}

	public int modelToClipMatrix() {
		return adapter.getModelToClipMatrixUL();
	}

	private GameObjectHandler nextHandler(GameObjectType type) {
		return new GameObjectHandler(type, identifierCounter++);
	}

	private byte[] load(File file) {
		if(!file.exists() || file.isDirectory()) {
			throw new RuntimeException("Not exists or not a file: " + file.getAbsolutePath());
		}

		try (FileInputStream stream = new FileInputStream(file)) {

			ByteArrayOutputStream output = new ByteArrayOutputStream();

			int len = 0;
			while((len = stream.read(buffer)) > -1) {
				if(len > 0) {
					output.write(buffer, 0, len);
				}
			}

			return output.toByteArray();
		} catch(IOException ex) {
			throw new RuntimeException("Error when loading file: " + file.getAbsolutePath(), ex);
		}
	}

	public void setBaseMatricesAndViewPort(GameSelectionLine selectionLine) {
		adapter.setBaseMatricesAndViewPort(selectionLine);
	}

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	private static final Logger logger = LoggerFactory.createLogger(OpenGLModelStoreImpl.class);
}
