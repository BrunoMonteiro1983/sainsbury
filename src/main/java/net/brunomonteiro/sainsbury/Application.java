package net.brunomonteiro.sainsbury;

import net.brunomonteiro.sainsbury.model.Product;
import net.brunomonteiro.sainsbury.service.HttpRequest;
import net.brunomonteiro.sainsbury.service.ProductService;
import net.brunomonteiro.sainsbury.service.SainsburyProductScraper;

import java.util.List;

public class Application {
	public static void main(String... args) {
		List<Product> products = new SainsburyProductScraper(new HttpRequest()).getProducts();
		ProductService productService = new ProductService();
		productService.addAll(products);
		
		System.out.println(productService.toJson());
	}
}