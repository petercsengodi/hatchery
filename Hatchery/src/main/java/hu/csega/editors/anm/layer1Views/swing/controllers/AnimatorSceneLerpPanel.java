package hu.csega.editors.anm.layer1Views.swing.controllers;

import hu.csega.editors.anm.layer4Data.model.AnimatorModel;
import hu.csega.games.units.UnitStore;

import java.awt.*;

import javax.swing.*;

public class AnimatorSceneLerpPanel extends JPanel {

	private AnimatorModel animatorModel;

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
		animatorModel = UnitStore.instance(AnimatorModel.class);

		this.startSceneLabel = new JLabel("Starting scene:");
		this.endSceneLabel = new JLabel("Ending scene:");
		this.writeFromLabel = new JLabel("Write from:");
		this.writeUntilLabel = new JLabel("Until:");

		this.lerpButton = new JButton("Do the LERP!");
		this.lerpButton.addActionListener(event -> doTheLerp());

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

	public void doTheLerp() {
		int startSceneIndex;
		try {
			startSceneIndex = Integer.parseInt(startScene.getText());
			if(startSceneIndex < 0) {
				startScene.setText("0");
			}
		} catch (NumberFormatException ex) {
			startScene.setText("invalid");
			return;
		}

		int endSceneIndex;
		try {
			endSceneIndex = Integer.parseInt(endScene.getText());
			if(endSceneIndex < 0) {
				endScene.setText("0");
			}
		} catch (NumberFormatException ex) {
			endScene.setText("invalid");
			return;
		}

		int writeFromIndex;
		try {
			writeFromIndex = Integer.parseInt(writeFrom.getText());
			if(writeFromIndex < 0) {
				writeFrom.setText("0");
			}
		} catch (NumberFormatException ex) {
			writeFrom.setText("invalid");
			return;
		}

		int writeUntilIndex;
		try {
			writeUntilIndex = Integer.parseInt(writeUntil.getText());
			if(writeUntilIndex < 0) {
				writeUntil.setText("0");
			}
		} catch (NumberFormatException ex) {
			writeUntil.setText("invalid");
			return;
		}

		animatorModel.lerp(startSceneIndex, endSceneIndex, writeFromIndex, writeUntilIndex);
	}

	private static final long serialVersionUID = 1L;
}
