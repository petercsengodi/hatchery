package hu.csega.editors.anm.layer1.swing.controllers;

import java.awt.*;

import javax.swing.*;

import hu.csega.editors.anm.layer1.swing.components.rotator.AnimatorRotatorComponent;
import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.editors.anm.ui.layout.panels.AnimatorPanelFixedSizeLayoutListener;
import hu.csega.editors.anm.ui.layout.panels.AnimatorPanelLayoutChangeListener;
import hu.csega.editors.anm.ui.layout.panels.AnimatorPanelLayoutManager;
import hu.csega.games.units.UnitStore;

public class AnimatorPartEditorPanel extends JPanel {

	private final AnimatorModel model;

	public AnimatorPanelLayoutManager layout;

	public JLabel labelJoints;
	public JScrollPane scrollJoints;
	public JList<String> joints;

	public JButton horizontalFlip;
	public JButton verticalFlip;

	public JButton rotateUp;
	public JButton rotateDown;
	public JButton rotateLeft;
	public JButton rotateRight;

	public JLabel xLabel;
	public JTextField xField;
	public JLabel yLabel;
	public JTextField yField;
	public JLabel zLabel;
	public JTextField zField;
	public JPanel xyzPanel;

	public JButton addButton;
	public JButton setButton;
	public JButton delButton;
	public JPanel buttonsPanel;

	public AnimatorRotatorComponent rotator;

	public AnimatorPartEditorPanel() {
		this.model = UnitStore.instance(AnimatorModel.class);

		this.xLabel = new JLabel("X:");
		this.xField = new JTextField(5);
		this.yLabel = new JLabel("Y:");
		this.yField = new JTextField(5);
		this.zLabel = new JLabel("Z:");
		this.zField = new JTextField(5);

		this.xyzPanel = new JPanel();
		this.xyzPanel.setLayout(new FlowLayout());
		this.xyzPanel.add(this.xLabel);
		this.xyzPanel.add(this.xField);
		this.xyzPanel.add(this.yLabel);
		this.xyzPanel.add(this.yField);
		this.xyzPanel.add(this.zLabel);
		this.xyzPanel.add(this.zField);

		this.addButton = new JButton("Add!");
		this.setButton = new JButton("Set!");
		this.delButton = new JButton("Delete!");

		this.buttonsPanel = new JPanel();
		this.buttonsPanel.setLayout(new FlowLayout());
		this.buttonsPanel.add(addButton);
		this.buttonsPanel.add(setButton);
		this.buttonsPanel.add(delButton);

		this.layout = new AnimatorPanelLayoutManager(200, 300);
		this.setLayout(layout);

		int lineOffset = 0;

		this.labelJoints = new JLabel("Joints:");
		this.add(this.labelJoints, new AnimatorPanelLayoutChangeListener() {
			@Override
			public void arrange(Component component, int width, int height) {
				component.setBounds(5, 5, width - 10, 20);
			}
		});

		this.joints = new JList<>();
		this.scrollJoints = new JScrollPane(this.joints);
		this.add(this.scrollJoints, new AnimatorPanelLayoutChangeListener() {
			@Override
			public void arrange(Component component, int width, int height) {
				component.setBounds(5, 27, width - 10, 160);
			}
		});

		lineOffset = 190;
		final int xyzPanelOffset = lineOffset;
		this.add(this.xyzPanel, new AnimatorPanelLayoutChangeListener() {
			@Override
			public void arrange(Component component, int width, int height) {
				component.setBounds(0, xyzPanelOffset, width, 30);
			}
		});

		lineOffset += 30;
		final int buttonsPanelOffset = lineOffset;
		this.add(this.buttonsPanel, new AnimatorPanelLayoutChangeListener() {
			@Override
			public void arrange(Component component, int width, int height) {
				component.setBounds(0, buttonsPanelOffset, width, 30);
			}
		});

		lineOffset += 40;
		final int flipButtonsOffset = lineOffset;
		this.horizontalFlip = new JButton("Horiz. Flip");
		this.add(this.horizontalFlip, new AnimatorPanelLayoutChangeListener() {
			@Override
			public void arrange(Component component, int width, int height) {
				component.setBounds(5, flipButtonsOffset, width / 2 - 10, 20);
			}
		});

		this.verticalFlip = new JButton("Vert. Flip");
		this.add(this.verticalFlip, new AnimatorPanelLayoutChangeListener() {
			@Override
			public void arrange(Component component, int width, int height) {
				component.setBounds(width / 2 + 5, flipButtonsOffset, width / 2 - 10, 20);
			}
		});

		lineOffset += 20;
		this.rotateUp = new JButton("▲");
		this.add(this.rotateUp, new AnimatorPanelFixedSizeLayoutListener(0, lineOffset) {
			@Override
			protected void resize(Component component, int offsetX, int offsetY, int width, int height) {
				component.setBounds(width / 2 - 25, offsetY + 10, 50, 20);
			}
		});

		this.rotateDown = new JButton("▼");
		this.add(this.rotateDown, new AnimatorPanelFixedSizeLayoutListener(0, lineOffset) {
			@Override
			protected void resize(Component component, int offsetX, int offsetY, int width, int height) {
				component.setBounds(width / 2 - 25, offsetY + 40, 50, 20);
			}
		});

		this.rotateLeft = new JButton("◄");
		this.add(this.rotateLeft, new AnimatorPanelFixedSizeLayoutListener(0, lineOffset) {
			@Override
			protected void resize(Component component, int offsetX, int offsetY, int width, int height) {
				component.setBounds(width / 2 - 90, offsetY + 25, 50, 20);
			}
		});

		this.rotateRight = new JButton("►");
		this.add(this.rotateRight, new AnimatorPanelFixedSizeLayoutListener(0, lineOffset) {
			@Override
			protected void resize(Component component, int offsetX, int offsetY, int width, int height) {
				component.setBounds(width / 2 + 40, offsetY + 25, 50, 20);
			}
		});

		lineOffset += 80;

		this.rotator = new AnimatorRotatorComponent();
		this.add(this.rotator, new AnimatorPanelFixedSizeLayoutListener(0, lineOffset) {
			@Override
			protected void resize(Component component, int offsetX, int offsetY, int width, int height) {
				Dimension preferredSize = component.getPreferredSize();
				component.setBounds(offsetX + 5, offsetY + 5, preferredSize.width, preferredSize.height);
			}
		});

		lineOffset += this.rotator.getPreferredSize().height + 10;

		applyEventMethods();
	}

	private void applyEventMethods() {
		addButton.addActionListener(event -> {
			double x = checkAndGetDoubleField(xField);
			double y = checkAndGetDoubleField(yField);
			double z = checkAndGetDoubleField(zField);
			model.addJointToSelectedPart(x, y, z);
		});

		setButton.addActionListener(event -> {
			double x = checkAndGetDoubleField(xField);
			double y = checkAndGetDoubleField(yField);
			double z = checkAndGetDoubleField(zField);
			model.modifySelectedJoint(x, y, z);
		});

		delButton.addActionListener(event -> {
			model.deleteSelectedJoint();
		});
	}

	private static double checkAndGetDoubleField(JTextField field) {
		try {
			return Double.parseDouble(field.getText());
		} catch(NumberFormatException ex) {
			field.setText("0.0");
			return 0.0;
		}
	}

	private static final long serialVersionUID = 1L;
}