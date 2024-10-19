package hu.csega.connection.vs;

public class DecryptedContent {

	private int size;
	private String contentType;
	private byte[] content;

	public DecryptedContent(int size, String contentType, byte[] content) {
		this.size = size;
		this.contentType = contentType;
		this.content = content;
	}

	public int getSize() {
		return size;
	}

	public String getContentType() {
		return contentType;
	}

	public byte[] asBytes() {
		return content;
	}

	public String asString() {
		return new String(content, EncryptBytes.UTF8);
	}

}
