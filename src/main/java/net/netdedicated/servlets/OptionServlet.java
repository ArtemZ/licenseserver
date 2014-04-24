package net.netdedicated.servlets;

import net.netdedicated.beans.ErrorBean;
import net.netdedicated.domain.Option;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: artemz
 * Date: 1/13/14
 * Time: 2:31 PM
 * To change this template use File | Settings | File Templates.
 */
@WebServlet(name = "OptionServlet")
public class OptionServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getRequestURI().equals("/option")){
			createOption(request, response);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getRequestURI().equals("/option/add")){
			request.getRequestDispatcher("/opt/add.jsp").forward(request, response);
		} else if (request.getRequestURI().equals("/option/list")){
			list(request, response);
		}

	}
	protected void createOption(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String key = request.getParameter("key");
		String value = request.getParameter("value");
		if(key == null || value == null){
			request.setAttribute("errorBean", new ErrorBean("Either key or value is null"));
			request.getRequestDispatcher("/opt/add.jsp").forward(request, response);
			return;
		}
		Option option = new Option(key, value);
		try {
			option.save();
		} catch (SQLException e){
			request.setAttribute("errorBean", new ErrorBean(e.getMessage()));
			request.getRequestDispatcher("/opt/add.jsp").forward(request, response);
			return;
		}
		response.sendRedirect("/option/list");
	}
	protected void list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<Option> options = null;
		try {
			options = Option.list(10l, 0l);
		}catch (SQLException e){
			throw new IOException(e.getMessage());
		}
		request.setAttribute("options", options);
		request.getRequestDispatcher("/opt/list.jsp").forward(request, response);
	}
}
