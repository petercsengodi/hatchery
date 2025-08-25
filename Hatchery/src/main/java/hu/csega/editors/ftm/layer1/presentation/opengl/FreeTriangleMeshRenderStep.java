package hu.csega.editors.ftm.layer1.presentation.opengl;

import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.engine.GameEngineCallback;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameObjectDirection;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.games.engine.g3d.GameObjectPosition;
import hu.csega.games.engine.intf.GameGraphics;

public class FreeTriangleMeshRenderStep implements GameEngineCallback {

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

		GameObjectHandler convertedModel = model.ensureConvertedModelIsBuilt(facade);

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
