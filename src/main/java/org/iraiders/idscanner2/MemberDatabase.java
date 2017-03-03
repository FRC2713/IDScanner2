package org.iraiders.idscanner2;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

public class MemberDatabase extends Database{
    public MemberDatabase(String serverN, int port, String databaseN, String user, String pass){
        super(serverN, port, databaseN, user, pass);
        if(conn != null) {
            String createMember = "CREATE TABLE IF NOT EXISTS members ( memberId varchar(45) NOT NULL PRIMARY KEY UNIQUE, memberName varchar(45) NOT NULL, memberStatus varchar(10) NOT NULL DEFAULT 'member', password varchar(64) DEFAULT NULL)";
            String createAttendance = "CREATE TABLE IF NOT EXISTS memberAttendance ( memberId varchar(45) NOT NULL, date varchar(15) NOT NULL, time double NOT NULL PRIMARY KEY UNIQUE)";
            try {
                stmt = conn.createStatement();
                DatabaseMetaData meta = conn.getMetaData();
                ResultSet res = meta.getTables(null, null, "members", new String[] {"TABLE"});
                if(!res.next()){
                    stmt.executeUpdate(createMember);
                    String password = JOptionPane.showInputDialog("Please input a default admin password.");
                    String hash = Password.hashPassword(password, "admin");
                    stmt.executeUpdate("INSERT INTO members (memberId, memberName, memberStatus, password) VALUES ('admin', 'Admin', 'admin', '"+hash+"');");
                }
                stmt.execute(createAttendance);
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String getPassword(String memberId){
        try {
            pstmt = conn.prepareStatement("SELECT password FROM members WHERE memberId=?");
            pstmt.setString(1, memberId);
            res = pstmt.executeQuery();
            if(res.next()){
                return res.getString("password");
            }else{
                return "";
            }
        }catch(SQLException e){
            e.printStackTrace();
            return "";
        }
    }

    public String getStatus(String memberId){
        try {
            pstmt = conn.prepareStatement("SELECT memberStatus FROM members where memberId=?");
            pstmt.setString(1, memberId);
            res = pstmt.executeQuery();
            if(res.next()){
                return res.getString("memberStatus");
            }else{
                return "";
            }
        }catch(SQLException e){
            e.printStackTrace();
            return "";
        }
    }

    public boolean updateAttendance(String memberId){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());
        double time = System.currentTimeMillis();

        try{
            pstmt = conn.prepareStatement("SELECT * FROM memberAttendance WHERE memberId = ?");
            pstmt.setString(1, memberId);
            res = pstmt.executeQuery();
        }catch(SQLException e){
            try{
                pstmt = conn.prepareStatement("INSERT INTO memberAttendance(memberId, date, time) values (?, ?, ?)");
                pstmt.setString(1, memberId);
                pstmt.setString(2, today);
                pstmt.setDouble(3, time);
                pstmt.executeUpdate();
                return true;
            }catch(SQLException ex){
                return false;
            }
        }

        try{
            while(res.next()){
                if(res.getString("date").equals(today)){
                    return false;
                }
            }
        }catch(SQLException e){
            return false;
        }
        try{
            pstmt = conn.prepareStatement("INSERT INTO memberAttendance(memberId, date, time) values (?, ?, ?)");
            pstmt.setString(1, memberId);
            pstmt.setString(2, today);
            pstmt.setDouble(3, time);
            pstmt.executeUpdate();
            return true;
        }catch(SQLException e){
            return false;
        }
    }

    public String queryMemberName(String memberId){
        //return "" == does not exist;
        String name;
        boolean next = true;
        try{
            pstmt = conn.prepareStatement("SELECT * FROM members WHERE memberId=?");
            pstmt.setString(1, memberId);
            res = pstmt.executeQuery();
            next = res.next();
            name = res.getString("memberName");
            res.close();
            pstmt.close();
        }catch(SQLException e){
            if(e.getErrorCode() == 0 && next){
                return "-1";
            }
            return "";
        }


        try{
            res.close();
            pstmt.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return name;
    }

    //SELECT * FROM Members.members WHERE memberId='600740';
    //named update because it update data instead of reading it.
    public boolean updateAddMember(String name, String id){
        try{
            pstmt = conn.prepareStatement("insert into members(memberId, memberName) values (?, ?)");
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }

        try{
            pstmt.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return true;
    }


}
