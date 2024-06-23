package hu.csega.text;

import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshSnapshots;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

public class CustomCharset {

	public static final int CHARACTERS = 256;
	public static final int WIDTH = 9;
	public static final int HEIGHT = 13;

	public static CustomCharset load() {
		byte[] bytes = FreeTriangleMeshSnapshots.readAllBytes(CustomCharset.class.getResourceAsStream("charsets.dat"));
		return new CustomCharset(bytes);
	}

	public CustomCharset(byte[] charsetBytes) {
		if(charsetBytes.length < WIDTH * HEIGHT * CHARACTERS) {
			logger.warning("CustomCharset is initialized with a byte array of invalid length.");
			return;
		}

		int cursor = 0;
		for(int c = 0; c < CustomCharset.CHARACTERS; c++) {
			for(int y = 0; y < CustomCharset.HEIGHT; y++) {
				for(int x = 0; x < CustomCharset.WIDTH; x++) {
					content[c][y][x] = (charsetBytes[cursor++] & 0xFF);
				}
			}
		}

	} // end of ctr

	public final int[][][] content = new int[CHARACTERS][HEIGHT][WIDTH];

	private static final Logger logger = LoggerFactory.createLogger(FreeTriangleMeshSnapshots.class);
}
