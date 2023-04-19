package hu.csega.games.library;

import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.games.library.animation.v1.xml.SAnimation;
import hu.csega.games.library.reference.SAnimationRef;
import hu.csega.games.library.util.FileUtil;
import hu.csega.games.library.xml.v1.XmlReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

public class AnimationLibrary {

	private ResourceAdapter resourceAdapter;

	public AnimationLibrary(ResourceAdapter resourceAdapter) {
		this.resourceAdapter = resourceAdapter;

		String animsPath = resourceAdapter.animationFolder();
		List<String> ret = new ArrayList<>();
		FileUtil.collectFiles(animsPath, ret);

		this.animations = new HashMap<>();
		for(String fileName : ret) {
			String defaultName = FileUtil.cleanUpName(fileName);
			SAnimation animation = load(fileName, defaultName);
			SAnimationRef key = new SAnimationRef(animation.getName());
			this.animations.put(key, animation);
		}
	}

	public SAnimation resolve(SAnimationRef ref) {
		return animations.get(ref);
	}

	private static SAnimation load(String fileName, String defaultName) {
		SAnimation animation;
		try {
			animation = (SAnimation) XmlReader.read(fileName);
		} catch (IOException | SAXException ex) {
			throw new RuntimeException(ex);
		}

		return animation;
	}

	private Map<SAnimationRef, SAnimation> animations;
}