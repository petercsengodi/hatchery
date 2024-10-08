package hu.csega.games.library.pixel.v1;

import java.awt.Color;
import java.io.Serializable;

public class Pixel implements Serializable {

	public int red;
	public int green;
	public int blue;
	public int alpha;

	public Color convertToColor() {
		Color color = new Color(red, green, blue, alpha);
		return color;
	}

	public void copyValuesInto(Pixel pixel) {
		pixel.red = this.red;
		pixel.green = this.green;
		pixel.blue = this.blue;
		pixel.alpha = this.alpha;

	}

	public Pixel copy() {
		Pixel ret = new Pixel();
		copyValuesInto(ret);
		return ret;
	}

	public String toJSConstruct() {
		// HACK: In reality 255 means fully transparent, 0 means fully opaque.
		int opacity = (alpha >= 128 ? 0 : 255);
		return "{ \"r\": " + red +", \"g\": " + green + ", \"b\": " + blue + ", \"a\": " + opacity + " }";
	}

	@Override
	public String toString() {
		return "R:"+red+"|G:"+green+"|B:"+blue+"|A:"+alpha;
	}

	private static final long serialVersionUID = 1L;
}
