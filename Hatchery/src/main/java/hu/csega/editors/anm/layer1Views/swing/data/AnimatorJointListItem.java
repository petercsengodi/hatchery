package hu.csega.editors.anm.layer1Views.swing.data;

public class AnimatorJointListItem {

	private String identifier;
	private String label;

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
		return "[Identifier: " + identifier + ']';
	}
}
