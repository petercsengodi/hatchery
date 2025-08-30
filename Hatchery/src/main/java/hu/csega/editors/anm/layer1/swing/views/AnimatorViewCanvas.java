package hu.csega.editors.anm.layer1.swing.views;

import hu.csega.editors.AnimatorStarter;
import hu.csega.editors.FreeTriangleMeshToolStarter;
import hu.csega.editors.anm.common.CommonEditorModel;
import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.editors.common.lens.EditorLensPipeline;
import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.ftm.layer1.presentation.swing.view.FreeTriangleMeshPictogram;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.intf.GameCanvas;
import hu.csega.games.engine.intf.GameWindow;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.*;

public class AnimatorViewCanvas extends JPanel implements GameCanvas, MouseListener, MouseMotionListener, MouseWheelListener {

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

	private LinkedHashMap<Class<?>, AnimatorView> registeredViews = new LinkedHashMap<>();
	private Class<?> cachedAnimatorModelClass = null;
	private AnimatorView cachedAnimatorView = null;
	private AnimatorView emptyView;

	public AnimatorViewCanvas(GameEngineFacade facade) {
		this.facade = facade;
		this.emptyView = new AnimatorEmptyView(facade, this);

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

	public GameEngineFacade getFacade() {
		return facade;
	}

	public CommonEditorModel getCurrentModel() {
		AnimatorModel animatorModel = (AnimatorModel) facade.model();
		return animatorModel.selectModel();
	}

	public AnimatorView getCurrentView() {
		CommonEditorModel currentModel = getCurrentModel();
		if(currentModel == null)
			return emptyView;

		Class<?> modelClass = currentModel.getClass();
		if(cachedAnimatorModelClass == modelClass) {
			return cachedAnimatorView;
		}

		cachedAnimatorModelClass = modelClass;
		for(Map.Entry<Class<?>, AnimatorView> entry : registeredViews.entrySet()) {
			if(entry.getKey().isAssignableFrom(modelClass)) {
				cachedAnimatorView = entry.getValue();
				return cachedAnimatorView;
			}
		}

		return emptyView;
	}

	public void registerView(Class<?> modelClass, AnimatorView view) {
		registeredViews.put(modelClass, view);
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

		String label = "< unknown >";
		AnimatorView currentView = getCurrentView();
		currentView.paint(g2d, lastSize.width, lastSize.height);
		label = currentView.label();

		Set<FreeTriangleMeshPictogram> pictograms = refreshPictograms();
		if(pictograms != null && !pictograms.isEmpty()) {
			for(FreeTriangleMeshPictogram p : pictograms) {
				BufferedImage img = AnimatorStarter.SPRITES[p.action];
				g2d.drawImage(img, (int)p.x, (int)p.y, null);
			}
		}

		g2d.setColor(Color.BLACK);
		g2d.drawLine(0, 0, 300, 0);
		g2d.drawString(label, 10, 20);

		Rectangle selectionBox = calculateSelectionBox();
		if(selectionBox != null) {
			g2d.setColor(Color.red);
			calculateSelectionBox();
			g2d.drawRect(selectionBox.x, selectionBox.y, selectionBox.width, selectionBox.height);
		}

		g.drawImage(buffer, 0, 0, null);
	}

	public void repaintEverything() {
		facade.window().repaintEverything();
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
				AnimatorView currentView = getCurrentView();
				EditorPoint moveFrom = currentView.transformToModel(mouseLeftAt.x, mouseLeftAt.y);
				EditorPoint moveTo = currentView.transformToModel(trackedMousePosition.x, trackedMousePosition.y);
				if (moveFrom != null && moveTo != null) {
					currentView.moveSelected(moveFrom, moveTo);
					repaintEverything();
				}
			}

			mouseLeftAt.x = trackedMousePosition.x;
			mouseLeftAt.y = trackedMousePosition.y;
		}

