package com.xyz.cbt;

import org.slf4j.Logger;


import java.io.*;

public class ExecuteCommand implements Serializable {
   // private static Logger LOGGER = Logger.getLogger(com.adobe.cbm.ExecuteCommand.class);

    String os=null;
    public String getOsName(){
        if(os==null)
            os=System.getProperty("os.name");
        return os;
    }
    public boolean isWindows()
    {
        return getOsName().startsWith("Windows");
    }
    public boolean execute(String command){


        //logobj1.log(msg,level);
       // PrintStream printinfo = new PrintStream(logobj1);
        //printinfo.println("running the command");
        //return execute(command,printinfo);
        return true;
    }
    public boolean execute(String command, Logger logobj){

        boolean result=true;
        ProcessBuilder processBuilder = new ProcessBuilder();
        String exeName=isWindows()==true?"cmd.exe":"bash";
        String arg=isWindows()==true?"/c":"-c";
        processBuilder.command(exeName,arg,command);
        try {

            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line+ "\n");
            }
            int exitVal = process.waitFor();
            if (exitVal == 0) {
                logobj.info("Success!"+output);
            } else {
                result=false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
