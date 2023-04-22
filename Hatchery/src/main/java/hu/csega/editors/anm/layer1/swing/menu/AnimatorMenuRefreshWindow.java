package hu.csega.editors.anm.layer1.swing.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

class AnimatorMenuRefreshWindow implements ActionListener {

	private JFrame frame;

	AnimatorMenuRefreshWindow(JFrame frame) {
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		frame.invalidate();
		frame.validate();
		frame.repaint();
	}

}
