package hu.csega.editors.ftm.layer1.presentation.opengl;

import java.util.List;

import hu.csega.editors.FreeTriangleMeshToolStarter;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshTriangle;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;
import hu.csega.games.engine.GameEngineCallback;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameModelBuilder;
import hu.csega.games.engine.g3d.GameModelStore;
import hu.csega.games.engine.g3d.GameObjectDirection;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.games.engine.g3d.GameObjectPosition;
import hu.csega.games.engine.g3d.GameObjectVertex;
import hu.csega.games.engine.g3d.GameTexturePosition;
import hu.csega.games.engine.intf.GameGraphics;

public class FreeTriangleMeshRenderStep implements GameEngineCallback {

	private GameObjectHandler convertedModel = null;
	private GameObjectPlacement modelPlacement = new GameObjectPlacement();

	private GameObjectPosition cameraPosition = new GameObjectPosition(0f, 0f, 0f);
	private GameObjectPosition cameraTarget = new GameObjectPosition(0f, 0f, 0f);
	private GameObjectDirection cameraUp = new GameObjectDirection(0f, 1f, 0f);
	private GameObjectPlacement cameraPlacement = new GameObjectPlacement();

	@Override
	public Object call(GameEngineFacade facade) {
		FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
		if(model == null)
			return facade;

		if(model.isInvalid()) {
			List<FreeTriangleMeshVertex> vertices = model.getVertices();
			List<FreeTriangleMeshTriangle> triangles = model.getTriangles();

			GameModelStore store = facade.store();

			if(convertedModel != null) {
				store.dispose(convertedModel);
				convertedModel = null;
			}

			if(!triangles.isEmpty()) {
				GameModelBuilder builder = new GameModelBuilder();

				String textureFilename = model.getTextureFilename();
				if(textureFilename == null || textureFilename.isEmpty())
					textureFilename = FreeTriangleMeshToolStarter.DEFAULT_TEXTURE_FILE;

				GameObjectHandler textureHandler = store.loadTexture(textureFilename);
				builder.setTextureHandler(textureHandler);

				for(FreeTriangleMeshVertex v : vertices) {
					GameObjectPosition p = new GameObjectPosition((float)v.getPX(), (float)v.getPY(), (float)v.getPZ());
					GameObjectDirection d = new GameObjectDirection((float)v.getNX(), (float)v.getNY(), (float)v.getNZ());
					GameTexturePosition tex = new GameTexturePosition((float)v.getTX(), (float)v.getTY());
					builder.getVertices().add(new GameObjectVertex(p, d, tex));
				}

				for(FreeTriangleMeshTriangle t : triangles) {
					if(model.enabled(t)) {
						builder.getIndices().add(t.getVertex1());
						builder.getIndices().add(t.getVertex2());
						builder.getIndices().add(t.getVertex3());
					}
				}

				convertedModel = store.buildMesh(builder);
			}

			model.setInvalid(false);
		}

		double alfa = model.getOpenGLAlpha();
		double beta = model.getOpenGLBeta();
		double distance = model.getOpenGLZoom();

		// Rendering

		if(convertedModel != null) {
			GameGraphics g = facade.graphics();

			double y = distance * Math.sin(beta);
			double distanceReduced = distance * Math.cos(beta);

			cameraPosition.x = (float)(Math.cos(alfa) * distanceReduced);
			cameraPosition.y = (float) y;
			cameraPosition.z = (float)(Math.sin(alfa) * distanceReduced);

			cameraPlacement.setPositionTargetUp(cameraPosition, cameraTarget, cameraUp);

			g.placeCamera(cameraPlacement);
			g.drawModel(convertedModel, modelPlacement);
		}

		return facade;
	}

}
