package hu.csega.games.library.pixel.v1;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PixelLibrary extends ArrayList<PixelSheet> {

	public PixelLibrary(int size) {
		super(size);
		for(int i = 0; i < size; i++) {
			super.add(null);
		}

		used = new boolean[size];
	}

	public static PixelLibrary load(File f) {
		try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(f))) {
            return (PixelLibrary) stream.readObject();
	    } catch(Exception ex) {
	        throw new RuntimeException("Loading failed!", ex);
	    }
	}

	public static PixelLibrary load(Class<?> neighborClass, String fileName) {
		try (ObjectInputStream stream = new ObjectInputStream(neighborClass.getResourceAsStream(fileName))) {
            return (PixelLibrary) stream.readObject();
	    } catch(Exception ex) {
	        throw new RuntimeException("Loading failed!", ex);
	    }
	}

    public void save(File f) {
        try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(f))) {
            stream.writeObject(this);
        } catch(Exception ex) {
            throw new RuntimeException("Saving failed!", ex);
        }
    }

	public void generateJS(File f) {
		int counter = 0;
		Map<String, Integer> map = new HashMap<>();

		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(f))) {
			writer.write("var pal = []; // palette\n");

			// 1st round: Construct palette
			for(int i = 0; i < size(); i++) {
				if(used[i]) {
					PixelSheet sheet = get(i);
					int minx = 0;
					int maxx = PixelSheet.WIDTH - 1;
					int miny = 0;
					int maxy = PixelSheet.HEIGHT - 1;

					for (int y = miny; y <= maxy; y++) {
						for (int x = minx; x <= maxx; x++) {
							Pixel pixel = sheet.pixels[x][y];
							String key = pixel.toString();
							if(!map.containsKey(key)) {
								String js = pixel.toJSConstruct();
								int value = counter++;
								map.put(key, value);
								writer.write("pal[" + value + "] = " + js + ";\n");
							}
						}
					}
				}
			} // enf dor i


			// 2nd round: List only used pixels
			writer.write("\nvar img = [];\n");

			for(int i = 0; i < size(); i++) {
				if(used[i]) {
					writer.flush();
					writer.write("img[" + i + "] = [\n");

					PixelSheet sheet = get(i);
					int minx = 0;
					int maxx = PixelSheet.WIDTH - 1;
					int miny = 0;
					int maxy = PixelSheet.HEIGHT - 1;

					boolean first = true;

					for (int y = miny; y <= maxy; y++) {
						writer.write("[");
						for (int x = minx; x <= maxx; x++) {
							Pixel pixel = sheet.pixels[x][y];
							String key = pixel.toString();
							writer.write(String.valueOf(map.get(key)));
							if(x < maxx)
								writer.write(",");
						}

						writer.write("]");
						if(y < maxy)
							writer.write(",");
						writer.write("\n");
					}

					writer.write("];\n");
					writer.flush();
				}
			}
		} catch(Exception ex) {
			throw new RuntimeException("Saving failed!", ex);
		}
	}

    public void setUsed(int i) {
    	used[i] = true;
    }

    public BufferedImage[] convertToImages() {
    	return convertToImages(1, PixelSpriteResizePolicy.NONE);
    }

    public BufferedImage[] convertToImages(int zoom, PixelSpriteResizePolicy policy) {
    	if(zoom < 1)
    		throw new IllegalArgumentException("Zoom must be greater or equal to 1!");

    	BufferedImage[] ret = new BufferedImage[size()];

    	for(int i = 0; i < size(); i++) {
    		if(used[i]) {

    			PixelSheet sheet = get(i);

    			int minx = 0;
    			int maxx = PixelSheet.WIDTH - 1;
    			int miny = 0;
    			int maxy = PixelSheet.HEIGHT - 1;

    			if(policy == PixelSpriteResizePolicy.ONLY_USED_AREA) {
        			minx = PixelSheet.WIDTH - 1;
        			maxx = 0;
        			miny = PixelSheet.HEIGHT - 1;
        			maxy = 0;

    				for(int y = 0; y < PixelSheet.HEIGHT; y++){
      				  for(int x = 0; x < PixelSheet.WIDTH; x++){

      					  Pixel pixel = sheet.pixels[x][y];
      					  if(pixel.alpha > 0) {
      						  minx = Math.min(minx, x);
      						  maxx = Math.max(maxx, x);
      						  miny = Math.min(miny, y);
      						  maxy = Math.max(maxy, y);
      					  }

      				  }
    				}

    				if(minx > maxx || miny > maxy)
    					continue; // not used after all

    			} // end if policy == PixelSpriteResizePolicy.ONLY_USED_AREA

    			BufferedImage image = new BufferedImage(PixelSheet.WIDTH * zoom, PixelSheet.HEIGHT * zoom, BufferedImage.TYPE_INT_ARGB);

    			for(int y = miny; y <= maxy; y++){
    				  for(int x = minx; x <= maxx; x++){

    					  Pixel pixel = sheet.pixels[x][y];
    					  int p = (pixel.alpha << 24) | (pixel.red << 16) | (pixel.green << 8) | pixel.blue;

    					  for(int yy = 0; yy < zoom; yy ++)
    						  for(int xx = 0; xx < zoom; xx ++)
    							  image.setRGB(x * zoom + xx, y * zoom + yy, p);

    				  }
    				}

    			ret[i] = image;
    		}
    	}

    	return ret;
    }

    @Override
    public PixelSheet get(int index) {
    	PixelSheet ret = super.get(index);
    	if(ret == null) {
    		ret = new PixelSheet();
    		super.set(index, ret);
    	}

    	return ret;
    }

	@Override
	public void add(int index, PixelSheet element) {
		throw new UnsupportedOperationException("add");
	}

	@Override
	public boolean add(PixelSheet element) {
		throw new UnsupportedOperationException("add");
	}

	@Override
	public PixelSheet set(int index, PixelSheet element) {
		throw new UnsupportedOperationException("set");
	}

	private boolean[] used;

	private static final long serialVersionUID = 1L;
}
