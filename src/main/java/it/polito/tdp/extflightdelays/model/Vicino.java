package it.polito.tdp.extflightdelays.model;

public class Vicino implements Comparable<Vicino>{
	private Airport a;
	private Double distanza;
	
	public Vicino(Airport a, Double distanza) {
		super();
		this.a = a;
		this.distanza = distanza;
	}

	public Airport getA() {
		return a;
	}

	public double getDistanza() {
		return distanza;
	}

	@Override
	public int compareTo(Vicino other) {
		return -(this.distanza.compareTo(other.getDistanza()));
	}
	
	
}
