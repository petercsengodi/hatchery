package hu.csega.connection.vs;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONRequest {

	private String username = VirtualSpaceProperties.CONNECTION_USERNAME;
	private String password = VirtualSpaceProperties.CONNECTION_PASSWORD;
	private String endpoint = VirtualSpaceProperties.CONNECTION_ENDPOINT;

    public boolean authenticate() {

    	try {
            JSONObject request = new JSONObject();
            request.put("action", "login");
            request.put("username", username);
            request.put("password", password);

            JSONObject response = send(request);

            if(response.has("error")) {
                System.out.println("ERROR: " + response.optString("mnemonic") + " Description: " + response.optString("error"));
                return false;
            }

            return true;
    	} catch(Exception ex) {
    		throw new RuntimeException("Authentication failed!", ex);
    	}
    }

    public JSONObject send(JSONObject request) {
        String content = request.toString();
        System.out.println("\nSending: " + content + '\n');
        byte[] bytes = content.getBytes(UTF_8);
        int length = bytes.length;

        String responseString = null;

        try {
            URL url = new URL(endpoint);
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setUseCaches(false);
            http.setDoOutput(true);

            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            if(phpSessionId != null) {
                http.setRequestProperty("Cookie", COOKIE_PREFIX + phpSessionId);
            }

            http.connect();

            try(OutputStream os = http.getOutputStream()) {
                os.write(bytes);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try(InputStream is = http.getInputStream()) {
                int l = 0;
                while((l = is.read(buffer)) >= 0) {
                    if(l == 0)
                        continue;

                    baos.write(buffer, 0, l);
                }

                bytes = baos.toByteArray();
                length = bytes.length;
            }

            responseString = new String(bytes, UTF_8);
            JSONObject response = new JSONObject(responseString);

            Map<String, List<String>> headerFields = http.getHeaderFields();
            for(Entry<String, List<String>> entry : headerFields.entrySet()) {
                if("Set-Cookie".equalsIgnoreCase(entry.getKey())) {
                    List<String> cookies = entry.getValue();
                    for(String cookie : cookies) {
                        if(cookie.startsWith(COOKIE_PREFIX)) {
                            String suffix = cookie.substring(COOKIE_PREFIX.length());
                            String original = phpSessionId;

                            int index = suffix.indexOf(';');
                            if(index < 0) {
                                phpSessionId = suffix;
                            } else {
                                phpSessionId = suffix.substring(0, index);
                            }

                            if(original == null || !original.equals(phpSessionId))
                                System.out.println("PHP Session ID set to: " + phpSessionId);
                        }
                    }
                }
            }

            return response;
        } catch(JSONException ex) {
        	System.out.println("RESPONSE STRING:\n" + responseString);
        	System.out.flush();
            throw new RuntimeException("Error during sending request!", ex);
        } catch(Exception ex) {
            throw new RuntimeException("Error during sending request!", ex);
        }
    }

    private String phpSessionId = null;
    private byte[] buffer = new byte[2048];

    private static final String COOKIE_PREFIX = "PHPSESSID=";

    private static final Charset UTF_8 = Charset.forName("UTF-8");
}
