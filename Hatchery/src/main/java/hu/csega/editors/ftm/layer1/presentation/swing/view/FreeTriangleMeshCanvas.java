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
import hu.csega.editors.ftm.layer1.presentation.swing.menu.FreeTriangleMeshContextMenu;
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

	private BufferedImage buffer = null;

	protected boolean mouseLeftPressed = false;
	protected boolean mouseRightPressed = false;
	protected final Point mouseLeftStarted = new Point(0, 0);
	protected final Point mouseLeftAt = new Point(0, 0);
	protected final Point mouseRightAt = new Point(0, 0);
	protected final Point trackedMousePosition = new Point(0, 0);

	protected long selectionLastChanged = -1L;
	protected int selectionMinX;
	protected int selectionMinY;
	protected int selectionMaxX;
	protected int selectionMaxY;

	private boolean selectionBoxEnabled = false;
	private Point selectionStart = new Point();
	private Point selectionEnd = new Point();
	private Rectangle selectionBox = new Rectangle();
	private int selectedPictogramAction = -1;

	protected EditorLensPipeline lenses = new EditorLensPipeline();
	protected int zoomIndex = FreeTriangleMeshModel.DEFAULT_ZOOM_INDEX;

	protected final FreeTriangleMeshContextMenu contextMenu;

	public FreeTriangleMeshCanvas(GameEngineFacade facade) {
		this.facade = facade;
		setPreferredSize(PREFERRED_SIZE);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);

		GameWindow window = facade.window();
		KeyListener keyListener = (KeyListener) window;
		addKeyListener(keyListener);

		this.contextMenu = new FreeTriangleMeshContextMenu(this);
	}

	public Component getRealCanvas() {
		return this;
	}

	public GameEngineFacade getFacade() {
		return facade;
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

	protected abstract EditorPoint transformToScreen(EditorPoint p);

	/**
	 * @return May be null, as not all views can transform to model.
	 */
	protected abstract EditorPoint transformToModel(int x, int y);

	protected abstract void translate(double x, double y);

	protected abstract void selectAll(EditorPoint topLeft, EditorPoint bottomRight, boolean add);

	protected abstract void selectFirst(EditorPoint p, double radius, boolean add);

	protected abstract void createVertexAt(EditorPoint p);

	protected abstract void moveSelected(EditorPoint p1, EditorPoint p2);

	protected abstract EditorPoint transformVertexToPoint(FreeTriangleMeshVertex vertex);

	protected FreeTriangleMeshModel getModel() {
		return (FreeTriangleMeshModel) facade.model();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();

		if(mouseLeftPressed) {
			if(selectionBoxEnabled && valid(mouseLeftAt)) {
				int dx = mouseLeftAt.x - trackedMousePosition.x;
				int dy = mouseLeftAt.y - trackedMousePosition.y;
				selectionEnd.x -= dx;
				selectionEnd.y -= dy;
				repaint();
			} else if(e.isControlDown() && valid(mouseLeftAt)) {
				EditorPoint moveFrom = transformToModel(mouseLeftAt.x, mouseLeftAt.y);
				EditorPoint moveTo = transformToModel(trackedMousePosition.x, trackedMousePosition.y);
				if(moveFrom != null && moveTo != null) {
					moveSelected(moveFrom, moveTo);
					repaintEverything();
				}
			}

			mouseLeftAt.x = trackedMousePosition.x;
			mouseLeftAt.y = trackedMousePosition.y;
		}

		if(mouseRightPressed && valid(mouseRightAt)) {
			int dx = trackedMousePosition.x - mouseRightAt.x;
			int dy = trackedMousePosition.y - mouseRightAt.y;
			translate(dx, dy);
			pictograms = null;
			repaint();
			mouseRightAt.x = trackedMousePosition.x;
			mouseRightAt.y = trackedMousePosition.y;
		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();

		if(mouseRightPressed && valid(mouseRightAt)) {
			translate(trackedMousePosition.x - mouseRightAt.x, trackedMousePosition.y - mouseRightAt.y);
			mouseRightAt.x = trackedMousePosition.x;
			mouseRightAt.y = trackedMousePosition.y;
			repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();

		if(e.getButton() == 1) {
			mouseLeftPressed = true;
			mouseLeftStarted.x = e.getX();
			mouseLeftStarted.y = e.getY();
			mouseLeftAt.x = e.getX();
			mouseLeftAt.y = e.getY();
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

		if(e.getButton() == 3 && valid(mouseRightAt)) {
			mouseRightPressed = true;
			mouseRightAt.x = e.getX();
			mouseRightAt.y = e.getY();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();

		if(e.getButton() == 1) {
			mouseLeftPressed = false;
			if(selectionBoxEnabled) {
				calculateSelectionBox();
				EditorPoint p1 = transformToModel(selectionStart.x, selectionStart.y);
				EditorPoint p2 = transformToModel(selectionEnd.x, selectionEnd.y);
				if(p1 != null && p2 != null) {
					selectAll(p1, p2, e.isShiftDown());
					selectionBoxEnabled = false;
					repaintEverything();
				}
			}

			if(selectedPictogramAction >= 0 && valid(mouseLeftStarted) && valid(mouseLeftAt)) {
				int action = selectedPictogramAction;
				int dx = e.getX() - mouseLeftStarted.x;
				int dy = e.getY() - mouseLeftStarted.y;
				EditorPoint started = transformToModel(mouseLeftStarted.x, mouseLeftStarted.y);
				EditorPoint ended = transformToModel(mouseLeftAt.x, mouseLeftAt.y);
				if(started != null && ended != null) {
					mouseLeftStarted.x = Integer.MIN_VALUE;
					mouseLeftStarted.y = Integer.MIN_VALUE;
					mouseLeftAt.x = Integer.MIN_VALUE;
					mouseLeftAt.y = Integer.MIN_VALUE;
					selectedPictogramAction = -1;
					pictogramAction(action, dx, dy, started, ended);
					repaintEverything();
				}
			}

			FreeTriangleMeshModel model = getModel();
			model.finalizeMove();
		} else if(e.getButton() == 3) {
			mouseRightPressed = false;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();

		if(e.getButton() == MouseEvent.BUTTON1) {
			if(e.isControlDown()) {
				// create new vertex
				createVertexAtXY(e.getX(), e.getY());
			} else {
				// select one vertex
				EditorPoint p = transformToModel(e.getX(), e.getY());
				if(p != null) {
					selectFirst(p, 5, e.isShiftDown());
					repaintEverything();
				}
			}
		}

		if(e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
			contextMenu.setPosition(e.getX(), e.getY());
		    contextMenu.show(this, e.getX(), e.getY());
        }
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();

		int numberOfRotations = e.getWheelRotation();
		zoomIndex -= numberOfRotations;
		if(zoomIndex < 0)
			zoomIndex = 0;
		else if(zoomIndex >= FreeTriangleMeshModel.ZOOM_VALUES.length)
			zoomIndex = FreeTriangleMeshModel.ZOOM_VALUES.length - 1;
		lenses.setScale(FreeTriangleMeshModel.ZOOM_VALUES[zoomIndex]);
		repaint();
	}

	public void createVertexAtXY(int x, int y) {
		EditorPoint p = transformToModel(x, y);
		if(p != null) {
			createVertexAt(p);
			repaintEverything();
		}
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

	protected abstract void paint2d(Graphics2D g);

	protected boolean valid(Point p) {
		return p.x != Integer.MIN_VALUE && p.x != Integer.MAX_VALUE && p.y != Integer.MIN_VALUE && p.y != Integer.MAX_VALUE;
	}

	private static final long serialVersionUID = 1L;
}
