package org.iraiders.idscanner2;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;

import java.io.File;

abstract public class Database{
  boolean active = false; //False when a connection is not achieved. True if one is.
  static Connection conn;
  Statement stmt;
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
        //System.out.println("The driver name is " + meta.getDriverName());
        //System.out.println("A new database has been created.");
        active = true;
      }
    }catch(SQLException e){
      System.out.println("Sql error: "+e);
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
      if(conn != null){
        conn.close();
      }
    }catch(SQLException e){
      System.out.println("SQL Error: "+e);
    }
  }
}
