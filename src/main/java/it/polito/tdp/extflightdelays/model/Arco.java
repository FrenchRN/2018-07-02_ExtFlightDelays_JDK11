package it.polito.tdp.extflightdelays.model;

public class Arco {
	private Airport a1;
	private Airport a2;
	private double dist;
	
	public Arco(Airport a1, Airport a2, double dist) {
		super();
		this.a1 = a1;
		this.a2 = a2;
		this.dist = dist;
	}

	public Airport getA1() {
		return a1;
	}

	public Airport getA2() {
		return a2;
	}

	public double getDist() {
		return dist;
	}
	
	
	
}
