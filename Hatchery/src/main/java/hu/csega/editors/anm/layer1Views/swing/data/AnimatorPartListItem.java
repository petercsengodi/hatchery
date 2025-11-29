package hu.csega.editors.anm.layer1Views.swing.data;

public class AnimatorPartListItem {

	private String identifier;
	private String label;
	private String mesh;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getMesh() {
		return mesh;
	}

	public void setMesh(String mesh) {
		this.mesh = mesh;
	}

	@Override
	public String toString() {
		if(label == null) {
			return nameFromIndexAndMesh();
		}

		String trimmed = label.trim();
		if(trimmed.length() == 0) {
			return nameFromIndexAndMesh();
		}

		return trimmed;
	}

	private String nameFromIndexAndMesh() {
		return "[Identifier: " + identifier + ", " + mesh + ']';
	}
}
