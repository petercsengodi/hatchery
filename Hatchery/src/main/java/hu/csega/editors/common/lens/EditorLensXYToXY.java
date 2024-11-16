package hu.csega.editors.common.lens;

public class EditorLensXYToXY implements EditorLens {

	@Override
	public void fromModelToScreen(EditorPoint original) {
		original.setY(-original.getY());
	}

	@Override
	public void fromScreenToModel(EditorPoint original) {
		original.setY(-original.getY());
	}

}
