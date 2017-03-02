# IDScanner
Built way too late at night

To Use: 

1. Clone the respository
2. Navigate to the directory you cloned it into.
3. If you have not yet installed gradle, do so.
4. Run gradle clean build in the directory.
5. navigate to the build/distributions folder and find the IDScanner2.zip and unzip it to the desired location.
6. Run the the bin/IDScanner2 script in the directory you unzipped the file into.

To configure: 

1. Run the script to create the configuration file bin/files/config.ini.
2. open config.ini in your text editor of choice and change the values to what you see fit.
3. If you want to change the admin password go to http://www.xorbin.com/tools/sha256-hash-calculator and type the your desired password in to the text box. Then, append the letters HUMONEONSDFH on the end and click calculate.
4. Open the Main.java file and navigate to the checkPassword method. 
5. In the return statement, change the string inside the .equals() statment to the output to the hash value the was created by the calculator.
6. Repeat steps 2-6 of the to use section.
