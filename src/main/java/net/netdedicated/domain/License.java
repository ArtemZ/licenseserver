package net.netdedicated.domain;

import net.netdedicated.db.ConnectionPoolHolder;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: artemz
 * Date: 1/5/14
 * Time: 2:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class License {
	private Long id;
	private String type;
	private byte[] ip;
	public License(){}
	public License(Long id, String type, byte[] address){
		this.id = id;
		this.type = type;
		this.ip = address;
	}
	public void save() throws SQLException, NullPointerException {
		Connection connection = ConnectionPoolHolder.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO LICENSES (IPADDR, LIC_TYPE) values (?, ?)",
				Statement.RETURN_GENERATED_KEYS);
		if(this.type == null || this.ip == null){
			throw new NullPointerException("License type or IP is null");
		}
		preparedStatement.setBytes(1, ip);
		preparedStatement.setString(2, type);
		preparedStatement.executeUpdate();
		ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
		if(generatedKeys.next()){
			this.id = generatedKeys.getLong(1);
		} else {
			throw new SQLException("No ID retrieved");
		}
		generatedKeys.close();
		preparedStatement.close();
		connection.close();
	}
	public static License findBy(String propertyName, Object propertyValue) throws SQLException{
		Connection connection = ConnectionPoolHolder.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement("select * from LICENSES where "  + propertyName + " = ? " );
		if(propertyValue instanceof byte[]){
			preparedStatement.setBytes(1, (byte[])propertyValue);
		} else if (propertyValue instanceof Long){
			preparedStatement.setLong(1, (Long)propertyValue);
		} else if (propertyValue instanceof String){
			preparedStatement.setString(1, (String)propertyValue);
		}
		ResultSet rs = preparedStatement.executeQuery();
		License license = null;
		if(rs.next()){
			license = new License();
			license.setId(rs.getLong("ID"));
			license.setIp(rs.getBytes("IPADDR"));
			license.setType(rs.getString("LIC_TYPE"));
		}
		rs.close();
		preparedStatement.close();
		connection.close();
		return license;
	}
	public void delete() throws SQLException {
		Connection connection = ConnectionPoolHolder.getConnection();
		PreparedStatement statement = connection.prepareStatement("delete from LICENSES where ID = ?");
		statement.setLong(1, this.id);
		statement.execute();
		statement.close();
		connection.close();
	}
	public static List<License> list(Long limit, Long offset) throws SQLException {
		List<License> licenses = new ArrayList<License>();
		Connection connection = ConnectionPoolHolder.getConnection();
		PreparedStatement  statement = connection.prepareStatement("select * from LICENSES LIMIT ? OFFSET ?");
		statement.setLong(1, limit);
		statement.setLong(2, offset);
		ResultSet rs = statement.executeQuery();
		License license;
		while (rs.next()){
			license = new License();
			license.setId(rs.getLong("ID"));
			license.setIp(rs.getBytes("IPADDR"));
			license.setType(rs.getString("LIC_TYPE"));
			licenses.add(license);
		}
		rs.close();
		statement.close();
		connection.close();
		return licenses;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public byte[] getIp() {
		return ip;
	}

	public void setIp(byte[] ip) {
		this.ip = ip;
	}
	public String toXML() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(baos);
		encoder.writeObject(this);
		encoder.flush();
		encoder.close();
		baos.close();
		return baos.toString();
	}
}
