package hu.csega.editors.ftm.layer4.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hu.csega.editors.ftm.layer5.integration.FileSystemIntegration;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

public class FreeTriangleMeshSnapshots {

	private final List<byte[]> previousStates = new ArrayList<>();
	private final List<byte[]> nextStates = new ArrayList<>();

	public void addState(Serializable state) {
		byte[] snapshot = FileSystemIntegration.serialize(state);
		previousStates.add(snapshot);
		nextStates.clear();
	}

	public void removeTopState() {
		previousStates.remove(previousStates.size() - 1);
	}

	public Serializable undo(Serializable current) {
		if(previousStates.isEmpty())
			return null;

		byte[] currentState = FileSystemIntegration.serialize(current);
		nextStates.add(currentState);
		byte[] snapshot = previousStates.remove(previousStates.size() - 1);
		return FileSystemIntegration.deserialize(snapshot);
	}

	public Serializable redo(Serializable current) {
		if(nextStates.isEmpty())
			return null;

		byte[] currentState = FileSystemIntegration.serialize(current);
		previousStates.add(currentState);
		byte[] snapshot = nextStates.remove(nextStates.size() - 1);
		return FileSystemIntegration.deserialize(snapshot);
	}

	public void clear() {
		previousStates.clear();
		nextStates.clear();
	}

	public Serializable currentSnapshot() {
		byte[] snapshot = previousStates.get(previousStates.size() - 1);
		return FileSystemIntegration.deserialize(snapshot);
	}

	public static void writeAllBytes(String fileName, byte[] bytes) {
		File file = new File(fileName);
		writeAllBytes(file, bytes);
	}

	public static void writeAllBytes(File file, byte[] bytes) {
		if(bytes == null || bytes.length == 0) {
			logger.error("Not writing zero sized file: " + file.getName());
			return;
		}

		try (OutputStream stream = new FileOutputStream(file)) {
			stream.write(bytes);
		} catch(IOException ex) {
			logger.error("Error during writing file: " + file.getName(), ex);
		}
	}

	public static byte[] readAllBytes(String fileName) {
		File file = new File(fileName);
		return readAllBytes(file);
	}

	public static byte[] readAllBytes(File file) {
		int size = (int)file.length();
		if(size == 0) {
			logger.error("Zero sized file: " + file.getName());
			return null;
		}

		byte[] ret = new byte[size];
		byte[] array = new byte[2000];
		int pos = 0;
		int read;

		try (InputStream ios = new FileInputStream(file)) {
			while ( (read = ios.read(array, 0, 2000)) >= 0 ) {
				if(read == 0)
					continue;

				if(pos + read > size) {
					logger.error("Invalid sized file: " + file.getName());
					return null;
				}

				System.arraycopy(array, 0, ret, pos, read);
				pos += read;
			}
		} catch(IOException ex) {
			logger.error("Error during reading file: " + file.getName(), ex);
			return null;
		}

		return ret;
	}

	public static byte[] readAllBytes(InputStream stream) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] array = new byte[2000];
		int read;

		try {
			while ( (read = stream.read(array, 0, 2000)) >= 0 ) {
				if(read == 0)
					continue;

				baos.write(array, 0, read);
			}
		} catch(IOException ex) {
			logger.error("Error during reading stream.", ex);
			return null;
		} finally {
			try {
				stream.close();
			} catch (IOException ex) {
				logger.warning("Could not close stream.");
			}
		}

		return baos.toByteArray();
	}

	private static final Logger logger = LoggerFactory.createLogger(FreeTriangleMeshSnapshots.class);
}
