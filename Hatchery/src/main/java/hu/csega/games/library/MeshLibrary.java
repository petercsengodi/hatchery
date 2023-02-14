package hu.csega.games.library;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

import hu.csega.games.library.reference.SMeshRef;
import hu.csega.games.library.mesh.v1.xml.SMesh;
import hu.csega.games.library.util.FileUtil;
import hu.csega.games.library.xml.v1.XmlReader;

public class MeshLibrary {

	private String root;

	public MeshLibrary(String root) {
		this.root = root;

		List<String> ret = new ArrayList<>();
		FileUtil.collectFiles(root, ret);

		this.meshes = new HashMap<>();
		for(String fileName : ret) {
			String defaultName = FileUtil.cleanUpName(fileName);
			SMesh mesh = load(fileName, defaultName);
			SMeshRef key = new SMeshRef(mesh.getName());
			meshes.put(key, mesh);
		}
	}

	public SMesh resolve(SMeshRef ref) {
		return meshes.get(ref);
	}

	private static SMesh load(String fileName, String defaultName) {
		SMesh mesh;
		try {
			mesh = (SMesh) XmlReader.read(fileName);
		} catch (IOException | SAXException ex) {
			throw new RuntimeException(ex);
		}

		return mesh;
	}

	private Map<SMeshRef, SMesh> meshes;
}