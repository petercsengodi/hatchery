package hu.csega.editors.anm.layer1Views.swing.wireframe;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collection;

import javax.swing.JPanel;

import hu.csega.editors.anm.components.ComponentWireFrameRenderer;
import hu.csega.editors.anm.components.ComponentWireFrameTransformer;
import hu.csega.games.units.UnitStore;

public class AnimatorWireFrameView extends JPanel implements ComponentWireFrameRenderer {

	private final int indexOfX, indexOfY;
	private ComponentWireFrameTransformer source;
	private AnimatorWireFrame wireFrame;

	public AnimatorWireFrameView(int indexOfX, int indexOfY) {
		this.source = UnitStore.instance(ComponentWireFrameTransformer.class);
		this.indexOfX = indexOfX;
		this.indexOfY = indexOfY;
	}

	@Override
	public void paint(Graphics g) {
		int width = this.getWidth();
		int height = this.getHeight();

		g.setColor(Color.darkGray);
		g.fillRect(0, 0, width, height);

		if(wireFrame == null) {
			wireFrame = source.transform(null);
		}

		if(wireFrame != null) {
			g.translate(width / 2, height / 2);

			Collection<AnimatorWireFrameLine> lines = wireFrame.getLines();
			if(lines != null) {
				for(AnimatorWireFrameLine line : lines) {
					AnimatorWireFramePoint source = line.getSource();
					int x1 = (int)source.valueOfIndex(indexOfX);
					int y1 = (int)source.valueOfIndex(indexOfY);

					AnimatorWireFramePoint destination = line.getDestination();
					int x2 = (int)destination.valueOfIndex(indexOfX);
					int y2 = (int)destination.valueOfIndex(indexOfY);

					g.setColor(line.getColor());
					g.drawLine(x1, y1, x2, y2);

					g.setColor(source.getColor());
					g.drawRect(x1 - 2, y1 - 2, 5, 5);

					g.setColor(destination.getColor());
					g.drawRect(x2 - 2, y2 - 2, 5, 5);
				}
			}

			Collection<AnimatorWireFramePoint> points = wireFrame.getPoints();
			if(points != null) {
				for(AnimatorWireFramePoint point : points) {
					int x = (int) point.valueOfIndex(indexOfX);
					int y = (int) point.valueOfIndex(indexOfY);

					g.setColor(point.getColor());
					if(point.isCross()) {
						g.drawLine(x-10, y-10, x+10, y+10);
						g.drawLine(x-10, y+10, x+10, y-10);
					} else {
						g.drawOval(x-5, y-5, 11, 11);
					}
				}
			}

			g.translate(-width / 2, -height / 2);
		}
	}

	@Override
	public Void provide() {
		return null;
	}

	@Override
	public void accept(AnimatorWireFrame wireFrame) {
		this.wireFrame = wireFrame;
		this.repaint();
	}

	private static final long serialVersionUID = 1L;
}
