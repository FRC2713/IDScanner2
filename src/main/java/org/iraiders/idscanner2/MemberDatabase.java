package org.iraiders.idscanner2;

import java.sql.SQLException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MemberDatabase extends Database{
  public MemberDatabase(String url){
    super(url);
    //Creates new tables if none exist
    String createMember = "CREATE TABLE IF NOT EXISTS members ( memberId varchar(45) NOT NULL PRIMARY KEY UNIQUE, memberName varchar(45) NOT NULL)";
    String createAttendance = "CREATE TABLE IF NOT EXISTS memberAttendance ( memberId varchar(45) NOT NULL, date varchar(15) NOT NULL, time double NOT NULL PRIMARY KEY UNIQUE)";
    try{
      stmt = conn.createStatement();
      stmt.execute(createMember);
      stmt.execute(createAttendance);
    }catch(SQLException e){
      System.out.println(e.getMessage());
    }
  }

  public boolean updateAttendance(String memberId){
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String today = dateFormat.format(new Date());
    double time = System.currentTimeMillis();

    try{
      stmt = conn.createStatement();
      res = stmt.executeQuery("SELECT * FROM memberAttendance WHERE memberId = '"+memberId+"'");
    }catch(SQLException e){ //If there is no memberAttendance row with the given memberId
      try{
        stmt.executeUpdate("INSERT INTO memberAttendance(memberId, date, time) values ('"+memberId+"', '"+today+"', '"+time+"')");
        return true;
      }catch(SQLException ex){
          System.out.println("Line 32: "+ex);
        return false;
      }
    }

    try{
      while(res.next()){
        if(res.getString("date").equals(today)){ //If there is a date in the attendance table that is the same as today's date
            // System.out.println("Date Lines up: "+res.getString("date"));
          return false;
        }
      }
    }catch(SQLException e){
      return false;
    }
    try{ //If the date is not found, update with today's date
      stmt.executeUpdate("INSERT INTO memberAttendance(memberId, date, time) values ('"+memberId+"', '"+today+"', '"+time+"')");
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
      stmt = conn.createStatement();
      res = stmt.executeQuery("SELECT * FROM members WHERE memberId='"+memberId+"'");
      next = res.next(); //Returns false if nothing is returned
      name = res.getString("memberName");
    }catch(SQLException e){
	  if(e.getErrorCode() == 0 && next){ //If the code does not reach the res.next() then next remains true.
	      System.out.println("Error: "+e);
		  return "-1";
	  }
      return "";
    }


    try{
      res.close();
      stmt.close();
    }catch(SQLException e){
      //System.out.println("Line XX: "+e);
    }
    return name;
  }

  //named update because it updates data instead of reading it.
  public boolean updateAddMember(String name, String id) {
      try {
          stmt = conn.createStatement();
          stmt.executeUpdate("insert into members(memberId, memberName) values ('" + id + "', '" + name + "')");
      } catch (SQLException e) { //Happens if two equal ID's are added
          return false;
      }

      try {
          stmt.close();
      } catch (SQLException e) {
          //System.out.println("Line XX: "+e);
      }
      return true;
  }
}
