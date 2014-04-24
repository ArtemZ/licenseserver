package net.netdedicated.servlets;

import net.netdedicated.beans.ErrorBean;
import net.netdedicated.domain.License;
import net.netdedicated.domain.Option;
import net.netdedicated.pgp.PgpHelper;
import org.bouncycastle.openpgp.PGPException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.*;
import java.security.SignatureException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: artemz
 * Date: 1/3/14
 * Time: 12:38 PM
 * To change this template use File | Settings | File Templates.
 */

public class LicenseServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getRequestURI().equals("/license")){
			String ipaddr = request.getParameter("ipaddr");
			String type = request.getParameter("type");
			if (ipaddr == null || ipaddr.length() < 1){

				request.setAttribute("errorBean", new ErrorBean("IP Address is not specified or has wrong format"));
				RequestDispatcher dispatcher = request.getRequestDispatcher("/lic/add.jsp");
				dispatcher.forward(request, response);
				return;
			}
			if (type == null || type.length() < 1){
				System.out.println("error adding license: type");
				request.setAttribute("errorBean", new ErrorBean("IP type is not specified or has wrong format"));
				RequestDispatcher dispatcher = request.getRequestDispatcher("/lic/add.jsp");
				dispatcher.forward(request, response);
			}
			License license = new License();
			license.setType(type);
			license.setIp(InetAddress.getByName(ipaddr).getAddress());
			try {
				license.save();
			} catch (SQLException e){
				request.setAttribute("errorBean", new ErrorBean("SQLException: " + e.getMessage()));
				RequestDispatcher dispatcher = request.getRequestDispatcher("/lic/add.jsp");
				dispatcher.forward(request, response);
			}
		} else if (request.getRequestURI().equals("/license/apicreate")){
			postApiCreate(request, response);
		} else if (request.getRequestURI().equals("/license/apidelete")){
			postApiDelete(request, response);
		}

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 if (request.getRequestURI().equals("/license")){
			 showLicense(request, response);
		 } else if (request.getRequestURI().equals("/license/list")){
			 list(request, response);
		 } else if (request.getRequestURI().equals("/license/add")){
			 /*System.out.println("in /license/add");*/
			 RequestDispatcher dispatcher = request.getRequestDispatcher("/lic/add.jsp");
			 dispatcher.include(request, response);
		 } else if (request.getRequestURI().equals("/license/testApiCreate")){
			 testApiCreate(request, response);
		 } else if (request.getRequestURI().equals("/license/testApiDelete")){
			 testApiDelete(request, response);
		 } else if(request.getRequestURI().equals("/license/testEncode")){
			testEncode(request, response);
		 } else if (request.getRequestURI().equals("/license/encoded")){
			 encoded(request, response);
		 } else if (request.getRequestURI().equals("/license/apilicense")){
			 getApiLicense(request, response);
		 }
 	}
	protected void showLicense(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Properties properties = new Properties();
		License license = null;
		try {
			license = License.findBy("IPADDR", InetAddress.getByName(request.getRemoteAddr()).getAddress());
		} catch (SQLException e){
			properties.setProperty("ERROR", e.getMessage());
			properties.store(response.getWriter(), "comments");
			return;
		}
		if (license == null){
			properties.setProperty("ERROR", "No licenses found for your IP");
			properties.store(response.getWriter(), "comments");
			return;
		} else {
			properties.setProperty("license.type", license.getType());
			properties.setProperty("license.ip", InetAddress.getByAddress(license.getIp()).getHostAddress() );
			try {
				for(Option option : Option.list(100l, 0l)){
					properties.setProperty(option.getKey(), option.getValue());
				}
			} catch (SQLException e){
				properties.setProperty("ERROR", e.getMessage());
			}

			properties.store(response.getWriter(), "");
		}
	}
	protected void list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<License> licenses = null;
		try {
			licenses = License.list(10l, 0l);
		}catch (SQLException e){
			throw new IOException(e.getMessage());
		}

		request.setAttribute("licenses", licenses);
		request.getRequestDispatcher("/lic/list.jsp").forward(request, response);
	}
	protected void getApiCreate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write("getApiCreate");
	}
	protected void getApiLicense(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Properties properties = new Properties();
		response.setContentType("application/xml");
		License license;
		try {
			license = License.findBy("IPADDR", InetAddress.getByName(request.getParameter("ipaddr")).getAddress());
		} catch (SQLException e){
			properties.setProperty("ERROR", e.getMessage());
			properties.store(response.getWriter(), "");
			return;
		}
		if (license == null){
			properties.setProperty("ERROR", "No license found");
			properties.store(response.getWriter(), "");
			return;
		}
		response.getWriter().write(license.toXML());
	}
	protected void postApiCreate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String licXml = URLDecoder.decode(request.getParameter("license"), "UTF-8").trim();
		response.setContentType("application/xml");
		response.setCharacterEncoding("UTF-8");
		License license = null;
		if(licXml == null || licXml.length() < 1){
			response.getWriter().write(new ErrorBean("error: no input license").toXML());
			return;
		}
		XMLDecoder d = new XMLDecoder(new ByteArrayInputStream(licXml.getBytes()));
		//todo: catch casting exception
		license = (License)d.readObject();
		if (license == null){
			//todo: return an errorbean
			response.getWriter().write("license read error");
			return;
		}
		//saving license
		try {
			license.save();
		} catch (SQLException e){
			response.getWriter().write(new ErrorBean("Error saving license: " + e.getMessage()).toXML());
			return;
		}
		response.getWriter().write(license.toXML());
	}
	protected void postApiDelete(HttpServletRequest request, HttpServletResponse response) throws  ServletException, IOException{
		License license = null;
		try {
			license = License.findBy("ID", Long.parseLong(request.getParameter("license.id")));
		} catch (SQLException e){
			response.getWriter().write(new ErrorBean(e.getMessage()).toXML());
			return;
		}
		if(license == null){
			response.getWriter().write(new ErrorBean("License not found").toXML());
			return;
		}
		try {
			license.delete();
		} catch (SQLException e){
			response.getWriter().write(new ErrorBean(e.getMessage()).toXML());
			return;
		}

	}
	protected void testApiCreate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		License license = new License(12l, "P2", InetAddress.getByName("127.0.0.3").getAddress());
		String encodedCredentials = DatatypeConverter.printBase64Binary("admin:admin".getBytes()) ;
		URL url = new URL("http://localhost:8080/license/apicreate");
		HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
		urlConnection.setRequestMethod("POST");
		urlConnection.setDoOutput(true);
		urlConnection.setRequestProperty("Authorization", "Basic " + encodedCredentials);

		String data = "license=" + URLEncoder.encode(license.toXML(), "UTF-8");

		OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
		writer.write(data);
		writer.flush();


		BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null){
			System.out.println(line);
		}
		writer.close();
		reader.close();
	}
	protected void testApiDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String encodedCredentials = DatatypeConverter.printBase64Binary("admin:admin".getBytes()) ;
		URL url = new URL("http://localhost:8080/license/apidelete");
		HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
		urlConnection.setRequestMethod("POST");
		urlConnection.setDoOutput(true);
		urlConnection.setRequestProperty("Authorization", "Basic " + encodedCredentials);

		String data = "license.id=" + URLEncoder.encode("1", "UTF-8");

		OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
		writer.write(data);
		writer.flush();


		BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null){
			System.out.println(line);
		}
		writer.close();
		reader.close();
	}
	protected void encoded(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		Properties licenseProperties = new Properties();
		License license = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ServletOutputStream sos = response.getOutputStream();
		try {
			license = License.findBy("IPADDR", InetAddress.getByName(request.getRemoteAddr()).getAddress());
		} catch (SQLException e){
			licenseProperties.setProperty("ERROR", e.getMessage());
			licenseProperties.store(baos, "comments");
			return;
		}
		if (license == null){
			licenseProperties.setProperty("ERROR", "No licenses found for your IP");
			licenseProperties.store(baos, "comments");
			return;
		} else {
			licenseProperties.setProperty("ipaddr", InetAddress.getByAddress(license.getIp()).getHostAddress());
			licenseProperties.setProperty("license.type", license.getType());
			try {
				for(Option option : Option.list(100l, 0l)){
					licenseProperties.setProperty(option.getKey(), option.getValue());
				}
			} catch (SQLException e){
				licenseProperties.setProperty("ERROR", e.getMessage());
			}
			licenseProperties.store(baos, "comments");

		}
		byte[] encodedData = null;

		/*licenseProperties.store(baos, "comments");*/
		try {
			encodedData = PgpHelper.encode(baos.toByteArray());
		} catch (PGPException e){
			response.sendError(response.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} finally {
			baos.close();
		}

		response.setContentType("application/octet-stream");
		if(encodedData == null){
			sos.write(baos.toByteArray());
		} else {
			sos.write(encodedData);
		}

	}
	protected void testEncode(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		byte[] encodedData;
		try {
			encodedData = PgpHelper.encode("encoded".getBytes());
		} catch (PGPException e){
			throw new IOException(e.getMessage());
		}
		response.setContentType("application/octet-stream");
		ServletOutputStream sos = response.getOutputStream();
		sos.write(encodedData);
		/*System.out.println(encodedData);
		response.getWriter().write(encodedData.toString());*/
	}
}
