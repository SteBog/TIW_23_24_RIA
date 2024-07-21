package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.beans.Gruppi;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.GruppiDAO;

/**
 * Servlet implementation class CreateGroup
 */
@WebServlet("/CreateGroup")
@MultipartConfig
public class CreateGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    
    public CreateGroup() {
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

			ServletContext servletContext = getServletContext();

		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// se non sono loggato torno alla pagina di login
		String loginpath = getServletContext().getContextPath() + "/index.html";

		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Utente non definito");
			return;
		}
		
		String nome = null;
		String descrizione = null;
		int giorni = 0;
		int minPartecipanti = 0;
		int maxPartecipanti = 0;
		
		String temp = null;
		
		try {
			nome = StringEscapeUtils.escapeJava(request.getParameter("nome"));
			descrizione = StringEscapeUtils.escapeJava(request.getParameter("descrizione"));
			if (nome == null || nome.isEmpty() || descrizione == null || descrizione.isEmpty()) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Missing parameters");
				return;
			}
			
			temp = StringEscapeUtils.escapeJava(request.getParameter("giorni"));
			if (temp == null || temp.isEmpty()) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Missing parameters");
				return;
			}
			giorni = Integer.parseInt(temp);
			
			temp = StringEscapeUtils.escapeJava(request.getParameter("minPartecipanti"));
			if (temp == null || temp.isEmpty()) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Missing parameters");
				return;
			}
			minPartecipanti = Integer.parseInt(temp);
			
			temp = StringEscapeUtils.escapeJava(request.getParameter("maxPartecipanti"));
			if (temp == null || temp.isEmpty()) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Missing parameters");
				return;
			}
			maxPartecipanti = Integer.parseInt(temp);
			
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error");
			return;
		}
		
		String path;
		
		//controllo che i parametri min e max siano giusti 
		if(!checkMinMax(minPartecipanti, maxPartecipanti)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Min non deve essere maggiore di Max");
			return;
		}
		//controllo che giorni sia mmaggiore di 0
		if(giorni==0){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("La durata deve essere maggiore di zero");
			return;
		}
		
		//controllo che non esista un gruppo con il nome uguale
		GruppiDAO groupDAO = new GruppiDAO(connection);
		try {
			if(groupDAO.alreadyExistingGroup(nome)) {
				response.setStatus(HttpServletResponse.SC_CONFLICT);
				response.getWriter().println("Group already existing");
				return;
			}
			
		}catch (SQLException e){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Server error.");
			return;
		}
		
		//se tutto ok vado alla pagina della anagrafica, passando i dati inseriti come parametro
		Gruppi gruppo = new Gruppi();
		User user = (User) request.getSession().getAttribute("user");
		gruppo.setAdmin(user.getUsername());
		gruppo.setNome(nome);
		gruppo.setDescrizione(descrizione);
		gruppo.setDurata(giorni);
		gruppo.setMinPartecipanti(minPartecipanti);
		gruppo.setMaxPartecipanti(maxPartecipanti);
		
		request.getSession().setAttribute("gruppo", gruppo);
	}
	
	private Boolean checkMinMax(int min, int max) {
		if (min < 0 || max < 0) return false;
		return min <= max;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
