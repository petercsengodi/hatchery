package hu.csega.games.library.mesh.v1.ftm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshCube;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshLine;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshSnapshots;
import hu.csega.editors.ftm.util.FreeTriangleMeshSphereLineIntersection;

public class FreeTriangleMeshModel implements Serializable {

	private transient FreeTriangleMeshSnapshots _snapshots;
	private FreeTriangleMeshMesh mesh = new FreeTriangleMeshMesh();
	private Collection<Object> selectedObjects = new HashSet<>(); // May also be ArrayList in serialized objects.
	private long selectionLastChanged;
	private List<FreeTriangleMeshGroup> groups = new ArrayList<>();
	private int lastSelectedTriangleIndex = -1;

	private double canvasXYTranslateX;
	private double canvasXYTranslateY;
	private double canvasXYZoom = 1.0;

	private double canvasXZTranslateX;
	private double canvasXZTranslateY;
	private double canvasXZZoom = 1.0;

	private double canvasZYTranslateX;
	private double canvasZYTranslateY;
	private double canvasZYZoom = 1.0;

	private double openGLTranslateX;
	private double openGLTranslateY;
	private double openGLAlpha;
	private double openGLBeta;
	private double openGLZoom = 1.0;

	private boolean moved = false;
	private transient boolean built = false;
	private String textureFilename;

	private double grid = 10.0;

	public void migrateData() {
		if(selectedObjects == null)
			selectedObjects = new HashSet<>();
		else
			selectedObjects = new HashSet<>(selectedObjects);
	}

	public boolean isInvalid() {
		return !built;
	}

	public void setInvalid(boolean invalid) {
		this.built = !invalid;
	}

	public void invalidate() {
		this.built = false;
		invalidateSelection();
	}

	public void invalidateSelection() {
		this.selectionLastChanged = System.currentTimeMillis();
	}

	public long getSelectionLastChanged() {
		return selectionLastChanged;
	}

	public FreeTriangleMeshSnapshots snapshots() {
		if(_snapshots == null)
			_snapshots = new FreeTriangleMeshSnapshots();
		return _snapshots;
	}

	public void undo() {
		Serializable newState = snapshots().undo(mesh);
		if(newState != null) {
			clearSelection();
			mesh = (FreeTriangleMeshMesh) newState;
			invalidate();
		}
	}

	public void redo() {
		Serializable newState = snapshots().redo(mesh);
		if(newState != null) {
			clearSelection();
			mesh = (FreeTriangleMeshMesh) newState;
			invalidate();
		}
	}

	public void clearSelection() {
		selectedObjects.clear();
	}

	public void selectAll(FreeTriangleMeshCube cube, boolean add) {
		if(!add)
			clearSelection();

		for(FreeTriangleMeshVertex vertex : mesh.getVertices()) {
			if(enabled(vertex) && cube.contains(vertex)) {
				if(!selectedObjects.remove(vertex))
					selectedObjects.add(vertex);
			}
		}

		invalidateSelection();
	}

	public void selectFirst(FreeTriangleMeshSphereLineIntersection intersection, FreeTriangleMeshLine line, double radius, boolean add) {
		Object selectedBefore = null;
		if(selectedObjects.size() == 1)
			selectedBefore = selectedObjects.iterator().next();

		if(!add)
			clearSelection();

		Object selection = null;
		double minT = Double.MAX_VALUE;
		double t;

		intersection.setLineSource(line.getX1(), line.getY1(), line.getZ1());
		intersection.setLineTarget(line.getX2(), line.getY2(), line.getZ2());
		intersection.setSphereRadius(radius);

		for(FreeTriangleMeshVertex vertex : mesh.getVertices()) {
			intersection.setSphereCenter(vertex.getPX(), vertex.getPY(), vertex.getPZ());
			intersection.calculateConstants();

			if(intersection.solutionExists()) {
				intersection.calculateResults();
				t = intersection.lowestT();
				if(t < minT) {
					selection = vertex;
				}
			}
		}

		if(selection != null) {
			if(add) {
				if(!selectedObjects.remove(selection)) {
					// we add, if wasn't contained.
					// if contained, then deleted when calculating the condition.
					selectedObjects.add(selection);
				}
			}

			if(!add) {
				if(selectedBefore == selection) {
					// do nothing, de-select the object
				} else {
					selectedObjects.add(selection);
				}
			}
		}

		invalidateSelection();
	}