		if(mouseRightPressed && valid(mouseRightAt)) {
			int dx = trackedMousePosition.x - mouseRightAt.x;
			int dy = trackedMousePosition.y - mouseRightAt.y;

			AnimatorView currentView = getCurrentView();
			currentView.translate(dx, dy);

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
			AnimatorView currentView = getCurrentView();
			currentView.translate(trackedMousePosition.x - mouseRightAt.x, trackedMousePosition.y - mouseRightAt.y);

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
			AnimatorView currentView = getCurrentView();
			mouseLeftPressed = false;

			if(selectionBoxEnabled) {
				calculateSelectionBox();

				EditorPoint p1 = currentView.transformToModel(selectionStart.x, selectionStart.y);
				EditorPoint p2 = currentView.transformToModel(selectionEnd.x, selectionEnd.y);
				if (p1 != null && p2 != null) {
					currentView.selectAll(p1, p2, e.isShiftDown());
				}

				selectionBoxEnabled = false;
				repaintEverything();
			}

			if(selectedPictogramAction >= 0 && valid(mouseLeftStarted) && valid(mouseLeftAt)) {
				int action = selectedPictogramAction;
				int dx = e.getX() - mouseLeftStarted.x;
				int dy = e.getY() - mouseLeftStarted.y;
				EditorPoint started = currentView.transformToModel(mouseLeftStarted.x, mouseLeftStarted.y);
				EditorPoint ended = currentView.transformToModel(mouseLeftAt.x, mouseLeftAt.y);
				if (started != null && ended != null) {
					mouseLeftStarted.x = Integer.MIN_VALUE;
					mouseLeftStarted.y = Integer.MIN_VALUE;
					mouseLeftAt.x = Integer.MIN_VALUE;
					mouseLeftAt.y = Integer.MIN_VALUE;
					selectedPictogramAction = -1;

					Rectangle selectionRectangle = new Rectangle(selectionMinX, selectionMinY, selectionMaxX - selectionMinX, selectionMaxY - selectionMinY);
					currentView.pictogramAction(action, dx, dy, started, ended, selectionRectangle);
				}

				repaintEverything();
			}

			currentView.finalizeMove();
		} else if(e.getButton() == 3) {
			mouseRightPressed = false;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();

		if(e.getButton() == MouseEvent.BUTTON1) {
			AnimatorView currentView = getCurrentView();
			if(e.isControlDown()) {
				currentView.controlPlusLeftClick(e.getX(), e.getY());
			} else {
				EditorPoint p = currentView.transformToModel(e.getX(), e.getY());
				if (p != null) {
					currentView.selectFirst(p, 5, e.isShiftDown());
					repaintEverything();
				}
			}
		}

		if(e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
			AnimatorView currentView = getCurrentView();
			currentView.showContextMenu(this, e.getX(), e.getY());
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

	private Set<FreeTriangleMeshPictogram> refreshPictograms() {
		CommonEditorModel model = getCurrentModel();
		if(pictograms == null || selectionLastChanged < model.getSelectionLastChanged()) {
			AnimatorView currentView = getCurrentView();

			selectionMinX = Integer.MAX_VALUE;
			selectionMinY = Integer.MAX_VALUE;
			selectionMaxX = Integer.MIN_VALUE;
			selectionMaxY = Integer.MIN_VALUE;

			Collection<AnimatorObject> selectedObjects = model.getSelectedObjects();
			int numberOfSelectedItems = (selectedObjects == null ? 0 : selectedObjects.size());
			if(numberOfSelectedItems > 0) {

				selectionLastChanged = model.getSelectionLastChanged();
				pictograms = new HashSet<>();

				// TODO: Make this algorithm generic to a bounding box.
				for(Object obj : selectedObjects) {
					if(obj instanceof FreeTriangleMeshVertex) {
						FreeTriangleMeshVertex v = ((FreeTriangleMeshVertex)obj);
						EditorPoint p = currentView.transformToScreen(new EditorPoint(v.getPX(), v.getPY(), v.getPZ(), 0.0));

						int x = (int) p.getX();
						int y = (int) p.getY();
						if(x < selectionMinX) { selectionMinX = x; }
						if(y < selectionMinY) { selectionMinY = y; }
						if(x > selectionMaxX) { selectionMaxX = x; }
						if(y > selectionMaxY) { selectionMaxY = y; }
					}
				}

				currentView.generatePictograms(numberOfSelectedItems, selectionMinX, selectionMinY, selectionMaxX, selectionMaxY, pictograms);
			}
		}

		return pictograms;
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

	protected boolean valid(Point p) {
		return p.x != Integer.MIN_VALUE && p.x != Integer.MAX_VALUE && p.y != Integer.MIN_VALUE && p.y != Integer.MAX_VALUE;
	}

	private static final long serialVersionUID = 1L;
}
