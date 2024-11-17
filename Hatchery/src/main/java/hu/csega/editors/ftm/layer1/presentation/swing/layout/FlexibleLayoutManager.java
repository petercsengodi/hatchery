package hu.csega.editors.ftm.layer1.presentation.swing.layout;

import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.awt.*;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.*;

public class FlexibleLayoutManager implements LayoutManager, LayoutManager2 {

	public static final boolean DEBUG_LAYOUT = true;

	private final Container wrappedContainer;
	private final Map<Component, FlexibleLayoutCalculation> componentsToLayoutCalculations = new Hashtable<>();

	private int width = 1024;
	private int height = 768;
	private boolean invalid = true;

	public FlexibleLayoutManager(Container wrappedContainer) {
		this.wrappedContainer = wrappedContainer;
		this.wrappedContainer.setLayout(this);
	}

	public void addComponent(Component component, FlexibleLayoutCalculation flexibleLayoutCalculation) {
		if(DEBUG_LAYOUT) {
			FlexibleLayoutDebugPanel debugPanel = new FlexibleLayoutDebugPanel();
			this.componentsToLayoutCalculations.put(debugPanel, flexibleLayoutCalculation);
			this.wrappedContainer.add(debugPanel);
		}

		this.componentsToLayoutCalculations.put(component, flexibleLayoutCalculation);
		this.wrappedContainer.add(component);
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void setSize(Dimension dimension) {
		this.width = dimension.width;
		this.height = dimension.height;
	}

	public void updateAfterAllComponentsAreAdded() {
		this.recalculatePositions(width, height);
	}

	@Override
	public void addLayoutComponent(Component component, Object constraints) {
		invalid = true;
	}

	@Override
	public void addLayoutComponent(String name, Component component) {
		invalid = true;
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	@Override
	public void invalidateLayout(Container target) {
		invalid = true;
	}

	@Override
	public void removeLayoutComponent(Component component) {
		componentsToLayoutCalculations.remove(component);
		invalid = true;
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		return new Dimension(1024, 768);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return new Dimension(800, 600);
	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
		return new Dimension(8000, 6000);
	}

	@Override
	public void layoutContainer(Container parent) {
		logger.debug("Parent: " + (parent != null ? parent.getClass().getName() : "–"));

		if(invalid && parent != null) {
			int width = parent.getWidth();
			int height = parent.getHeight();
			recalculatePositions(width, height);
		}
	}

	private void recalculatePositions(int width, int height) {
		this.width = width;
		this.height = height;

		logger.debug("Recalculating positions for a box " + width + 'x' + height + '.');
		for(Map.Entry<Component, FlexibleLayoutCalculation> entry : componentsToLayoutCalculations.entrySet()) {
			Component component = entry.getKey();
			FlexibleLayoutCalculation calculation = entry.getValue();
			component.setBounds(calculation.calculateRectangle(width, height));
		}

		this.invalid = false;
	}

	private static final Logger logger = LoggerFactory.createLogger(FlexibleLayoutManager.class);
}
