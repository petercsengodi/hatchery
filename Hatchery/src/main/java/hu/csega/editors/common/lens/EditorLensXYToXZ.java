package hu.csega.editors.common.lens;

public class EditorLensXYToXZ implements EditorLens {

	@Override
	public void fromModelToScreen(EditorPoint original) {
		double tmp = original.getY();
		original.setY(original.getZ());
		original.setZ(tmp);
	}

	@Override
	public void fromScreenToModel(EditorPoint original) {
		double tmp = original.getY();
		original.setY(original.getZ());
		original.setZ(tmp);
	}

}
