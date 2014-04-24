package net.netdedicated;

/**
 * Created with IntelliJ IDEA.
 * User: artemz
 * Date: 12/30/13
 * Time: 10:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigHolder {
	private static Config config;

	public static Config getConfig() {
		return config;
	}

	public static void setConfig(Config config) {
		ConfigHolder.config = config;
	}
}