	public void selectNextTriangle() {
		List<FreeTriangleMeshTriangle> triangles = mesh.getTriangles();
		if(triangles == null || triangles.isEmpty()) {
			lastSelectedTriangleIndex = -1;
			selectedObjects.clear();
			return;
		}

		if(lastSelectedTriangleIndex > 0 && lastSelectedTriangleIndex < triangles.size() && selectedObjects.isEmpty()) {
			FreeTriangleMeshTriangle t = triangles.get(lastSelectedTriangleIndex);
			FreeTriangleMeshVertex v1 = mesh.getVertices().get(t.getVertex1());
			FreeTriangleMeshVertex v2 = mesh.getVertices().get(t.getVertex2());
			FreeTriangleMeshVertex v3 = mesh.getVertices().get(t.getVertex3());
			if(enabled(v1) && enabled(v2) && enabled(v3)) {
				selectedObjects.add(v1);
				selectedObjects.add(v2);
				selectedObjects.add(v3);
				return;
			}
		}

		selectedObjects.clear();

		lastSelectedTriangleIndex++;
		while(lastSelectedTriangleIndex < triangles.size()) {
			FreeTriangleMeshTriangle t = triangles.get(lastSelectedTriangleIndex);
			FreeTriangleMeshVertex v1 = mesh.getVertices().get(t.getVertex1());
			FreeTriangleMeshVertex v2 = mesh.getVertices().get(t.getVertex2());
			FreeTriangleMeshVertex v3 = mesh.getVertices().get(t.getVertex3());
			if(!enabled(v1) || !enabled(v2) || !enabled(v3)) {
				lastSelectedTriangleIndex++;
			} else {
				selectedObjects.add(v1);
				selectedObjects.add(v2);
				selectedObjects.add(v3);
				break;
			}
		}

		if(lastSelectedTriangleIndex >= triangles.size()) {
			lastSelectedTriangleIndex = -1;
		}

		invalidateSelection();
	}

	public void unboundVertices() {
		List<FreeTriangleMeshTriangle> triangles = mesh.getTriangles();
		if(triangles == null || triangles.isEmpty() || selectedObjects == null || selectedObjects.isEmpty()) {
			return;
		}

		snapshots().addState(mesh);
		boolean changed = false;

		Iterator<FreeTriangleMeshTriangle> it = triangles.iterator();
		while(it.hasNext()) {
			FreeTriangleMeshTriangle t = it.next();
			FreeTriangleMeshVertex v1 = mesh.getVertices().get(t.getVertex1());
			FreeTriangleMeshVertex v2 = mesh.getVertices().get(t.getVertex2());
			FreeTriangleMeshVertex v3 = mesh.getVertices().get(t.getVertex3());
			if(selectedObjects.contains(v1) || selectedObjects.contains(v2) || selectedObjects.contains(v3)) {
				it.remove();
				changed = true;
			}
		}

		if(changed) {
			invalidate();
		} else {
			snapshots().removeTopState();
		}
	}

	public void unboundTriangles() {
		List<FreeTriangleMeshTriangle> triangles = mesh.getTriangles();
		if(triangles == null || triangles.isEmpty() || selectedObjects == null || selectedObjects.isEmpty()) {
			return;
		}

		snapshots().addState(mesh);
		boolean changed = false;

		Iterator<FreeTriangleMeshTriangle> it = triangles.iterator();
		while(it.hasNext()) {
			FreeTriangleMeshTriangle t = it.next();
			FreeTriangleMeshVertex v1 = mesh.getVertices().get(t.getVertex1());
			FreeTriangleMeshVertex v2 = mesh.getVertices().get(t.getVertex2());
			FreeTriangleMeshVertex v3 = mesh.getVertices().get(t.getVertex3());
			if(selectedObjects.contains(v1) && selectedObjects.contains(v2) && selectedObjects.contains(v3)) {
				it.remove();
				changed = true;
			}
		}


		if(changed) {
			invalidate();
		} else {
			snapshots().removeTopState();
		}
	}

