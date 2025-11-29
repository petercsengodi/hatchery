package hu.csega.editors.anm.layer1Views.swing.views;

import hu.csega.editors.FreeTriangleMeshToolStarter;
import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.ftm.layer1.presentation.swing.view.FreeTriangleMeshPictogram;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshTriangle;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

public class AnimatorMeshTextureView extends AnimatorView {

	private final int frameWidth = 300;
	private final int frameHeight = 300;

	private final int imageWidth = 280;
	private final int imageHeight = 280;

	private final int imageLeft = 10;
	private final int imageTop = 10;

	private int originalWidth = imageWidth; // Max value.
	private int originalHeight = imageHeight; // Max value.

	private String textureFilename = null;
	private BufferedImage textureImage = null;
	private boolean triedToLoadImage = false;

	private String textureRoot;

	public AnimatorMeshTextureView(GameEngineFacade facade, AnimatorViewCanvas canvas, String textureRoot) {
		super(facade, canvas);
		this.textureRoot = textureRoot;
	}

	@Override
	public String label() {
		return "Texture";
	}

	@Override
	protected void paintView(Graphics2D g, int width, int height) {
		FreeTriangleMeshModel model = getModel(FreeTriangleMeshModel.class);
		if(model == null)
			return;

		String textureFilenameInModel = model.getTextureFilename();
		if(textureFilenameInModel == null || textureFilenameInModel.isEmpty())
			textureFilenameInModel = FreeTriangleMeshToolStarter.DEFAULT_TEXTURE_FILE;

		if(textureFilename == null || !textureFilename.equals(textureFilenameInModel)) {
			textureFilename = textureFilenameInModel;
			triedToLoadImage = false;
			textureImage = null;
		}

		if(textureImage == null) {
			if(triedToLoadImage) {
				// ...
			} else {
				triedToLoadImage = true;
				try {
					textureImage = ImageIO.read(new File(textureRoot + textureFilename));

					double originalWidth = textureImage.getWidth();
					double originalHeight = Math.min(textureImage.getHeight(), imageHeight);
					if(originalWidth < imageWidth && originalHeight < imageHeight) {
						this.originalWidth = (int) originalWidth;
						this.originalHeight = (int) originalHeight;
					} else if(originalWidth >= originalHeight) {
						this.originalWidth = (int) originalWidth;
						this.originalHeight = (int) (originalHeight * imageWidth / originalWidth);
					} else {
						this.originalHeight = (int) originalHeight;
						this.originalWidth = (int) (originalWidth * imageHeight / originalHeight);
					}

				} catch(Exception ex) {
					textureImage = null;
				}
			}
		}

		if(textureImage != null) {
			g.drawImage(textureImage, imageLeft, imageTop, imageWidth, imageHeight, null);
		}

		Collection<AnimatorObject> selectedObjects = model.getSelectedObjects();

		List<FreeTriangleMeshVertex> vertices = model.getVertices();
		List<FreeTriangleMeshTriangle> triangles = model.getTriangles();

		g.setColor(Color.darkGray);
		for(FreeTriangleMeshTriangle triangle : triangles) {
			if(model.enabled(triangle)) {
				FreeTriangleMeshVertex v1 = vertices.get(triangle.getVertex1());
				FreeTriangleMeshVertex v2 = vertices.get(triangle.getVertex2());
				FreeTriangleMeshVertex v3 = vertices.get(triangle.getVertex3());

				if(selectedObjects.contains(v1) || selectedObjects.contains(v2) || selectedObjects.contains(v3)) {
					EditorPoint p1 = transformVertexToPoint(v1);
					EditorPoint p2 = transformVertexToPoint(v2);
					EditorPoint p3 = transformVertexToPoint(v3);
					g.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
					g.drawLine((int)p2.getX(), (int)p2.getY(), (int)p3.getX(), (int)p3.getY());
					g.drawLine((int)p3.getX(), (int)p3.getY(), (int)p1.getX(), (int)p1.getY());
				}
			}
		}

		g.setColor(Color.red);
		for(Object object : selectedObjects) {

			if(object instanceof FreeTriangleMeshVertex) {
				FreeTriangleMeshVertex vertex = (FreeTriangleMeshVertex) object;
				if(model.enabled(vertex)) {
					EditorPoint p = transformVertexToPoint(vertex);
					g.drawRect((int)p.getX() - 2, (int)p.getY() - 2, 5, 5);
				}
			}
		}
	}

