package hu.csega.editors.common.lens;

public class EditorLensPipeline {

	private EditorLensTranslateImpl translate = new EditorLensTranslateImpl();
	private EditorLensScaleImpl scale = new EditorLensScaleImpl();
	private EditorLensScreenTransformationImpl screenTransformation = new EditorLensScreenTransformationImpl();

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

	public void setTranslation(EditorPoint translation) {
		EditorPoint current = translate.getTranslation();
		current.setX(translation.getX());
		current.setY(translation.getY());
		current.setZ(translation.getZ());
		current.setW(0.0);
	}

	public void addTranslation(double x, double y, double z) {
		EditorPoint ep = new EditorPoint(x, y, z, 0.0);
		screenTransformation.fromScreenToModel(ep);
		scale.fromScreenToModel(ep);

		EditorPoint current = translate.getTranslation();
		current.addValuesFrom(ep);
		current.setW(0.0);
	}

	public void addTranslation(EditorPoint translation) {
		EditorPoint ep = new EditorPoint(translation);
		screenTransformation.fromScreenToModel(ep);
		scale.fromScreenToModel(ep);

		EditorPoint current = translate.getTranslation();
		current.addValuesFrom(ep);
		current.setW(0.0);
	}

	public EditorPoint fromModelToScreen(double x, double y, double z) {
		EditorPoint result = new EditorPoint(x, y, z, 1.0);
		translate.fromModelToScreen(result);
		scale.fromModelToScreen(result);
		screenTransformation.fromModelToScreen(result);
		return result;
	}

	public EditorPoint fromModelToScreen(EditorPoint original) {
		EditorPoint result = new EditorPoint(original);
		translate.fromModelToScreen(result);
		scale.fromModelToScreen(result);
		screenTransformation.fromModelToScreen(result);
		return result;
	}

	public EditorPoint fromScreenToModel(EditorPoint original) {
		EditorPoint result = new EditorPoint(original);
		screenTransformation.fromScreenToModel(result);
		scale.fromScreenToModel(result);
		translate.fromScreenToModel(result);
		return result;
	}

	public EditorPoint fromScreenToModel(double x, double y) {
		EditorPoint result = new EditorPoint(x, y, 0, 1);
		screenTransformation.fromScreenToModel(result);
		scale.fromScreenToModel(result);
		translate.fromScreenToModel(result);
		return result;
	}

}
