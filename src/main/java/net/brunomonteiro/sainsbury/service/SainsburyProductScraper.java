package net.brunomonteiro.sainsbury.service;

import net.brunomonteiro.sainsbury.exception.MissingProductFieldException;
import net.brunomonteiro.sainsbury.exception.ProductScrapeException;
import net.brunomonteiro.sainsbury.exception.SainsburyUnavailableException;
import net.brunomonteiro.sainsbury.model.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SainsburyProductScraper implements ProductScraper {
	private static final String URL = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";

	private static final String PRICE_REGEX = "\\d+(\\.\\d+)?";
	private static final String LEFT_KILO_CAL_REGEX = "(\\d+)\\s*kcal";
	private static final String RIGHT_KILO_CAL_REGEX = "kcal\\s*(\\d+)";
	
	private List<Product> products;
	private HttpRequest httpRequest;
	
	public SainsburyProductScraper(HttpRequest httpRequest) {
		this.httpRequest = httpRequest;
		this.products = new ArrayList<>();
		
		try {
			this.retrieveProducts();
			
		} catch(SainsburyUnavailableException | MissingProductFieldException ex) {
			throw ex;
			
		} catch(RuntimeException ex) {
			throw new ProductScrapeException(ex);
		}
	}
	
	private void retrieveProducts() {
		Document doc = this.getPageContent(URL);
		Elements productsElement = doc.select(".productLister .product");
		for(Element productElement : productsElement) {
			String detailsUrl = productElement.selectFirst("h3 a").attr("abs:href");
			this.products.add(this.getProduct(detailsUrl));
		}
	}
	
	private Product getProduct(String detailsUrl) {
		Document doc = this.getPageContent(detailsUrl);
		
		String title = this.getTitle(doc);
		String description = this.getDescription(doc);
		String unitPrice = this.getUnitPrice(doc);
		
		Product product = new Product(title, description, unitPrice);
		this.getKiloCalPer100g(doc).ifPresent(product::setKiloCalPer100g);
		
		return product;
	}
	
	private String getTitle(Document doc) {
		try {
			String title = doc.selectFirst(".productSummary .productTitleDescriptionContainer").text();
			if(!title.isEmpty()) {
				return title;
			}
		} catch(NullPointerException ignored) {}

		throw new MissingProductFieldException("Title");
	}
	
	private String getDescription(Document doc) {
		Elements rows = doc.select("#information .productText p");
		for(Element row : rows) {
			if(!row.text().isEmpty()) {
				return row.text();
			}
		}
		return "";
	}
	
	private String getUnitPrice(Document doc) {
		String priceText = this.getUnitPriceText(doc);
		Pattern decimalPattern = Pattern.compile(PRICE_REGEX);
		Matcher matcher = decimalPattern.matcher(priceText);

		if(!matcher.find() || matcher.group().isEmpty()) {
			throw new MissingProductFieldException("Price");
		}

		return matcher.group();
	}
	
	private String getUnitPriceText(Document doc) {
		try {
			return doc.selectFirst(".pricing .pricePerUnit").ownText();

		} catch(NullPointerException ignored) {}

		throw new MissingProductFieldException("Price");
	}

	private OptionalInt getKiloCalPer100g(Document doc) {
		String tableContent = doc.select(".nutritionTable").text();

		OptionalInt kCal = this.getIntegerFromMatch(LEFT_KILO_CAL_REGEX, tableContent);
		if(kCal.isPresent()) return kCal;

		return this.getIntegerFromMatch(RIGHT_KILO_CAL_REGEX, tableContent);
	}

	private OptionalInt getIntegerFromMatch(String regex, String content) {
		Pattern kCalPattern = Pattern.compile(regex);
		Matcher matcher = kCalPattern.matcher(content);

		if(matcher.find() && !matcher.group(1).isEmpty()) {
			return OptionalInt.of(Integer.parseInt(matcher.group(1)));
		}

		return OptionalInt.empty();
	}
	
	private Document getPageContent(String url) {
		try {
			String htmlContent = this.httpRequest.getContent(url);
			return Jsoup.parse(htmlContent, url);
			
		} catch(IOException ex) {
			throw new SainsburyUnavailableException(url, ex.getMessage());
		}
	}
	
	public List<Product> getProducts() {
		return this.products;
	}
}
