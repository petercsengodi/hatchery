package hu.csega.editors.ftm.layer1.presentation.opengl;

import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.intf.GameKeyListener;

public class FreeTriangleMeshKeyListener implements GameKeyListener {

	public FreeTriangleMeshKeyListener() {
	}

	@Override
	public void hit(GameEngineFacade facade, char key) {
		if(key == 27) { // ESC: remove selection
			FreeTriangleMeshModel model = (FreeTriangleMeshModel)facade.model();
			model.clearSelection();
			facade.window().repaintEverything();
		}

		if(key == 'd' || key == 'D' || key == 127) { // del
			FreeTriangleMeshModel model = (FreeTriangleMeshModel)facade.model();
			model.deleteVertices();
			facade.window().repaintEverything();
		}

		if(key == 't' || key == 'T') { // T: create triangle (strip)
			FreeTriangleMeshModel model = (FreeTriangleMeshModel)facade.model();
			model.createTriangleStrip();
			facade.window().repaintEverything();
		}

		if(key == 'g' || key == 'G') { // G: snap vertices to grid
			FreeTriangleMeshModel model = (FreeTriangleMeshModel)facade.model();
			model.snapVerticesToGrid();
			facade.window().repaintEverything();
		}

		if(key == 'c' || key == 'C') { // C: duplicate current selection
			FreeTriangleMeshModel model = (FreeTriangleMeshModel)facade.model();
			model.duplicateCurrentSelection();
			facade.window().repaintEverything();
		}

		if(key == 'm' || key == 'M') { // M: merge vertices
			FreeTriangleMeshModel model = (FreeTriangleMeshModel)facade.model();
			model.mergeVertices();
			facade.window().repaintEverything();
		}

		if(key == 'n' || key == 'N') { // N: select next triangle
			FreeTriangleMeshModel model = (FreeTriangleMeshModel)facade.model();
			model.selectNextTriangle();
			facade.window().repaintEverything();
		}

		if(key == 'r' || key == 'R') { // R: reverse current selection
			FreeTriangleMeshModel model = (FreeTriangleMeshModel)facade.model();
			model.reverseCurrentSelection();
			facade.window().repaintEverything();
		}

		if(facade.control().isControlOn()) {
			if(key == 'z' || key == 'Z' || key == 26) { // CTRL + Z: undo
				FreeTriangleMeshModel model = (FreeTriangleMeshModel)facade.model();
				model.undo();
				facade.window().repaintEverything();
			}

			if(key == 'y' || key == 'Y' || key == 25) { // CTRL + Y: re-do
				FreeTriangleMeshModel model = (FreeTriangleMeshModel)facade.model();
				model.redo();
				facade.window().repaintEverything();
			}

			if(key >= '0' && key <= '9') { // CTRL + NUM: set selection group
				FreeTriangleMeshModel model = (FreeTriangleMeshModel)facade.model();
				model.setGroupForSelectedVertices(key - '0');
				facade.window().repaintEverything();
			}

			if(key == 'u' || key == 'U') { // CTRL + U: unbound all triangles containing selected vertices
				FreeTriangleMeshModel model = (FreeTriangleMeshModel)facade.model();
				model.unboundVertices();
				facade.window().repaintEverything();
			}
		} else {
			if(key >= '0' && key <= '9') { // NUM: toggle selection group visibility
				FreeTriangleMeshModel model = (FreeTriangleMeshModel)facade.model();
				model.setToggleGroupVisibility(key - '0');
				facade.window().repaintEverything();
			}

			if(key == 'u' || key == 'U') { // U: unbound triangles where all vertices are selected
				FreeTriangleMeshModel model = (FreeTriangleMeshModel)facade.model();
				model.unboundTriangles();
				facade.window().repaintEverything();
			}
		}
	}

}
