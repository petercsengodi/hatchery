package hu.csega.games.library.mesh.v1.ftm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FreeTriangleMeshMesh implements Serializable {

	private List<FreeTriangleMeshVertex> vertices = new ArrayList<>();
	private List<FreeTriangleMeshTriangle> triangles = new ArrayList<>();

	public List<FreeTriangleMeshVertex> getVertices() {
		return vertices;
	}

	public void setVertices(List<FreeTriangleMeshVertex> vertices) {
		this.vertices = vertices;
	}

	public List<FreeTriangleMeshTriangle> getTriangles() {
		return triangles;
	}

	public void setTriangles(List<FreeTriangleMeshTriangle> triangles) {
		this.triangles = triangles;
	}

	public void addStrip(FreeTriangleMeshVertex... vertices) {
		int last2Index = -1, last1Index = -1, last0Index;
		for(FreeTriangleMeshVertex vertex : vertices) {
			last0Index = this.vertices.size();
			this.vertices.add(vertex);

			if(last2Index > -1) {
				if(last2Index % 2 == 0) {
					this.triangles.add(new FreeTriangleMeshTriangle(last2Index, last1Index, last0Index));
				} else {
					this.triangles.add(new FreeTriangleMeshTriangle(last1Index, last2Index, last0Index));
				}
			}

			last2Index = last1Index;
			last1Index = last0Index;
		}
	}

	public void addTriangles(List<FreeTriangleMeshVertex> vertices, List<FreeTriangleMeshTriangle> relativeTriangles) {
		int offset = this.vertices.size();
		this.vertices.addAll(vertices);

		for(FreeTriangleMeshTriangle relativeTriangle : relativeTriangles) {
			FreeTriangleMeshTriangle absoluteTriangle = new FreeTriangleMeshTriangle(
					relativeTriangle.getVertex1() + offset,
					relativeTriangle.getVertex2() + offset,
					relativeTriangle.getVertex3() + offset
			);

			this.triangles.add(absoluteTriangle);
		}
	}

	private static final long serialVersionUID = 1L;
}
