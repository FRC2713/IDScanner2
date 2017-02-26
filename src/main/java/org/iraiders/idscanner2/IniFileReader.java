package org.iraiders.idscanner2;

import java.io.*;
import java.lang.StringBuilder;
import java.lang.Throwable;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IniFileReader{
    private String filePath;

    public IniFileReader(String fP){
        filePath = fP;
        Path p = Paths.get(filePath);
        Path folder = p.getParent();
        boolean dirSuccess = folder.toFile().mkdir();
        if(dirSuccess){
            System.out.println("Created file directory");
        }

        File writeFile = new File(filePath);
        try{
            boolean fileSuccess = writeFile.createNewFile();
            if(fileSuccess){
                System.out.println("Auto generated config file");
                PrintWriter writer = new PrintWriter(filePath, "UTF-8");
                writer.println(";Auto generated");
                writer.println("[NameConfig]");
                writer.println("maxNameLength=20");
                writer.println("minNameLength=3");
                writer.println();
                writer.println("[IdConfig]");
                writer.println("maxIdLength=20");
                writer.println("minIdLength=3");
                writer.close();
            }
        }catch(IOException e){
            System.out.println("Failed to create file");
        }
    }

    public IniProperty [] readFile(){
        try{
            File file = new File(filePath);
            FileReader reader = new FileReader(file);
            FileReader readerCounter = new FileReader(file);

            BufferedReader lineCounter = new BufferedReader(readerCounter);
            int lineCount = 0;
            String currentLineCounter = lineCounter.readLine();
            char currentChar;
            while(currentLineCounter != null){
                try{
                    currentChar = currentLineCounter.charAt(0);
                }catch(Throwable ex){
                    currentChar = ';';
                }
                if(currentChar != '[' && currentChar != ';'){
                    lineCount++;
                }
                currentLineCounter = lineCounter.readLine();
            }
            IniProperty [] config = new IniProperty [lineCount];

            BufferedReader buffer = new BufferedReader(reader);
            String currentLine;
            String currentSection;
            int count = 0;
            int whileCounter = 0;
            StringBuilder section = new StringBuilder().append("None");
            StringBuilder name;
            StringBuilder value;

            do{
                currentLine = buffer.readLine();
                //System.out.println(currentLine);
                if(currentLine != null){
                    if(getCurrentChar(currentLine, 0) == ';'){

                    }else if(getCurrentChar(currentLine, 0) == '['){
                        count = 1;
                        section = new StringBuilder();
                        while(getCurrentChar(currentLine, count) != ']'){
                            section.append(getCurrentChar(currentLine, count));

                            count++;
                        }
                        currentSection = section.toString();
                    }else{
                        count = 0;
                        name = new StringBuilder();
                        while(getCurrentChar(currentLine, count) != '='){
                            name.append(getCurrentChar(currentLine, count));

                            count++;
                        }
                        count++;
                        value = new StringBuilder();
                        for(int i = count; i < currentLine.length(); i++){
                            value.append(getCurrentChar(currentLine, i));
                        }
                        config[whileCounter] = new IniProperty(name.toString(), value.toString(), section.toString());
                        whileCounter++;
                    }
                }
            }while(currentLine != null);
            return config;
        }catch(FileNotFoundException ex){
            System.out.println(ex);
            return null;
        }catch(IOException ex){
            System.out.println(ex);
            return null;
        }
    }

    static char getCurrentChar(String in, int loc){
        char currentChar;
        try{
            currentChar = in.charAt(loc);
        }catch(Throwable ex){
            currentChar = ';';
        }
        return currentChar;
    }
}
