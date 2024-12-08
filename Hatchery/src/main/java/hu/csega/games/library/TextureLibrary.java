package hu.csega.games.library;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import hu.csega.games.library.reference.STextureRef;
import hu.csega.games.library.util.FileUtil;

public class TextureLibrary {

	private String root;
	private boolean texturesPreloaded;
	private Map<STextureRef, BufferedImage> textures;

	public TextureLibrary(String root) {
		this.root = root;
		this.textures = new HashMap<>();
	}

	public void preloadTextures() {
		List<String> ret = new ArrayList<>();
		FileUtil.collectFiles(root, ret);

		for(String fileName : ret) {
			STextureRef key = new STextureRef();
			key.setName(FileUtil.cleanUpName(fileName));
			BufferedImage value = load(root, fileName);
			this.textures.put(key, value);
		}

		this.texturesPreloaded = true;
	}

	public BufferedImage resolve(STextureRef ref) {
		BufferedImage bufferedImage = textures.get(ref);
		if(bufferedImage != null)
			return bufferedImage;

		if(texturesPreloaded)
			throw new RuntimeException("Textures are preloaded, but this one is missing: " + ref);

		BufferedImage value = load(root, ref.getName());
		this.textures.put(ref, value);
		return value;
	}

	private static BufferedImage load(String root, String filename) {
		String fn;
		if(filename.charAt(0) == '/') {
			fn = filename;
		} else {
			fn = root + '/' + filename;
		}

		BufferedImage bufferedImage;
		try {
			bufferedImage = ImageIO.read(new File(fn));
		} catch (IOException ex) {
			throw new RuntimeException("Could not read: " + fn, ex);
		}

		return bufferedImage;
	}

}