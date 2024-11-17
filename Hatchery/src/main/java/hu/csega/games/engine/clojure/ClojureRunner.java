package hu.csega.games.engine.clojure;

import java.io.IOException;

import clojure.lang.Compiler;
import clojure.lang.RT;
import hu.csega.games.engine.GameEngineBuilder;
import hu.csega.games.engine.env.Environment;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

public class ClojureRunner {

	public static Object loadAndStartClojureProgram(Environment env, String filename) {
		Object ret;

		try {
			RT.init();
			ret = Compiler.loadFile(filename);
			if(ret != null) {
				logger.debug("Class of returned object: " + ret.getClass().getName());
			} else {
				logger.debug("No object is returned.");
			}

			if(ret == null || !ret.getClass().equals(GameEngineBuilder.class)) {
				logger.error("An instance of `trash.oldschool.engine.GameEngineBuilder` class should be returned!");
				ret = null;
			}

			if(ret != null) {
				GameEngineBuilder builder = (GameEngineBuilder) ret;
				builder.startEngine(env);
			}
		} catch (IOException e) {
			logger.error("Error occurred when loading / starting clojure program: " + filename);
			ret = null;
		}

		return ret;
	}

	private static final Logger logger = LoggerFactory.createLogger(ClojureRunner.class);
}
