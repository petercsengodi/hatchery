package hu.csega;

import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import org.junit.Test;

public class PlaceholderTest {

	@Test
	public void test() {
		logger.info("This is just a placeholder test.");
	}

	private static final Logger logger = LoggerFactory.createLogger(PlaceholderTest.class);

}
