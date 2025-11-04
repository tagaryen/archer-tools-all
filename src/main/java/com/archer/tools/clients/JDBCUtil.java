package com.archer.tools.clients;

import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.jdbc.ConnectionImpl;
import com.mysql.cj.jdbc.JdbcConnection;

import java.sql.SQLException;
import java.util.Properties;

public class JDBCUtil {

	private static final byte ED = 96;
	private static final byte[] DE = {68, 69, 83, 67, 82, 73, 66, 69, 32, 96};
	private static final byte[] ST = {83, 72, 79, 87, 32, 116, 97, 98, 108, 101, 115};
	

    public static boolean testMysqlConnection(String endpoint, String database, String user, String pwd) {
    	return testMysqlConnection(endpoint, database, user, pwd, null);
    }
	
    public static boolean testMysqlConnection(String endpoint, String database, String user, String pwd, String table) {
        JdbcConnection conn = null;
        try {
            String jdbcUrl = "jdbc:mysql://"+endpoint+"/"+database+"?connectTimeout=1000";
            Properties properties = new Properties();
            properties.setProperty("user", user);
            properties.setProperty("password", pwd);
            ConnectionUrl url = ConnectionUrl.getConnectionUrlInstance(jdbcUrl, properties);
            conn = ConnectionImpl.getInstance(url.getMainHost());
            java.sql.Statement statement = conn.createStatement();
            String s = "";
            if(table != null) {
            	byte[] ts = table.getBytes();
            	byte[] nbs = new byte[DE.length + ts.length + 1];
            	System.arraycopy(DE, 0, nbs, 0, DE.length);
            	System.arraycopy(ts, 0, nbs, DE.length, ts.length);
            	nbs[nbs.length-1] = ED;
            	s = new String(nbs);
            } else {
            	s = new String(ST);
            }
        	statement.executeQuery(s);
            return true;
        } catch (SQLException e) {
        	return false;
		} finally {
        	try {
        		if(conn != null) {
    				conn.close();
        		}
			} catch (SQLException ignore) {}
        }
    }

}
