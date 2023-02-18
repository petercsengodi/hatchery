package hu.csega.games.library;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

import hu.csega.editors.FreeTriangleMeshToolStarter;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshSnapshots;
import hu.csega.editors.ftm.layer5.integration.FileSystemIntegration;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.reference.SMeshRef;
import hu.csega.games.library.mesh.v1.xml.SMesh;
import hu.csega.games.library.util.FileUtil;
import hu.csega.games.library.xml.v1.XmlReader;

public class MeshLibrary {

	private Map<SMeshRef, Object> meshes;
	private String root;

	public MeshLibrary(String root) {
		this.root = root;

		List<String> ret = new ArrayList<>();
		FileUtil.collectFiles(root, ret);

		this.meshes = new HashMap<>();
		for(String filename : ret) {
			String defaultName = FileUtil.cleanUpName(filename);
			Object mesh = load(filename, defaultName);
			SMeshRef key = new SMeshRef(defaultName);
			meshes.put(key, mesh);
		}
	}

	public Object resolve(SMeshRef ref) {
		return meshes.get(ref);
	}

	private static Object load(String filename, String defaultName) {
		if(filename.endsWith(".ftm")) {
			File file = new File(filename);
			byte[] serialized = FreeTriangleMeshSnapshots.readAllBytes(file);
			FreeTriangleMeshModel model = (FreeTriangleMeshModel) FileSystemIntegration.deserialize(serialized);
			if (model == null) {
				model = new FreeTriangleMeshModel();
				model.setTextureFilename(FreeTriangleMeshToolStarter.DEFAULT_TEXTURE_FILE);
			}

			return model;
		} else if(filename.endsWith(".json")) {
			File file = new File(filename.replace(".json", ".ftm"));
			byte[] serialized = FreeTriangleMeshSnapshots.readAllBytes(file);
			FreeTriangleMeshModel model = (FreeTriangleMeshModel) FileSystemIntegration.deserialize(serialized);
			if (model == null) {
				model = new FreeTriangleMeshModel();
				model.setTextureFilename(FreeTriangleMeshToolStarter.DEFAULT_TEXTURE_FILE);
			}

			return model;
		} else {
			SMesh mesh;
			try {
				mesh = (SMesh) XmlReader.read(filename);
			} catch (IOException | SAXException ex) {
				throw new RuntimeException("Could not read file: " + filename, ex);
			}

			return mesh;
		}
	}

}