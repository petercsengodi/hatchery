package hu.csega.editors.common.lens;

public class EditorLensClippingCoordinatesTransformationImpl implements EditorLens {

	private double widthStretch;
	private double widthBase;
	private double heightStretch;
	private double heightBase;

	public void setWindowWidth(int windowWidth) {
		this.widthBase = this.widthStretch = windowWidth / 2.0;
	}

	public void setWindowHeight(int windowHeight) {
		this.heightBase = this.heightStretch = windowHeight / 2.0;
	}

	@Override
	public void fromModelToScreen(EditorPoint original) {
		original.setX(original.getX() * widthStretch + widthBase);
		original.setY((-original.getY()) * heightStretch + heightBase);
	}

	@Override
	public void fromScreenToModel(EditorPoint original) {
		original.setX((original.getX() - widthBase) / widthStretch);
		original.setY(-((original.getY() - heightBase) / heightStretch));
	}

}
