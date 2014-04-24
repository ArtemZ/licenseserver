package net.netdedicated.db;


import net.netdedicated.Config;
import net.netdedicated.ConfigHolder;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created with IntelliJ IDEA.
 * User: artemz
 * Date: 12/30/13
 * Time: 10:30 AM
 * To change this template use File | Settings | File Templates.
 */

public class DBInit implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent event) {
		/*ServletContext servletContext = event.getServletContext();
		if(ConfigHolder.getConfig().getEnviroment().equals("prod")){

		}*/
		Connection connection = null;
		Config config = ConfigHolder.getConfig();
		try {
			ConnectionPoolHolder.init(config.getJdbcUrl(), config.getJdbcUsername(), config.getJdbcPassword());
			connection = ConnectionPoolHolder.getConnection();
		}catch (SQLException e){
			e.printStackTrace();
			System.exit(1);
		} catch (ClassNotFoundException cnfe){
			cnfe.printStackTrace();
			System.exit(1);
		}
		Statement createStatement = null;
		try {
			createStatement = connection.createStatement();

			//create licenses table
			createStatement.execute("CREATE TABLE IF NOT EXISTS LICENSES (ID INTEGER NOT NULL AUTO_INCREMENT, IPADDR VARBINARY(16), LIC_TYPE CHAR(5), UNIQUE (ID), PRIMARY KEY (ID))");
			//create users table
			createStatement.execute("CREATE TABLE IF NOT EXISTS users (ID INTEGER(11) NOT NULL AUTO_INCREMENT, login VARCHAR(255), password VARCHAR(255), UNIQUE (ID, login), PRIMARY KEY (ID))");
			//create role table
			createStatement.execute("CREATE TABLE IF NOT EXISTS role (ID INTEGER(11) NOT NULL AUTO_INCREMENT, name VARCHAR(255), UNIQUE (ID), PRIMARY KEY (ID))");
			//create role_permission table
			createStatement.execute("CREATE TABLE IF NOT EXISTS role_permission (roleId INTEGER(11), permission VARCHAR(255) DEFAULT NULL)");
			//create user_roles table
			createStatement.execute("CREATE TABLE IF NOT EXISTS user_roles (userId INTEGER(11), roleId INTEGER(11), roleName VARCHAR(255) DEFAULT NULL)");
			//create options table
			createStatement.execute("CREATE TABLE IF NOT EXISTS OPTIONS (ID INTEGER not null AUTO_INCREMENT, key varchar(255), value VARCHAR(255) DEFAULT NULL, unique (key))");



		} catch (SQLException e){
			e.printStackTrace();
			System.exit(1);
		}

		//check if admin user exists, create him otherwise
		try {
			ResultSet rs = createStatement.executeQuery("SELECT * FROM users WHERE  login = 'admin'");
			if(!rs.next()){
				//create user administrator
				createStatement.execute("INSERT INTO users VALUES ('1', 'admin', 'admin')");
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
			System.exit(1);
		}
		try {
			createStatement.close();
			connection.close();
		} catch (SQLException e){
			e.printStackTrace();
		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		ConnectionPoolHolder.shutdown();
	}
}
