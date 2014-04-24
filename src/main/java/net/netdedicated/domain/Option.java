package net.netdedicated.domain;

import net.netdedicated.db.ConnectionPoolHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: artemz
 * Date: 1/12/14
 * Time: 7:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class Option {
	private Long id;
	private String key;
	private String value;
	public Option(String key, String value){
		this.key = key;
		this.value = value;
	}
	public Option(){}
	public void save() throws SQLException {
		Connection connection = ConnectionPoolHolder.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO OPTIONS (key, value) values (?, ?)");
		preparedStatement.setString(1, key);
		preparedStatement.setString(2, value);
		preparedStatement.executeUpdate();
		ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
		if(generatedKeys.next()){
			this.id = generatedKeys.getLong(1);
		} else {
			throw new SQLException("No IDs received");
		}
		generatedKeys.close();
		preparedStatement.close();
		connection.close();
	}
	public static Option findBy(String propertyName, String propertyValue) throws SQLException {
		Connection connection = ConnectionPoolHolder.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement("select * from OPTIONS where "  + propertyName + " = ? " );
		preparedStatement.setString(1, propertyValue);
		ResultSet rs = preparedStatement.executeQuery();
		Option option = null;
		if(rs.next()){
			option = new Option();
			option.setId(rs.getLong("ID"));
			option.setKey(rs.getString("key"));
			option.setValue(rs.getString("value"));
		}
		rs.close();
		preparedStatement.close();
		connection.close();
		return option;
	}
	public static List<Option> list(Long limit, Long offset) throws SQLException {
		List<Option> options = new ArrayList<Option>();
		Connection connection = ConnectionPoolHolder.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM OPTIONS LIMIT ? OFFSET ?");
		preparedStatement.setLong(1, limit);
		preparedStatement.setLong(2, offset);
		ResultSet rs = preparedStatement.executeQuery();
		Option option;
		while (rs.next()){
			option = new Option(rs.getString("key"), rs.getString("value"));
			option.setId(rs.getLong("ID"));
			options.add(option);
		}
		return options;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
