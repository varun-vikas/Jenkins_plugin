package com.xyz.cbt;

import logging.LogUtils;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import org.jenkinsci.Symbol;
import org.jenkinsci.remoting.RoleChecker;
import org.kohsuke.stapler.DataBoundConstructor;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

public class CommandPlugin extends FileOperation{
    private final String command;
    public String getCommand() {
        return command;
    }

    @DataBoundConstructor
    public CommandPlugin(String command) {
    this.command=command;
     }
    public boolean perform(Run build, FilePath workspace, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException {
        boolean result=false;
        listener.getLogger().println("command:"+command);
        try {

            FilePath ws = workspace;
            result=ws.act(new TargetFileCallable(command,listener));
        } catch (RuntimeException e) {
            return false;
        }
        catch (Exception e) {
            return false;
        }
        return result;
    }
    private static final class TargetFileCallable implements FilePath.FileCallable<Boolean> {
        private static final long serialVersionUID = 1;
        private String command;
        private final TaskListener listener;
        public TargetFileCallable(String command,TaskListener listener) {
            this.command=command;
            this.listener=listener;
        }
        @Override
        public Boolean invoke(File ws, VirtualChannel channel) throws IOException {
            Logger logger = LogUtils.getLogger(CommandPlugin.class);
            RunCommand rc= new RunCommand();
            return rc.execute(command,logger);

        }
        @Override
        public void checkRoles(RoleChecker checker) throws SecurityException {

        }
    }
    @Extension
    @Symbol("RunCommandPlugin")
    public static class DescriptorImpl extends FileOperationDescriptor {
        public String getDisplayName() {
            return "Command";
        }
    }
}
