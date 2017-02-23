package org.iraiders.idscanner2;

import java.sql.*;

public class AdminCommands {
    private String databasePath;
    Connection conn;
    Statement stmt;
    ResultSet res;

    public AdminCommands(String dbPath){
        databasePath = dbPath;
        try{
            conn = DriverManager.getConnection(dbPath);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
            }
            conn.close();
        }catch(SQLException e){
            System.out.println("Sql error: "+e);
        }
    }

    boolean openConn(){
        try {
            conn = DriverManager.getConnection(databasePath);
        }catch(SQLException e){
            return false;
        }
        if(conn != null){
            return true;
        }else{
            return false;
        }
    }

    boolean closeConn(){
        try{
            conn.close();
            return true;
        }catch(SQLException e){
            return false;
        }
    }

    public int getNumAttendance(String memberId){
        openConn();
        boolean next = true;
        int meetingCounter = 0;
        try{
            stmt = conn.createStatement();
            res = stmt.executeQuery("SELECT * FROM memberAttendance WHERE memberId='"+memberId+"'");
            while(res.next()){
                meetingCounter++;
            }
            closeConn();
        }catch(SQLException e){
            if(e.getErrorCode() == 0 && next == true){ //If the code does not reach the res.next() then next remains true.
                System.out.println("Error: "+e);
                meetingCounter = 0;
            }
            closeConn();
        }
        return meetingCounter;
    }

    public int getPercentAttendance(String memberId){
        return 0;
    }

    public boolean changeName(String memberId, String newName){
        try{
            openConn();
            stmt = conn.createStatement();
            res = stmt.executeQuery("SELECT memberName FROM members WHERE memberId='"+memberId+"'");
            if(!res.next()){
                System.out.println("Line 48");
                res.close();
                return false;
            }
            String currentName = res.getString("memberName");
            if(newName.equals(currentName)) {
                System.out.println("Line 53");
                res.close();
                closeConn();
                return true;
            }else {
                res.close();
                stmt.close();
                stmt = conn.createStatement();
                System.out.println("Line 56: success");
                stmt.executeUpdate("UPDATE members SET memberName='" + newName + "' WHERE memberId='" + memberId + "'");
                closeConn();
                return true;
            }
        }catch(SQLException e){
            System.out.println("Line 61: "+e);
            closeConn();
            return false;
        }
    }
}
