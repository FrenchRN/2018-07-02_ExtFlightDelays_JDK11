package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	private ExtFlightDelaysDAO dao;
	private Graph<Airport,DefaultWeightedEdge> grafo;
	private Map<Integer,Airport> idMap;
	private List<Airport> soluzione;
	private int best;
	
	public Model() {
		dao = new ExtFlightDelaysDAO();
	}
	
	public void creaGrafo(Double dist) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		idMap = new HashMap<>();
		dao.getVertici(dist,idMap);
		for(Arco a : this.dao.getArchi(dist,idMap)) {
			DefaultWeightedEdge edge = this.grafo.getEdge(a.getA1(), a.getA2());
			if(edge == null)
				Graphs.addEdge(this.grafo, a.getA1(), a.getA2(), a.getDist());
		}
	}
	
	public List<Vicino> getVicini(Airport air){
		if(grafo != null) {
			List<Vicino> list = new ArrayList<>();
			for(Airport a : Graphs.neighborListOf(this.grafo, air))
				list.add(new Vicino(a,this.grafo.getEdgeWeight(this.grafo.getEdge(air, a))));
			Collections.sort(list);
			return list;
		}else return null;
	}
	
	public int getNVertex() {
		if(grafo != null)
			return this.grafo.vertexSet().size();
		return 0;
	}
	
	public int getNArchi() {
		if (grafo != null)
			return this.grafo.edgeSet().size();
		return 0;
	}
	
	public Set<Airport> getAirports(){
		if (grafo != null)
			return this.grafo.vertexSet();
		return null;
	}

	public String creaPercorso(Double dist, Airport value) {
		if(grafo != null) {
			String sol = "";
			best = 0;
			soluzione = new ArrayList<>();
			List<Airport> parziale = new ArrayList<>();
			parziale.add(value);
			this.ricorsiva(parziale,dist);
			sol = "Elenco degli aereoporti attraversati: \n";
			sol = sol + value + "\n";
			for(Airport a : soluzione)
				sol = sol + a +"\n";
			sol = sol + "Con una distanza totale di "+this.getTot(soluzione)+" miglia \n";
			return sol;
		}else return null;
	}
	
	private void ricorsiva(List<Airport> parziale, double dist) {
		for(Airport a : Graphs.neighborListOf(this.grafo, parziale.get(parziale.size()-1))) {
			if(!parziale.contains(a) && !(this.getTot(parziale) + this.grafo.getEdgeWeight(this.grafo.getEdge(parziale.get(parziale.size()-1), a)) > dist )) {
				parziale.add(a);
				this.ricorsiva(parziale, dist);
				parziale.remove(parziale.size()-1);
			}
		}
		if(parziale.size() > best) {
			best = parziale.size();
			soluzione = new ArrayList<>(parziale);
		}
	}
	
	private double getTot(List<Airport> parziale) {
		double tot = 0;
		int i = 1;
		while(i < parziale.size()) {
			tot = tot + this.grafo.getEdgeWeight(this.grafo.getEdge(parziale.get(i-1), parziale.get(i)));
			i++;
		}
		return tot;
	}
}
