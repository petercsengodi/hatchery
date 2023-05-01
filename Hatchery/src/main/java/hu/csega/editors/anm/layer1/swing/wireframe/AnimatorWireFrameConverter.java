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
import java.util.ArrayList;
import java.util.Collection;
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

		// FIXME: some lightweight pattern not to always create new objects or something
		AnimatorWireFrame result = new AnimatorWireFrame();
		if(parts != null) {
			collectWireFrame(parts, result);
		}

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
		Collection<AnimatorWireFrameLine> lines = new ArrayList<>();

		String mesh1 = part.getMesh();
		String fn = FileUtil.cleanUpName(mesh1);
		SMeshRef ref = new SMeshRef(fn);
		Object mesh = meshLibrary.resolve(ref);
		if(mesh == null) {
			return;
		}

        GameTransformation transformation = part.getTransformation();
		if(mesh instanceof SMesh) {
			convert((SMesh) mesh, transformation, lines);
		} else if(mesh instanceof FreeTriangleMeshModel) {
			convert((FreeTriangleMeshModel) mesh, transformation, lines);
		} else {
			throw new ClassCastException("Unknown format for mesh: " + mesh.getClass().getName());
		}

		result.addLines(lines);
	}

	private void convert(SMesh mesh, GameTransformation transformation, Collection<AnimatorWireFrameLine> lines) {
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
					AnimatorWireFramePoint p1 = new AnimatorWireFramePoint(v1.get(0), v1.get(1), v1.get(2), color);
					AnimatorWireFramePoint p2 = new AnimatorWireFramePoint(v2.get(0), v2.get(1), v2.get(2), color);

                    if(transformation != null) {
                        p1.transform(transformation);
                        p2.transform(transformation);
                    }

                    lines.add(new AnimatorWireFrameLine(p1, p2, color));
				}
			}
		}
	}

	private void convert(FreeTriangleMeshModel mesh, GameTransformation transformation, Collection<AnimatorWireFrameLine> lines) {
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

			AnimatorWireFramePoint p1 = new AnimatorWireFramePoint(v1.getPX(), v1.getPY(), v1.getPZ(), color);
			AnimatorWireFramePoint p2 = new AnimatorWireFramePoint(v2.getPX(), v2.getPY(), v2.getPZ(), color);
			AnimatorWireFramePoint p3 = new AnimatorWireFramePoint(v3.getPX(), v3.getPY(), v3.getPZ(), color);

            if(transformation != null) {
                p1.transform(transformation);
                p2.transform(transformation);
                p3.transform(transformation);
            }

			lines.add(new AnimatorWireFrameLine(p1, p2, color));
			lines.add(new AnimatorWireFrameLine(p2, p3, color));
			lines.add(new AnimatorWireFrameLine(p3, p1, color));
		}
	}


}
