package it.polito.tdp.PremierLeague.model;

import java.time.Month;

public class TestModel {

	public static void main(String[] args) {
		
		Model m = new Model();
		
		m.creaGrafo(Month.of(1), 45);
		System.out.println("\n" + m.getConnessioneMax());

	}

}
