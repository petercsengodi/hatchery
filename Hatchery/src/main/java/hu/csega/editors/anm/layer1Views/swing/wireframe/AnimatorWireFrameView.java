package hu.csega.editors.anm.layer1Views.swing.wireframe;

import hu.csega.editors.anm.components.ComponentWireFrameConverter;
import hu.csega.editors.anm.components.ComponentWireFrameRenderer;
import hu.csega.games.units.Dependency;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class AnimatorWireFrameView extends JPanel implements ComponentWireFrameRenderer {

	private final int indexOfX, indexOfY;
	private ComponentWireFrameConverter wireFrameConverter;
	private AnimatorWireFrame wireFrame;

	public AnimatorWireFrameView(int indexOfX, int indexOfY) {
		this.indexOfX = indexOfX;
		this.indexOfY = indexOfY;
	}

	@Override
	public void paint(Graphics g) {
		int width = this.getWidth();
		int height = this.getHeight();

		g.setColor(Color.darkGray);
		g.fillRect(0, 0, width, height);

		if(wireFrame == null && wireFrameConverter != null) {
			wireFrame = wireFrameConverter.getWireFrame();
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
	public void invalidate() {
		wireFrame = null;
		super.invalidate();
	}

	@Dependency
	public void setWireFrameConverter(ComponentWireFrameConverter wireFrameConverter) {
		this.wireFrameConverter = wireFrameConverter;
	}

	private static final long serialVersionUID = 1L;
}
