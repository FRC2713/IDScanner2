package org.iraiders.idscanner2;

import java.sql.*;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

abstract public class Database{
    boolean active = false; //False when a connection is not achieved. True if one is.
    static Connection conn;
    Statement stmt;
    PreparedStatement pstmt;
    ResultSet res;

    public Database(String serverN, int port, String databaseN, String user, String pass){
        MysqlDataSource dataSource = new MysqlDataSource();
        //System.out.println("Setting user to: "+user+", setting password to: "+pass+", setting server to: "+serverN+":"+port+", at database: "+databaseN);
        dataSource.setServerName(serverN);
        dataSource.setPortNumber(port);
        dataSource.setDatabaseName(databaseN);
        dataSource.setUser(user);
        dataSource.setPassword(pass);
        try{
            conn = dataSource.getConnection();
            active = true;
        }catch(SQLException e){
            e.printStackTrace();
        }

    }

    public void exit(){
        System.out.println("Closing Connections");
        try{
            if(res != null){
                res.close();
            }
            if(stmt != null){
                stmt.close();
            }
            if(pstmt != null){
                pstmt.close();
            }
            if(conn != null){
                conn.close();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