	@Override
	protected EditorPoint transformToScreen(EditorPoint p) {
		return new EditorPoint(imageWidth * p.getX() + imageLeft, imageHeight * (1 - p.getY()) + imageTop, 0.0, 1.0);
	}

	@Override
	protected EditorPoint transformToModel(double x, double y) {
		return new EditorPoint((x - imageLeft) / (double) imageWidth, 1.0 - (y - imageTop) / (double) imageHeight, 0.0, 1.0);
	}

	@Override
	protected void translate(double x, double y) {
		// not applicable
	}

	@Override
	protected void selectAll(EditorPoint topLeft, EditorPoint bottomRight, boolean add) {
		// not applicable
	}

	@Override
	protected void selectFirst(EditorPoint p, double radius, boolean add) {
		// not applicable
	}

	@Override
	protected void moveSelected(EditorPoint p1, EditorPoint p2) {
		FreeTriangleMeshModel model = getModel(FreeTriangleMeshModel.class);
		if(model == null)
			return;

		model.moveTexture(p2.getX() - p1.getX(), p2.getY() - p1.getY());
	}

	protected EditorPoint transformVertexToPoint(FreeTriangleMeshVertex vertex) {
		int width = (textureImage == null ? imageWidth : Math.min(imageWidth, textureImage.getWidth()));
		int height = (textureImage == null ? imageHeight : Math.min(imageHeight, textureImage.getHeight()));

		double x = vertex.getTX() * width + imageLeft;
		double y = (1 - vertex.getTY()) * height + imageTop;
		return new EditorPoint(x, y, 0.0, 1.0);
	}

	@Override
	protected void generatePictograms(int numberOfSelectedItems, int selectionMinX, int selectionMinY, int selectionMaxX, int selectionMaxY, Set<FreeTriangleMeshPictogram> pictograms) {
		if(numberOfSelectedItems > 1) {
			pictograms.add(new FreeTriangleMeshPictogram(FreeTriangleMeshPictogram.UP_LEFT_ARROW, selectionMinX - 16, selectionMinY - 16));
			pictograms.add(new FreeTriangleMeshPictogram(FreeTriangleMeshPictogram.UP_RIGHT_ARROW, selectionMaxX, selectionMinY - 16));
			pictograms.add(new FreeTriangleMeshPictogram(FreeTriangleMeshPictogram.DOWN_LEFT_ARROW, selectionMinX - 16, selectionMaxY));
			pictograms.add(new FreeTriangleMeshPictogram(FreeTriangleMeshPictogram.DOWN_RIGHT_ARROW, selectionMaxX, selectionMaxY));
		}
	}

	@Override
	protected void pictogramAction(int action, int dx, int dy, EditorPoint started, EditorPoint ended, Rectangle selection) {
		FreeTriangleMeshModel model = getModel(FreeTriangleMeshModel.class);
		if(model == null)
			return;

		switch(action) {
			case FreeTriangleMeshPictogram.DOWN_RIGHT_ARROW: {
				EditorPoint fixed = transformToModel(selection.getX(), selection.getY());
				if(fixed != null) {
					model.elasticTextureMove(fixed, started, ended);
				}
			} break;
			case FreeTriangleMeshPictogram.UP_LEFT_ARROW: {
				EditorPoint fixed = transformToModel(selection.getX() + selection.getWidth(), selection.getY() + selection.getHeight());
				if(fixed != null) {
					model.elasticTextureMove(fixed, started, ended);
				}
			} break;
			case FreeTriangleMeshPictogram.UP_RIGHT_ARROW: {
				EditorPoint fixed = transformToModel(selection.getX(), selection.getY() + selection.getHeight());
				if(fixed != null) {
					model.elasticTextureMove(fixed, started, ended);
				}
			} break;
			case FreeTriangleMeshPictogram.DOWN_LEFT_ARROW: {
				EditorPoint fixed = transformToModel(selection.getX() + selection.getWidth(), selection.getY());
				if(fixed != null) {
					model.elasticTextureMove(fixed, started, ended);
				}
			} break;
		}
	}

}
