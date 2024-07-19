package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

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

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;

/**
 * Servlet implementation class GetUserList
 */
@WebServlet("/GetUserList")
public class GetUserList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
    
    public GetUserList() {
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
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Devi essere loggato");
			return;
		}
		
		//carico tutti gli utenti
		ArrayList<User> utenti = null;
		UserDAO userDao = new UserDAO(connection);
		try {
			utenti = userDao.getUserNormalData();
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy MMM dd").create();
			
			String json = gson.toJson(utenti);
			
			response.getWriter().println(json);
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Si è verificato un errore");
			return;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
