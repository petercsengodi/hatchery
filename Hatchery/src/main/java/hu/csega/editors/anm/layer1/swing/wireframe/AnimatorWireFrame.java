package hu.csega.editors.anm.layer1.swing.wireframe;

import hu.csega.games.engine.g3d.GameTransformation;

import java.util.ArrayList;
import java.util.Collection;

public class AnimatorWireFrame {

	private GameTransformation centerPartTransformation = new GameTransformation();
	private Collection<AnimatorWireFrameLine> lines;
	private Collection<AnimatorWireFramePoint> points;

	public GameTransformation getCenterPartTransformation() {
		return centerPartTransformation;
	}

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

	public void addLine(AnimatorWireFrameLine line) {
		if(this.lines == null) {
			this.lines = new ArrayList<>();
		}

		this.lines.add(line);
	}

	public Collection<AnimatorWireFramePoint> getPoints() {
		return points;
	}

	public void setPoints(Collection<AnimatorWireFramePoint> points) {
		this.points = points;
	}

	public void addPoints(Collection<AnimatorWireFramePoint> points) {
		if(this.points == null) {
			this.points = new ArrayList<>();
		}

		this.points.addAll(points);
	}

	public void addPoint(AnimatorWireFramePoint point) {
		if(this.points == null) {
			this.points = new ArrayList<>();
		}

		this.points.add(point);
	}
}
