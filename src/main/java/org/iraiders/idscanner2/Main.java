package org.iraiders.idscanner2;

import javax.swing.JOptionPane;

@SuppressWarnings("InfiniteLoopStatement")
public class Main {

  final static String databasePath = "./src/db/members.db";

  final static int maxNameLength = 20;
  final static int minNameLength = 3;
  final static int maxIdLength = 20;
  final static int minIdLength = 3;


  public static void main(String [] args){
    System.out.println("Starting Connection");
    start();
  }

  static String getUserId(){
    String id;
    do{ //Checks the length of the Id
      id = JOptionPane.showInputDialog("What is your id?");
      if(id == null){
        return id; //If the user hits cancel it is interpreted as an exit command
      }else if(id.length() > maxIdLength){
        JOptionPane.showMessageDialog(null, "IDs are a maximum of "+maxIdLength+" characters.");
      }else if(id.length() < minIdLength){
        JOptionPane.showMessageDialog(null, "IDs are a minimum of "+minIdLength+" characters.");
      }
    }while(id.length() > maxIdLength || id.length() < minIdLength);  //Makes sure that the id is inside the bounds provided above
    return id.toLowerCase();
  }

  static String getUserName(){
    String name;
    do{
      name = JOptionPane.showInputDialog("What is your name?");
      if(name == null){
        break;
      }else if(name.length() > maxNameLength){
        JOptionPane.showMessageDialog(null, "Names are a maximum of "+maxNameLength+" characters.");
      }else if(name.length() < minNameLength){
        JOptionPane.showMessageDialog(null, "Names are a minimum of "+minNameLength+" characters.");
      }
    }while(name.length() > maxNameLength || name.length() < minNameLength);
    //System.out.println("Name is: "+name);
    return name;
  }

  static void start(){
    String dbPath = "jdbc:sqlite:"+databasePath;
    MemberDatabase store = new MemberDatabase(dbPath);
    while(true){
      String id = getUserId();  //Gets the name based on the ID from the database


      //These are conditional statements that allow certain IDs or Names to do certain things
      if(id == null){
        int option = JOptionPane.showConfirmDialog(null, "Do you really want to quit?", "Do you really want to quit?", JOptionPane.YES_NO_OPTION);
        if(option == 0){
          break;
        }else{
          continue;
        }
      }
	  if(store.active == false){
		JOptionPane.showMessageDialog(null, "Database connection inactive, retrying");
		store = new MemberDatabase(dbPath);
		continue;
	  }
      String name = store.queryMemberName(id);
	  if(name.equals("-1")){
		JOptionPane.showMessageDialog(null, "Database connection inactive, retrying");
		store = new MemberDatabase(dbPath);
		continue;
	  }else if(name.length() < 1){ //If the query returns no name this runs
        //Annother loop the check the length, this time of the name
        name = getUserName();
        //Name is null if the user presses cancel
        if(name != null){
          //If the user doesnt press cancel the database is updated with the new name.
          store.updateAddMember(name, id);
        }else{
          continue;
        }
      }

      if(store.updateAttendance(id)){
        //System.out.println("Welcome "+name);  //After all the logic is done the user is welcomed
        JOptionPane.showMessageDialog(null, "Thanks for coming, " + name + "!");
      }else{
        JOptionPane.showMessageDialog(null, "You can only log in once per meeting "+name+"."); //if false is returned then the person already logged in
      }
    }
    //When the while loop is broken out of all the connections to the database are closed using database.exit();
    System.out.println("Robots don't quit... \nBut this isn't a Robot!");
    store.exit();
  }
}
