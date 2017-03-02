package org.iraiders.idscanner2;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SuppressWarnings("InfiniteLoopStatement")
public class Main {
    final private static String writePath = "./files/attendance.txt";
    final private static String configPath = "./files/config.ini";

    final static String serverName = "localhost";
    final static int port = 8889;
    final static String databaseName = "Members";
    final static String user = "root";
    final static String dbPassword = "root";


    private static int maxNameLength;
    private static int minNameLength;
    private static int minNameNumbers;
    private static int minNameLetters;
    private static int minNameSymbols;

    private static int maxIdLength;
    private static int minIdLength;
    private static int minIdNumbers;
    private static int minIdLetters;
    private static int minIdSymbols;

    private static MemberDatabase store;


    public static void main(String [] args){
        System.out.println("Starting Connection");
        start();
    }

    static String getUserId(){
        String id;
        int numberCount;
        int letterCount;
        int symbolCount;
        boolean idFailed;

        do{ //Checks the length of the Id
            numberCount = 0;
            letterCount = 0;
            symbolCount = 0;
            idFailed = false;
            id = JOptionPane.showInputDialog("What is your id?");
            if(id == null){
                return null;
            }
            for(int i = 0; i < id.length(); i++){
                char currentChar = id.charAt(i);
                if((currentChar >= 97 && currentChar <= 122) || (currentChar >= 65 && currentChar <= 90)){
                    letterCount++;
                }else if(currentChar >= 48 && currentChar <= 57){
                    numberCount++;
                }else{
                    symbolCount++;
                }
            }
            if(id.length() > maxIdLength){
                idFailed = true;
                JOptionPane.showMessageDialog(null, "IDs are a maximum of "+maxIdLength+" characters.");
            }else if(id.length() < minIdLength){
                idFailed = true;
                JOptionPane.showMessageDialog(null, "IDs are a minimum of "+minIdLength+" characters.");
            }else if(numberCount < minIdNumbers){
                idFailed = true;
                JOptionPane.showMessageDialog(null, "ID's must have at least "+minIdNumbers+" numbers");
            }else if(letterCount < minIdLetters){
                idFailed = true;
                JOptionPane.showMessageDialog(null, "ID's must have at least "+minIdLetters+" letters");
            }else if(symbolCount < minIdSymbols){
                idFailed = true;
                JOptionPane.showMessageDialog(null, "ID's must have at least "+minIdSymbols+" symbols");
            }
        }while(idFailed);  //Makes sure that the id is inside the bounds provided above
        return id.toLowerCase();
    }

    static String getUserName(){
        String name;
        int numberCount;
        int letterCount;
        int symbolCount;
        boolean nameFailed;
        do{
            numberCount = 0;
            letterCount = 0;
            symbolCount = 0;
            nameFailed = false;
            name = JOptionPane.showInputDialog("What is your name?");
            if(name == null){
                return null;
            }
            for(int i = 0; i < name.length(); i++){
                char currentChar = name.charAt(i);
                if((currentChar >= 97 && currentChar <= 122) || (currentChar >= 65 && currentChar <= 90)){
                    letterCount++;
                }else if(currentChar >= 48 && currentChar <= 57){
                    numberCount++;
                }else{
                    symbolCount++;
                }
            }
            if(name.length() > maxNameLength){
                nameFailed = true;
                JOptionPane.showMessageDialog(null, "Names are a maximum of "+maxNameLength+" characters.");
            }else if(name.length() < minNameLength){
                nameFailed = true;
                JOptionPane.showMessageDialog(null, "Names are a minimum of "+minNameLength+" characters.");
            }else if(numberCount < minNameNumbers){
                nameFailed = true;
                JOptionPane.showMessageDialog(null, "Names must have at least "+minNameNumbers+" numbers");
            }else if(letterCount < minNameLetters){
                nameFailed = true;
                JOptionPane.showMessageDialog(null, "Names must have at least "+minNameLetters+" letters");
            }else if(symbolCount < minNameSymbols){
                nameFailed = true;
                JOptionPane.showMessageDialog(null, "Names must have at least "+minNameSymbols+" symbols");
            }
        }while(nameFailed);
        return name;
    }

    static void startAdmin(String serverN, int port, String databaseN, String user, String pass){
        AdminCommands admin = new AdminCommands(serverName, port, databaseName, user, dbPassword);
        String command = JOptionPane.showInputDialog("What command would you like to execute?\n(help to get list of commands)");
        while(command != null){
            if (command.equalsIgnoreCase("change name") || command.equalsIgnoreCase("cn")) {
                String id = JOptionPane.showInputDialog("What Id");
                String name = JOptionPane.showInputDialog("What is the new name?");
                if (!(id == null || name == null)) {
                    if (!admin.changeName(id, name)) {
                        JOptionPane.showMessageDialog(null, "Name Change Failed");
                    } else {
                        JOptionPane.showMessageDialog(null, "Name Changed");
                    }
                }
            } else if (command.equalsIgnoreCase("get attendance") || command.equalsIgnoreCase("ga")) {
                String id = JOptionPane.showInputDialog("What Id");
                if (id != null) {
                    String name = store.queryMemberName(id);
                    if (name.length() < 1) {
                        name = id;
                    }
                    JOptionPane.showMessageDialog(null, name + " has attended " + admin.getNumAttendance(id) + " times.\nPercentage of max attendance: " + admin.getPercentAttendance(id) + "%");
                }
            } else if (command.equalsIgnoreCase("write file") || command.equalsIgnoreCase("wf")) {
                admin.writeFile(writePath, maxNameLength, maxIdLength);
                JOptionPane.showMessageDialog(null, "Writing file");
            } else if (command.equalsIgnoreCase("help")) {
                JOptionPane.showMessageDialog(null, "Get Attendance (GA): Display attendance by ID\nChange Name (CN): Change name by ID\nWrite File (WF): Write full attendance info to file");
            } else {
                JOptionPane.showMessageDialog(null, "That command does not exist");
            }
            command = JOptionPane.showInputDialog("What command would you like to execute?\n(help to get list of commands)");
        }
    }

