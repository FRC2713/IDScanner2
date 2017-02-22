package org.iraiders.idscanner2;

import java.sql.*;

public class AdminCommands {
    Connection conn;
    Statement stmt;
    ResultSet res;

    public AdminCommands(String dbPath){
        try{
            conn = DriverManager.getConnection(dbPath);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
            }
        }catch(SQLException e){
            System.out.println("Sql error: "+e);
        }
    }

    public int getNumAttendance(String memberId){
        boolean next = true;
        int meetingCounter = 0;
        try{
            stmt = conn.createStatement();
            res = stmt.executeQuery("SELECT * FROM memberAttendance WHERE memberId='"+memberId+"'");
            while(res.next()){
                meetingCounter++;
            }
        }catch(SQLException e){
            if(e.getErrorCode() == 0 && next == true){ //If the code does not reach the res.next() then next remains true.
                System.out.println("Error: "+e);
                meetingCounter = 0;
            }
        }
        return meetingCounter;
    }

    public int getPercentAttendance(String memberId){
        return 0;
    }

    public boolean changeName(String memberId){
        return false;
    }
}
