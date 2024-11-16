package hu.csega.editors.common.lens;

public class EditorLensXYToZY implements EditorLens {

	@Override
	public void fromModelToScreen(EditorPoint original) {
		double tmp = original.getX();
		original.setX(original.getZ());
		original.setZ(tmp);
	}

	@Override
	public void fromScreenToModel(EditorPoint original) {
		double tmp = original.getX();
		original.setX(original.getZ());
		original.setZ(tmp);
	}

}
