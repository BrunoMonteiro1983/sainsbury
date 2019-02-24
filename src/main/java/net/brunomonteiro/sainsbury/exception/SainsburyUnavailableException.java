package net.brunomonteiro.sainsbury.exception;

public class SainsburyUnavailableException extends RuntimeException {
	public SainsburyUnavailableException(String url, String message) {
		super("Calling " + url + " returned an error: " + message);
	}
}
