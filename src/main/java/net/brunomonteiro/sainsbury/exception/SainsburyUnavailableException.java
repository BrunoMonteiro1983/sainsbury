package net.brunomonteiro.sainsbury.exception;

import java.io.IOException;

public class SainsburyUnavailableException extends RuntimeException {
	public SainsburyUnavailableException(String url, String message) {
		super("Calling " + url + " returned an error: " + message);
	}
}
