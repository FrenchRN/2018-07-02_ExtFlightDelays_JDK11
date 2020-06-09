package it.polito.tdp.extflightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.extflightdelays.model.Airline;
import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Arco;
import it.polito.tdp.extflightdelays.model.Flight;

public class ExtFlightDelaysDAO {

	public List<Airline> loadAllAirlines() {
		String sql = "SELECT * from airlines";
		List<Airline> result = new ArrayList<Airline>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Airline(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRLINE")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Airport> loadAllAirports() {
		String sql = "SELECT * FROM airports";
		List<Airport> result = new ArrayList<Airport>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRPORT"),
						rs.getString("CITY"), rs.getString("STATE"), rs.getString("COUNTRY"), rs.getDouble("LATITUDE"),
						rs.getDouble("LONGITUDE"), rs.getDouble("TIMEZONE_OFFSET"));
				result.add(airport);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Flight> loadAllFlights() {
		String sql = "SELECT * FROM flights";
		List<Flight> result = new LinkedList<Flight>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("ID"), rs.getInt("AIRLINE_ID"), rs.getInt("FLIGHT_NUMBER"),
						rs.getString("TAIL_NUMBER"), rs.getInt("ORIGIN_AIRPORT_ID"),
						rs.getInt("DESTINATION_AIRPORT_ID"),
						rs.getTimestamp("SCHEDULED_DEPARTURE_DATE").toLocalDateTime(), rs.getDouble("DEPARTURE_DELAY"),
						rs.getDouble("ELAPSED_TIME"), rs.getInt("DISTANCE"),
						rs.getTimestamp("ARRIVAL_DATE").toLocalDateTime(), rs.getDouble("ARRIVAL_DELAY"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public void getVertici(Double dist, Map<Integer, Airport> idMap) {
		final String sql = "SELECT a1.ID as id1,a1.IATA_CODE as ia1,a1.AIRPORT as ai1,a1.CITY as ci1,a1.STATE as st1,a1.COUNTRY as co1,a1.LATITUDE as lat1,a1.LONGITUDE as lon1,a1.TIMEZONE_OFFSET as ti1, " + 
				"a2.ID as id2,a2.IATA_CODE as ia2,a2.AIRPORT as ai2,a2.CITY as ci2,a2.STATE as st2,a2.COUNTRY as co2,a2.LATITUDE as lat2,a2.LONGITUDE as lon2,a2.TIMEZONE_OFFSET as ti2 " + 
				"FROM flights, airports AS a1, airports AS a2 " + 
				"WHERE flights.ORIGIN_AIRPORT_ID > flights.DESTINATION_AIRPORT_ID AND flights.ORIGIN_AIRPORT_ID = a1.ID " + 
				"		AND flights.DESTINATION_AIRPORT_ID = a2.ID " + 
				"GROUP BY a1.ID,a1.IATA_CODE,a1.AIRPORT,a1.CITY,a1.STATE,a1.COUNTRY,a1.LATITUDE,a1.LONGITUDE,a1.TIMEZONE_OFFSET, " + 
				"			a2.ID,a2.IATA_CODE,a2.AIRPORT,a2.CITY,a2.STATE,a2.COUNTRY,a2.LATITUDE,a2.LONGITUDE,a2.TIMEZONE_OFFSET " + 
				"HAVING AVG(flights.DISTANCE) >= ?";
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, dist);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				if(!idMap.containsKey(rs.getInt("id1"))) {
					Airport airport = new Airport(rs.getInt("id1"), rs.getString("ia1"), rs.getString("ai1"),
							rs.getString("ci1"), rs.getString("st1"), rs.getString("co1"), rs.getDouble("lat1"),
							rs.getDouble("lon1"), rs.getDouble("ti1"));
					idMap.put(airport.getId(), airport);
				}
				if(!idMap.containsKey(rs.getInt("id2"))) {
					Airport airport = new Airport(rs.getInt("id2"), rs.getString("ia2"), rs.getString("ai2"),
							rs.getString("ci2"), rs.getString("st2"), rs.getString("co2"), rs.getDouble("lat2"),
							rs.getDouble("lon2"), rs.getDouble("ti2"));
					idMap.put(airport.getId(), airport);
				}
			}

			conn.close();
			

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Arco> getArchi(Double dist, Map<Integer, Airport> idMap) {
		final String sql = "SELECT f.ORIGIN_AIRPORT_ID,f.DESTINATION_AIRPORT_ID, AVG(distance) as media " + 
				"FROM flights AS f " + 
				"GROUP BY f.ORIGIN_AIRPORT_ID, f.DESTINATION_AIRPORT_ID " + 
				"HAVING AVG (DISTANCE) >= ?";
		try {
			List<Arco> list = new ArrayList<>();
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, dist);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport a1 = idMap.get(rs.getInt("ORIGIN_AIRPORT_ID"));
				Airport a2 = idMap.get(rs.getInt("DESTINATION_AIRPORT_ID"));
				list.add(new Arco(a1,a2,rs.getDouble("media")));
			}

			conn.close();
			return list;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
}

