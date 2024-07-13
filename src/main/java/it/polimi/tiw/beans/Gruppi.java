package it.polimi.tiw.beans;

public class Gruppi {
	private int id;
	private String nome ;
	private String descrizione;
	private int durata;
	private String admin;
	private int maxPartecipanti;
	private int minPartecipanti;
	

	/*
	 * getter
	 */

	public int getID() {
		return id;
	}
	public String getNome() {
		return nome;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public int getDurata() { //questa è espressa in giorni
		return durata;
	}
	public String getadmin() {
		return admin;
	}
	public int getMaxPartecipanti() {
		return maxPartecipanti;
	}
	public int getMinPartecipanti() {
		return minPartecipanti;
	}
	
	
	/*
	 * setter
	 */
	public void setID(int id) {
		this.id=id;
	}
	public void setNome(String nome) {
		this.nome=nome;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione=descrizione;
	}
	public void setDurata(int durata) {
		this.durata=durata;
	}
	public void setAdmin(String admin) {
		this.admin=admin;
	}
	public void setMaxPartecipanti(int maxPartecipanti) {
		this.maxPartecipanti=maxPartecipanti;
	}
	public void setMinPartecipanti(int minPartecipanti) {
		this.maxPartecipanti=minPartecipanti;
	}
	
	
	
	
}