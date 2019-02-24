package net.brunomonteiro.sainsbury.model;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@ToString
public class Product {
	private static final int MAX_DECIMAL_PRECISION = 2;
	
	private String title;
	private Integer kiloCalPer100g;
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
}
