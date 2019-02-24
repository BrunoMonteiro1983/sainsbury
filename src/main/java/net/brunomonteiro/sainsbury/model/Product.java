package net.brunomonteiro.sainsbury.model;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class Product {
	private static final int MAX_DECIMAL_PRECISION = 2;
	
	private String title;

	@SerializedName("kcal_per_100g")
	private Integer kiloCalPer100g;

	@SerializedName("unit_price")
	private BigDecimal unitPrice;
	
	private String description;

	public Product(String title, String description, String unitPrice) {
		this.title = title;
		this.description = description;
		this.unitPrice = new BigDecimal(unitPrice);

		if(this.unitPrice.scale() > MAX_DECIMAL_PRECISION) {
			this.unitPrice = this.unitPrice.setScale(MAX_DECIMAL_PRECISION, RoundingMode.UP);
		}
	}

	public Product(String title, String description, String unitPrice, int kiloCalPer100g) {
		this(title, description, unitPrice);
		this.setKiloCalPer100g(kiloCalPer100g);
	}
	
	public void setKiloCalPer100g(int kiloCalPer100g) {
		this.kiloCalPer100g = kiloCalPer100g;
	}

	public String toJson() {
		return new GsonBuilder().disableHtmlEscaping().create().toJson(this);
	}
}
