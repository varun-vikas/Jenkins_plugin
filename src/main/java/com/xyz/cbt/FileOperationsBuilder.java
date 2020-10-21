package com.xyz.cbt;

import logging.LogUtils;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FileOperationsBuilder extends Builder implements  SimpleBuildStep{

    private final List<FileOperation> fileOperations;
    @DataBoundConstructor
    public FileOperationsBuilder(List<FileOperation> fileOperations) {
        this.fileOperations = fileOperations == null ? new ArrayList<FileOperation>() : new ArrayList<FileOperation>(fileOperations);
    }

    public List<FileOperation> getFileOperations() {
        return Collections.unmodifiableList(fileOperations);
    }

    @Override
    public  void perform(Run build,FilePath workspace,Launcher launcher,TaskListener listener)throws InterruptedException,IOException{
        boolean result = false;
        if (fileOperations.size() > 0) {
            for (FileOperation item : fileOperations) {
                listener.getLogger().println("item: "+item.toString());
                try {
                    result = item.perform(build, workspace, launcher, listener);
                    if (!result) break;
                }
                catch (Exception e){
                    result=false;
                    break;
                }
            }

        } else {
            LogUtils.addHandler(listener.getLogger());
            LogUtils.getLogger(FileOperationsBuilder.class).info("sample message");
            listener.getLogger().println("No Build Step added.");
            result = true;

        }
        if (!result) {
            throw new AbortException("Build Step Failed");
        }
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }


    @Extension
    @Symbol("fileOperations")
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return "CBS";
        }


        @SuppressWarnings("unused")
        public List<FileOperationDescriptor> getFileOperationDescriptors() {
            List<FileOperationDescriptor> result = new ArrayList<>();
            Jenkins j = Jenkins.getInstance();
            if (j == null) {
                return result;
            }
            for (Descriptor<FileOperation> d : j.getDescriptorList(FileOperation.class)) {
                if (d instanceof FileOperationDescriptor) {
                    FileOperationDescriptor descriptor = (FileOperationDescriptor) d;
                    result.add(descriptor);
                }
            }
            return result;
        }
    }
}