	public void createVertexAt(double x, double y, double z) {
		snapshots().addState(mesh);
		FreeTriangleMeshVertex vertex = new FreeTriangleMeshVertex(x, y, z);
		vertex.setTX(RND.nextDouble());
		vertex.setTY(RND.nextDouble());
		mesh.getVertices().add(vertex);
	}

	public void createTriangleStrip() {
		if(selectedObjects.size() < 3)
			return;

		snapshots().addState(mesh);

		List<FreeTriangleMeshVertex> vertices = mesh.getVertices();
		List<FreeTriangleMeshTriangle> triangles = mesh.getTriangles();
		Iterator<Object> it = selectedObjects.iterator();

		int i1 = vertices.indexOf(it.next());
		int i2 = vertices.indexOf(it.next());
		int i3;

		for(int i = 2; i < selectedObjects.size(); i++) {
			i3 = vertices.indexOf(it.next());
			triangles.add(new FreeTriangleMeshTriangle(i1, i2, i3));
			i1 = i2;
			i2 = i3;
		}

		invalidate();
	}

	public void deleteVertices() {
		if(selectedObjects.isEmpty())
			return;

		List<FreeTriangleMeshVertex> vertices = mesh.getVertices();
		if(vertices.isEmpty())
			return;

		snapshots().addState(mesh);

		int[] mapping = new int[vertices.size()];
		int mapToIndex = 0;
		int currentIndex = 0;

		Iterator<FreeTriangleMeshVertex> vit = vertices.iterator();
		while(vit.hasNext()) {
			FreeTriangleMeshVertex v = vit.next();
			if(selectedObjects.remove(v)) { // if contains
				// delete vertex
				mapping[currentIndex] = -1;
				vit.remove();
			} else {
				// add index to mapping
				mapping[currentIndex] = mapToIndex++;
			}
			currentIndex++;
		}

		Iterator<FreeTriangleMeshTriangle> tit = mesh.getTriangles().iterator();
		while(tit.hasNext()) {
			FreeTriangleMeshTriangle t = tit.next();

			mapToIndex = mapping[t.getVertex1()];
			if(mapToIndex == -1) {
				tit.remove();
				continue;
			} else {
				t.setVertex1(mapToIndex);
			}

			mapToIndex = mapping[t.getVertex2()];
			if(mapToIndex == -1) {
				tit.remove();
				continue;
			} else {
				t.setVertex2(mapToIndex);
			}

			mapToIndex = mapping[t.getVertex3()];
			if(mapToIndex == -1) {
				tit.remove();
				continue;
			} else {
				t.setVertex3(mapToIndex);
			}
		}

		invalidate();
	}

	public void moveSelected(double x, double y, double z) {
		if(selectedObjects.isEmpty())
			return;

		if(!moved) {
			snapshots().addState(mesh);
			moved = true;
		}

		for(Object object : selectedObjects) {
			if(object instanceof FreeTriangleMeshVertex) {
				FreeTriangleMeshVertex v = (FreeTriangleMeshVertex) object;
				v.move(x, y, z);
			}
		}

		invalidate();
	}

	public void elasticMove(EditorPoint fixed, EditorPoint started, EditorPoint ended) {
		if(selectedObjects.isEmpty())
			return;

		if(!moved) {
			snapshots().addState(mesh);
			moved = true;
		}

		for(Object object : selectedObjects) {
			if(object instanceof FreeTriangleMeshVertex) {
				FreeTriangleMeshVertex v = (FreeTriangleMeshVertex) object;
				double x = v.getPX();
				double y = v.getPY();
				double z = v.getPZ();

				if(x != fixed.getX()) {
					double r1 = started.getX() - fixed.getX();
					double r2 = ended.getX() - fixed.getX();
					if(Math.abs(r1) > 0) {
						double l1 = x - fixed.getX();
						double l2 = l1 * r2 / r1;
						v.setPX(fixed.getX() + l2);
					}
				}

				if(y != fixed.getY()) {
					double r1 = started.getY() - fixed.getY();
					double r2 = ended.getY() - fixed.getY();
					if(Math.abs(r1) > 0) {
						double l1 = y - fixed.getY();
						double l2 = l1 * r2 / r1;
						v.setPY(fixed.getY() + l2);
					}
				}

				if(z != fixed.getZ()) {
					double r1 = started.getZ() - fixed.getZ();
					double r2 = ended.getZ() - fixed.getZ();
					if(Math.abs(r1) > 0) {
						double l1 = y - fixed.getZ();
						double l2 = l1 * r2 / r1;
						v.setPZ(fixed.getZ() + l2);
					}
				}
			}
		}

		invalidate();
	}

