package hu.csega.toolshed.logging;

public abstract class AbstractLogger implements Logger {

	private final Level level;
	private final String className;

	protected AbstractLogger(String className, Level level) {
		this.level = level;
		this.className = className;
	}

	protected abstract void print(String className, Level level, String message, Throwable t);

	@Override
	public void trace(Object message) {
		trace(message, null);
	}

	@Override
	public void trace(Object message, Throwable t) {
		if(level.getSeverity() >= Level.TRACE.getSeverity())
			print(className, Level.TRACE, convert(message), t);
	}

	@Override
	public void debug(Object message) {
		debug(message, null);
	}

	@Override
	public void debug(Object message, Throwable t) {
		if(level.getSeverity() >= Level.DEBUG.getSeverity())
			print(className, Level.DEBUG, convert(message), t);
	}

	@Override
	public void info(Object message) {
		info(message, null);
	}

	@Override
	public void info(Object message, Throwable t) {
		if(level.getSeverity() >= Level.INFO.getSeverity())
			print(className, Level.INFO, convert(message), t);
	}

	@Override
	public void warning(Object message) {
		warning(message, null);
	}

	@Override
	public void warning(Object message, Throwable t) {
		if(level.getSeverity() >= Level.WARNING.getSeverity())
			print(className, Level.WARNING, convert(message), t);
	}

	@Override
	public void error(Object message) {
		error(message, null);
	}

	@Override
	public void error(Object message, Throwable t) {
		if(level.getSeverity() >= Level.ERROR.getSeverity())
			print(className, Level.ERROR, convert(message), t);
	}

	@Override
	public boolean traceShown() {
		return (level.getSeverity() >= Level.TRACE.getSeverity());
	}

	@Override
	public boolean debugShown() {
		return (level.getSeverity() >= Level.DEBUG.getSeverity());
	}

	@Override
	public boolean infoShown() {
		return (level.getSeverity() >= Level.INFO.getSeverity());
	}

	@Override
	public boolean warningShown() {
		return (level.getSeverity() >= Level.WARNING.getSeverity());
	}

	@Override
	public boolean errorShown() {
		return (level.getSeverity() >= Level.ERROR.getSeverity());
	}

	private static final String convert(Object object) {
		return (object == null ? "null" : object.toString());
	}
}
