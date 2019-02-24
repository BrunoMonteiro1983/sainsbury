package net.brunomonteiro.sainsbury.model;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class TotalCostTest {

	@Test
	public void testInitialStateReturnsZero() {
		TotalCost totalCost = new TotalCost();
		assertEquals("0.00", totalCost.getGross().toString());
		assertEquals("0.00", totalCost.getVat().toString());
	}

	@Test
	public void testAddGrossReturnsCorrectSum() {
		TotalCost totalCost = new TotalCost();
		totalCost.addGross(new BigDecimal("1"));
		totalCost.addGross(new BigDecimal("1"));

		assertEquals("2.00", totalCost.getGross().toString());
	}

	@Test
	public void testAddGrossWithDifferentPrecisionReturnsCorrectSumAndPrecision() {
		TotalCost totalCost = new TotalCost();
		totalCost.addGross(new BigDecimal("1.5"));
		totalCost.addGross(new BigDecimal("1.50"));

		assertEquals("3.00", totalCost.getGross().toString());
	}

	@Test
	public void testAddingGrossReturnsCorrectVat() {
		TotalCost totalCost = new TotalCost();
		totalCost.addGross(new BigDecimal("3"));
		totalCost.addGross(new BigDecimal("2"));
		totalCost.addGross(new BigDecimal("5"));

		assertEquals("10.00", totalCost.getGross().toString());
		assertEquals("1.66", totalCost.getVat().toString());
	}

	@Test
	public void testGrossDecimalRoundsUpToNextValue() {
		TotalCost totalCost = new TotalCost();
		totalCost.addGross(new BigDecimal("2.999"));

		assertEquals("3.00", totalCost.getGross().toString());
	}

	@Test
	public void testReturnedJsonIncludesTotalCostValues() {
		TotalCost totalCost = new TotalCost();
		totalCost.addGross(new BigDecimal("5"));

		assertEquals("{\"gross\":5.00,\"vat\":0.83}", totalCost.toJSON());
	}
}