	public void elasticTextureMove(EditorPoint fixed, EditorPoint started, EditorPoint ended) {
		if(selectedObjects.isEmpty())
			return;

		if(!moved) {
			snapshots().addState(mesh);
			moved = true;
		}

		for(Object object : selectedObjects) {
			if(object instanceof FreeTriangleMeshVertex) {
				FreeTriangleMeshVertex v = (FreeTriangleMeshVertex) object;
				double x = v.getTX();
				double y = v.getTY();

				if(x != fixed.getX()) {
					double r1 = started.getX() - fixed.getX();
					double r2 = ended.getX() - fixed.getX();
					if(Math.abs(r1) > 0) {
						double l1 = x - fixed.getX();
						double l2 = l1 * r2 / r1;
						v.setTX(truncate(fixed.getX() + l2, 0.0, 1.0));
					}
				}

				if(y != fixed.getY()) {
					double r1 = started.getY() - fixed.getY();
					double r2 = ended.getY() - fixed.getY();
					if(Math.abs(r1) > 0) {
						double l1 = y - fixed.getY();
						double l2 = l1 * r2 / r1;
						v.setTY(truncate(fixed.getY() + l2, 0.0, 1.0));
					}
				}
			}
		}

		invalidate();
	}

	public void finalizeMove() {
		moved = false;
	}

	public void moveTexture(double horizontalMove, double verticalMove) {
		if(selectedObjects.isEmpty())
			return;

		if(!moved) {
			snapshots().addState(mesh);
			moved = true;
		}

		for(Object object : selectedObjects) {
			if(object instanceof FreeTriangleMeshVertex) {
				FreeTriangleMeshVertex v = (FreeTriangleMeshVertex) object;
				v.moveTexture(horizontalMove, verticalMove);
			}
		}

		invalidate();
	}

	public void snapVerticesToGrid() {
		if(selectedObjects.isEmpty())
			return;

		snapshots().addState(mesh);

		for(Object object : selectedObjects) {
			if(object instanceof FreeTriangleMeshVertex) {
				FreeTriangleMeshVertex v = (FreeTriangleMeshVertex) object;
				v.setPX(Math.round(v.getPX() / grid) * grid);
				v.setPY(Math.round(v.getPY() / grid) * grid);
				v.setPZ(Math.round(v.getPZ() / grid) * grid);
			}
		}

		invalidate();
	}

	public void duplicateCurrentSelection() {
		if(selectedObjects.isEmpty())
			return;

		snapshots().addState(mesh);

		List<Object> toCopy = new ArrayList<>(selectedObjects);
		selectedObjects.clear();

		Map<Integer, Integer> map = new HashMap<>();

		List<FreeTriangleMeshVertex> vertices = mesh.getVertices();

		for(Object object : toCopy) {
			if(object instanceof FreeTriangleMeshVertex) {
				FreeTriangleMeshVertex v = (FreeTriangleMeshVertex) object;
				FreeTriangleMeshVertex v2 = v.copy();
				v2.move(10.0, 10.0, 10.0);
				mesh.getVertices().add(v2);
				selectedObjects.add(v2);
				map.put(vertices.indexOf(v), vertices.indexOf(v2));
			}
		}

		List<FreeTriangleMeshTriangle> newTriangles = new ArrayList<>();

		for(FreeTriangleMeshTriangle t : mesh.getTriangles()) {
			Integer i1 = map.get(t.getVertex1());
			Integer i2 = map.get(t.getVertex2());
			Integer i3 = map.get(t.getVertex3());
			if(i1 != null && i2 != null && i3 != null) {
				FreeTriangleMeshTriangle t2 = new FreeTriangleMeshTriangle(i1, i2, i3);
				newTriangles.add(t2);
			}
		}

		if(!newTriangles.isEmpty()) {
			mesh.getTriangles().addAll(newTriangles);
		}

		invalidate();
	}

