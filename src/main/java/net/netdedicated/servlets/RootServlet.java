package net.netdedicated.servlets;
import net.netdedicated.db.ConnectionPoolHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created with IntelliJ IDEA.
 * User: artemz
 * Date: 12/27/13
 * Time: 8:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class RootServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write(request.getRequestURI() + "<br>");
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		try {
			connection = ConnectionPoolHolder.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery("SELECT * FROM users WHERE  login = 'admin'");
			if(rs.next()){
				response.getWriter().write("admin exists");
			} else {
				response.getWriter().write("admin doesnt exists");
			}
		} catch (SQLException e){
			response.getWriter().write("Exception: " + e.getMessage());
			return;
		}

	}

}
