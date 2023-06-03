package hu.csega.games.adapters.opengl.models;

import hu.csega.games.adapters.opengl.OpenGLProfileAdapter;

public class OpenGLCustomMeshContainer extends OpenGLModelContainer {

	public OpenGLCustomMeshContainer(String filename, OpenGLModelStoreImpl store, OpenGLProfileAdapter adapter, OpenGLMeshBuilder builder) {
		super(filename, store, adapter);
		this.builder = builder;
	}

	@Override
	public OpenGLMeshBuilder builder() {
		return builder;
	}

	private OpenGLMeshBuilder builder;
}
