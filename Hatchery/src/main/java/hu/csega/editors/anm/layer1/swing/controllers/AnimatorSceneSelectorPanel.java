package hu.csega.editors.anm.layer1.swing.controllers;

import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.games.units.UnitStore;

import java.awt.*;

import javax.swing.*;

public class AnimatorSceneSelectorPanel extends JPanel {

	private AnimatorModel animatorModel;

	private JLabel numberOfScenesLabel;
	private JLabel currentSceneLabel;
	private JLabel copyFromSceneLabel;

	private JButton changeNumberOfScenes;
	private JButton changeCurrentScene;
	private JButton previouseScene;
	private JButton nextScene;
	private JButton copy;
	private JButton copyFromThePrevious;

	private JTextField numberOfScenes;
	private JTextField currentScene;
	private JTextField copyFromScene;

	private JPanel upperPanel;
	private JPanel middlePanel;
	private JPanel lowerPanel;

	public AnimatorSceneSelectorPanel() {
		this.animatorModel = UnitStore.instance(AnimatorModel.class);

		this.numberOfScenesLabel = new JLabel("Number of scenes:");
		this.currentSceneLabel = new JLabel("Current scene:");
		this.copyFromSceneLabel = new JLabel("Copy from scene:");

		this.numberOfScenes = new JTextField(4);
		this.currentScene = new JTextField(4);
		this.copyFromScene = new JTextField(4);

		this.changeNumberOfScenes = new JButton("Change!");
		this.changeNumberOfScenes.addActionListener(event -> {
			try {
				this.animatorModel.changeNumberOfScenes(Integer.parseInt(numberOfScenes.getText()));
			} catch(NumberFormatException ex) {
				this.animatorModel.justRefreshViews();
			}
		});

		this.changeCurrentScene = new JButton("Change!");
		this.changeCurrentScene.addActionListener(event -> {
			try {
				this.animatorModel.selectScene(Integer.parseInt(currentScene.getText()));
			} catch(NumberFormatException ex) {
				this.animatorModel.justRefreshViews();
			}
		});

		this.previouseScene = new JButton("-");
		this.previouseScene.addActionListener(event -> this.animatorModel.previouseScene());

		this.nextScene = new JButton("+");
		this.nextScene.addActionListener(event -> this.animatorModel.nextScene());

		this.copy = new JButton("Copy!");
		this.copy.addActionListener(event -> {
			try {
				this.animatorModel.copySelectedSceneFrom(Integer.parseInt(copyFromScene.getText()));
			} catch(NumberFormatException ex) {
				copyFromScene.setText("invalid");
			}
		});

		this.copyFromThePrevious = new JButton("Copy from the previous!");
		this.copyFromThePrevious.addActionListener(event -> this.animatorModel.copyFromThePrevious());

		this.upperPanel = new JPanel();
		this.upperPanel.setLayout(new FlowLayout());
		this.upperPanel.add(this.numberOfScenesLabel);
		this.upperPanel.add(this.numberOfScenes);
		this.upperPanel.add(this.changeNumberOfScenes);

		this.middlePanel = new JPanel();
		this.middlePanel.setLayout(new FlowLayout());
		this.middlePanel.add(this.currentSceneLabel);
		this.middlePanel.add(this.currentScene);
		this.middlePanel.add(this.changeCurrentScene);
		this.middlePanel.add(this.previouseScene);
		this.middlePanel.add(this.nextScene);

		this.lowerPanel = new JPanel();
		this.lowerPanel.setLayout(new FlowLayout());
		this.lowerPanel.add(this.copyFromSceneLabel);
		this.lowerPanel.add(this.copyFromScene);
		this.lowerPanel.add(this.copy);
		this.lowerPanel.add(this.copyFromThePrevious);

		this.setLayout(new GridLayout(4, 1));
		this.add(new JSeparator(JSeparator.HORIZONTAL));
		this.add(upperPanel);
		this.add(middlePanel);
		this.add(lowerPanel);

	}

	private static final long serialVersionUID = 1L;
}
