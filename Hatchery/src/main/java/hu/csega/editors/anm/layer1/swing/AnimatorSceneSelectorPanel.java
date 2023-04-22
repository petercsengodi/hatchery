package hu.csega.editors.anm.layer1.swing;

import java.awt.*;

import javax.swing.*;

public class AnimatorSceneSelectorPanel extends JPanel {

	private JLabel numberOfScenesLabel;
	private JLabel currentSceneLabel;
	private JLabel copyFromSceneLabel;

	private JButton changeNumberOfScenes;
	private JButton copy;

	private JTextField numberOfScenes;
	private JTextField currentScene;
	private JTextField copyFromScene;

	private JPanel upperPanel;
	private JPanel lowerPanel;

	public AnimatorSceneSelectorPanel() {
		this.numberOfScenesLabel = new JLabel("Number of scenes:");
		this.currentSceneLabel = new JLabel("Current scene:");
		this.copyFromSceneLabel = new JLabel("Copy from scene:");

		this.changeNumberOfScenes = new JButton("Change!");
		this.copy = new JButton("Copy!");

		this.numberOfScenes = new JTextField(4);
		this.currentScene = new JTextField(4);
		this.copyFromScene = new JTextField(4);

		this.upperPanel = new JPanel();
		this.upperPanel.setLayout(new FlowLayout());
		this.upperPanel.add(this.numberOfScenesLabel);
		this.upperPanel.add(this.numberOfScenes);
		this.upperPanel.add(this.changeNumberOfScenes);

		this.lowerPanel = new JPanel();
		this.lowerPanel.setLayout(new FlowLayout());
		this.lowerPanel.add(this.currentSceneLabel);
		this.lowerPanel.add(this.currentScene);
		this.lowerPanel.add(this.copyFromSceneLabel);
		this.lowerPanel.add(this.copyFromScene);
		this.lowerPanel.add(this.copy);

		this.setLayout(new GridLayout(3, 1));
		this.add(new JSeparator(JSeparator.HORIZONTAL));
		this.add(upperPanel);
		this.add(lowerPanel);
	}

	private static final long serialVersionUID = 1L;
}
