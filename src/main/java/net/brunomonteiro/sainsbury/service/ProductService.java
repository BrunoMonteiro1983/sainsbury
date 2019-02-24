package net.brunomonteiro.sainsbury.service;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import net.brunomonteiro.sainsbury.model.Product;
import net.brunomonteiro.sainsbury.model.TotalCost;

import java.util.ArrayList;
import java.util.List;

public class ProductService {

	@SerializedName("results")
	private List<Product> products = new ArrayList<>();
	
	private TotalCost total = new TotalCost();
	
	public void addAll(List<Product> products) {
		for(Product product : products) {
			this.add(product);
		}
	}
	
	public void add(Product product) {
		this.total.addGross(product.getUnitPrice());
		this.products.add(product);
	}

	public TotalCost getTotal() {
		return this.total;
	}
	
	public List<Product> getProducts() {
		return new ArrayList<>(this.products);
	}

	public String toJson() {
		return this.toJson(true);
	}

	public String toJson(boolean prettyPrinting) {
		GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping();

		if(prettyPrinting) {
			gsonBuilder.setPrettyPrinting();
		}

		return gsonBuilder.create().toJson(this);
	}
}
