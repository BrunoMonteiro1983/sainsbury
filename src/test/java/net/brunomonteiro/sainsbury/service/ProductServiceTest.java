package net.brunomonteiro.sainsbury.service;

import net.brunomonteiro.sainsbury.TestHelper;
import net.brunomonteiro.sainsbury.model.Product;
import net.brunomonteiro.sainsbury.model.TotalCost;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProductServiceTest {
	private ProductService productService;
	
	@Before
	public void setUp() {
		this.productService = new ProductService();
	}

	@Test
	public void testNoProductsReturnsEmptyList() {
		assertTrue(this.productService.getProducts().isEmpty());
	}

	@Test
	public void testOneProductReturnsProduct() {
		Product product = new Product("Title", "", "5");
		this.productService.add(product);
		
		assertEquals(1, this.productService.getProducts().size());
		assertEquals(product, this.productService.getProducts().get(0));
	}

	@Test
	public void testOneProductReturnsCorrectTotal() {
		this.productService.add(new Product("Title", "", "5"));
		
		TotalCost totalCost = this.productService.getTotal();
		assertEquals("5.00", totalCost.getGross().toString());
		assertEquals("0.83", totalCost.getVat().toString());
	}

	@Test
	public void testProductServiceListIsImmutable() {
		this.productService.add(new Product("Title", "", "5"));
		assertEquals(1, this.productService.getProducts().size());
		
		List<Product> list = this.productService.getProducts();
		list.add(new Product("Title 2", "", "1"));
		
		assertEquals(1, this.productService.getProducts().size());
		assertEquals("Title", this.productService.getProducts().get(0).getTitle());
	}

	@Test
	public void testTwoProductsAddUpToCorrectTotal() {
		Product productOne = new Product("Title 1", "Description", "8.23");
		Product productTwo = new Product("Title 2", "Description", "18.27");

		List<Product> productList = new ArrayList<>();
		productList.add(productOne);
		productList.add(productTwo);
		
		this.productService.addAll(productList);

		TotalCost totalCost = this.productService.getTotal();
		assertEquals("26.50", totalCost.getGross().toString());
		assertEquals("4.41", totalCost.getVat().toString());
	}

	@Test
	public void testEmptyListReturnsEmptyJson() {
		assertEquals("{\"results\":[],\"total\":{\"gross\":0.00,\"vat\":0.00}}",
			this.productService.toJson(false));
	}
	
	@Test
	public void testThreeProductsReturnProperJson() throws IOException, URISyntaxException {
		Product productOne = new Product("Sainsbury's Strawberries 400g", "by Sainsbury's strawberries", "1.75", 33);
		Product productTwo = new Product("Sainsbury's Blueberries 200g", "by Sainsbury's blueberries", "1.75", 45);
		Product productThree = new Product("Sainsbury's Cherry Punnet 200g", "Cherries", "1.5", 52);

		List<Product> productList = new ArrayList<>();
		productList.add(productOne);
		productList.add(productTwo);
		productList.add(productThree);
		
		this.productService.addAll(productList);

		String expectedJson = TestHelper.getFileContent("product-service-test-three-products.json");
		assertEquals(expectedJson, this.productService.toJson());
	}
}