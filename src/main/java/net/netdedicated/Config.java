package net.netdedicated;


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.*;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: artemz
 * Date: 12/29/13
 * Time: 12:02 AM
 * To change this template use File | Settings | File Templates.
 */

public class Config implements ServletContextListener{
	private String serverVersion;
	private String enviroment;
	private String jdbcUrl, jdbcUsername, jdbcPassword, keyRingUrl;
	@Override
	public void contextInitialized(ServletContextEvent event){
		ServletContext servletContext = event.getServletContext();
		if(System.getProperty("env.name") != null && System.getProperty("env.name").equals("dev")){
			this.enviroment = "dev";
			servletContext.log("Entering development environment");
		} else {
			this.enviroment = "prod";
			servletContext.log("Entering production environment");
		}
		final Properties properties = new Properties();
		if(enviroment.equals("prod") && new File("/usr/local/licenseserver/etc/config.properties").exists()){
			try {
				properties.load(new FileInputStream("/usr/local/licenseserver/etc/config.properties"));
			} catch (IOException e){
				e.printStackTrace();
				servletContext.log("System configuration not found in production environment");
				System.exit(1);
			}
			this.jdbcUrl = properties.getProperty("jdbc.url");
			this.jdbcUsername = properties.getProperty("jdbc.username");
			this.jdbcPassword = properties.getProperty("jdbc.password");
			this.keyRingUrl = properties.getProperty("keyring.url");
		} else if (enviroment.equals("dev")){
			File tempDir = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
			this.jdbcUrl = "jdbc:h2:" + tempDir.getAbsolutePath() + "/testDb";
			this.jdbcUsername = "sa";
			this.jdbcPassword = "";
			this.keyRingUrl = "/home/artemz/.gnupg/secring.gpg";
		}
		ConfigHolder.setConfig(this);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public String getServerVersion() {
		return serverVersion;
	}

	public String getEnviroment() {
		return enviroment;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public String getJdbcUsername() {
		return jdbcUsername;
	}

	public String getJdbcPassword() {
		return jdbcPassword;
	}

	public String getKeyRingUrl() {
		return keyRingUrl;
	}
}
