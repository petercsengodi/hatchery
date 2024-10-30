package hu.csega.editors.ftm.layer1.presentation.swing.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.Set;

import javax.swing.JPanel;

import hu.csega.editors.common.lens.EditorLensPipeline;
import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.intf.GameCanvas;
import hu.csega.games.engine.intf.GameWindow;

public abstract class FreeTriangleMeshCanvas extends JPanel implements GameCanvas, MouseListener, MouseMotionListener, MouseWheelListener {

	protected GameEngineFacade facade;
	protected Set<FreeTriangleMeshPictogram> pictograms;

	public static final Dimension PREFERRED_SIZE = new Dimension(400, 300);
	protected Dimension lastSize = new Dimension(PREFERRED_SIZE.width, PREFERRED_SIZE.height);
	protected int PICT_SIZE_X = 16;
	protected int PICT_SIZE_Y = 16;

	public static final double[] ZOOM_VALUES = { 0.0001, 0.001, 0.01, 0.1, 0.2, 0.3, 0.5, 0.75, 1.0, 1.25, 1.50, 2.0, 3.0, 4.0, 5.0, 10.0, 100.0 };
	public static final int DEFAULT_ZOOM_INDEX = 8;

	private BufferedImage buffer = null;

	private boolean mouseLeftPressed = false;
	private boolean mouseRightPressed = false;
	private Point mouseLeftStarted = new Point(0, 0);
	private Point mouseLeftAt = new Point(0, 0);
	private Point mouseRightAt = new Point(0, 0);

	protected long selectionLastChanged = -1L;
	protected double selectionMinX;
	protected double selectionMinY;
	protected double selectionMaxX;
	protected double selectionMaxY;

	private boolean selectionBoxEnabled = false;
	private Point selectionStart = new Point();
	private Point selectionEnd = new Point();
	private Rectangle selectionBox = new Rectangle();
	private int selectedPictogramAction = -1;

	protected EditorLensPipeline lenses = new EditorLensPipeline();
	protected int zoomIndex = DEFAULT_ZOOM_INDEX;

	public FreeTriangleMeshCanvas(GameEngineFacade facade) {
		this.facade = facade;
		setPreferredSize(PREFERRED_SIZE);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);

