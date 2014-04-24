package net.netdedicated.db;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;

/**
 * Created with IntelliJ IDEA.
 * User: artemz
 * Date: 1/4/14
 * Time: 9:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class ShiroBoneCPDataSource extends BoneCPDataSource{
	public ShiroBoneCPDataSource() {
		//assert ConnectionPoolHolder.getConnectionPool() != null;
		//assert ConnectionPoolHolder.getConnectionPool().getConfig() != null;
		//super();
		super(ConnectionPoolHolder.getConnectionPool().getConfig());
	}
	public ShiroBoneCPDataSource(BoneCPConfig config){
		super(config);
	}
}
