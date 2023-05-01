package hu.csega.editors.anm.layer1.swing.wireframe;

import java.util.ArrayList;
import java.util.Collection;

public class AnimatorWireFrame {

	private Collection<AnimatorWireFrameLine> lines;

	public Collection<AnimatorWireFrameLine> getLines() {
		return lines;
	}

	public void setLines(Collection<AnimatorWireFrameLine> lines) {
		this.lines = lines;
	}

	public void addLines(Collection<AnimatorWireFrameLine> lines) {
		if(this.lines == null) {
			this.lines = new ArrayList<>();
		}

		this.lines.addAll(lines);
	}

}
