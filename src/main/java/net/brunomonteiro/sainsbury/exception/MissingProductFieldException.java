package net.brunomonteiro.sainsbury.exception;

public class MissingProductFieldException extends RuntimeException {
	public MissingProductFieldException(String message) {
		super(message);
	}
}
