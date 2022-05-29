package it.polito.tdp.PremierLeague.model;

import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private Graph<Match,DefaultWeightedEdge> grafo;
	private PremierLeagueDAO dao;
	
	public Model() {
		dao = new PremierLeagueDAO();
	}
	
	public void creaGrafo(Month mese, int minutiMinimi) {
		grafo = new SimpleWeightedGraph<Match,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(this.grafo, dao.getMatchesMonth(mese));
		
		for(Match m1: this.grafo.vertexSet())
			for(Match m2: this.grafo.vertexSet())
				if(!m1.equals(m2) && m1.getMatchID() > m2.getMatchID()) {	// match diversi
					int peso = dao.calcolaPeso(minutiMinimi, mese, m1, m2);
					if(peso > 0)	// AGGIUNGO ARCO SE PESO MAGGIORE DI 0!
						Graphs.addEdge(this.grafo, m1, m2, peso);
				}
		
		System.out.format("Creato grafo con %d vertici e %d archi",
				this.grafo.vertexSet().size(),this.grafo.edgeSet().size());
		
	}
	
	public int numVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int numArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Match> getMatchCombo() {
		List<Match> matches = new ArrayList<Match>(this.grafo.vertexSet());
		Collections.sort(matches);
		return matches;
	}
	
	public List<CoppiaMatch> getConnessioneMax() {
		
		int pesoMax = 0;
		List<CoppiaMatch> coppie = new ArrayList<CoppiaMatch>();
		
		// Ricerca arco di peso massimo
		for(DefaultWeightedEdge e: this.grafo.edgeSet())
			if(this.grafo.getEdgeWeight(e) > pesoMax)
				pesoMax = (int)this.grafo.getEdgeWeight(e);
		// Archi di peso massimo
		for(DefaultWeightedEdge e: this.grafo.edgeSet())
			if(this.grafo.getEdgeWeight(e) == pesoMax)
				coppie.add(new CoppiaMatch(this.grafo.getEdgeSource(e),
						this.grafo.getEdgeTarget(e), pesoMax));
		
		return coppie;
	}
}
