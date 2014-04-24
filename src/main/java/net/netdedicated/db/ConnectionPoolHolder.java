package net.netdedicated.db;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: artemz
 * Date: 12/28/13
 * Time: 10:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionPoolHolder {
	private static BoneCP connectionPool;
	public static void init(String url, String username, String password) throws SQLException, ClassNotFoundException{
		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(password);
		Class.forName("org.h2.Driver");
		connectionPool = new BoneCP(config);
	}
	public static Connection getConnection() throws SQLException{
		if(connectionPool != null){
			return connectionPool.getConnection();
		}
		return null;
	}

	public static BoneCP getConnectionPool() {
		return connectionPool;
	}

	public static void shutdown(){
		connectionPool.shutdown();
	}
}
