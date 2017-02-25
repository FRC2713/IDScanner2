package org.iraiders.idscanner2;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

abstract public class Database{
    boolean active = false; //False when a connection is not achieved. True if one is.
    static Connection conn;
    Statement stmt;
    PreparedStatement pstmt;
    ResultSet res;


    public Database(String url){
        try{
            Path p = Paths.get(url);
            Path folder = p.getParent();
            boolean success = folder.toFile().mkdir();

            if(success){
                System.out.println("Created db directory");
            }

            String databasePath = "jdbc:sqlite:"+url;

            if(conn == null){
                conn = DriverManager.getConnection(databasePath);
                System.out.println("New Connection to database open");
            }

            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                active = true;
            }
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
