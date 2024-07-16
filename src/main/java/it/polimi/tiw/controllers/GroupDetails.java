package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Gruppi;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.GruppiDAO;
import it.polimi.tiw.dao.PartecipationDAO;

/**
 * Servlet implementation class GroupDetails
 */
@WebServlet("/GroupDetails")
public class GroupDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

    public GroupDetails() {
        super();
    }

    public void init() throws ServletException {
		ServletContext context = getServletContext();

    	try {
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
		//se non sono loggato torno alla pagina di login 
		String loginpath = getServletContext().getContextPath() + "/index.html";
		
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		//	codice per farmi dare la descrizione del singolo gruppo con id = id
		String id_param = request.getParameter("groupId");
		Integer id = -1;
		
		//se è nullo lancio una eccezione
		if (id_param == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing group ID");
			return;
		}
		
		//se non è un numero lancio una eccezione
		try {
			id = Integer.parseInt(id_param);
		} catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid group ID");
			return;
		}
		
		
		Gruppi group = null;
		try {
			group = new GruppiDAO(connection).getGroupById(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (group == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("Group not found");
			return;
		}
			
		//	codice per farmi dare l'elenco dei partecipanti del singolo gruppo con id = id
		ArrayList<String> partecipanti = new ArrayList<>();
		
		try {
			partecipanti = new PartecipationDAO(connection).getPartecipants(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		// verifico che l'utente appartenga a quel gruppo
		// in caso negativo non può visualizzare quel gruppo
		User utente = (User)session.getAttribute("user");
		
		if (!group.getadmin().equals(utente.getUsername()) && !partecipanti.contains(utente.getUsername())) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().println("Forbidden ID");
			return;
		}
		
		Map<String, Object> result = new HashMap<>();
		result.put("group", group);
		result.put("partecipants", partecipanti);
		
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy MMM dd").create();
		
		String json = gson.toJson(result);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(json);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
