package sos.base.util.sampler;

import java.sql.SQLException;

public abstract class SOSSampler {

//	protected Connection connection;
//
//	public SOSSampler(String url, String uname, String pass) {
//		//		Class.forName("com.mysql.jdbc.Driver");
//		//		connection = DriverManager.getConnection(url, uname, pass);
//	}

	public void step() {
		try {
			insert();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected abstract void insert() throws SQLException;

}