	public void reverseCurrentSelection() {
		if(selectedObjects.isEmpty())
			return;

		snapshots().addState(mesh);

		List<Object> toCopy = new ArrayList<>(selectedObjects);
		selectedObjects.clear();

		Set<Integer> selectedIndices = new HashSet<>();

		List<FreeTriangleMeshVertex> vertices = mesh.getVertices();

		for(Object object : toCopy) {
			if(object instanceof FreeTriangleMeshVertex) {
				FreeTriangleMeshVertex v = (FreeTriangleMeshVertex) object;
				selectedIndices.add(vertices.indexOf(v));
			}
		}

		for(FreeTriangleMeshTriangle t : mesh.getTriangles()) {
			if(selectedIndices.contains(t.getVertex1()) && selectedIndices.contains(t.getVertex2()) && selectedIndices.contains(t.getVertex3())) {
				t.reverse();
			}
		}

		invalidate();
	}

	public void flip(boolean x, boolean y, boolean z) {
		if(selectedObjects == null || selectedObjects.isEmpty() || (!x && !y && !z)) {
			return;
		}

		snapshots().addState(mesh);
		for(Object obj : selectedObjects) {
			if(obj instanceof FreeTriangleMeshVertex) {
				FreeTriangleMeshVertex v = (FreeTriangleMeshVertex) obj;
				if(x) { v.setPX(-v.getPX()); }
				if(y) { v.setPY(-v.getPY()); }
				if(z) { v.setPZ(-v.getPZ()); }
			}
		}

		invalidate();
	}

	public void rotate90(boolean x, boolean y, boolean z) {
		if(selectedObjects == null || selectedObjects.isEmpty() || (!x && !y && !z)) {
			return;
		}

		snapshots().addState(mesh);
		for(Object obj : selectedObjects) {
			if(obj instanceof FreeTriangleMeshVertex) {
				FreeTriangleMeshVertex v = (FreeTriangleMeshVertex) obj;

				if(x) {
					double tmp = v.getPY();
					v.setPY(v.getPZ());
					v.setPZ(-tmp);
				}

				if(y) {
					double tmp = v.getPX();
					v.setPX(v.getPZ());
					v.setPZ(-tmp);
				}

				if(z) {
					double tmp = v.getPX();
					v.setPX(v.getPY());
					v.setPY(-tmp);
				}
			}
		}

		invalidate();
	}

	public void setGroupForSelectedVertices(int i) {
		initGroupsIfNeeded();
		if(selectedObjects != null && !selectedObjects.isEmpty()) {
			for(Object object : selectedObjects) {
				if(object instanceof FreeTriangleMeshVertex) {
					FreeTriangleMeshVertex v = (FreeTriangleMeshVertex) object;
					v.setGroup(i);
				}
			}
		}

		invalidate();
	}

	public void setToggleGroupVisibility(int i) {
		if(i >= 1 && i <= 9) {
			initGroupsIfNeeded();
			FreeTriangleMeshGroup group = groups.get(i - 1);
			boolean newValue = !group.isEnabled();
			group.setEnabled(newValue);

			if(newValue == false && selectedObjects != null) {
				Iterator<Object> it = selectedObjects.iterator();
				while(it.hasNext()) {
					Object object = it.next();
					if(object instanceof FreeTriangleMeshVertex) {
						FreeTriangleMeshVertex vertex = (FreeTriangleMeshVertex) object;
						if(vertex.getGroup() == group.getGroup()) {
							it.remove();
						}
					}
				}
			}

			invalidate();
		}
	}

	public boolean enabled(FreeTriangleMeshTriangle triangle) {
		List<FreeTriangleMeshVertex> vertices = getVertices();
		FreeTriangleMeshVertex vertex;

		vertex = vertices.get(triangle.getVertex1());
		if(!enabled(vertex)) {
			return false;
		}

		vertex = vertices.get(triangle.getVertex2());
		if(!enabled(vertex)) {
			return false;
		}

		vertex = vertices.get(triangle.getVertex3());
		if(!enabled(vertex)) {
			return false;
		}

		return true;
	}

