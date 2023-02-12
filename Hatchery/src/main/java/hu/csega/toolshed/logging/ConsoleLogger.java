package hu.csega.toolshed.logging;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class ConsoleLogger extends AbstractLogger {

	protected ConsoleLogger(String className, Level level) {
		super(className, level);
	}

	@Override
	protected void print(String className, Level level, String message, Throwable t) {
		StringBuilder builder = new StringBuilder()
				.append(level.name())
				.append(" - ")
				.append(className)
				.append(" : ")
				.append(message);

		if(t != null) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			PrintWriter writer = new PrintWriter(stream);
			t.printStackTrace(writer);
			writer.flush();
			writer.close();

			try {
				builder.append('\n').append(new String(stream.toByteArray(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// we just simply print stacktraces; but this won't happen
				e.printStackTrace();
				t.printStackTrace();
			}
		}

		System.out.println(builder.toString());
	}

}