    static boolean checkPassword(String pass){
        pass += "HUMONEONSDFH"; //This is totally professionally salting it... or something;
        byte[] passHash;
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(pass.getBytes("UTF-8"));
            passHash = md.digest();
        }catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            passHash = null;
        }
        return String.format("%064x", new java.math.BigInteger(1, passHash)).equals("fc7c008267b305329208efc2ee3ded3fb2844186ff3aa6ae71044f26f4d4a430");
    }

    static void start(){
        IniFileReader config = new IniFileReader(configPath);
        IniProperty [] configList = config.readFile();
        for(IniProperty i : configList){
            switch(i.name){
                case "maxNameLength": maxNameLength = Integer.parseInt(i.value);
                    break;
                case "minNameLength": minNameLength = Integer.parseInt(i.value);
                    break;
                case "minNameNumbers": minNameNumbers = Integer.parseInt(i.value);
                    break;
                case "minNameLetters": minNameLetters = Integer.parseInt(i.value);
                    break;
                case "minNameSymbols": minNameSymbols = Integer.parseInt(i.value);
                    break;
                case "maxIdLength": maxIdLength = Integer.parseInt(i.value);
                    break;
                case "minIdLength": minIdLength = Integer.parseInt(i.value);
                    break;
                case "minIdNumbers": minIdNumbers = Integer.parseInt(i.value);
                    break;
                case "minIdLetters": minIdLetters = Integer.parseInt(i.value);
                    break;
                case "minIdSymbols": minIdSymbols = Integer.parseInt(i.value);
                    break;
                default: System.out.println("Unknown config option");
            }
        }
        store = new MemberDatabase(serverName, port, databaseName, user, dbPassword);
        while(true){
            if(!store.active){ // If the database can't connect
                try {
                    Thread.sleep(2000);
                }catch(InterruptedException e){
                    System.out.println("Interrupted");
                }
                store = new MemberDatabase(serverName, port, databaseName, user, dbPassword); //Retries
                int option = JOptionPane.showConfirmDialog(null, "Database connection inactive. Press Ok to retry or Cancel to quit.\n(If the problem persists tell a software team member)", "No Connection", JOptionPane.OK_CANCEL_OPTION);
                if(option == 1){
                    System.exit(0);
                }else{
                    continue;
                }
            }

            String id = getUserId();  //Gets the name based on the ID from the database
            if(id == null){ //Id is null if the user presses cancel. Cancel means exit.
                int option = JOptionPane.showConfirmDialog(null, "Do you really want to quit?", "Do you really want to quit?", JOptionPane.YES_NO_OPTION);
                if(option == 0){
                    break;
                }else{
                    continue;
                }
            }
            if(id.equalsIgnoreCase("Admin")){
                JPanel panel = new JPanel();
                JLabel label = new JLabel("What is the password?");
                JPasswordField pass = new JPasswordField(10);
                panel.add(label);
                panel.add(pass);
                String[] options = new String[]{"Ok", "Cancel"};
                int option = JOptionPane.showOptionDialog(null, panel, "The title",
                        JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, options, options[0]);
                if(option == 0) {
                    char[] password = pass.getPassword();
                    if(checkPassword(new String(password))){
                        startAdmin(serverName, port, databaseName, user, dbPassword);
                    }else{
                        JOptionPane.showMessageDialog(null, "Incorrect password.");
                    }
                }
            }else {
                String name = store.queryMemberName(id);
                if (name.equals("-1")) { //
                    JOptionPane.showMessageDialog(null, "Database connection inactive, retrying");
                    store = new MemberDatabase(serverName, port, databaseName, user, dbPassword);
                    continue;
                } else if (name.length() < 1) { //If the query returns no name this runs
                    //Another loop the check the length, this time of the name
                    name = getUserName();
                    while (name != null && name.equalsIgnoreCase(id)) { //Checks if id == name
                        JOptionPane.showMessageDialog(null, "Name cannot equal Id");
                        name = getUserName();
                    }
                    //Name is null if the user presses cancel
                    if (name != null) {
                        //If the user doesn't press cancel the database is updated with the new name.
                        store.updateAddMember(name, id);
                    } else {
                        continue;
                    }
                }

                if (store.updateAttendance(id)) {
                    //System.out.println("Welcome "+name);  //After all the logic is done the user is welcomed
                    JOptionPane.showMessageDialog(null, "Thanks for coming, " + name + "!");
                } else {
                    JOptionPane.showMessageDialog(null, "You can only log in once per meeting " + name + "."); //if false is returned then the person already logged in
                }
            }
        }
        //When the while loop is broken out of all the connections to the database are closed using database.exit();
        System.out.println("Robots don't quit... \nBut this isn't a Robot!");
        store.exit();
    }
}
