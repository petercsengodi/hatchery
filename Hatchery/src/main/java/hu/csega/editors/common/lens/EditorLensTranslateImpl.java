package hu.csega.editors.common.lens;

public class EditorLensTranslateImpl implements EditorLens {

	private EditorPoint translation = new EditorPoint(0.0, 0.0, 0.0, 0.0);

	public void addTranslation(double x, double y, double z) {
		this.translation.setX(translation.getX() + x);
		this.translation.setY(translation.getY() + y);
		this.translation.setZ(translation.getZ() + z);
	}

	public void setTranslation(double x, double y, double z) {
		this.translation.setX(x);
		this.translation.setY(y);
		this.translation.setZ(z);
	}

	@Override
	public void fromModelToScreen(EditorPoint original) {
		original.setX(original.getX() + translation.getX());
		original.setY(original.getY() + translation.getY());
		original.setZ(original.getZ() + translation.getZ());
		original.setW(original.getW() + translation.getW());
	}

	@Override
	public void fromScreenToModel(EditorPoint original) {
		original.setX(original.getX() - translation.getX());
		original.setY(original.getY() - translation.getY());
		original.setZ(original.getZ() - translation.getZ());
		original.setW(original.getW() - translation.getW());
	}
}
