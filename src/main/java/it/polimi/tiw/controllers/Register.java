package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.dao.UserDAO;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
@MultipartConfig
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public Register() {
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


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String usrn = null;
		String email  = null;
		String pwd = null;
		String cfpwd = null;
		String nome = null;
		String cognome = null;
		
		usrn = StringEscapeUtils.escapeJava(request.getParameter("Username"));
		email = StringEscapeUtils.escapeJava(request.getParameter("Email"));
		pwd = StringEscapeUtils.escapeJava(request.getParameter("Password"));
		cfpwd = StringEscapeUtils.escapeJava(request.getParameter("cfPassword"));
		nome = StringEscapeUtils.escapeJava(request.getParameter("nome"));
		cognome = StringEscapeUtils.escapeJava(request.getParameter("cognome"));
		
		//controllo che nessuno dei campi sia vuoto 
		if (usrn == null || usrn.isBlank() || pwd == null || pwd.isBlank() ||
				email == null || email.isBlank() || cfpwd==null || cfpwd.isBlank() || 
				nome == null || nome.isBlank() || cognome == null || cognome.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Perfavore compilare i campi");
			return;
		}

		//controllo che la mail sia effettivamente una mail, altrimenti stampo un messaggio 
		if(!testMail(email)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Indirizzo mail non valido");
			return;
		}
		
		//controllo che pwd e cfpwd siano UGUALI
		if(!pwd.equals(cfpwd)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Le due password non sono uguali");
			return;
		}
		
		//provo l'inserimento nel database
		UserDAO usr = new UserDAO(connection);
		
		try {
			Boolean result = usr.addUser(usrn, pwd, email, nome, cognome);
			
			if (!result) {
				response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
				response.getWriter().println("Esiste già un utente con questo username");
				return;
				
			} else {
				request.getSession().setAttribute("user", usr);
				response.setStatus(HttpServletResponse.SC_OK);
				response.getWriter().println(usrn);
				return;
			}
			
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Si è verificato un errore");
			return;
		}
	}

	private boolean testMail(String mail) {
		String emailPattern = "^[\\w\\.-]+@[a-zA-Z\\d\\.-]+\\.[a-zA-Z]{2,}$";
		Pattern pattern = Pattern.compile(emailPattern);
		Matcher matcher = pattern.matcher(mail);
		return matcher.matches();
	}
}
