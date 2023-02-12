package hu.csega.toolshed.logging;

public class LoggerFactory {

	private static Level defaultLevel = Level.DEBUG;

	public static Logger getDefaultImplementation(Class<?> loggedClass) {
		return new ConsoleLogger(loggedClass.getSimpleName(), defaultLevel);
	}

	public static Logger createLogger(Class<?> loggedClass) {
		return getDefaultImplementation(loggedClass);
	}

	public static void setDefaultLevel(Level level) {
		defaultLevel = level;
	}
}
