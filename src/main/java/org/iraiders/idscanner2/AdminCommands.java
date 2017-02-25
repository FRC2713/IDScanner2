package org.iraiders.idscanner2;

import java.sql.*;

public class AdminCommands extends Database{
    private Statement stmt;
    private ResultSet res;

    public AdminCommands(String dbPath){
        super(dbPath);
    }

    public int getNumAttendance(String memberId){
        int meetingCounter = 0;
        try{
            stmt = conn.createStatement();
            res = stmt.executeQuery("SELECT * FROM memberAttendance WHERE memberId='"+memberId+"'");
            while(res.next()){
                meetingCounter++;
            }
            res.close();
            stmt.close();
        }catch(SQLException e){
            if(e.getErrorCode() == 0){
                System.out.println("Error: "+e);
                meetingCounter = 0;
            }
        }
        return meetingCounter;
    }

    public int getPercentAttendance(String memberId){
        try {
            Statement memberStmt = conn.createStatement(); //Need to create new variables because of the getNumAttendance
            ResultSet memberRes = memberStmt.executeQuery("SELECT memberId FROM members");

            int maxAttendance = 0;
            int currentAttendance = 0;
            while(memberRes.next()){
                currentAttendance = getNumAttendance(memberRes.getString("memberId"));
                if(currentAttendance > maxAttendance){
                    maxAttendance = currentAttendance;
                }
            }
            double percent = ((double) getNumAttendance(memberId) / maxAttendance)*100;

            return (int) percent;
        }catch(SQLException e){
            System.out.println("Line 37: "+e);
        }

        return 0;
    } //Needs to use the total number of meetings. what is the best way to handle that? Maybe take the max attendance and use that as the total.

    public boolean changeName(String memberId, String newName){
        try{
            stmt = conn.createStatement();
            res = stmt.executeQuery("SELECT memberName FROM members WHERE memberId='"+memberId+"'");
            if(!res.next()){
                res.close();
                stmt.close();
                return false;
            }
            String currentName = res.getString("memberName");
            res.close();
            stmt.close();
            if(newName.equals(currentName)) {
                System.out.println("Line 53");
                return true;
            }else {
                stmt = conn.createStatement();
                stmt.executeUpdate("UPDATE members SET memberName='" + newName + "' WHERE memberId='" + memberId + "'");
                stmt.close();
                return true;
            }
        }catch(SQLException e){
            System.out.println("Line 61: "+e);
            return false;
        }
    }
}
