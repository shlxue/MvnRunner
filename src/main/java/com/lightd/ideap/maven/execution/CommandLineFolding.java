package com.lightd.ideap.maven.execution;

import com.intellij.execution.ConsoleFolding;
import com.lightd.ideap.maven.MvnCommandFolding;

import java.util.Arrays;
import java.util.List;

public class CommandLineFolding  extends ConsoleFolding implements MvnCommandFolding {
    private String jdkHome;
    private String mainClass;
    private String goalStr;

    @Override
    public boolean shouldFoldLine(String line) {
        if (jdkHome != null && line.startsWith(jdkHome) && line.contains(mainClass)) {
            jdkHome = null;
            return true;
        }
        return false;
    }

    @Override
    public String getPlaceHolder(String text) {
        return getPlaceholderText(Arrays.asList(text));
    }

    @Override
    public boolean byMavenRun() {
        return mainClass != null;
    }

    @Override
    public String getPlaceholderText(List<String> lines) {
        return "mvn " + goalStr;
    }

    @Override
    public void placeMaven(String jdkHome, String mainClass, String goalStr) {
        this.jdkHome = jdkHome;
        this.mainClass = mainClass;
        this.goalStr = goalStr;
    }
}
