package hu.csega.editors.ftm.layer1.presentation.swing.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import hu.csega.editors.FreeTriangleMeshToolStarter;
import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshTriangle;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;
import hu.csega.games.engine.GameEngineFacade;

public class FreeTriangleMeshTexture extends FreeTriangleMeshCanvas {

	private int frameWidth = 300;
	private int frameHeight = 300;

	private int imageWidth = 280;
	private int imageHeight = 280;

	private int imageLeft = 10;
	private int imageTop = 10;

	private int originalWidth = imageWidth; // Max value.
	private int originalHeight = imageHeight; // Max value.

	private String textureFilename = null;
	private BufferedImage textureImage = null;
	private boolean triedToLoadImage = false;

	private String textureRoot;

	public FreeTriangleMeshTexture(GameEngineFacade facade, String textureRoot) {
		super(facade);
		this.textureRoot = textureRoot;
	}

	@Override
	protected void paint2d(Graphics2D g) {
		FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
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

		Collection<Object> selectedObjects = model.getSelectedObjects();

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

		Set<FreeTriangleMeshPictogram> pictograms = refreshPictograms(model);
		if(pictograms != null && !pictograms.isEmpty()) {
			for(FreeTriangleMeshPictogram p : pictograms) {
				BufferedImage img = FreeTriangleMeshToolStarter.SPRITES[p.action];
				g.drawImage(img, (int)p.x, (int)p.y, null);
			}
		}
	}

	@Override
	protected void translate(double x, double y) {
		// not applicable
	}

	@Override
	protected void zoom(double delta) {
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
	protected void createVertexAt(EditorPoint p) {
	}

	@Override
	protected void moveSelected(EditorPoint p1, EditorPoint p2) {
		FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();

		double x = p2.getX() - p1.getX();
		double y = p2.getY() - p1.getY();

		int width = (textureImage == null ? imageWidth : Math.min(imageWidth, textureImage.getWidth()));
		int height = (textureImage == null ? imageHeight : Math.min(imageHeight, textureImage.getHeight()));

		double horizontalStretch = 1.0 / width;
		double horizontalMove = x * horizontalStretch;

		double verticalStretch = 1.0 / height;
		double verticalMove = -y * verticalStretch;

		model.moveTexture(horizontalMove, verticalMove);
	}

	@Override
	protected EditorPoint transformVertexToPoint(FreeTriangleMeshVertex vertex) {
		int width = (textureImage == null ? imageWidth : Math.min(imageWidth, textureImage.getWidth()));
		int height = (textureImage == null ? imageHeight : Math.min(imageHeight, textureImage.getHeight()));

		double x = vertex.getTX() * width;
		double y = (1 - vertex.getTY()) * height;
		return new EditorPoint(x, y, 0.0, 1.0);
	}

	@Override
	protected Set<FreeTriangleMeshPictogram> refreshPictograms(FreeTriangleMeshModel model) {
		Collection<Object> selectedObjects = model.getSelectedObjects();
		if(selectedObjects == null || selectedObjects.size() < 2) {
			return null;
		}

		if(pictograms == null || selectionLastChanged < model.getSelectionLastChanged()) {
			selectionLastChanged = model.getSelectionLastChanged();
			pictograms = new HashSet<>();
			int widthDiv2 = lastSize.width / 2;
			int heightDiv2 = lastSize.height / 2;

			selectionMinX = Double.POSITIVE_INFINITY;
			selectionMinY = Double.POSITIVE_INFINITY;
			selectionMaxX = Double.NEGATIVE_INFINITY;
			selectionMaxY = Double.NEGATIVE_INFINITY;

			for(Object obj : selectedObjects) {
				if(obj instanceof FreeTriangleMeshVertex) {
					FreeTriangleMeshVertex v = ((FreeTriangleMeshVertex)obj);
					EditorPoint p = transformVertexToPoint(v);

					double x = p.getX();
					double y = p.getY();
					if(x < selectionMinX) { selectionMinX = x; }
					if(y < selectionMinY) { selectionMinY = y; }
					if(x > selectionMaxX) { selectionMaxX = x; }
					if(y > selectionMaxY) { selectionMaxY = y; }
				}
			}

			pictograms.add(new FreeTriangleMeshPictogram(FreeTriangleMeshPictogram.UP_LEFT_ARROW, selectionMinX + widthDiv2 - 16, selectionMinY + heightDiv2 - 16));
			pictograms.add(new FreeTriangleMeshPictogram(FreeTriangleMeshPictogram.UP_RIGHT_ARROW, selectionMaxX + widthDiv2, selectionMinY + heightDiv2 - 16));
			pictograms.add(new FreeTriangleMeshPictogram(FreeTriangleMeshPictogram.DOWN_LEFT_ARROW, selectionMinX + widthDiv2 - 16, selectionMaxY + heightDiv2));
			pictograms.add(new FreeTriangleMeshPictogram(FreeTriangleMeshPictogram.DOWN_RIGHT_ARROW, selectionMaxX + widthDiv2, selectionMaxY + heightDiv2));
		}

		return pictograms;
	}

	@Override
	protected void pictogramAction(int action, int dx, int dy, EditorPoint started, EditorPoint ended) {
		System.out.println("ACTION: " + action + " dx: " + dx + " dy: " + dy + " Start: " +started + " End: " + ended);

		FreeTriangleMeshModel model = getModel();
		switch(action) {
			case FreeTriangleMeshPictogram.DOWN_RIGHT_ARROW: {
				EditorPoint fixed = lenses.fromScreenToModel(new EditorPoint(selectionMinX, selectionMinY, 0.0, 1.0));
				model.elasticTextureMove(fixed, started, ended);
			} break;
			case FreeTriangleMeshPictogram.UP_LEFT_ARROW: {
				EditorPoint fixed = lenses.fromScreenToModel(new EditorPoint(selectionMaxX, selectionMaxY, 0.0, 1.0));
				model.elasticTextureMove(fixed, started, ended);
			} break;
			case FreeTriangleMeshPictogram.UP_RIGHT_ARROW: {
				EditorPoint fixed = lenses.fromScreenToModel(new EditorPoint(selectionMinX, selectionMaxY, 0.0, 1.0));
				model.elasticTextureMove(fixed, started, ended);
			} break;
			case FreeTriangleMeshPictogram.DOWN_LEFT_ARROW: {
				EditorPoint fixed = lenses.fromScreenToModel(new EditorPoint(selectionMaxX, selectionMinY, 0.0, 1.0));
				model.elasticTextureMove(fixed, started, ended);
			} break;
		}
	}

	private static final long serialVersionUID = 1L;
}
