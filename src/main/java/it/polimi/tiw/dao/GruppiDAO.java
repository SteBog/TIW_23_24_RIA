package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import it.polimi.tiw.beans.Gruppi;
import java.util.ArrayList;

public class GruppiDAO {

	private Connection connection;

	public GruppiDAO(Connection connection) {
		this.connection = connection;
	}

	// (admin)
	// ritorna i gruppi creati dallo user ancora attivi
	public ArrayList<Gruppi> getActiveGroupsByUser(String username) throws SQLException {
		String query = "SELECT *, DATEDIFF(durata, CURDATE()) AS diff FROM gruppi WHERE durata >= CURDATE() AND admin = ?";
		ArrayList<Gruppi> listaGruppi = new ArrayList<Gruppi>();
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Gruppi temp = new Gruppi();
					temp.setID(result.getInt("ID"));
					temp.setNome(result.getString("nome"));
					temp.setDescrizione(result.getString("descrizione"));
					temp.setDurata(result.getInt("diff")); // non so se funzionerà come sperato
					temp.setAdmin(result.getString("admin"));
					temp.setMaxPartecipanti(result.getInt("max_partecipanti"));
					temp.setMinPartecipanti(result.getInt("min_partecipanti"));

					listaGruppi.add(temp);

				}
				return listaGruppi;
			}

		}

	}

	// (chiunque)
	// ritorna un gruppo dato l'id del gruppo
	public Gruppi getGroupById(int id) throws SQLException {
		String query = "SELECT *, DATEDIFF(durata, CURDATE()) AS diff FROM gruppi WHERE gruppi.ID = ?";
		Gruppi gruppo = null;

		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, id);
			result = pstatement.executeQuery();

			while (result.next()) {
				gruppo = new Gruppi();
				gruppo.setID(result.getInt("ID"));
				gruppo.setNome(result.getString("nome"));
				gruppo.setDescrizione(result.getString("descrizione"));
				gruppo.setDurata(result.getInt("diff"));
				gruppo.setAdmin(result.getString("admin"));
				gruppo.setMaxPartecipanti(result.getInt("max_partecipanti"));
				gruppo.setMinPartecipanti(result.getInt("min_partecipanti"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (result != null)
					result.close();
			} catch (SQLException e1) {
				throw e1;
			}
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (SQLException e1) {
				throw e1;
			}
		}
		return gruppo;
	}

	// (admin)
	// crea un gruppo
	// chiama il PartecipationDAO per aggiungere gli inviti
	public void addGroup(String nome, String descrizione, int durata, String admin, int maxPartecipanti,
			int minPartecipanti, ArrayList<String> partecipanti) throws SQLException {

		String query = "INSERT INTO gruppi (nome, descrizione, durata, admin, min_partecipanti, max_partecipanti) VALUES (?, ?, DATE_ADD(CURDATE(), INTERVAL ? DAY), ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, nome);
			pstatement.setString(2, descrizione);
			pstatement.setInt(3, durata);
			pstatement.setString(4, admin); // qua si mette lo username
			pstatement.setInt(5, minPartecipanti);
			pstatement.setInt(6, maxPartecipanti);
			pstatement.executeUpdate();
		}

		// aggiunto i partecipanti
		addToGroup(connection, nome, admin, partecipanti);

	}

	//(sistema)
	// aggiunge tutti i partecioanti al gruppo creato
	private void addToGroup(Connection connection, String nome,  String admin, ArrayList<String> partecipanti) throws SQLException {
		
		int temp;
		String query2 = "SELECT ID FROM gruppi WHERE nome = ?"; //il nome è unique!!!
		try (PreparedStatement pstatement2 = connection.prepareStatement(query2);) {
			pstatement2.setString(1, nome);
			try(ResultSet result = pstatement2.executeQuery();){
				result.next();
				temp = result.getInt("ID");
			}
		}
		//chiamo il partecipation dao e aggiungo tutti i partecipanti 
		PartecipationDAO pdao = new PartecipationDAO(this.connection);
		pdao.addPartecipation(partecipanti, temp);
		
		
	}

	// (admin)
	// controlla se esiste un gruppo con un certo nome
	// ritorna true se esiste
	public Boolean alreadyExistingGroup(String nome) throws SQLException {
		String query = "SELECT * FROM gruppi where nome = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, nome);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					return false;
				else {
					return true;
				}
			}
		}
	}

}