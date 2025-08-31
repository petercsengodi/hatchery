package hu.csega.editors.anm.layer1.opengl;

import hu.csega.editors.anm.common.CommonEditorModel;
import hu.csega.editors.anm.layer1.swing.views.AnimatorView;
import hu.csega.editors.anm.layer1.view3d.AnimatorSet;
import hu.csega.editors.anm.layer1.view3d.AnimatorSetPart;
import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.games.engine.GameEngineCallback;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameTransformation;
import hu.csega.games.engine.intf.GameGraphics;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnimatorRenderStep implements GameEngineCallback {

	private LinkedHashMap<Class<?>, AnimatorRenderer> registeredRenderers = new LinkedHashMap<>();
	private Class<?> cachedAnimatorModelClass = null;
	private AnimatorRenderer cachedAnimatorRenderer = null;

	private AnimatorMouseController mouseController;

	public void setMouseController(AnimatorMouseController mouseController) {
		this.mouseController = mouseController;
	}

	public void registerRenderer(Class<?> modelClass, AnimatorRenderer renderer) {
		registeredRenderers.put(modelClass, renderer);
	}

	public AnimatorRenderer selectRenderer(CommonEditorModel currentModel) {
		if(currentModel == null)
			return null;

		Class<?> modelClass = currentModel.getClass();
		if(cachedAnimatorModelClass == modelClass) {
			return cachedAnimatorRenderer;
		}

		cachedAnimatorModelClass = modelClass;
		for(Map.Entry<Class<?>, AnimatorRenderer> entry : registeredRenderers.entrySet()) {
			if(entry.getKey().isAssignableFrom(modelClass)) {
				cachedAnimatorRenderer = entry.getValue();
				return cachedAnimatorRenderer;
			}
		}

		return null;
	}

	@Override
	public Object call(GameEngineFacade facade) {
		GameGraphics g = facade.graphics();
		AnimatorModel animatorModel = (AnimatorModel) facade.model();
		if(mouseController != null)
			animatorModel.refreshCamera(mouseController);

		CommonEditorModel model = animatorModel.selectModel();
		g.placeCamera(model.cameraPlacement());

		AnimatorRenderer renderer = selectRenderer(model);
		renderer.paint(facade, g, model);

		return facade;
	}

}
