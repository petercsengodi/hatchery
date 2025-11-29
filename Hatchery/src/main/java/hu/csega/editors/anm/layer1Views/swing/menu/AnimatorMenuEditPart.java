package hu.csega.editors.anm.layer1Views.swing.menu;

import hu.csega.editors.anm.components.ComponentRefreshViews;
import hu.csega.editors.anm.layer1Views.swing.AnimatorUIComponents;
import hu.csega.editors.anm.layer4Data.model.AnimatorModel;
import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.games.engine.ftm.GameMesh;
import hu.csega.games.engine.ftm.GameTriangle;
import hu.csega.games.engine.ftm.GameVertex;
import hu.csega.games.library.animation.v1.anm.AnimationPart;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshMesh;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshTriangle;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;
import hu.csega.games.units.UnitStore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class AnimatorMenuEditPart implements ActionListener {

	private ResourceAdapter resourceAdapter;

	@Override
	public void actionPerformed(ActionEvent e) {
		AnimatorModel model = UnitStore.instance(AnimatorModel.class);
		String identifier = model.currentPartIdentifier();
		model.setPartAsModel(identifier);

		AnimationPersistent persistent = model.getPersistent();
		FreeTriangleMeshModel freeTriangleMeshModel = persistent.locateMesh(identifier);
		if(freeTriangleMeshModel == null) {
			// Conversion is needed, oh no.
			AnimationPart animationPart = persistent.getAnimation().locatePart(identifier);
			String filename = animationPart.getMesh();
			if (filename == null || filename.length() == 0) {
				throw new RuntimeException("Filename is missing!");
			}

			if(resourceAdapter == null) {
				resourceAdapter = UnitStore.instance(ResourceAdapter.class);
				if(resourceAdapter == null) {
					throw new RuntimeException("Missing component: " + ResourceAdapter.class.getSimpleName());
				}
			}

			if (filename.charAt(0) != '/') {
				filename = resourceAdapter.meshRoot() + filename;
			}

			freeTriangleMeshModel = convertExportedMesh(filename);
			persistent.putMeshModel(identifier, freeTriangleMeshModel);
		}

		if(identifier != null) {
			AnimatorUIComponents components = UnitStore.instance(AnimatorUIComponents.class);
			components.frame.setJMenuBar(AnimatorMenu.MESH_MENU_BAR);

			ComponentRefreshViews refreshViews = UnitStore.instance(ComponentRefreshViews.class);
			refreshViews.refreshAll();
		}
	}

	private FreeTriangleMeshModel convertExportedMesh(String absolutePath) {
		File file = new File(absolutePath);
		byte[] bytes = load(file);
		String string = new String(bytes, UTF_8);

		try {
			JSONObject json = new JSONObject(string);
			GameMesh mesh = new GameMesh();
			mesh.fromJSONObject(json);
			mesh.setTexture(resourceAdapter.cleanUpResourceFilename(mesh.getTexture()));

			FreeTriangleMeshModel meshModel = new FreeTriangleMeshModel();
			meshModel.setTextureFilename(mesh.getTexture());

			FreeTriangleMeshMesh meshMesh = meshModel.getMeshMesh();

			List<FreeTriangleMeshVertex> targetVertices = new ArrayList<>();
			for(GameVertex vertex : mesh.getVertices()) {
				FreeTriangleMeshVertex targetVertex = new FreeTriangleMeshVertex(vertex.getPX(), vertex.getPY(), vertex.getPZ());
				targetVertex.setNX(vertex.getNX());
				targetVertex.setNY(vertex.getNY());
				targetVertex.setNZ(vertex.getNZ());
				targetVertex.setTX(vertex.getTX());
				targetVertex.setTY(vertex.getTY());
				targetVertices.add(targetVertex);
			}

			List<FreeTriangleMeshTriangle> targetTriangles = new ArrayList<>();
			for(GameTriangle triangle : mesh.getTriangles()) {
				FreeTriangleMeshTriangle targetTriangle = new FreeTriangleMeshTriangle(triangle.getV1(), triangle.getV2(), triangle.getV3());
				targetTriangles.add(targetTriangle);
			}

			meshMesh.addTriangles(targetVertices, targetTriangles);
			return meshModel;
		} catch(JSONException ex) {
			throw new RuntimeException("Couldn't parse file: " + absolutePath);
		}
	}

	private byte[] load(File file) {
		if(!file.exists() || file.isDirectory()) {
			throw new RuntimeException("Not exists or not a file: " + file.getAbsolutePath());
		}

		byte[] buffer = new byte[4096];
		try (FileInputStream stream = new FileInputStream(file)) {
			ByteArrayOutputStream output = new ByteArrayOutputStream();

			int len;
			while((len = stream.read(buffer)) > -1) {
				if(len > 0) {
					output.write(buffer, 0, len);
				}
			}

			return output.toByteArray();
		} catch(IOException ex) {
			throw new RuntimeException("Error when loading file: " + file.getAbsolutePath(), ex);
		}
	}

	private static final Charset UTF_8 = Charset.forName("UTF-8");

}
