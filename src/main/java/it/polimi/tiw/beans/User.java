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
	
	public User() {};

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
	
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
}