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

    public AdminCommands(String serverN, int port, String databaseN, String user, String pass){
        super(serverN, port, databaseN, user, pass);
        store = new MemberDatabase(serverN, port, databaseN, user, pass);
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
            int maxNameLength = 0;
            int maxIdLength = 0;
            String memberName;
            for(int i = 0; i < memberIds.size(); i++){
                memberName = store.queryMemberName(memberIds.get(i));
                if(memberName.length() > maxNameLength){
                    maxNameLength =  memberName.length();
                }
                if(memberIds.get(i).length() > maxIdLength){
                    maxIdLength = memberIds.get(i).length();
                }
                membersAttendance[i][0] = Integer.toString(getNumAttendance(memberIds.get(i)));
                membersAttendance[i][1] = Integer.toString(getPercentAttendance(memberIds.get(i)));
            }
            maxNameLength++; //The file looks better this way.
            maxIdLength++;
            stmt.close();

            try{
                String nameString;
                String idString;
                String attendanceString;
                String percentageString;
                PrintWriter writer = new PrintWriter(writePath, "UTF-8");
                for(int i = 0; i < memberIds.size(); i++){
                    nameString = String.format("Name: %-"+maxNameLength+"."+maxNameLength+"s", store.queryMemberName(memberIds.get(i)));
                    idString = String.format("| Id: %-"+maxIdLength+"."+maxIdLength+"s", memberIds.get(i));
                    attendanceString = String.format("| Meetings Attended: %3.3s", membersAttendance[i][0]);
                    percentageString = String.format("| Percentage Attended: %4.4s", membersAttendance[i][1]+"%");
                    writer.println(nameString+idString+attendanceString+percentageString+"\n");
                }
                writer.close();
                return true;
            }catch(FileNotFoundException|UnsupportedEncodingException e){
                e.printStackTrace();
                return false;
            }
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public int getNumAttendance(String memberId){
        int meetingCounter = 0;
        try{
            pstmt = conn.prepareStatement("SELECT * FROM memberAttendance WHERE memberId=?");
            pstmt.setString(1, memberId);
            res = pstmt.executeQuery();
            while(res.next()){
                meetingCounter++;
            }
            res.close();
            pstmt.close();
        }catch(SQLException e){
            if(e.getErrorCode() == 0){
                e.printStackTrace();
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
            int currentAttendance;
            while(memberRes.next()){
                currentAttendance = getNumAttendance(memberRes.getString("memberId"));
                if(currentAttendance > maxAttendance){
                    maxAttendance = currentAttendance;
                }
            }
            double percent = ((double) getNumAttendance(memberId) / maxAttendance)*100;

            return (int) percent;
        }catch(SQLException e){
            e.printStackTrace();
        }

        return 0;
    } //Needs to use the total number of meetings. what is the best way to handle that? Maybe take the max attendance and use that as the total.

    public boolean changeName(String memberId, String newName){
        try{
            pstmt = conn.prepareStatement("SELECT memberName FROM members WHERE memberId=?");
            pstmt.setString(1, memberId);
            res = pstmt.executeQuery();
            if(!res.next()){
                res.close();
                pstmt.close();
                return false;
            }
            String currentName = res.getString("memberName");
            res.close();
            pstmt.close();
            if(newName.equals(currentName)) {
                return true;
            }else {
                pstmt = conn.prepareStatement("UPDATE members SET memberName=? WHERE memberId=?");
                pstmt.setString(1, newName);
                pstmt.setString(2, memberId);
                pstmt.executeUpdate();
                pstmt.close();
                return true;
            }
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}
