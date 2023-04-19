package hu.csega.editors.library;

import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.games.library.reference.STextureRef;
import hu.csega.games.units.Dependency;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImageLibrary {

	private ResourceAdapter resourceAdapter;
	private String projectMarker;
	private String textureFolder;
	private Map<STextureRef, BufferedImage> textures;

	public ImageLibrary() {
		this.textures = new HashMap<>();
	}

	@Dependency
	public void setResourceAdapter(ResourceAdapter resourceAdapter) {
		this.resourceAdapter = resourceAdapter;
		this.projectMarker = '/' + resourceAdapter.projectName() + '/';
		this.textureFolder = resourceAdapter.textureFolder();
	}

	public STextureRef referenceOf(String path) {
		String name = null;
		if(path.startsWith(textureFolder)) {
			name = path.substring(textureFolder.length());
		} else if(path.charAt(0) == '/') {
			int index = path.indexOf(projectMarker);
			if(index > 0) {
				name = path.substring(index + projectMarker.length());
			}
		} else {
			name = path;
		}

		return (name == null ? null : new STextureRef(name));
	}

	public BufferedImage resolve(STextureRef ref) {
		return textures.computeIfAbsent(ref, r -> {
			String filename = ref.getName().replaceAll("/", File.separator);
			File file = new File(textureFolder + filename);

			try {
				return ImageIO.read(file);
			} catch (IOException ex) {
				logger.warning("Could not read file: " + file.getAbsolutePath(), ex);
				return null;
			}
		});
	}

	private static final Logger logger = LoggerFactory.createLogger(ImageLibrary.class);
}