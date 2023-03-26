package hu.csega.editors.anm.layer1.swing.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import hu.csega.editors.anm.components.ComponentRefreshViews;
import hu.csega.editors.common.SerializationUtil;
import hu.csega.games.library.animation.v1.anm.Animation;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.editors.anm.layer5.files.storage.LegacyAnimationParser;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshSnapshots;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.library.animation.v1.xml.SAnimation;
import hu.csega.games.units.UnitStore;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

@SuppressWarnings("unused")
class AnimatorMenuItemOpen implements ActionListener {

	private JFrame frame;
	private JFileChooser openDialog;
	private GameEngineFacade facade;

	public AnimatorMenuItemOpen(JFrame frame, JFileChooser openDialog, GameEngineFacade facade) {
		this.frame = frame;
		this.openDialog = openDialog;
		this.facade = facade;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int result = openDialog.showDialog(frame, "Open");

		switch(result) {
		case JFileChooser.APPROVE_OPTION:
			File file = openDialog.getSelectedFile();
			byte[] bytes = FreeTriangleMeshSnapshots.readAllBytes(file);
			if(bytes == null || bytes.length == 0) {
				// FIXME handle this case

			} else if(bytes[0] == (byte)'<') {
				// Loading legacy animation (or exported animation?), XML

				ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
				SAnimation parsedAnimationFile = (SAnimation) LegacyAnimationParser.parseAnimationFile(stream);
				logger.info("Loaded object: " + parsedAnimationFile);

				Animation migratedObject = null; // FIXME

				logger.info("Migrated object: " + migratedObject);
				AnimatorModel model = (AnimatorModel) facade.model();
				model.loadAnimation(file.getAbsolutePath(), migratedObject);
			} else {
				// Loading serialized, binary data
				AnimationPersistent persistent = SerializationUtil.deserialize(bytes, AnimationPersistent.class);
			}

			ComponentRefreshViews refreshViews = UnitStore.instance(ComponentRefreshViews.class);
			refreshViews.refreshAll();
			break;

		default:
			break;
		}
	}

	private static final Logger logger = LoggerFactory.createLogger(AnimatorMenuItemOpen.class);
}
