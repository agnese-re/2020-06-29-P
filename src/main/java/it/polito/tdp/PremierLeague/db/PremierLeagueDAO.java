package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Player;

public class PremierLeagueDAO {
	
	public List<Player> listAllPlayers(){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Match> listAllMatches(){
		String sql = "SELECT m.MatchID, m.TeamHomeID, m.TeamAwayID, m.teamHomeFormation, m.teamAwayFormation, m.resultOfTeamHome, m.date, t1.Name, t2.Name   "
				+ "FROM Matches m, Teams t1, Teams t2 "
				+ "WHERE m.TeamHomeID = t1.TeamID AND m.TeamAwayID = t2.TeamID";
		List<Match> result = new ArrayList<Match>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				
				Match match = new Match(res.getInt("m.MatchID"), res.getInt("m.TeamHomeID"), res.getInt("m.TeamAwayID"), res.getInt("m.teamHomeFormation"), 
							res.getInt("m.teamAwayFormation"),res.getInt("m.resultOfTeamHome"), res.getTimestamp("m.date").toLocalDateTime(), res.getString("t1.Name"),res.getString("t2.Name"));
				
				
				result.add(match);

			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Match> getMatchesMonth(Month mese) {
		String sql = "SELECT m.*, t1.name AS nome1, t2.name AS nome2 "
				+ "FROM matches m, teams t1, teams t2 "
				+ "WHERE m.TeamHomeID = t1.TeamID "
				+ "	AND m.TeamAwayID = t2.TeamID "
				+ "	AND MONTH(m.Date) = ? "
				+ "ORDER BY m.Date";
		List<Match> result = new ArrayList<Match>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, mese.getValue());
			ResultSet res = st.executeQuery();
			
			while (res.next()) {
				Match match = new Match(res.getInt("MatchID"), res.getInt("TeamHomeID"), res.getInt("TeamAwayID"), res.getInt("teamHomeFormation"), 
							res.getInt("teamAwayFormation"),res.getInt("resultOfTeamHome"), res.getTimestamp("date").toLocalDateTime(),res.getString("nome1"),res.getString("nome2"));
				result.add(match);
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int calcolaPeso(int minutiMinimi, Month mese, Match m1, Match m2) {
		String sql = "SELECT COUNT(*) AS pesoArco "
				+ "FROM actions a1, actions a2 "
				+ "WHERE a1.PlayerID = a2.PlayerID "
				+ "	AND a1.TimePlayed >= ? "
				+ "	AND a2.TimePlayed >= ? "
				+ "	AND a1.MatchID = ? "
				+ "	AND a2.MatchID = ? "
				+ "	AND a1.MatchID IN(SELECT matches.MatchID "
				+ "							FROM matches "
				+ "							WHERE MONTH(DATE) = ?) "
				+ "	AND a2.MatchID IN(SELECT matches.MatchID "
				+ "							FROM matches "
				+ "							WHERE MONTH(DATE) = ?)";
		int pesoArco;
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, minutiMinimi);
			st.setInt(2, minutiMinimi);
			st.setInt(3, m1.getMatchID());
			st.setInt(4, m2.getMatchID());
			st.setInt(5, mese.getValue());
			st.setInt(6, mese.getValue());
			ResultSet res = st.executeQuery();
			
			res.first();
			pesoArco = res.getInt("pesoArco");
			
			conn.close();
			return pesoArco;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
}
