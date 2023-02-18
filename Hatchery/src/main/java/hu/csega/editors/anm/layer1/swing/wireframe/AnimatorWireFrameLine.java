package hu.csega.editors.anm.layer1.swing.wireframe;

import java.awt.Color;

public class AnimatorWireFrameLine {

	private AnimatorWireFramePoint source;
	private AnimatorWireFramePoint destination;
	private Color color;

	public AnimatorWireFrameLine() {
	}

	public AnimatorWireFrameLine(AnimatorWireFramePoint source, AnimatorWireFramePoint destination, Color color) {
		this.source = source;
		this.destination = destination;
		this.color = color;
	}

	public AnimatorWireFramePoint getSource() {
		return source;
	}

	public void setSource(AnimatorWireFramePoint source) {
		this.source = source;
	}

	public AnimatorWireFramePoint getDestination() {
		return destination;
	}

	public void setDestination(AnimatorWireFramePoint destination) {
		this.destination = destination;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
