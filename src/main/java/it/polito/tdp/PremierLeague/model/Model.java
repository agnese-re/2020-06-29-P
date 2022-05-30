package it.polito.tdp.PremierLeague.model;

import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private Graph<Match,DefaultWeightedEdge> grafo;
	private PremierLeagueDAO dao;
	
	private List<Match> soluzioneBest;
	private int pesoMassimo;
	
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
		
		if(this.grafo == null)
			throw new RuntimeException("Grafo non esistente "
					+ " per la combinazione di parametri 'MIN' e 'MESE'");
		
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
	
	/* *** RICORSIONE *** */
	public List<Match> calcolaPercorso(Match matchStart, Match matchEnd) {
		if(this.grafo == null)
			throw new RuntimeException("Grafo non esistente "
					+ " per la combinazione di parametri 'MIN' e 'MESE'");
		
		this.soluzioneBest = new ArrayList<Match>();
		this.pesoMassimo = 0;
		
		List<Match> parziale = new ArrayList<Match>();
		parziale.add(matchStart);	// vertice di partenza aggiunto a 'parziale'
		ricorsione(parziale, matchEnd);
		return soluzioneBest; 
	}
	
	private void ricorsione(List<Match> parziale, Match matchEnd) {
		// casi terminali
		if(parziale.get(parziale.size()-1).equals(matchEnd)) {	// destinazione raggiunta
			System.out.println("\n" + parziale);	// DEBUG
			int pesoParziale = calcolaPesoPercorso(parziale);
			// E' la soluzione migliore?
			if(pesoParziale > pesoMassimo) {
				pesoMassimo = pesoParziale;
				soluzioneBest = new ArrayList<Match>(parziale);
			} 
				return;		// esco da questa chiamata ricorsiva
		}
		// algoritmo ricorsivo con generazione di tutti i sottoproblemi
		List<Match> vicini = Graphs.neighborListOf(this.grafo, parziale.get(parziale.size()-1));
		for(Match vicino: vicini)
			if( (!vicino.getTeamHomeNAME().equals(parziale.get(parziale.size()-1).getTeamHomeNAME()) ||
					!vicino.getTeamAwayNAME().equals(parziale.get(parziale.size()-1).getTeamAwayNAME())) 
					&& !parziale.contains(vicino) ) {
			parziale.add(vicino);
			ricorsione(parziale,matchEnd);
			parziale.remove(parziale.size()-1);
		}
	
	}
	
	public int calcolaPesoPercorso(List<Match> parziale) {
		int pesoPercorso = 0;
		
		for(int indice = 0; indice < parziale.size()-1; indice++) {
			Match matchP = parziale.get(indice);
			Match matchA = parziale.get(indice+1);
			/* I due vertici sono collegati da un unico arco, essendo il grafo semplice.
			 	Il SimpleGraph elimina i self-loops e i multiple-edges */
			DefaultWeightedEdge edge = this.grafo.getEdge(matchP, matchA);
	
			pesoPercorso += (int)this.grafo.getEdgeWeight(edge);
		}
		
		return pesoPercorso;
	}
	
	/* public List<Match> componenteConnessa(Match partenza) {
		ConnectivityInspector<Match,DefaultWeightedEdge> ci =
				new ConnectivityInspector<>(this.grafo);
		List<Match> connessi = new ArrayList<Match>(ci.connectedSetOf(partenza));
		return connessi;
	} */
}
