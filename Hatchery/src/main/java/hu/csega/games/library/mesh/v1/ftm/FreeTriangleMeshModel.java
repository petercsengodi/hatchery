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

import hu.csega.editors.FreeTriangleMeshToolStarter;
import hu.csega.editors.anm.common.CommonEditorModel;
import hu.csega.editors.anm.layer1.swing.views.AnimatorObject;
import hu.csega.editors.anm.layer1.swing.views.AnimatorView;
import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshCube;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshLine;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshSnapshots;
import hu.csega.editors.ftm.util.FreeTriangleMeshMathLibrary;
import hu.csega.editors.ftm.util.FreeTriangleMeshSphereLineIntersection;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameModelBuilder;
import hu.csega.games.engine.g3d.GameModelStore;
import hu.csega.games.engine.g3d.GameObjectDirection;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.games.engine.g3d.GameObjectPosition;
import hu.csega.games.engine.g3d.GameObjectVertex;
import hu.csega.games.engine.g3d.GameTexturePosition;

public class FreeTriangleMeshModel implements Serializable, CommonEditorModel {

	public static final double[] ZOOM_VALUES = { 0.0001, 0.001, 0.01, 0.1, 0.2, 0.3, 0.5, 0.75, 1.0, 1.25, 1.50,
			2.0, 3.0, 4.0, 5.0, 10.0, 20.0, 50.0, 100.0, 200.0, 500.0, 1000.0, 2000.0, 5000.0 };
	public static final int DEFAULT_ZOOM_INDEX = 8;

	private transient FreeTriangleMeshSnapshots _snapshots;
	private transient GameObjectHandler convertedModel;

	private FreeTriangleMeshMesh mesh = new FreeTriangleMeshMesh();
	private Collection<AnimatorObject> selectedObjects = new HashSet<>(); // May also be ArrayList in serialized objects.
	private long selectionLastChanged;
	private Object hoverOverObject;
	private List<FreeTriangleMeshGroup> groups = new ArrayList<>();
	private int lastSelectedTriangleIndex = -1;

	private double canvasXYTranslateX;
	private double canvasXYTranslateY;
	private int canvasXYZoomIndex = DEFAULT_ZOOM_INDEX;

	private double canvasXZTranslateX;
	private double canvasXZTranslateY;
	private int canvasXZZoomIndex = DEFAULT_ZOOM_INDEX;

	private double canvasZYTranslateX;
	private double canvasZYTranslateY;
	private int canvasZYZoomIndex = DEFAULT_ZOOM_INDEX;

	private double openGLTranslateX;
	private double openGLTranslateY;
	private double openGLAlpha;
	private double openGLBeta;
	private int openGLZoomIndex = DEFAULT_ZOOM_INDEX;

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

	public FreeTriangleMeshMesh getMeshMesh() {
		return mesh;
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

		AnimatorObject selection = null;
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
		Iterator<AnimatorObject> it = selectedObjects.iterator();

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

		removeVertices();

		invalidate();
	}

