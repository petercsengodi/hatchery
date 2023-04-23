package hu.csega.editors.anm.layer1.swing.controllers;

import java.awt.*;

import javax.swing.*;

public class AnimatorSceneLerpPanel extends JPanel {

	private JLabel startSceneLabel;
	private JLabel endSceneLabel;
	private JLabel writeFromLabel;
	private JLabel writeUntilLabel;

	private JButton lerpButton;

	private JTextField startScene;
	private JTextField endScene;
	private JTextField writeFrom;
	private JTextField writeUntil;

	private JPanel upperPanel;
	private JPanel lowerPanel;

	public AnimatorSceneLerpPanel() {
		this.startSceneLabel = new JLabel("Starting scene:");
		this.endSceneLabel = new JLabel("Ending scene:");
		this.writeFromLabel = new JLabel("Write from:");
		this.writeUntilLabel = new JLabel("Until:");

		this.lerpButton = new JButton("Do the LERP!");

		this.startScene = new JTextField(4);
		this.endScene = new JTextField(4);
		this.writeFrom = new JTextField(4);
		this.writeUntil = new JTextField(4);

		this.upperPanel = new JPanel();
		this.upperPanel.setLayout(new FlowLayout());
		this.upperPanel.add(this.startSceneLabel);
		this.upperPanel.add(this.startScene);
		this.upperPanel.add(this.endSceneLabel);
		this.upperPanel.add(this.endScene);

		this.lowerPanel = new JPanel();
		this.lowerPanel.setLayout(new FlowLayout());
		this.lowerPanel.add(this.writeFromLabel);
		this.lowerPanel.add(this.writeFrom);
		this.lowerPanel.add(this.writeUntilLabel);
		this.lowerPanel.add(this.writeUntil);
		this.lowerPanel.add(this.lerpButton);

		this.setLayout(new GridLayout(3, 1));
		this.add(new JSeparator(JSeparator.HORIZONTAL));
		this.add(upperPanel);
		this.add(lowerPanel);
	}

	private static final long serialVersionUID = 1L;
}
