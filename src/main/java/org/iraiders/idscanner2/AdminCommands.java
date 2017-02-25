package org.iraiders.idscanner2;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.sql.*;

import java.io.*;

public class AdminCommands extends Database{
    private Statement stmt;
    private ResultSet res;
    private MemberDatabase store;

    public AdminCommands(String dbPath){
        super(dbPath);
        store = new MemberDatabase(dbPath);
    }

    public boolean writeFile(String writePath){
        Path p = Paths.get(writePath);
        Path folder = p.getParent();
        boolean dirSuccess = folder.toFile().mkdir();
        if(dirSuccess){
            System.out.println("Created file directory");
        }

        File writeFile = new File(writePath);
        try{
            boolean fileSuccess = writeFile.createNewFile();
            if(fileSuccess){
                System.out.println("Created file");
            }
        }catch(IOException e){
            System.out.println("Failed to create file");
            return false;
        }

        try{
            ArrayList<String> memberIds = new ArrayList<>();
            stmt = conn.createStatement();
            res = stmt.executeQuery("SELECT memberId FROM members");
            while(res.next()){
                memberIds.add(res.getString("memberId"));
            }
            res.close();
            String [][] membersAttendance = new String [memberIds.size()][2]; //[i][0] hold num and [i][1] holds percentage
            for(int i = 0; i < memberIds.size(); i++){
                membersAttendance[i][0] = Integer.toString(getNumAttendance(memberIds.get(i)));
                membersAttendance[i][1] = Integer.toString(getPercentAttendance(memberIds.get(i)));
            }
            stmt.close();

            try{
                PrintWriter writer = new PrintWriter(writePath, "UTF-8");
                for(int i = 0; i < memberIds.size(); i++){
                    writer.println("Name: "+store.queryMemberName(memberIds.get(i))+", Meetings attended: "+membersAttendance[i][0]+", Percentage attended: "+membersAttendance[i][1]+"%");
                }
                writer.close();
                return true;
            }catch(FileNotFoundException|UnsupportedEncodingException e){
                System.out.println("Error: "+e);
                return false;
            }
        }catch(SQLException e){
            System.out.println("Write File SQLException: "+e);
            return false;
        }
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