	public boolean enabled(FreeTriangleMeshVertex vertex) {
		int groupIndex = vertex.getGroup() - 1;
		if(groupIndex < 0 || groupIndex >= 9) {
			return true;
		} else {
			initGroupsIfNeeded();
			FreeTriangleMeshGroup group = groups.get(groupIndex);
			return group.isEnabled();
		}
	}

	public void createBasicCube() {
		snapshots().addState(mesh);

		mesh.addStrip(
				new FreeTriangleMeshVertex(-100, -100, -100).texture(0, 0),
				new FreeTriangleMeshVertex(-100, 100, -100).texture(0, 1),
				new FreeTriangleMeshVertex(100, -100, -100).texture(1, 0),
				new FreeTriangleMeshVertex(100, 100, -100).texture(1, 1)
		);

		mesh.addStrip(
				new FreeTriangleMeshVertex(100, 100, 100).texture(1, 1),
				new FreeTriangleMeshVertex(-100, 100, 100).texture(0, 1),
				new FreeTriangleMeshVertex(100, -100, 100).texture(1, 0),
				new FreeTriangleMeshVertex(-100, -100, 100).texture(0, 0)
		);

		mesh.addStrip(
				new FreeTriangleMeshVertex(-100, -100, -100).texture(0, 0),
				new FreeTriangleMeshVertex(100, -100, -100).texture(1, 0),
				new FreeTriangleMeshVertex(-100, -100, 100).texture(0, 1),
				new FreeTriangleMeshVertex(100, -100, 100).texture(1, 1)
		);

		mesh.addStrip(
				new FreeTriangleMeshVertex(100, 100, 100).texture(1, 1),
				new FreeTriangleMeshVertex(100, 100, -100).texture(1, 0),
				new FreeTriangleMeshVertex(-100, 100, 100).texture(0, 1),
				new FreeTriangleMeshVertex(-100, 100, -100).texture(0, 0)
		);

		mesh.addStrip(
				new FreeTriangleMeshVertex(-100, -100, -100).texture(0, 0),
				new FreeTriangleMeshVertex(-100, -100, 100).texture(1, 0),
				new FreeTriangleMeshVertex(-100, 100, -100).texture(0, 1),
				new FreeTriangleMeshVertex(-100, 100, 100).texture(1, 1)
		);

		mesh.addStrip(
				new FreeTriangleMeshVertex(100, 100, 100).texture(1, 1),
				new FreeTriangleMeshVertex(100, -100, 100).texture(1, 0),
				new FreeTriangleMeshVertex(100, 100, -100).texture(0, 1),
				new FreeTriangleMeshVertex(100, -100, -100).texture(0, 0)
		);

		invalidate();
	}

	public void createBasicSphere() {
	}

	public void createBasicPatch10x10() {
		snapshots().addState(mesh);

		List<FreeTriangleMeshVertex> v = new ArrayList<>(130);
		List<FreeTriangleMeshTriangle> t = new ArrayList<>(105);
		int[][] i = new int[11][11];

		int index = 0;
		for(int xi = 0; xi <= 10; xi++) {
			for(int yi = 0; yi <= 10; yi++) {
				double tx = 0.2 + xi / 50.;
				double ty = 0.2 + yi / 50.0;
				v.add(new FreeTriangleMeshVertex(-50 + xi * 10, -50 + yi * 10, 0).texture(tx, ty));
				i[xi][yi] = index++;
			}
		}

		for(int xi = 0; xi < 10; xi++) {
			for(int yi = 0; yi < 10; yi++) {
				t.add(new FreeTriangleMeshTriangle(i[xi][yi], i[xi][yi + 1], i[xi + 1][yi]));
				t.add(new FreeTriangleMeshTriangle(i[xi][yi + 1], i[xi + 1][yi + 1], i[xi + 1][yi]));
			}
		}

		mesh.addTriangles(v, t);
		invalidate();
	}

	public List<FreeTriangleMeshGroup> getGroups() {
		initGroupsIfNeeded();
		return groups;
	}

	public List<FreeTriangleMeshVertex> getVertices() {
		return mesh.getVertices();
	}

	public void setVertices(List<FreeTriangleMeshVertex> vertices) {
		this.mesh.setVertices(vertices);
	}

