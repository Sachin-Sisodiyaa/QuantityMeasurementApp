package com.apps.quantitymanagementapp;

import static org.junit.Assert.*;
import org.junit.Test;
import com.apps.quantitymanagementapp.QuantityMeasurementApp.Feet;




public class QuantityMeasurementAppTest {
	
	@Test
	public void testFeetEquality_SameValue() {
		Feet f1 = new Feet(1.0);
		Feet f2 = new Feet(1.0);
		assertTrue(f1.equals(f2));
	}
	
	@Test
	public void testFeetEquality_DifferentValue() {
		Feet f1 = new Feet(1.0);
		Feet f2 = new Feet(7.0);
		assertFalse(f1.equals(f2));		
	}
	
	@Test
	public void testFeetEquality_NullComparison() {
		Feet f1 = new Feet(1.0);
		assertFalse(f1.equals(null));
	}
	
	@Test
	public void testFeetEquality_DifferentClass() {
		Feet f1 = new Feet(1.0);
		String str = "2.0";
		assertFalse(f1.equals(str));
	}
	
	@Test
	public void testFeetEquality_SameReference() {
		Feet f1 = new Feet(1.0);
		assertTrue(f1.equals(f1));	
	}

}
