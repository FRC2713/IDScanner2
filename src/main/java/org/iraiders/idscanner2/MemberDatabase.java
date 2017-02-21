package org.iraiders.idscanner2;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MemberDatabase extends Database{
  public MemberDatabase(String serverN, int port, String databaseN, String user, String pass){
    super(serverN, port, databaseN, user, pass);
  }

  public boolean updateAttendance(String memberId){
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String today = dateFormat.format(new Date());
    double time = System.currentTimeMillis();

    try{
      stmt = conn.createStatement();
      res = stmt.executeQuery("SELECT * FROM Members.memberAttendance WHERE memberId = '"+memberId+"'");
    }catch(SQLException e){
      try{
        stmt.executeUpdate("INSERT INTO Members.memberAttendance(memberId, date, time) values ('"+memberId+"', '"+today+"', '"+time+"')");
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
      stmt.executeUpdate("INSERT INTO Members.memberAttendance(memberId, date, time) values ('"+memberId+"', '"+today+"', '"+time+"')");
      return true;
    }catch(SQLException e){
      return false;
    }
  }

  public String queryMemberName(String memberId){
    //return "" == does not exist;
    String name = "";

    try{
      stmt = conn.createStatement();
      res = stmt.executeQuery("SELECT * FROM Members.members WHERE memberId='"+memberId+"'");
      res.next();
      name = res.getString("memberName");
    }catch(SQLException e){
      //System.out.println("Line 17: "+e.getErrorCode());
	  if(e.getErrorCode() == 0){
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

//SELECT * FROM Members.members WHERE memberId='600740';
  //named update because it update data instead of reading it.
  public boolean updateAddMember(String name, String id){
    try{
      stmt = conn.createStatement();
      stmt.executeUpdate("insert into Members.members(memberId, memberName) values ('"+id+"', '"+name+"')");
    }catch(SQLException e){
      //System.out.println("Line 31: "+e);
      return false;
    }

    try{
      stmt.close();
    }catch(SQLException e){
      //System.out.println("Line XX: "+e);
    }
    return true;
  }


}
