package hu.csega.editors.pixel;

import hu.csega.editors.pixel.original.PixelEditor;

public class StartOriginalEditor {

	public static final String PIX_FILE = "tmp.pix";
	public static final int MAXIMUM_NUMBER_OF_SHEETS = 100;

	public static void main(String[] args) {
		PixelEditor editor = new PixelEditor(PIX_FILE, MAXIMUM_NUMBER_OF_SHEETS);
		editor.startEditor();
	}

}
