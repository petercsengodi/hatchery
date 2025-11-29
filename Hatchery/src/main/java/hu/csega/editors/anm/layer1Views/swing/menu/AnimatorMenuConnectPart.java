package hu.csega.editors.anm.layer1Views.swing.menu;

import hu.csega.editors.anm.layer1Views.swing.components.connectjoints.ConnectJointsDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class AnimatorMenuConnectPart implements ActionListener {

	private ConnectJointsDialog dialog;

	public AnimatorMenuConnectPart(JFrame frame) {
		 dialog = new ConnectJointsDialog(frame);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		dialog.showConnectJointsDialog();
	}

}
