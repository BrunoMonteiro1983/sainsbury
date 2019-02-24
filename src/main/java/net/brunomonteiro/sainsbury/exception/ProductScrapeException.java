package net.brunomonteiro.sainsbury.exception;

public class ProductScrapeException extends RuntimeException {
	public ProductScrapeException(RuntimeException ex) {
		super(ex.getMessage(), ex.getCause());
	}
}
