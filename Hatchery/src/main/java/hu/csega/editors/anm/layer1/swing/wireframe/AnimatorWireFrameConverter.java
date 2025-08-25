package hu.csega.editors.anm.layer1.swing.wireframe;

import hu.csega.editors.anm.components.ComponentWireFrameConverter;
import hu.csega.editors.anm.layer1.swing.AnimatorUIComponents;
import hu.csega.editors.anm.layer1.view3d.AnimatorSetPart;
import hu.csega.games.engine.g3d.GameTransformation;
import hu.csega.games.library.MeshLibrary;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshTriangle;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;
import hu.csega.games.library.mesh.v1.xml.SEdge;
import hu.csega.games.library.mesh.v1.xml.SMesh;
import hu.csega.games.library.mesh.v1.xml.SShape;
import hu.csega.games.library.mesh.v1.xml.STriangle;
import hu.csega.games.library.mesh.v1.xml.SVertex;
import hu.csega.games.library.reference.SMeshRef;
import hu.csega.games.library.util.FileUtil;
import hu.csega.games.units.UnitStore;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector4f;

public class AnimatorWireFrameConverter implements ComponentWireFrameConverter {

	private MeshLibrary meshLibrary;
	private AnimatorWireFrame wireFrame;

	private AnimatorUIComponents components;


	@Override
	public AnimatorWireFrame transform(List<AnimatorSetPart> parts) {
		if(meshLibrary == null) {
			meshLibrary = UnitStore.instance(MeshLibrary.class);
		}

		AnimatorWireFrame result = new AnimatorWireFrame();

		// FIXME: some lightweight pattern not to always create new objects or something
		if(parts != null) {
			collectWireFrame(parts, result);
		}

		result.addPoint(new AnimatorWireFramePoint(0.0, 0.0, 0.0, Color.PINK, true));

		return result;
	}

	@Override
	public void accept(List<AnimatorSetPart> parts) {
		components = UnitStore.instance(AnimatorUIComponents.class);
		if(components == null) {
			components = UnitStore.instance(AnimatorUIComponents.class);
			if(components == null) {
				return;
			}
		}

		wireFrame = transform(parts);

		components.panelFront.accept(wireFrame);
		components.panelSide.accept(wireFrame);
		components.panelTop.accept(wireFrame);
		components.panelWireFrame.accept(wireFrame);
	}

	@Override
	public AnimatorWireFrame provide() {
		return wireFrame;
	}

	private void collectWireFrame(List<AnimatorSetPart> parts, AnimatorWireFrame result) {
		for(AnimatorSetPart part : parts) {
			collectWireframe(part, result);
		}
	}

	private void collectWireframe(AnimatorSetPart part, AnimatorWireFrame result) {
		String mesh1 = part.getMesh();
		Object mesh = part.getMeshModel();

		if(mesh == null) {
			String fn = FileUtil.cleanUpName(mesh1);
			SMeshRef ref = new SMeshRef(fn);
			mesh = meshLibrary.resolve(ref);
		}

		if(mesh == null) {
			return;
		}

        GameTransformation transformation = part.getTransformation();
		if(mesh instanceof SMesh) {
			convert((SMesh) mesh, transformation, result);
		} else if(mesh instanceof FreeTriangleMeshModel) {
			convert((FreeTriangleMeshModel) mesh, transformation, result);
		} else {
			throw new ClassCastException("Unknown format for mesh: " + mesh.getClass().getName());
		}

		List<AnimatorWireFramePoint> jointPoints = part.getJointPoints();
		if(jointPoints != null && !jointPoints.isEmpty()) {
			// WARNING: I modify the original objects here.
			for(AnimatorWireFramePoint jointPoint: jointPoints) {
				jointPoint.transform(transformation);
			}

			result.addPoints(jointPoints);
		}
	}

	private void convert(SMesh mesh, GameTransformation transformation, AnimatorWireFrame result) {
		Color color = Color.BLACK;

		List<SShape> figures = mesh.getFigures();
		for(SShape figure : figures) {
			List<STriangle> triangles = figure.getTriangles();
			for(STriangle triangle : triangles) {
				List<SEdge> edges = triangle.getEdges();
				for(SEdge edge : edges) {
					SVertex from = edge.getFrom();
					SVertex to = edge.getTo();

					Vector4f v1 = from.getPosition();
					Vector4f v2 = to.getPosition();
					AnimatorWireFramePoint p1 = new AnimatorWireFramePoint(v1.get(0), v1.get(1), v1.get(2), color, false);
					AnimatorWireFramePoint p2 = new AnimatorWireFramePoint(v2.get(0), v2.get(1), v2.get(2), color, false);
					AnimatorWireFramePoint center = new AnimatorWireFramePoint(0.0, 0.0, 0.0, Color.LIGHT_GRAY, true);

                    if(transformation != null) {
                        p1.transform(transformation);
                        p2.transform(transformation);
                        center.transform(transformation);
                    }

					result.addLine(new AnimatorWireFrameLine(p1, p2, color));
					result.addPoint(center);
				}
			}
		}
	}

	private void convert(FreeTriangleMeshModel mesh, GameTransformation transformation, AnimatorWireFrame result) {
		Map<Integer, FreeTriangleMeshVertex> map = new HashMap<>();
		Integer index = 0;

		List<FreeTriangleMeshVertex> vertices = mesh.getVertices();
		for(FreeTriangleMeshVertex vertex : vertices) {
			map.put(index, vertex);
			index++;
		}

		Color color = Color.BLACK;

		List<FreeTriangleMeshTriangle> triangles = mesh.getTriangles();
		for(FreeTriangleMeshTriangle triangle : triangles) {
			FreeTriangleMeshVertex v1 = map.get(triangle.getVertex1());
			FreeTriangleMeshVertex v2 = map.get(triangle.getVertex2());
			FreeTriangleMeshVertex v3 = map.get(triangle.getVertex3());

			AnimatorWireFramePoint p1 = new AnimatorWireFramePoint(v1.getPX(), v1.getPY(), v1.getPZ(), color, false);
			AnimatorWireFramePoint p2 = new AnimatorWireFramePoint(v2.getPX(), v2.getPY(), v2.getPZ(), color, false);
			AnimatorWireFramePoint p3 = new AnimatorWireFramePoint(v3.getPX(), v3.getPY(), v3.getPZ(), color, false);
			AnimatorWireFramePoint center = new AnimatorWireFramePoint(0.0, 0.0, 0.0, Color.LIGHT_GRAY, true);

            if(transformation != null) {
                p1.transform(transformation);
                p2.transform(transformation);
                p3.transform(transformation);
                center.transform(transformation);
            }

			result.addLine(new AnimatorWireFrameLine(p1, p2, color));
			result.addLine(new AnimatorWireFrameLine(p2, p3, color));
			result.addLine(new AnimatorWireFrameLine(p3, p1, color));
			result.addPoint(center);
		}
	}

}