	public void removeVertices() {
		List<FreeTriangleMeshVertex> vertices = mesh.getVertices();
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

				if(started.getX() != ended.getX() && x != fixed.getX()) {
					double r1 = started.getX() - fixed.getX();
					double r2 = ended.getX() - fixed.getX();
					if(Math.abs(r1) > 0) {
						double l1 = x - fixed.getX();
						double l2 = l1 * r2 / r1;
						v.setPX(fixed.getX() + l2);
					}
				}

				if(started.getY() != ended.getY() && y != fixed.getY()) {
					double r1 = started.getY() - fixed.getY();
					double r2 = ended.getY() - fixed.getY();
					if(Math.abs(r1) > 0) {
						double l1 = y - fixed.getY();
						double l2 = l1 * r2 / r1;
						v.setPY(fixed.getY() + l2);
					}
				}

				if(started.getZ() != ended.getZ() && z != fixed.getZ()) {
					double r1 = started.getZ() - fixed.getZ();
					double r2 = ended.getZ() - fixed.getZ();
					if(Math.abs(r1) > 0) {
						double l1 = z - fixed.getZ();
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

				if(started.getX() != ended.getX() && x != fixed.getX()) {
					double r1 = started.getX() - fixed.getX();
					double r2 = ended.getX() - fixed.getX();
					if(Math.abs(r1) > 0) {
						double l1 = x - fixed.getX();
						double l2 = l1 * r2 / r1;
						v.setTX(truncate(fixed.getX() + l2, 0.0, 1.0));
					}
				}

				if(started.getY() != ended.getY() && y != fixed.getY()) {
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

	public void mergeVertices() {
		if(selectedObjects.isEmpty())
			return;

		snapshots().addState(mesh);

		Set<Integer> verticesToRemove = new HashSet<>();
		int minimumIndex = Integer.MAX_VALUE;

		FreeTriangleMeshVertex avg = new FreeTriangleMeshVertex(0.0, 0.0, 0.0);

		for(Object object : selectedObjects) {
			if(object instanceof FreeTriangleMeshVertex) {
				FreeTriangleMeshVertex v = (FreeTriangleMeshVertex) object;
				int index = getVertices().indexOf(v);
				minimumIndex = Math.min(minimumIndex, index);
				verticesToRemove.add(index);

				avg.add(v);
			}
		}

		avg.divide(verticesToRemove.size());

		verticesToRemove.remove(minimumIndex);
		FreeTriangleMeshVertex vertexResult = getVertices().get(minimumIndex);
		vertexResult.copyValuesFrom(avg);
		selectedObjects.remove(vertexResult);

		for(FreeTriangleMeshTriangle triangle : getTriangles()) {
			if(verticesToRemove.contains(triangle.getVertex1()))
				triangle.setVertex1(minimumIndex);
			if(verticesToRemove.contains(triangle.getVertex2()))
				triangle.setVertex2(minimumIndex);
			if(verticesToRemove.contains(triangle.getVertex3()))
				triangle.setVertex3(minimumIndex);
		}

		removeVertices();

		invalidate();
	}

	public void selectAll() {
		selectedObjects.clear();
		selectedObjects.addAll(getVertices());
	}

	public void selectTriangle(FreeTriangleMeshTriangle triangle) {
		selectedObjects.clear();

		List<FreeTriangleMeshVertex> vertices = getVertices();
		selectedObjects.add(vertices.get(triangle.getVertex1()));
		selectedObjects.add(vertices.get(triangle.getVertex2()));
		selectedObjects.add(vertices.get(triangle.getVertex3()));
	}

	public void splitLongestEdge() {
		if(selectedObjects.size() < 2)
			return;

		double lengthOfLongestEdge = -1;
		Set<FreeTriangleMeshVertex> longestEdge = new HashSet<>();
		List<FreeTriangleMeshVertex> vertices = getVertices();

		for(FreeTriangleMeshTriangle t : getTriangles()) {
			FreeTriangleMeshVertex v1 = vertices.get(t.getVertex1());
			FreeTriangleMeshVertex v2 = vertices.get(t.getVertex2());
			FreeTriangleMeshVertex v3 = vertices.get(t.getVertex3());

			double d;

			d = distance(v1, v2);
			if(d > lengthOfLongestEdge) {
				longestEdge.clear();
				longestEdge.add(v1);
				longestEdge.add(v2);
				lengthOfLongestEdge = d;
			}

			d = distance(v2, v3);
			if(d > lengthOfLongestEdge) {
				longestEdge.clear();
				longestEdge.add(v2);
				longestEdge.add(v3);
				lengthOfLongestEdge = d;
			}

			d = distance(v3, v1);
			if(d > lengthOfLongestEdge) {
				longestEdge.clear();
				longestEdge.add(v3);
				longestEdge.add(v1);
				lengthOfLongestEdge = d;
			}
		}

		selectedObjects.clear();
		if(lengthOfLongestEdge > 0.000000001) {
			selectedObjects.addAll(longestEdge);
			splitTriangles();
		}
	}

	public void splitTriangles() {
		if(selectedObjects.size() < 2)
			return;

		snapshots().addState(mesh);

		List<FreeTriangleMeshTriangle> trianglesToAdd = new ArrayList<>();
		List<FreeTriangleMeshTriangle> trianglesToRemove = new ArrayList<>();

		if(selectedObjects.size() < 3) {
			// edge split
			FreeTriangleMeshVertex avg = new FreeTriangleMeshVertex(0.0, 0.0, 0.0);
			Iterator<AnimatorObject> it = selectedObjects.iterator();
			FreeTriangleMeshVertex v1, v2;
			avg.add(v1 = (FreeTriangleMeshVertex) it.next());
			avg.add(v2 = (FreeTriangleMeshVertex) it.next());
			avg.divide(2.0);

			int i1 = getVertices().indexOf(v1);
			int i2 = getVertices().indexOf(v2);
			List<FreeTriangleMeshVertex> vertices = mesh.getVertices();
			int iavg = vertices.size();
			vertices.add(avg);

			for(FreeTriangleMeshTriangle t : getTriangles()) {
				if(t.getVertex1() == i1 || t.getVertex2() == i1 || t.getVertex3() == i1) {
					if(t.getVertex1() == i2 || t.getVertex2() == i2 || t.getVertex3() == i2) {
						if(t.getVertex1() != i1 && t.getVertex1() != i2) {
							FreeTriangleMeshTriangle newTriangle = t.copy();
							t.setVertex2(iavg);
							newTriangle.setVertex3(iavg);
							trianglesToAdd.add(newTriangle);
						} else if(t.getVertex2() != i1 && t.getVertex2() != i2) {
							FreeTriangleMeshTriangle newTriangle = t.copy();
							t.setVertex1(iavg);
							newTriangle.setVertex3(iavg);
							trianglesToAdd.add(newTriangle);
						} else if(t.getVertex3() != i1 && t.getVertex3() != i2) {
							FreeTriangleMeshTriangle newTriangle = t.copy();
							t.setVertex1(iavg);
							newTriangle.setVertex2(iavg);
							trianglesToAdd.add(newTriangle);
						}
					}
				}
			}
		} else {
			// split only fully selected triangles
			List<FreeTriangleMeshVertex> vertices = getVertices();

			for(FreeTriangleMeshTriangle t : getTriangles()) {
				FreeTriangleMeshVertex v1 = vertices.get(t.getVertex1());
				FreeTriangleMeshVertex v2 = vertices.get(t.getVertex2());
				FreeTriangleMeshVertex v3 = vertices.get(t.getVertex3());
				if(selectedObjects.contains(v1) && selectedObjects.contains(v2) && selectedObjects.contains(v3)) {
					trianglesToRemove.add(t);

					FreeTriangleMeshVertex avg = new FreeTriangleMeshVertex(0.0, 0.0, 0.0);
					avg.add(v1);
					avg.add(v2);
					avg.add(v3);
					avg.divide(3.0);

					int iavg = vertices.size();
					vertices.add(avg);

					FreeTriangleMeshTriangle t1 = t.copy();
					t1.setVertex1(iavg);
					trianglesToAdd.add(t1);

					FreeTriangleMeshTriangle t2 = t.copy();
					t2.setVertex2(iavg);
					trianglesToAdd.add(t2);

					FreeTriangleMeshTriangle t3 = t.copy();
					t3.setVertex3(iavg);
					trianglesToAdd.add(t3);
				}
			}
		}

		if(!trianglesToAdd.isEmpty()) {
			getTriangles().addAll(trianglesToAdd);
		}

		if(!trianglesToRemove.isEmpty()) {
			getTriangles().removeAll(trianglesToRemove);
		}

		selectedObjects.clear();
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
				Iterator<AnimatorObject> it = selectedObjects.iterator();
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

	public void createBasicSphere(double rx, double ry, double rz, int density) {
		snapshots().addState(mesh);

		double PI2 = Math.PI * 2.0;
		double delta = PI2 / density;
		double deltaVertical = Math.PI / density;
		double limitAlpha = PI2 - 0.001;
		double limitBeta = Math.PI - 0.001;
		List<FreeTriangleMeshVertex> vertices = mesh.getVertices();
		List<FreeTriangleMeshTriangle> triangles = mesh.getTriangles();

		// FIXME: Correct algorithm.
		Integer topVertex = vertices.size();
		vertices.add(new FreeTriangleMeshVertex(0, ry, 0).texture(0.5, 0.5));
		Integer bottomVertex = vertices.size();
		vertices.add(new FreeTriangleMeshVertex(0, -ry, 0).texture(0.5, 0.5));

		int numberOfHorizontalVertices = 0;
		for(double alpha = 0; alpha < limitAlpha; alpha += delta) {
			numberOfHorizontalVertices++;
		}

		int numberOfVerticalVertices = 0;
		for(double beta = deltaVertical; beta < limitBeta; beta += deltaVertical) {
			numberOfVerticalVertices++;
		}

		Integer[][] inBetweenVertices = new Integer[numberOfVerticalVertices][numberOfHorizontalVertices];

		int vertical = 0;
		for(double beta = deltaVertical; beta < limitBeta; beta += deltaVertical) {
			int horizontal = 0;
			for(double alpha = 0; alpha < limitAlpha; alpha += delta) {
				inBetweenVertices[vertical][horizontal] = vertices.size();
				double tl = (1 - Math.cos(beta / 2)) / 2;
				if(tl < 0.0) { tl = 0.0; }
				if(tl > 0.5) { tl = 0.5; }

				double tx = tl * Math.sin(alpha) + 0.5;
				if(tx < 0.0) { tx = 0.0; }
				if(tx > 1.0) { tx = 1.0; }

				double ty = tl * Math.cos(alpha) + 0.5;
				if(ty < 0.0) { ty = 0.0; }
				if(ty > 1.0) { ty = 1.0; }

				FreeTriangleMeshVertex vertex = new FreeTriangleMeshVertex(
						FreeTriangleMeshMathLibrary.sphereX(rx, ry, rz, alpha, beta),
						FreeTriangleMeshMathLibrary.sphereY(rx, ry, rz, alpha, beta),
						FreeTriangleMeshMathLibrary.sphereZ(rx, ry, rz, alpha, beta)
				).texture(tx, ty);

				vertices.add(vertex);
				horizontal++;
			}
			vertical++;
		}


		for(int x = 0; x < numberOfHorizontalVertices; x++) {
			triangles.add(new FreeTriangleMeshTriangle(topVertex, inBetweenVertices[0][(x+1) % numberOfHorizontalVertices], inBetweenVertices[0][x]));
			for(int y = 0; y < numberOfVerticalVertices - 1; y++) {
				triangles.add(new FreeTriangleMeshTriangle(inBetweenVertices[y][x], inBetweenVertices[y][(x+1) % numberOfHorizontalVertices], inBetweenVertices[y + 1][x]));
				triangles.add(new FreeTriangleMeshTriangle(inBetweenVertices[y][(x+1) % numberOfHorizontalVertices], inBetweenVertices[y + 1][(x+1) % numberOfHorizontalVertices], inBetweenVertices[y + 1][x]));
			}
			triangles.add(new FreeTriangleMeshTriangle(inBetweenVertices[numberOfVerticalVertices - 1][x], inBetweenVertices[numberOfVerticalVertices - 1][(x+1) % numberOfHorizontalVertices], bottomVertex));
		}

		invalidate();
	}

	public void createBasicSphere_old1(double rx, double ry, double rz, int density) {
		snapshots().addState(mesh);

		double PI2 = Math.PI * 2.0;
		double delta = PI2 / density;
		double limitAlpha = PI2 - 0.001;
		double limitBeta = Math.PI - 0.001;
		List<FreeTriangleMeshVertex> vertices = mesh.getVertices();
		List<FreeTriangleMeshTriangle> triangles = mesh.getTriangles();

		// FIXME: Correct algorithm.
		Integer topVertex = vertices.size();
		vertices.add(new FreeTriangleMeshVertex(0, ry, 0).texture(0.5, 0.5));
		Integer bottomVertex = vertices.size();
		vertices.add(new FreeTriangleMeshVertex(0, -ry, 0).texture(0.5, 0.5));

		int numberOfHorizontalVertices = 0;
		int numberOfVerticalVertices = -1;
		for(double alpha = 0; alpha < limitAlpha; alpha += delta) {
			numberOfHorizontalVertices++;
			if(alpha < limitBeta)
				numberOfVerticalVertices++;
		}

		Integer[][] inBetweenVertices = new Integer[numberOfVerticalVertices][numberOfHorizontalVertices];

		int vertical = 0;
		for(double beta = delta; beta < limitBeta; beta += delta) {
			int horizontal = 0;
			for(double alpha = 0; alpha < limitAlpha; alpha += delta) {
				inBetweenVertices[vertical][horizontal] = vertices.size();
				double tl = (1 - Math.cos(beta / 2)) / 2;
				if(tl < 0.0) { tl = 0.0; }
				if(tl > 0.5) { tl = 0.5; }

				double tx = tl * Math.sin(alpha) + 0.5;
				if(tx < 0.0) { tx = 0.0; }
				if(tx > 1.0) { tx = 1.0; }

				double ty = tl * Math.cos(alpha) + 0.5;
				if(ty < 0.0) { ty = 0.0; }
				if(ty > 1.0) { ty = 1.0; }

				FreeTriangleMeshVertex vertex = new FreeTriangleMeshVertex(
						FreeTriangleMeshMathLibrary.sphereX(rx, ry, rz, alpha, beta),
						FreeTriangleMeshMathLibrary.sphereY(rx, ry, rz, alpha, beta),
						FreeTriangleMeshMathLibrary.sphereZ(rx, ry, rz, alpha, beta)
				).texture(tx, ty);

				vertices.add(vertex);
				horizontal++;
			}
			vertical++;
		}


		for(int x = 0; x < numberOfHorizontalVertices; x++) {
			triangles.add(new FreeTriangleMeshTriangle(topVertex, inBetweenVertices[0][(x+1) % numberOfHorizontalVertices], inBetweenVertices[0][x]));
			for(int y = 0; y < numberOfVerticalVertices - 1; y++) {
				triangles.add(new FreeTriangleMeshTriangle(inBetweenVertices[y][x], inBetweenVertices[y][(x+1) % numberOfHorizontalVertices], inBetweenVertices[y + 1][x]));
				triangles.add(new FreeTriangleMeshTriangle(inBetweenVertices[y][(x+1) % numberOfHorizontalVertices], inBetweenVertices[y + 1][(x+1) % numberOfHorizontalVertices], inBetweenVertices[y + 1][x]));
			}
			triangles.add(new FreeTriangleMeshTriangle(inBetweenVertices[numberOfVerticalVertices - 1][x], inBetweenVertices[numberOfVerticalVertices - 1][(x+1) % numberOfHorizontalVertices], bottomVertex));
		}

		invalidate();
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

	public Collection<AnimatorObject> getSelectedObjects() {
		return selectedObjects;
	}

	public void setSelectedObjects(Set<AnimatorObject> selectedObjects) {
		this.selectedObjects = selectedObjects;
	}

	public void setHoverOverObject(Object hoverOverObject) {
		this.hoverOverObject = hoverOverObject;
	}

	public Object getHoverOverObject() {
		return hoverOverObject;
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

	public int getCanvasXYZoomIndex() {
		return canvasXYZoomIndex;
	}

	public void setCanvasXYZoomIndex(int canvasXYZoomIndex) {
		this.canvasXYZoomIndex = canvasXYZoomIndex;
	}

	public double getCanvasXYZoom() {
		if(canvasXYZoomIndex < 0 || canvasXYZoomIndex >= ZOOM_VALUES.length) {
			canvasXYZoomIndex = DEFAULT_ZOOM_INDEX;
		}

		return ZOOM_VALUES[canvasXYZoomIndex];
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

	public int getCanvasXZZoomIndex() {
		return canvasXZZoomIndex;
	}

	public void setCanvasXZZoomIndex(int canvasXZZoomIndex) {
		this.canvasXZZoomIndex = canvasXZZoomIndex;
	}

	public double getCanvasXZZoom() {
		if(canvasXZZoomIndex < 0 || canvasXZZoomIndex >= ZOOM_VALUES.length) {
			canvasXZZoomIndex = DEFAULT_ZOOM_INDEX;
		}

		return ZOOM_VALUES[canvasXZZoomIndex];
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

	public int getCanvasZYZoomIndex() {
		return canvasZYZoomIndex;
	}

	public void setCanvasZYZoomIndex(int canvasZYZoomIndex) {
		this.canvasZYZoomIndex = canvasZYZoomIndex;
	}

	public double getCanvasZYZoom() {
		if(canvasZYZoomIndex < 0 || canvasZYZoomIndex >= ZOOM_VALUES.length) {
			canvasZYZoomIndex = DEFAULT_ZOOM_INDEX;
		}

		return ZOOM_VALUES[canvasZYZoomIndex];
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

	public void modifyOpenGLAlpha(double diff) {
		this.openGLAlpha += diff;
		if(this.openGLAlpha < -PI2) {
			this.openGLAlpha += PI2;
		} else if(this.openGLAlpha > PI2) {
			this.openGLAlpha -= PI2;
		}
	}

	public double getOpenGLBeta() {
		return openGLBeta;
	}

	public void setOpenGLBeta(double openGLBeta) {
		this.openGLBeta = openGLBeta;
	}

	public void modifyOpenGLBeta(double diff) {
		this.openGLBeta += diff;
		if(this.openGLBeta < -BETA_LIMIT) {
			this.openGLBeta = -BETA_LIMIT;
		} else if(this.openGLBeta > BETA_LIMIT) {
			this.openGLBeta = BETA_LIMIT;
		}
	}

	public int getOpenGLZoomIndex() {
		return openGLZoomIndex;
	}

	public void setOpenGLZoomIndex(int openGLZoomIndex) {
		this.openGLZoomIndex = openGLZoomIndex;
	}

	public void modifyOpenGLZoomIndex(int numberOfRotations) {
		this.openGLZoomIndex += numberOfRotations;
		if(this.openGLZoomIndex < 0) {
			this.openGLZoomIndex = 0;
		} else if(this.openGLZoomIndex >= ZOOM_VALUES.length) {
			this.openGLZoomIndex = ZOOM_VALUES.length - 1;
		}
	}

	public double getOpenGLZoom() {
		if(openGLZoomIndex < 0 || openGLZoomIndex >= ZOOM_VALUES.length) {
			openGLZoomIndex = DEFAULT_ZOOM_INDEX;
		}
		return ZOOM_VALUES[openGLZoomIndex];
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

	private static double distance(FreeTriangleMeshVertex v1, FreeTriangleMeshVertex v2) {
		double dx = v1.getPX() - v2.getPX();
		double dy = v1.getPY() - v2.getPY();
		double dz = v1.getPZ() - v2.getPZ();

		return Math.sqrt(dx*dx + dy*dy + dz*dz);
	}

	@Override
	public GameObjectPlacement cameraPlacement() {
		double alfa = getOpenGLAlpha();
		double beta = getOpenGLBeta();
		double distance = getOpenGLZoom();

		double y = distance * Math.sin(beta);
		double distanceReduced = distance * Math.cos(beta);

		GameObjectPosition cameraPosition = new GameObjectPosition();
		cameraPosition.x = (float)(Math.cos(alfa) * distanceReduced);
		cameraPosition.y = (float) y;
		cameraPosition.z = (float)(Math.sin(alfa) * distanceReduced);

		GameObjectPosition cameraTarget = new GameObjectPosition(0f, 0f, 0f);
		GameObjectDirection cameraUp = new GameObjectDirection(0f, 1f, 0f);

		GameObjectPlacement cameraPlacement = new GameObjectPlacement();
		cameraPlacement.setPositionTargetUp(cameraPosition, cameraTarget, cameraUp);
		return cameraPlacement;
	}

    public GameObjectHandler ensureConvertedModelIsBuilt(GameEngineFacade facade) {
		if(isInvalid()) {
			List<FreeTriangleMeshVertex> vertices = mesh.getVertices();
			List<FreeTriangleMeshTriangle> triangles = mesh.getTriangles();

			GameModelStore store = facade.store();

			if(convertedModel != null) {
				store.dispose(convertedModel);
				convertedModel = null;
			}

			if(!triangles.isEmpty()) {
				GameModelBuilder builder = new GameModelBuilder();

				String textureFilename = getTextureFilename();
				if(textureFilename == null || textureFilename.isEmpty())
					textureFilename = FreeTriangleMeshToolStarter.DEFAULT_TEXTURE_FILE;

				GameObjectHandler textureHandler = store.loadTexture(textureFilename);
				builder.setTextureHandler(textureHandler);

				for(FreeTriangleMeshVertex v : vertices) {
					GameObjectPosition p = new GameObjectPosition((float)v.getPX(), (float)v.getPY(), (float)v.getPZ());
					GameObjectDirection d = new GameObjectDirection((float)v.getNX(), (float)v.getNY(), (float)v.getNZ());
					GameTexturePosition tex = new GameTexturePosition((float)v.getTX(), (float)v.getTY());
					builder.getVertices().add(new GameObjectVertex(p, d, tex));
				}

				for(FreeTriangleMeshTriangle t : triangles) {
					if(enabled(t)) {
						builder.getIndices().add(t.getVertex1());
						builder.getIndices().add(t.getVertex2());
						builder.getIndices().add(t.getVertex3());
					}
				}

				convertedModel = store.buildMesh(builder);
			}

			setInvalid(false);
		}

		return convertedModel;
	}

	private static final double PI2 = 2*Math.PI;
	private static final double BETA_LIMIT = Math.PI / 2;

	private static final Random RND = new Random(System.currentTimeMillis());

	private static final long serialVersionUID = 1L;
}
