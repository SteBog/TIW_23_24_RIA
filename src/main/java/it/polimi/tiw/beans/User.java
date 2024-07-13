package it.polimi.tiw.beans;

public class User {
	private String username;
	private String password;
	private String email;
	private String nome;
	private String cognome;
	
	public User(String username, String password, String email, String nome, String cognome){
		this.username=username;
		this.password=password;
		this.email=email;
		this.nome=nome;
		this.cognome=cognome;
	}

	/*
	 * getter
	 */

	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public String getEmail() {
		return email;
	}
	public String getNome() {
		return nome;
	}
	public String getCognome() {
		return cognome;
	}
	
	
	
}