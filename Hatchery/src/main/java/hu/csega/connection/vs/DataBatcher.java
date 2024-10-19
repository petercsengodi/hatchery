package hu.csega.connection.vs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataBatcher {

	private static EncryptContent encryptContent = new EncryptContent();

	public static void main(String[] args) throws JSONException {
		JSONRequest connection = new JSONRequest();
		if (connection.authenticate()) {

			// check version
			boolean connectionSucceeded;

			{
				JSONObject request = new JSONObject();
				request.put("action", "version");
				JSONObject response = connection.send(request);

				System.out.println(response.toString(2));

				connectionSucceeded = !response.has("error");
				System.out.println("Connection succeeded: " + connectionSucceeded);
				System.out.println();
			}

			/*
			if (connectionSucceeded) {
				// deleteContent(connection, "/");

				byte[] contentBytes = "<html></html>".getBytes(EncryptBytes.UTF8);
				writeContent(connection, "/", "text/html; charset=utf-8", contentBytes);

				DecryptedContent dc = readContent(connection, "/");
				System.out.println(dc.asString());
			}
			*/

		}
	}

	private JSONRequest connection;
	private boolean working;

	public DataBatcher() {
		working = false;

		try {
			connection = new JSONRequest();
			if (connection.authenticate()) {

				JSONObject request = new JSONObject();
				request.put("action", "version");
				JSONObject response = connection.send(request);

				System.out.println(response.toString(2));

				working = !response.has("error");
				System.out.println("Connection succeeded: " + working);
				System.out.println();
			}
		} catch(Exception ex) {
			throw new RuntimeException("Error when initializing data batcher!", ex);
		}
	}

	public void store(String id, String contentType, String content) {
		byte[] bytes = content.getBytes(EncryptBytes.UTF8);
		store(id, contentType, bytes);
	}

	public void store(String id, String contentType, byte[] content) {
		try {
			writeContent(connection, id, contentType, content);
		} catch(Exception ex) {
			throw new RuntimeException("Error while storing content!", ex);
		}
	}

	public DecryptedContent load(String id) {
		try {
			return readContent(connection, id);
		} catch(Exception ex) {
			throw new RuntimeException("Error while loading content!", ex);
		}
	}

	public void remove(String id) {
		try {
			deleteContent(connection, id);
		} catch(Exception ex) {
			throw new RuntimeException("Error while removing content!", ex);
		}
	}

	static void deleteContent(JSONRequest connection, String id) throws JSONException {
		String sendId = encryptContent.hashId(id);

		JSONObject request = new JSONObject();
		request.put("action", "delete");
		request.put("id", sendId);
		connection.send(request);
	}

	static void writeContent(JSONRequest connection, String id, String contentType, byte[] content) throws JSONException {
		String sendId = encryptContent.hashId(id);
		String sendContent = encryptContent.encrypt(contentType, content);
		String sendStatus = "P";

		JSONObject item = new JSONObject();
		item.put("id", sendId);
		item.put("content", sendContent);
		item.put("status", sendStatus);

		JSONArray array = new JSONArray();
		array.put(item);

		JSONObject request = new JSONObject();
		request.put("action", "write");
		request.put("items", array);

		connection.send(request);
	}

	static DecryptedContent readContent(JSONRequest connection, String id) throws JSONException {
		String sendId = encryptContent.hashId(id);

		JSONObject request = new JSONObject();
		request.put("action", "read");
		request.put("id", sendId);

		JSONObject response = connection.send(request);
		JSONArray results = response.getJSONArray("results");

		JSONObject json = results.getJSONObject(0);
		// String sentId = json.getString("id");
		// String status = json.getString("status");
		// String lastModified = json.getString("lastModified");
		String sentContent = json.getString("content");

		return encryptContent.decrypt(sentContent);
	}

}
