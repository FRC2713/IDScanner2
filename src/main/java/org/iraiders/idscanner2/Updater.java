package org.iraiders.idscanner2;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarFile;

public class Updater {
    public static String jarUrl = "https://github.com/Veldrovive/testJar/releases/download/1.1.0/IDScanner2.jar";

    private static boolean downloadJar(String jarUrl){
        URL url;
        URLConnection con;
        DataInputStream dis;
        FileOutputStream fos;
        byte[] fileData;
        File dir = new File("./jars");
        dir.mkdir();
        try {
            url = new URL(jarUrl); //File Location goes here
            con = url.openConnection(); // open the url connection.
            dis = new DataInputStream(con.getInputStream());
            fileData = new byte[con.getContentLength()];
            for (int q = 0; q < fileData.length; q++) {
                fileData[q] = dis.readByte();
            }
            dis.close(); // close the data input stream
            fos = new FileOutputStream(new File("./jars/IDScanner2.jar")); //FILE Save Location goes here
            fos.write(fileData);  // write out the file we want to save.
            fos.close(); // close the output stream writer
            return true;
        }
        catch(Exception m) {
            System.out.println(m);
            return false;
        }
    }

    public static boolean updateVersion(){
        String newPath = "./jars/IDScanner2.jar";
        String currentJarPath = "../lib/IDScanner2.jar";

        boolean success = downloadJar(jarUrl);
        if(success) {
            Path p = Paths.get(newPath);
            Path folder = p.getParent();
            folder.toFile().mkdir();
            JarFile newJar;
            JarFile oldJar;
            try {
                File newFile = new File(newPath);
                File oldFile = new File(currentJarPath);

                newJar = new JarFile(newPath);
                oldJar = new JarFile(currentJarPath);

                String newVersionS = getNumbers(newJar.getManifest().getMainAttributes().getValue("version"));
                String oldVersionS = getNumbers(oldJar.getManifest().getMainAttributes().getValue("version"));

                System.out.println("New Version: " + newVersionS + ", Old Version: " + oldVersionS);

                int newVersion = Integer.parseInt(newVersionS);
                int oldVersion = Integer.parseInt(oldVersionS);
                if (newVersion > oldVersion) {
                    newFile.renameTo(oldFile);
                    AdminCommands.restart();
                    Main.exit();
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Already Up To Date");
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }else{
            return false;
        }
    }

    private static String getNumbers(String s){
        StringBuilder res = new StringBuilder();
        char [] string = s.toCharArray();
        for(char c : string){
            if(c >= 48 && c<= 57){
                res.append(c);
            }
        }
        String result = res.toString();
        return result;
    }
}
