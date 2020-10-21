package com.xyz.cbt;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Run;
import hudson.model.TaskListener;

public abstract class FileOperation extends AbstractDescribableImpl<FileOperation> {
    public abstract boolean perform(Run build, FilePath workspace, Launcher launcher,TaskListener listener) throws Exception;
}