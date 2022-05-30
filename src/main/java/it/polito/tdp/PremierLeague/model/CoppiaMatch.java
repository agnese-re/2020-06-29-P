package it.polito.tdp.PremierLeague.model;

public class CoppiaMatch {

	private Match m1;
	private Match m2;
	private int numGiocatori;
	
	public CoppiaMatch(Match m1, Match m2, int numGiocatori) {
		this.m1 = m1;
		this.m2 = m2;
		this.numGiocatori = numGiocatori;
	}

	public Match getM1() {
		return m1;
	}

	public void setM1(Match m1) {
		this.m1 = m1;
	}

	public Match getM2() {
		return m2;
	}

	public void setM2(Match m2) {
		this.m2 = m2;
	}

	public int getNumGiocatori() {
		return numGiocatori;
	}

	public void setNumGiocatori(int numGiocatori) {
		this.numGiocatori = numGiocatori;
	}

	@Override
	public String toString() {
		return "[" + m1.getMatchID() + "] " + m1.getTeamHomeNAME() + " vs. " + m1.getTeamAwayNAME() + 
				" - [" + m2.getMatchID() + "] " + m2.getTeamHomeNAME() + " vs. " + m2.getTeamAwayNAME() + 
				" (" + numGiocatori + ")";
	}
	
}
