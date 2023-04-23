package hu.csega.editors.anm.layer1.swing.components.rotator;

import java.awt.*;

import javax.swing.*;

public class AnimatorRotatorComponent extends JPanel implements LayoutManager {

	private AnimatorRotatorBinding binding = null;
	// private AnimatorRotatorBinding dummy = new AnimatorRotatorDummyBinding(this);

	private JScrollBar controlXAxis;
	private JScrollBar controlYAxis;
	private JScrollBar controlZAxis;
	private JPanel canvas;

	private final BoundedRangeModel xRange;
	private final BoundedRangeModel yRange;
	private final BoundedRangeModel zRange;

	public AnimatorRotatorComponent() {
		this.controlXAxis = new JScrollBar(JScrollBar.VERTICAL);
		this.controlYAxis = new JScrollBar(JScrollBar.HORIZONTAL);
		this.controlZAxis = new JScrollBar(JScrollBar.VERTICAL);
		this.canvas = new AnimatorRotatorCanvas(this);

		this.setLayout(this);

		// Without "name" argument the addLayoutComponent method is not called.
		this.add("controlXAxis", controlXAxis);
		this.add("controlYAxis", controlYAxis);
		this.add("controlZAxis", controlZAxis);
		this.add("canvas", canvas);

		this.xRange = new DefaultBoundedRangeModel(0, 10, -179, 190);
		this.xRange.addChangeListener(new AnimatorRotatorXAngleChanged(this, this.xRange));
		this.controlXAxis.setModel(this.xRange);

		this.yRange = new DefaultBoundedRangeModel(0, 10, -179, 190);
		this.yRange.addChangeListener(new AnimatorRotatorYAngleChanged(this, this.yRange));
		this.controlYAxis.setModel(this.yRange);

		this.zRange = new DefaultBoundedRangeModel(0, 10, -179, 190);
		this.zRange.addChangeListener(new AnimatorRotatorZAngleChanged(this, this.zRange));
		this.controlZAxis.setModel(this.zRange);

		AnimatorRotatorMouseListener mouseListener = new AnimatorRotatorMouseListener();
		this.canvas.addMouseListener(mouseListener);
		this.canvas.addMouseMotionListener(mouseListener);
		this.canvas.addMouseWheelListener(mouseListener);
	}

	public AnimatorRotatorBinding getBinding() {
		if(binding == null) {
			binding = new AnimatorModelBinding();
		}

		return binding;
	}

	public void repaintCanvas() {
		canvas.repaint();
	}

	public void refresh() {
		AnimatorRotatorBinding currentBinding = getBinding();
		xRange.setValue(convertDoubleAngle(currentBinding.currentXRotation()));
		yRange.setValue(convertDoubleAngle(currentBinding.currentYRotation()));
		zRange.setValue(convertDoubleAngle(currentBinding.currentZRotation()));
		repaintCanvas();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(AnimatorRotatorCanvas.CANVAS_MIN_SIZE + 2 * SCROLL_BAR_MIN_SIZE,
				AnimatorRotatorCanvas.CANVAS_MIN_SIZE + SCROLL_BAR_MIN_SIZE);
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
		if(comp == null)
			return;

		Dimension preferredSize = getPreferredSize();
		int width = (int) Math.max(this.getWidth(), preferredSize.getWidth());
		int height = (int) Math.max(this.getHeight(), preferredSize.getHeight());

		if(comp == controlXAxis) {
			comp.setBounds(0, 0, SCROLL_BAR_MIN_SIZE, height - SCROLL_BAR_MIN_SIZE);
		} else if(comp == controlYAxis) {
			comp.setBounds(SCROLL_BAR_MIN_SIZE, height - SCROLL_BAR_MIN_SIZE,
					width - 2 * SCROLL_BAR_MIN_SIZE, SCROLL_BAR_MIN_SIZE);
		} else if(comp == controlZAxis) {
			comp.setBounds(width - SCROLL_BAR_MIN_SIZE, 0, SCROLL_BAR_MIN_SIZE,
					height - SCROLL_BAR_MIN_SIZE);
		} else if(comp == canvas) {
			comp.setBounds(SCROLL_BAR_MIN_SIZE, 0, width - 2 * SCROLL_BAR_MIN_SIZE,
					height - SCROLL_BAR_MIN_SIZE);
		}
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		return getPreferredSize();
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return getPreferredSize();
	}

	@Override
	public void layoutContainer(Container parent) {
	}

	private int convertDoubleAngle(double value) {
		int v = ((int) value) % 360;
		if(v > 180) {
			v -= 360;
		}
		if(v < 179) {
			v += 360;
		}
		return v;
	}

	private static final int SCROLL_BAR_MIN_SIZE = 20;

	private static final long serialVersionUID = 1L;

}
