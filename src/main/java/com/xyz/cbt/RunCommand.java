package com.xyz.cbt;

import logging.LogUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;


public class RunCommand implements Serializable
{
    public RunCommand(){

    }
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

    public static void main( String[] args )
    {
        RunCommand obj = new RunCommand();
        obj.execute("echo 'vyshali'", LogUtils.getLogger(RunCommand.class));
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
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
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
