package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.PartecipationDAO;

/**
 * Servlet implementation class DeletePartecipant
 */
@WebServlet("/DeletePartecipant")
public class DeletePartecipant extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    public DeletePartecipant() {
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
		String groupName = null;
		String partName = null;
		String partLastName = null;
		
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("User not defined");
			return;
		}
		
		groupName = StringEscapeUtils.escapeJava(request.getParameter("groupName"));
		partName = StringEscapeUtils.escapeJava(request.getParameter("partecipantName"));
		partLastName = StringEscapeUtils.escapeJava(request.getParameter("partecipantLastName"));
		
		if (groupName == null || groupName.isBlank() || partName == null || partName.isBlank() || partLastName == null || partLastName.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Parametri mancanti");
			return;
		}
		
		User user = (User) session.getAttribute("user");
		PartecipationDAO partecipationDAO = new PartecipationDAO(connection);
		
		try {
			partecipationDAO.deletePartecipation(groupName, user.getUsername(), partName, partLastName);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Impossibile eseguire l'operazione");
			return;
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
