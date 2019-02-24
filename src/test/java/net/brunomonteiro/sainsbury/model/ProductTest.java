package net.brunomonteiro.sainsbury.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ProductTest {

	@Test
	public void testProductDataIsReturned() {
		Product product = new Product("Title", "Description", "1.01", 100);
		assertEquals("Title", product.getTitle());
		assertEquals("Description", product.getDescription());
		assertEquals("1.01", product.getUnitPrice().toString());
		assertEquals(100, (int) product.getKiloCalPer100g());
	}

	@Test
	public void testProductWithoutKiloCalReturnsNullOnKiloCal() {
		Product product = new Product("Title", "Description", "1.01");
		assertNull(product.getKiloCalPer100g());
	}

	@Test
	public void testUnitPricePrecisionIsRoundToTwoDecimals() {
		Product product = new Product("Title", "Description", "1.055");
		assertEquals("1.06", product.getUnitPrice().toString());
	}

	@Test
	public void testLowerThanTwoDecimalUnitPriceKeepsPrecision() {
		Product product = new Product("Title", "Description", "1");
		assertEquals("1", product.getUnitPrice().toString());

		product = new Product("Title", "Description", "1.5");
		assertEquals("1.5", product.getUnitPrice().toString());
	}
}