	public List<FreeTriangleMeshTriangle> getTriangles() {
		return mesh.getTriangles();
	}

	public void setTriangles(List<FreeTriangleMeshTriangle> triangles) {
		this.mesh.setTriangles(triangles);
	}

	public Collection<Object> getSelectedObjects() {
		return selectedObjects;
	}

	public void setSelectedObjects(Set<Object> selectedObjects) {
		this.selectedObjects = selectedObjects;
	}

	public double getCanvasXYTranslateX() {
		return canvasXYTranslateX;
	}

	public void setCanvasXYTranslateX(double canvasXYTranslateX) {
		this.canvasXYTranslateX = canvasXYTranslateX;
	}

	public double getCanvasXYTranslateY() {
		return canvasXYTranslateY;
	}

	public void setCanvasXYTranslateY(double canvasXYTranslateY) {
		this.canvasXYTranslateY = canvasXYTranslateY;
	}

	public double getCanvasXYZoom() {
		return canvasXYZoom;
	}

	public void setCanvasXYZoom(double canvasXYZoom) {
		this.canvasXYZoom = canvasXYZoom;
	}

	public double getCanvasXZTranslateX() {
		return canvasXZTranslateX;
	}

	public void setCanvasXZTranslateX(double canvasXZTranslateX) {
		this.canvasXZTranslateX = canvasXZTranslateX;
	}

	public double getCanvasXZTranslateY() {
		return canvasXZTranslateY;
	}

	public void setCanvasXZTranslateY(double canvasXZTranslateY) {
		this.canvasXZTranslateY = canvasXZTranslateY;
	}

	public double getCanvasXZZoom() {
		return canvasXZZoom;
	}

	public void setCanvasXZZoom(double canvasXZZoom) {
		this.canvasXZZoom = canvasXZZoom;
	}

	public double getCanvasZYTranslateX() {
		return canvasZYTranslateX;
	}

	public void setCanvasZYTranslateX(double canvasZYTranslateX) {
		this.canvasZYTranslateX = canvasZYTranslateX;
	}

	public double getCanvasZYTranslateY() {
		return canvasZYTranslateY;
	}

	public void setCanvasZYTranslateY(double canvasZYTranslateY) {
		this.canvasZYTranslateY = canvasZYTranslateY;
	}

	public double getCanvasZYZoom() {
		return canvasZYZoom;
	}

	public void setCanvasZYZoom(double canvasZYZoom) {
		this.canvasZYZoom = canvasZYZoom;
	}

	public double getOpenGLTranslateX() {
		return openGLTranslateX;
	}

	public void setOpenGLTranslateX(double openGLTranslateX) {
		this.openGLTranslateX = openGLTranslateX;
	}

	public double getOpenGLTranslateY() {
		return openGLTranslateY;
	}

	public void setOpenGLTranslateY(double openGLTranslateY) {
		this.openGLTranslateY = openGLTranslateY;
	}

	public double getOpenGLAlpha() {
		return openGLAlpha;
	}

	public void setOpenGLAlpha(double openGLAlpha) {
		this.openGLAlpha = openGLAlpha;
	}

	public double getOpenGLBeta() {
		return openGLBeta;
	}

	public void setOpenGLBeta(double openGLBeta) {
		this.openGLBeta = openGLBeta;
	}

	public double getOpenGLZoom() {
		return openGLZoom;
	}

	public void setOpenGLZoom(double openGLZoom) {
		this.openGLZoom = openGLZoom;
	}

	public String getTextureFilename() {
		return textureFilename;
	}

	public void setTextureFilename(String textureFilename) {
		this.textureFilename = textureFilename;
	}

	private double truncate(double value, double low, double high) {
		if(value < low)
			return low;
		else if(value > high)
			return high;
		else
			return value;
	}

	private void initGroupsIfNeeded() {
		if(groups == null || groups.isEmpty()) {
			if(groups == null) {
				groups = new ArrayList<>();
			}

			for(int i = 1; i <= 9; i++) {
				FreeTriangleMeshGroup group = new FreeTriangleMeshGroup(i);
				groups.add(group);
			}
		}
	}

	private static final Random RND = new Random(System.currentTimeMillis());

	private static final long serialVersionUID = 1L;
}
