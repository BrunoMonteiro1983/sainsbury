package net.brunomonteiro.sainsbury.service;

import net.brunomonteiro.sainsbury.TestHelper;
import net.brunomonteiro.sainsbury.exception.MissingProductFieldException;
import net.brunomonteiro.sainsbury.exception.ProductScrapeException;
import net.brunomonteiro.sainsbury.exception.SainsburyUnavailableException;
import net.brunomonteiro.sainsbury.model.Product;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.stubbing.OngoingStubbing;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.Mockito.*;

public class SainsburyProductScraperTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testEmptyResultReturnsEmptyList() throws IOException {
		String htmlContent = "";
		ProductScraper scraper = this.getScraperWithContentMocked(htmlContent);
		assertTrue(scraper.getProducts().isEmpty());
	}

	@Test(expected = SainsburyUnavailableException.class)
	public void testIOExceptionThrowsSainsburyUnavailableException() throws IOException {
		HttpRequest httpRequest = mock(HttpRequest.class);
		when(httpRequest.getContent(any())).thenThrow(new IOException());
		new SainsburyProductScraper(httpRequest);
	}

	@Test(expected = ProductScrapeException.class)
	public void testProductWithoutDetailsURLThrowsProductScrapeException() throws IOException {
		String htmlContent = "<ul class=\"productLister\"><li class=\"product\"><h3></h3></li></ul>";
		this.getScraperWithContentMocked(htmlContent);
	}

	@Test
	public void testProvidedProductDetailsURLIsCalled() throws IOException {
		String htmlContent = "<ul class=\"productLister\"><li class=\"product\"><h3><a href='ABC'></a></h3></li></ul>";
		String productContent = "<div class=\"productSummary\"><div class=\"productTitleDescriptionContainer\">Title</div></div>"
			+ "<div id=\"information\"><div class=\"productText\"><p>Description</p></div></div>"
			+ "<div class=\"pricing\"><div class=\"pricePerUnit\">12.5£</div></div>"
			+ "<table class=\"nutritionTable\"><tr><td>19kcal</td></tr></table>";

		HttpRequest httpRequest = mock(HttpRequest.class);
		when(httpRequest.getContent(any())).thenReturn(htmlContent).thenReturn(productContent);
		new SainsburyProductScraper(httpRequest);

		verify(httpRequest).getContent(endsWith("ABC"));
	}

	@Test
	public void testFullProductScrapeIsSuccessful() throws IOException {
		String htmlContent = "<ul class=\"productLister\"><li class=\"product\"><h3><a href=''></a></h3></li></ul>";
		String productContent = "<div class=\"productSummary\"><div class=\"productTitleDescriptionContainer\">Title</div></div>"
			+ "<div id=\"information\"><div class=\"productText\"><p>Description</p></div></div>"
			+ "<div class=\"pricing\"><div class=\"pricePerUnit\">12.5£</div></div>"
			+ "<table class=\"nutritionTable\"><tr><td>19kcal</td></tr></table>";

		ProductScraper scraper = this.getScraperWithContentMocked(htmlContent, productContent);
		assertEquals(1, scraper.getProducts().size());

		Product product = scraper.getProducts().get(0);
		assertEquals("Title", product.getTitle());
		assertEquals("Description", product.getDescription());
		assertEquals("12.5", product.getUnitPrice().toString());
		assertEquals(19, (int) product.getKiloCalPer100g());
	}

	@Test
	public void testMissingKiloCalReturnsProductWithoutIt() throws IOException {
		String htmlContent = "<ul class=\"productLister\"><li class=\"product\"><h3><a href=''></a></h3></li></ul>";
		String productContent = "<div class=\"productSummary\"><div class=\"productTitleDescriptionContainer\">Title</div></div>"
			+ "<div id=\"information\"><div class=\"productText\"><p>Description</p></div></div>"
			+ "<div class=\"pricing\"><div class=\"pricePerUnit\">12.5£</div></div>";

		ProductScraper scraper = this.getScraperWithContentMocked(htmlContent, productContent);
		assertEquals(1, scraper.getProducts().size());

		Product product = scraper.getProducts().get(0);
		assertEquals("Title", product.getTitle());
		assertEquals("Description", product.getDescription());
		assertEquals("12.5", product.getUnitPrice().toString());
		assertNull(product.getKiloCalPer100g());
	}

	@Test
	public void testMissingTitleReturnsMissingProductFieldException() throws IOException {
		String htmlContent = "<ul class=\"productLister\"><li class=\"product\"><h3><a href=''></a></h3></li></ul>";
		String productContent = "<div class=\"productSummary\"></div>"
			+ "<div id=\"information\"><div class=\"productText\"><p>Description</p></div></div>"
			+ "<div class=\"pricing\"><div class=\"pricePerUnit\">12.5£</div></div>";

		this.thrown.expect(MissingProductFieldException.class);
		this.thrown.expectMessage("Title");
		this.getScraperWithContentMocked(htmlContent, productContent);
	}

	@Test
	public void testEmptyTitleReturnsMissingProductFieldException() throws IOException {
		String htmlContent = "<ul class=\"productLister\"><li class=\"product\"><h3><a href=''></a></h3></li></ul>";
		String productContent = "<div class=\"productSummary\"><div class=\"productTitleDescriptionContainer\"></div></div></div>"
			+ "<div id=\"information\"><div class=\"productText\"><p>Description</p></div></div>"
			+ "<div class=\"pricing\"><div class=\"pricePerUnit\">12.5£</div></div>";

		this.thrown.expect(MissingProductFieldException.class);
		this.thrown.expectMessage("Title");
		this.getScraperWithContentMocked(htmlContent, productContent);
	}

	@Test
	public void testMissingDescriptionReturnsMissingProductFieldException() throws IOException {
		String htmlContent = "<ul class=\"productLister\"><li class=\"product\"><h3><a href=''></a></h3></li></ul>";
		String productContent = "<div class=\"productSummary\"><div class=\"productTitleDescriptionContainer\">Title</div></div>"
			+ "<div id=\"information\"></div>"
			+ "<div class=\"pricing\"><div class=\"pricePerUnit\">12.5£</div></div>";

		ProductScraper scraper = this.getScraperWithContentMocked(htmlContent, productContent);
		assertEquals(1, scraper.getProducts().size());

		Product product = scraper.getProducts().get(0);
		assertTrue(product.getDescription().isEmpty());
	}

	@Test
	public void testEmptyDescriptionReturnsProductWithEmptyDescription() throws IOException {
		String htmlContent = "<ul class=\"productLister\"><li class=\"product\"><h3><a href=''></a></h3></li></ul>";
		String productContent = "<div class=\"productSummary\"><div class=\"productTitleDescriptionContainer\">Title</div></div>"
			+ "<div id=\"information\"><div class=\"productText\"><p></p></div></div>"
			+ "<div class=\"pricing\"><div class=\"pricePerUnit\">12.5£</div></div>";

		ProductScraper scraper = this.getScraperWithContentMocked(htmlContent, productContent);
		assertEquals(1, scraper.getProducts().size());

		Product product = scraper.getProducts().get(0);
		assertTrue(product.getDescription().isEmpty());
	}

	@Test
	public void testMissingPriceReturnsMissingProductFieldException() throws IOException {
		String htmlContent = "<ul class=\"productLister\"><li class=\"product\"><h3><a href=''></a></h3></li></ul>";
		String productContent = "<div class=\"productSummary\"><div class=\"productTitleDescriptionContainer\">Title</div></div>"
			+ "<div id=\"information\"><div class=\"productText\"><p>Description</p></div></div>"
			+ "<div class=\"pricing\"></div>";

		this.thrown.expect(MissingProductFieldException.class);
		this.thrown.expectMessage("Price");
		this.getScraperWithContentMocked(htmlContent, productContent);
	}

	@Test
	public void testEmptyPriceReturnsMissingProductFieldException() throws IOException {
		String htmlContent = "<ul class=\"productLister\"><li class=\"product\"><h3><a href=''></a></h3></li></ul>";
		String productContent = "<div class=\"productSummary\"><div class=\"productTitleDescriptionContainer\">Title</div></div>"
			+ "<div id=\"information\"><div class=\"productText\"><p>Description</p></div></div>"
			+ "<div class=\"pricing\"><div class=\"pricePerUnit\"></div></div>";

		this.thrown.expect(MissingProductFieldException.class);
		this.thrown.expectMessage("Price");
		this.getScraperWithContentMocked(htmlContent, productContent);
	}

	@Test
	public void testInvalidPriceReturnsProductScrapeException() throws IOException {
		String htmlContent = "<ul class=\"productLister\"><li class=\"product\"><h3><a href=''></a></h3></li></ul>";
		String productContent = "<div class=\"productSummary\"><div class=\"productTitleDescriptionContainer\">Title</div></div>"
			+ "<div id=\"information\"><div class=\"productText\"><p>Description</p></div></div>"
			+ "<div class=\"pricing\"><div class=\"pricePerUnit\">£</div></div>";

		this.thrown.expect(MissingProductFieldException.class);
		this.thrown.expectMessage("Price");
		this.getScraperWithContentMocked(htmlContent, productContent);
	}

	@Test
	public void testMultipleProductsScrapeIsSuccessful() throws IOException, URISyntaxException {
		String htmlContent = TestHelper.getFileContent("scraper-test-multiple-products.html");
		String blueberriesHtmlContent = TestHelper.getFileContent("scraper-test-multiple-products-blueberries.html");
		String cherryHtmlContent = TestHelper.getFileContent("scraper-test-multiple-products-cherry.html");
		String berriesHtmlContent = TestHelper.getFileContent("scraper-test-multiple-products-berries.html");

		ProductScraper scraper = this.getScraperWithContentMocked(htmlContent, blueberriesHtmlContent,
			cherryHtmlContent, berriesHtmlContent);

		assertEquals(3, scraper.getProducts().size());

		Product blueberriesProduct = scraper.getProducts().get(0);
		assertEquals("Sainsbury's Blueberries 200g", blueberriesProduct.getTitle());
		assertEquals("by Sainsbury's blueberries", blueberriesProduct.getDescription());
		assertEquals("1.75", blueberriesProduct.getUnitPrice().toString());
		assertEquals(45, (int) blueberriesProduct.getKiloCalPer100g());

		Product cherryProduct = scraper.getProducts().get(1);
		assertEquals("Sainsbury's Cherry Punnet 200g", cherryProduct.getTitle());
		assertEquals("Cherries", cherryProduct.getDescription());
		assertEquals("1.50", cherryProduct.getUnitPrice().toString());
		assertEquals(52, (int) cherryProduct.getKiloCalPer100g());

		Product berriesProduct = scraper.getProducts().get(2);
		assertEquals("Sainsbury's Mixed Berries 300g", berriesProduct.getTitle());
		assertEquals("by Sainsbury's mixed berries", berriesProduct.getDescription());
		assertEquals("3.50", berriesProduct.getUnitPrice().toString());
		assertNull(berriesProduct.getKiloCalPer100g());
	}

	private ProductScraper getScraperWithContentMocked(String htmlContent) throws IOException {
		HttpRequest httpRequest = mock(HttpRequest.class);
		when(httpRequest.getContent(any())).thenReturn(htmlContent);

		return new SainsburyProductScraper(httpRequest);
	}

	private ProductScraper getScraperWithContentMocked(String htmlContent, String... productsContent) throws IOException {
		HttpRequest httpRequest = mock(HttpRequest.class);
		OngoingStubbing<String> mockedAction = when(httpRequest.getContent(any())).thenReturn(htmlContent);

		for(String productContent : productsContent) {
			mockedAction = mockedAction.thenReturn(productContent);
		}

		return new SainsburyProductScraper(httpRequest);
	}
}
