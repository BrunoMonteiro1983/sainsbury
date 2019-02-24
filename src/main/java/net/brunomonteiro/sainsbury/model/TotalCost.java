package net.brunomonteiro.sainsbury.model;

import com.google.gson.GsonBuilder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class TotalCost {
	private static final String VAT_PERCENTAGE = ".2";
	private static final int DECIMAL_PRECISION = 2;
	private static final BigDecimal VAT_DIVIDE_FACTOR;

	static {
		VAT_DIVIDE_FACTOR = new BigDecimal(VAT_PERCENTAGE).add(new BigDecimal(1));
	}

	private BigDecimal gross;
	private BigDecimal vat;

	public TotalCost() {
		this.setGross(new BigDecimal(0));
	}

	private void setGross(BigDecimal value) {
		this.gross = this.getScaled(value);
		this.setVat();
	}

	private BigDecimal getScaled(BigDecimal value) {
		return value.setScale(DECIMAL_PRECISION, RoundingMode.UP);
	}

	private void setVat() {
		this.vat = this.getScaled(this.gross.subtract(this.gross.divide(VAT_DIVIDE_FACTOR, RoundingMode.UP)));
	}

	public void addGross(BigDecimal value) {
		this.setGross(this.gross.add(value));
	}

	public String toJSON() {
		return new GsonBuilder().disableHtmlEscaping().create().toJson(this);
	}
}
