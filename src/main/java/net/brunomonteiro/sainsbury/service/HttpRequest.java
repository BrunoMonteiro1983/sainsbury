package net.brunomonteiro.sainsbury.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class HttpRequest {
	public String getContent(String url) throws IOException {
		StringBuilder content = new StringBuilder();
		URLConnection con = new URL(url).openConnection();
		try(BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
			String line;
			while((line = in.readLine()) != null) {
				content.append(line);
			}
		}
	
		return content.toString();
	}
}
