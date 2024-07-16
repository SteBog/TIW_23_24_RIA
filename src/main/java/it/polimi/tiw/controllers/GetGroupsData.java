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
 * Servlet implementation class GetGroupsData
 */
@WebServlet("/GetGroupsData")
public class GetGroupsData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public GetGroupsData() {
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
		//se non sono loggato torno alla pagina di login 
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		User user = (User) session.getAttribute("user");
		GruppiDAO gruppiDAO = new GruppiDAO(connection);
		ArrayList<Gruppi> adminGroups = new ArrayList<Gruppi>();
		
		try {
			adminGroups = gruppiDAO.getActiveGroupsByUser(user.getUsername());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover groups");
			return;
		}
		
		PartecipationDAO partecipationDAO = new PartecipationDAO(connection);
		ArrayList<Gruppi> GroupsWithUser = new ArrayList<Gruppi>();
		try {
			GroupsWithUser = partecipationDAO.getGroupsWithUser(user.getUsername());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover groups");
			return;
		}
		
		Map<String, ArrayList<Gruppi>> result = new HashMap<>();
		result.put("adminGroups", adminGroups);
		result.put("groupsWithUser", GroupsWithUser);
		
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(result);
		
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
