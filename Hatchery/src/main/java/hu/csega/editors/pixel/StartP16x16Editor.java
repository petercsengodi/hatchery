package hu.csega.editors.pixel;

import hu.csega.editors.pixel.p32x32.P32x32Editor;

public class StartP16x16Editor {

	public static final String PIX_FILE = "tmp.p16";
	public static final String GENERATED_FILE = "generated16x16.js";
	public static final int MAXIMUM_NUMBER_OF_SHEETS = 100;

	public static void main(String[] args) {
		P32x32Editor editor = new P32x32Editor(PIX_FILE, GENERATED_FILE, MAXIMUM_NUMBER_OF_SHEETS, 16, 16);
		editor.startEditor();
	}

}
