package hu.csega.editors.anm.layer1.swing.menu;

import hu.csega.editors.anm.layer1.swing.connect.ConnectJointsDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnimatorMenuConnectPart implements ActionListener {

	private ConnectJointsDialog dialog = new ConnectJointsDialog();

	@Override
	public void actionPerformed(ActionEvent e) {
		dialog.showConnectJointsDialog();
	}

}