		GameWindow window = facade.window();
		KeyListener keyListener = (KeyListener) window;
		addKeyListener(keyListener);
	}

	public Component getRealCanvas() {
		return this;
	}

	protected void somethingChanged() {
		selectionLastChanged = -1L;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	@Override
	public void paint(Graphics g) {
		Dimension size = getSize();
		if(buffer == null || !lastSize.equals(size)) {
			buffer = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
			lastSize.width = size.width;
			lastSize.height = size.height;
		}

		Graphics2D g2d = (Graphics2D)buffer.getGraphics();
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillRect(0, 0, lastSize.width, lastSize.height);
		g2d.setColor(Color.black);

		paint2d(g2d);

		g.drawImage(buffer, 0, 0, null);
	}

	public void repaintEverything() {
		facade.window().repaintEverything();
	}

	protected abstract void translate(double x, double y);

	protected abstract void zoom(double delta);

	protected abstract void selectAll(EditorPoint topLeft, EditorPoint bottomRight, boolean add);

	protected abstract void selectFirst(EditorPoint p, double radius, boolean add);

	protected abstract void createVertexAt(EditorPoint p);

	protected abstract void moveSelected(EditorPoint p1, EditorPoint p2);

	protected abstract EditorPoint transformVertexToPoint(FreeTriangleMeshVertex vertex);

	protected FreeTriangleMeshModel getModel() {
		FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
		return model;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point p = new Point(e.getX(), e.getY());

		if(mouseLeftPressed) {
			if(selectionBoxEnabled) {
				int dx = mouseLeftAt.x - p.x;
				int dy = mouseLeftAt.y - p.y;
				selectionEnd.x -= dx;
				selectionEnd.y -= dy;
				repaint();
			} else if(e.isControlDown()) {
				EditorPoint moveFrom = transformToModel(mouseLeftAt.x, mouseLeftAt.y);
				EditorPoint moveTo = transformToModel(p.x, p.y);
				moveSelected(moveFrom, moveTo);
				repaintEverything();
			}
			mouseLeftAt.x = p.x;
			mouseLeftAt.y = p.y;
		}

		if(mouseRightPressed) {
			int dx = p.x - mouseRightAt.x;
			int dy = p.y - mouseRightAt.y;
			translate(dx, dy);
			pictograms = null;
			repaint();
			mouseRightAt.x = p.x;
			mouseRightAt.y = p.y;
		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Point p = new Point(e.getX(), e.getY());

		if(mouseRightPressed) {
			translate(p.x - mouseRightAt.x, p.y - mouseRightAt.y);
			mouseRightAt.x = p.x;
			mouseRightAt.y = p.y;
			repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == 1) {
			mouseLeftPressed = true;
			mouseLeftStarted = new Point(e.getX(), e.getY());
			mouseLeftAt = new Point(e.getX(), e.getY());
			selectedPictogramAction = -1;
			if(!e.isControlDown()) {
				if(pictograms != null && !pictograms.isEmpty()) {
					for (FreeTriangleMeshPictogram pictogram : pictograms) {
						if (mouseLeftAt.x >= pictogram.x && mouseLeftAt.x < pictogram.x + PICT_SIZE_X &&
								mouseLeftAt.y >= pictogram.y && mouseLeftAt.y < pictogram.y + PICT_SIZE_Y) {
							selectedPictogramAction = pictogram.action;
						}
					}
				}

				if(selectedPictogramAction < 0) {
					selectionBoxEnabled = true;
					selectionStart.x = selectionEnd.x = mouseLeftAt.x;
					selectionStart.y = selectionEnd.y = mouseLeftAt.y;
				}
			}
		}

		if(e.getButton() == 3) {
			mouseRightPressed = true;
			mouseRightAt = new Point(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == 1) {
			mouseLeftPressed = false;
			if(selectionBoxEnabled) {
				calculateSelectionBox();
				EditorPoint p1 = transformToModel(selectionStart.x, selectionStart.y);
				EditorPoint p2 = transformToModel(selectionEnd.x, selectionEnd.y);
				selectAll(p1, p2, e.isShiftDown());
				selectionBoxEnabled = false;
				repaintEverything();
			}

			if(selectedPictogramAction >= 0) {
				int action = selectedPictogramAction;
				int dx = e.getX() - mouseLeftStarted.x;
				int dy = e.getY() - mouseLeftStarted.y;
				EditorPoint started = transformToModel(mouseLeftStarted.x, mouseLeftStarted.y);
				EditorPoint ended = transformToModel(mouseLeftAt.x, mouseLeftAt.y);
				mouseLeftStarted = null;
				mouseLeftAt = null;
				selectedPictogramAction = -1;
				pictogramAction(action, dx, dy, started, ended);
				repaintEverything();
			}

			FreeTriangleMeshModel model = getModel();
			model.finalizeMove();
		} else if(e.getButton() == 3) {
			mouseRightPressed = false;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(e.isControlDown()) {
				// create new vertex
				EditorPoint p = transformToModel(e.getX(), e.getY());
				createVertexAt(p);
				repaintEverything();
			} else {
				// select one vertex
				EditorPoint p = transformToModel(e.getX(), e.getY());
				selectFirst(p, 5, e.isShiftDown());
				repaintEverything();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int numberOfRotations = e.getWheelRotation();
		zoomIndex -= numberOfRotations;
		if(zoomIndex < 0)
			zoomIndex = 0;
		else if(zoomIndex >= ZOOM_VALUES.length)
			zoomIndex = ZOOM_VALUES.length - 1;
		lenses.setScale(ZOOM_VALUES[zoomIndex]);
		repaint();
	}

	protected Rectangle calculateSelectionBox() {
		if(selectionBoxEnabled) {
			selectionBox.x = Math.min(selectionStart.x, selectionEnd.x);
			selectionBox.y = Math.min(selectionStart.y, selectionEnd.y);
			selectionBox.width = Math.abs(selectionStart.x - selectionEnd.x);
			selectionBox.height = Math.abs(selectionStart.y - selectionEnd.y);
			return selectionBox;
		} else {
			return null;
		}
	}

	protected abstract Set<FreeTriangleMeshPictogram> refreshPictograms(FreeTriangleMeshModel model);

	protected abstract void pictogramAction(int action, int dx, int dy, EditorPoint started, EditorPoint ended);

	private EditorPoint transformToModel(int x, int y) {
		int widthDiv2 = lastSize.width / 2;
		int heightDiv2 = lastSize.height / 2;
		return lenses.fromScreenToModel(x - widthDiv2, y - heightDiv2);
	}

	protected double distance(double x1, double y1, double x2, double y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		return Math.sqrt(dx*dx + dy*dy);
	}

	protected abstract void paint2d(Graphics2D g);

	private static final long serialVersionUID = 1L;
}
