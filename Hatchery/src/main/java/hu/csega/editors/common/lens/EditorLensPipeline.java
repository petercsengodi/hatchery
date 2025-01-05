package hu.csega.editors.common.lens;

public class EditorLensPipeline {

	private EditorLensTranslateImpl translate = null;
	private EditorLensScaleImpl scale;
	private EditorLens screenTransformation;
	private EditorLens screenDirection = null;
	private EditorTransformation customTransformation;

	public void setCustomTransformation(EditorTransformation customTransformation) {
		this.customTransformation = customTransformation;
	}

	public void setScreenTransformation(EditorLens screenTransformation) {
		this.screenTransformation = screenTransformation;
	}

	public void screenXY() {
		screenTransformation = new EditorLensAWTScreenTransformationImpl();
		screenDirection = new EditorLensXYToXY();
	}

	public void screenXZ() {
		screenTransformation = new EditorLensAWTScreenTransformationImpl();
		screenDirection = new EditorLensXYToXZ();
	}

	public void screenZY() {
		screenTransformation = new EditorLensAWTScreenTransformationImpl();
		screenDirection = new EditorLensXYToZY();
	}

	public void setScale(double scaling) {
		if(scale == null) {
			scale = new EditorLensScaleImpl();
		}

		EditorPoint current = scale.getScaling();
		current.setX(scaling);
		current.setY(scaling);
		current.setZ(scaling);
		current.setW(1.0);
	}

	public void setScale(EditorPoint scaling) {
		if(scale == null) {
			scale = new EditorLensScaleImpl();
		}

		EditorPoint current = scale.getScaling();
		current.setX(scaling.getX());
		current.setY(scaling.getY());
		current.setZ(scaling.getZ());
		current.setW(1.0);
	}

	public void addTranslation(double x, double y, double z) {
		if(translate == null) {
			translate = new EditorLensTranslateImpl();
		}

		translate.addTranslation(x, y, z);
	}

	public EditorPoint fromModelToScreen(double x, double y, double z) {
		EditorPoint result = new EditorPoint(x, y, z, 1.0);

		if(scale != null) {
			scale.fromModelToScreen(result);
		}

		if(customTransformation != null) {
			customTransformation.fromModelToScreen(result);
		}

		if(screenTransformation != null) {
			screenTransformation.fromModelToScreen(result);
		}

		if(screenDirection != null) {
			screenDirection.fromModelToScreen(result);
		}

		if(translate != null) {
			translate.fromModelToScreen(result);
		}

		return result;
	}

	public EditorPoint fromModelToScreen(EditorPoint original) {
		EditorPoint result = new EditorPoint(original);

		if(scale != null) {
			scale.fromModelToScreen(result);
		}

		if(customTransformation != null) {
			customTransformation.fromModelToScreen(result);
		}

		if(screenTransformation != null) {
			screenTransformation.fromModelToScreen(result);
		}

		if(screenDirection != null) {
			screenDirection.fromModelToScreen(result);
		}

		if(translate != null) {
			translate.fromModelToScreen(result);
		}

		return result;
	}

	public EditorPoint fromScreenToModel(EditorPoint original) {
		EditorPoint result = new EditorPoint(original);

		if(translate != null) {
			translate.fromScreenToModel(result);
		}

		if(screenDirection != null) {
			screenDirection.fromScreenToModel(result);
		}

		if(screenTransformation != null) {
			screenTransformation.fromScreenToModel(result);
		}

		if(customTransformation != null) {
			customTransformation.fromScreenToModel(result);
		}

		if(scale != null) {
			scale.fromScreenToModel(result);
		}

		return result;
	}

}
