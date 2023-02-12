package hu.csega.toolshed.logging;

public interface Logger {

	public void trace(Object message);

	public void trace(Object message, Throwable t);

	public void debug(Object message);

	public void debug(Object message, Throwable t);

	public void info(Object message);

	public void info(Object message, Throwable t);

	public void warning(Object message);

	public void warning(Object message, Throwable t);

	public void error(Object message);

	public void error(Object message, Throwable t);

	public boolean traceShown();

	public boolean debugShown();

	public boolean infoShown();

	public boolean warningShown();

	public boolean errorShown();

}
