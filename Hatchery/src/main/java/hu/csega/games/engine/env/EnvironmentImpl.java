package hu.csega.games.engine.env;

import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class EnvironmentImpl implements Environment {

	@Override
	public void registerForDisposing(Disposable disposable) {
		disposables.add(disposable);
	}

	@Override
	public void notifyExiting() {
		synchronized (this) {
			this.notify();
		}
	}

	/**
	 * Only reachable by the game main frame.
	 * @throws IOException
	 */
	public void waitForExiting() {
		try {

			synchronized (this) {
				this.wait();
			}

			logger.info("Exiting application.");
		} catch(InterruptedException ex) {
			throw new GameEngineException("Interruption when waiting.", ex)
			.description("Main running class was waiting for the game to finish while an "
					+ "InterruptedException occurred.");
		}
	}

	/**
	 * Only reachable by the game main frame.
	 * @throws IOException
	 */
	public void finish() {
		for(Disposable disposable : disposables) {
			disposable.dispose();
		}
	}

	private final Set<Disposable> disposables = new HashSet<>();

	private static final Logger logger = LoggerFactory.createLogger(EnvironmentImpl.class);
}
