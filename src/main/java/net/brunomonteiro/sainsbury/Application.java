package net.brunomonteiro.sainsbury;

import net.brunomonteiro.sainsbury.model.Product;
import net.brunomonteiro.sainsbury.service.HttpRequest;
import net.brunomonteiro.sainsbury.service.SainsburyProductScraper;

import java.util.List;

public class Application {
	public static void main(String... args) {
		List<Product> products = new SainsburyProductScraper(new HttpRequest()).getProducts();
		for(Product product : products) {
			System.out.println(product.getTitle() + " - " + product.getUnitPrice() + "£");
		}
	}
}