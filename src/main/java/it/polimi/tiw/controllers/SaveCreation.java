package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.Gruppi;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.GruppiDAO;
import it.polimi.tiw.dao.UserDAO;

/**
 * Servlet implementation class SaveCreation
 */
@WebServlet("/SaveCreation")
@MultipartConfig
public class SaveCreation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
    
    public SaveCreation() {
        super();
    }
    
	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("User not defined");
			return;
		}
		
		Gruppi gruppo = (Gruppi) request.getSession().getAttribute("gruppo");
		
		if (gruppo == null) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Group not defined");
			return;
		}
		
		// prendo lo username di chi sta effettuando l'operazione
		User user = (User)session.getAttribute("user");
		String admin = user.getUsername();

		// prendo gli user selezionati
		String[] utentiSelezionati = request.getParameterValues("selectedUsers");
		
		ArrayList<String> utenti = new ArrayList<>();
		for (String userId : utentiSelezionati) {
			utenti.add(userId);
		}

		// controllo che il numero di elementi sia corretto
		int isOk = checkNumPart(utentiSelezionati, gruppo.getMinPartecipanti(), gruppo.getMaxPartecipanti());

		// carico il numero di tentativi o lo setto a 1 (prima visita)
		Integer tentativi = (Integer) session.getAttribute("tentativi");
		if (tentativi == null) {
			tentativi = 1;
			request.getSession().setAttribute("tentativi", tentativi);
		}

		// controllo quante volte l'utente ha fatto l'accesso a questa pagina
		if (checkTries(tentativi)) {
			// se è a 3 lo porto alla pagina CANCELLAZIONE
			//prima del redirect resetto il contatore per delle prossime creazioni 
			tentativi = null;
			response.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);
			response.getWriter().println("Troppi tentativi");
			return;
		}

		// se è ok procedo con la creazione del gruppo e resetto il contatore
		if (isOk==0) {
			GruppiDAO groupDao = new GruppiDAO(connection);
			
			try {
				groupDao.addGroup(gruppo.getNome(), gruppo.getDescrizione(), gruppo.getDurata(), admin, gruppo.getMaxPartecipanti(), gruppo.getMinPartecipanti(), utenti);
				tentativi = null;
				request.getSession().setAttribute("tentativi", tentativi);
				request.getSession().setAttribute("gruppo", null);
				
				//vado alla home
				response.setStatus(HttpServletResponse.SC_OK);
				response.getWriter().println("OK");
				return;
				
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Error");
				return;
			}
			
		} else {
			// altrimenti aumento il contatore
			tentativi++;
			request.getSession().setAttribute("tentativi", tentativi);
			
			//poi ritorno su questa pagina 
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Numero di partecipanti non soddisfa requisiti");
			return;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	// ritorna true se il gruppo è ok altrimenti false
	private int checkNumPart(String[] utenti, int minPartecipanti, int maxPartecipanti) {
		
		if(utenti.length < minPartecipanti) {
			//ho troppi pochi utenti selezionati 
			return 1;
		} else if(utenti.length > maxPartecipanti) {
			//ho più utenti selezionati del dovuto
			return 2;
		}else {
			return 0;
		}
	}

	// controlla che l'utente non abbia superato i tre tentativi
	private Boolean checkTries(int tentativi) {
		return tentativi == 3;
	}
}
