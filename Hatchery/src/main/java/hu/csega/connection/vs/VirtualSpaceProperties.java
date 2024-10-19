package hu.csega.connection.vs;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Properties;

public class VirtualSpaceProperties {

	private static final Properties PROPERTIES = new Properties();

	public static final String CONNECTION_USERNAME;
	public static final String CONNECTION_PASSWORD;
	public static final String CONNECTION_ENDPOINT;
	public static final byte[] ENCRYPTION_KEY;
	public static final byte[] ENCRYPTION_IV;

	static {
		try (InputStream is = new FileInputStream("conf/virtual-space.properties")) {
			PROPERTIES.load(is);

			CONNECTION_USERNAME = PROPERTIES.getProperty("connection.username");
			CONNECTION_PASSWORD = PROPERTIES.getProperty("connection.password");
			CONNECTION_ENDPOINT = PROPERTIES.getProperty("connection.endpoint");
			Base64.Decoder base64Decoder = Base64.getDecoder();
			ENCRYPTION_KEY = base64Decoder.decode(PROPERTIES.getProperty("encryption.key"));
			ENCRYPTION_IV = base64Decoder.decode(PROPERTIES.getProperty("encryption.iv"));
		} catch(Exception ex) {
			throw new RuntimeException("Error loading properties!", ex);
		}
	}

}
