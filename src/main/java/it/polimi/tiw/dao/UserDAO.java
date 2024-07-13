package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.User;
import java.util.ArrayList;

public class UserDAO {

	private Connection connection;

	public UserDAO(Connection connection) {
		this.connection = connection;
	}

	// (login)
	// metodo per il controllo delle credenziali di accesso di un utente registrato
	// se non trova l'utente ritorna null, altrimenti ritorna User
	public User checkCredentials(String username, String password) throws SQLException {
		String query = "SELECT * FROM users WHERE username = ? AND password = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					return null;
				else {
					result.next();
					User user = new User(result.getString("username"), result.getString("password"),
							result.getString("email"), result.getString("nome"), result.getString("cognome"));
					return user;
				}
			}
		}
	}

	// (registrazione)
	// aggiunge l'utente al database,
	// a patto che non esista u utente con lo stesso username
	//altrimenti ritona false
	public Boolean addUser(String username, String password, String email, String nome, String cognome)
			throws SQLException {

		if (!alreadyExistingUsername(username)) {

			String query = "INSERT INTO users VALUES (?, ?, ?, ?, ?)";

			try (PreparedStatement pstatement = connection.prepareStatement(query);) {
				pstatement.setString(1, username);
				pstatement.setString(2, password);
				pstatement.setString(3, email);
				pstatement.setString(4, nome);
				pstatement.setString(5, cognome);
				pstatement.executeUpdate();
				
				return true;
			}
		} else {
			return false;
		}

	}

	// (registrazione)
	// metodo che verifica se lo username è già esistente
	public Boolean alreadyExistingUsername(String username) throws SQLException {
		String query = "SELECT username FROM users WHERE username = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					return false;
				else {
					return true;
				}
			}
		}
	}
	
	//(admin)
	//restituisce l'anagrafica di tutti gli user registrati, ordinata
	//proprio tutti!
	public ArrayList<User> getAllUser() throws SQLException{
		String query = "SELECT * FROM users ORDER BY cognome asc";
		ArrayList<User> listaUtenti = new ArrayList<User>();
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					User user = new User(result.getString("username"), result.getString("password"),
							result.getString("email"), result.getString("nome"), result.getString("cognome"));
					listaUtenti.add(user);
				}

				return listaUtenti;
			}
		}
		
		
	}

}