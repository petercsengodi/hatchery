package hu.csega.editors.common.lens;

public class EditorLensPipeline {

	private EditorLensTranslateImpl translate = new EditorLensTranslateImpl();
	private EditorLensScaleImpl scale = new EditorLensScaleImpl();
	private EditorLensScreenTransformationImpl screenTransformation = new EditorLensScreenTransformationImpl();
	private EditorLens screenDirection = null;

	public void screenXY() {
		screenDirection = new EditorLensXYToXY();
	}

	public void screenXZ() {
		screenDirection = new EditorLensXYToXZ();
	}

	public void screenZY() {
		screenDirection = new EditorLensXYToZY();
	}

	public void setScale(double scaling) {
		EditorPoint current = scale.getScaling();
		current.setX(scaling);
		current.setY(scaling);
		current.setZ(scaling);
		current.setW(1.0);
	}

	public void setScale(EditorPoint scaling) {
		EditorPoint current = scale.getScaling();
		current.setX(scaling.getX());
		current.setY(scaling.getY());
		current.setZ(scaling.getZ());
		current.setW(1.0);
	}

	public void addTranslation(double x, double y, double z) {
		translate.addTranslation(x, y, z);
	}

	public EditorPoint fromModelToScreen(double x, double y, double z) {
		EditorPoint result = new EditorPoint(x, y, z, 1.0);
		scale.fromModelToScreen(result);
		screenTransformation.fromModelToScreen(result);
		screenDirection.fromModelToScreen(result);
		translate.fromModelToScreen(result);
		return result;
	}

	public EditorPoint fromModelToScreen(EditorPoint original) {
		EditorPoint result = new EditorPoint(original);
		scale.fromModelToScreen(result);
		screenTransformation.fromModelToScreen(result);
		screenDirection.fromModelToScreen(result);
		translate.fromModelToScreen(result);
		return result;
	}

	public EditorPoint fromScreenToModel(EditorPoint original) {
		EditorPoint result = new EditorPoint(original);
		translate.fromScreenToModel(result);
		screenDirection.fromScreenToModel(result);
		screenTransformation.fromScreenToModel(result);
		scale.fromScreenToModel(result);
		return result;
	}

}
