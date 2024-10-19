package hu.csega.connection.vs;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;

public class EncryptContent {

	private EncryptBytes encryptBytes = new EncryptBytes();

	public String hashId(String id) {
		return encryptBytes.hash(id);
	}

	public String encrypt(String contentType, String content) {
		byte[] contentBytes = content.getBytes(EncryptBytes.UTF8);
		return encrypt(contentType, contentBytes);
	}

	public String encrypt(String contentType, byte[] content) {
		int contentLength = content.length;
		String header = contentLength + "|" + contentType + "|";
		byte[] headerBytes = header.getBytes(EncryptBytes.UTF8);

		int length = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(headerBytes, 0, headerBytes.length);
		length += headerBytes.length;
		baos.write(content, 0, content.length);
		length += content.length;
		int modulo = length % EncryptBytes.BLOCK_SIZE;
		if(modulo != 0) {
			int remainingBytes = EncryptBytes.BLOCK_SIZE - modulo;
			byte[] rest = new byte[remainingBytes];
			RANDOM.nextBytes(rest);
			baos.write(rest, 0, rest.length);
		}

		byte[] input = baos.toByteArray();
		byte[] result = encryptBytes.encrypt(input);
		return Base64.getEncoder().encodeToString(result);
	}

	public DecryptedContent decrypt(String encrypted) {
		byte[] encryptedBytes = Base64.getDecoder().decode(encrypted);
		byte[] result = encryptBytes.decrypt(encryptedBytes);

		int index = 0;
		while(result[index] != (byte)('|'))
			index++;

		int size = Integer.parseInt(new String(result, 0, index, EncryptBytes.UTF8));

		index++;
		int offset = index;
		while(result[index] != (byte)('|'))
			index++;

		String contentType = new String(result, offset, index - offset, EncryptBytes.UTF8);
		byte[] content = new byte[size];
		System.arraycopy(result, index+1, content, 0, size);
		return new DecryptedContent(size, contentType, content);
	}

	private static final Random RANDOM = new Random(System.currentTimeMillis());
}
