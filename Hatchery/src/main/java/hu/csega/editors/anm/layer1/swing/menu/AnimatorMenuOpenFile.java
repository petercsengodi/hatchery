package hu.csega.editors.anm.layer1.swing.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import hu.csega.editors.anm.components.ComponentRefreshViews;
import hu.csega.editors.common.SerializationUtil;
import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.games.library.animation.v1.anm.Animation;
import hu.csega.games.library.animation.v1.anm.AnimationPart;
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
class AnimatorMenuOpenFile implements ActionListener {

	private JFrame frame;
	private JFileChooser openDialog;
	private GameEngineFacade facade;
	private ResourceAdapter resourceAdapter;

	public AnimatorMenuOpenFile(JFrame frame, JFileChooser openDialog, GameEngineFacade facade) {
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
			AnimatorModel model = (AnimatorModel) facade.model();
			byte[] bytes = FreeTriangleMeshSnapshots.readAllBytes(file);
			if(bytes == null || bytes.length == 0) {
				model.setPersistent(null);
			} else if(bytes[0] == (byte)'<') {
				// Loading legacy animation (or exported animation?), XML

				ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
				SAnimation parsedAnimationFile = (SAnimation) LegacyAnimationParser.parseAnimationFile(stream);
				logger.info("Loaded object: " + parsedAnimationFile);

				Animation migratedObject = null; // FIXME

				logger.info("Migrated object: " + migratedObject);
				model.loadAnimation(file.getAbsolutePath(), migratedObject);
			} else {
				// Loading serialized, binary data
				AnimationPersistent persistent = SerializationUtil.deserialize(bytes, AnimationPersistent.class);

				cleanUpMeshFilenames(persistent.getAnimation().getParts());

				model.setPersistent(persistent);
				model.clearSnapshots();
			}

			ComponentRefreshViews refreshViews = UnitStore.instance(ComponentRefreshViews.class);
			refreshViews.refreshAll();
			break;

		default:
			break;
		}
	}

	private void cleanUpMeshFilenames(Map<String, AnimationPart> parts) {
		if(parts != null && !parts.isEmpty()) {
			if(resourceAdapter == null) {
				resourceAdapter = UnitStore.instance(ResourceAdapter.class);
			}

			for(AnimationPart part : parts.values()) {
				part.setMesh(resourceAdapter.cleanUpResourceFilename(part.getMesh()));
			}
		}
	}

	private static final Logger logger = LoggerFactory.createLogger(AnimatorMenuOpenFile.class);
}
