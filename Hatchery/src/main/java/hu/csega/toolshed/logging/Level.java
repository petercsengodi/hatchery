package hu.csega.toolshed.logging;

public enum Level implements Comparable<Level> {
	NONE(0), ERROR(10), WARNING(20), INFO(30), DEBUG(40), TRACE(50);
	
	private final int severity;
	
	private Level(int severity) {
		this.severity = severity;
	}

	public int getSeverity() {
		return severity;
	}
}
